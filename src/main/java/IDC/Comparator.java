package IDC;

import IDC.Comparison.Impl.Simple.ClassDiff;
import IDC.EvolActions.Impl.EvolutionaryActionComposite;
import IDC.Comparison.Interfaces.IClassCompare;
import IDC.Comparison.Interfaces.IPropertyCompare;
import IDC.EvolActions.Factories.ComparatorFactory;
import IDC.EvolActions.Impl.Additions.TimeSliceCreator;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import IDC.EvolActions.Interfaces.IAddClass;
import IDC.Metrics.ClassPropertyMetrics;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class Comparator 
{
    OntModel ontologyModel;
    OntModel instanceModel;
    OntModel evolvedModel;
    EvolutionaryActionComposite executer;
    List<ClassPropertyMetrics> clsPropMetrics ;
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        
    public Comparator(OntModel ontologyModel, OntModel instanceModel) 
    {
        this.ontologyModel = ontologyModel;
        this.instanceModel = instanceModel;
    
        this.evolvedModel  = ontologyModel;
//    this.evolvedModel  = instanceModel; //ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.executer      = new EvolutionaryActionComposite();
        
        
        Ontology evolvedOnt = this.evolvedModel.createOntology("");
        evolvedOnt.addImport(this.evolvedModel.createResource(OntologyUtils.ONT_TIME_URL));
     
        clsPropMetrics = new ArrayList<ClassPropertyMetrics>();
    }

    
    private boolean validateModels()
    {
        if(ontologyModel.isEmpty() || instanceModel.isEmpty() || !instanceModel.listIndividuals().hasNext())
        {
            Utilities.logInfo( "Models have not been instantiated properly." );
            return false;
        }
   
        Utilities.logInfo( "Models have been instantiated properly." );
        return true;
    }
    
    public void run() 
    {
        // end app if no models are valid
        
        /**if(!validateModels())
            return;
        **/
        
        // guidado pelas instancias
        // só funciona para as instâncias que tenham CLASSES associadas
        // reasoner desligado
        ExtendedIterator<Individual> listIndividuals = instanceModel.listIndividuals();
        
//        Utilities.logInfo("\n\n==========================================\nListing all individuals...\n\n ");
        
        while(listIndividuals.hasNext())
        {
            Individual instance = listIndividuals.next();
           
//            Utilities.logInfo("Current Individual: " + instance.getURI());
     
            if(Utilities.isInIgnoreList(instance.getURI()))
                continue;
            
            //this.compareClasses(instance);
            //this.compareProperties(instance);     
            this.compareShapes(instance);
        }
        
        executer.execute(ontologyModel, evolvedModel);
        
        // verificar se é preciso acrescentar validaçoes temporais em classes
        updateTemporalRestrictions(ontologyModel, evolvedModel);
       
        
        // eu nao preciso de copiar as instancias, estou só preocupada com a 
        // evolucao da TBOX
        // copyInstances(ontologyModel, evolvedModel);
    }
    
   
    private void compareShapes(Individual instance)
    {
        boolean ignore = Utilities.isInIgnoreList(instance.getURI());
        if(ignore) return;
        
//        Utilities.logInfo("\n\n%%%%%%%%%%%%%%%%%\nAnalysing the shape of Individual " 
//                + instance.getURI() + ".");

        IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(instance, ontologyModel);
       
        if(classComparator != null)
        {
            EvolutionaryAction compare = classComparator.compare();
            this.executer.add(compare);
        }    
    }
    
    
    private void compareProperties(Individual instance)
    {
        List<Statement> listProperties = instance.listProperties().toList();
               
//        Utilities.logInfo("\n\n%%%%%%%%%%%%%%%%%\nIterating through the properties of Individual " 
//                + instance.getURI() + ". Currently with " + listProperties.size() + " properties.");

        for(Statement stmt : listProperties)
        {
            Utilities.logInfo(OntologyUtils.printStatement(stmt));
            this.compareProperty(stmt);
        }
        
//        Utilities.logInfo("\n\nFinished iterating through Individual " 
//                + instance.getURI() + "'s properties\n%%%%%%%%%%%%%%%%%\n\n");
    }
    
   
    private void compareProperty(Statement t)
    {
        boolean ignore = Utilities.isInIgnoreList(t.getPredicate().getURI());
        
        if(ignore) return;
        IPropertyCompare comparator = ComparatorFactory.getInstance().getPropertyComparator(t, this.ontologyModel);
        
        if(comparator != null)
        {
            EvolutionaryAction compare = comparator.compare();
            this.executer.add(compare);
        }      
    }

    
    /*
    * Verifica se as classes da instânica existem. Se não, adiciona-as.
    */
    private void compareClasses(Individual instance) 
    {
//        Utilities.logInfo("COMPARE CLASSES for instance " 
//                + instance.getURI()
//                + ".\n Iterating through its classes...");
        // procura todas as classes a que a instancia possa pertencer
        
        ExtendedIterator<OntClass> listOntClasses = instance.listOntClasses(true);
        for(OntClass cls : listOntClasses.toList())
        {
            boolean ignore = Utilities.isInIgnoreList(cls.getURI());
            
            if(ignore) continue;
            
            IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(cls, this.ontologyModel); 
            
            if(classComparator!=null)
            {
                IAddClass createAddClassAction = (IAddClass) classComparator.compare();
                if(createAddClassAction!=null)
                    this.executer.add(createAddClassAction);  
            }
        }
    }

    public String printStats() 
    {
        return this.executer.toString();
    }

    private void updateTemporalRestrictions(OntModel ontologyModel, OntModel evolvedModel) 
    {
        
        // ver se a classe tem slices
        // ver qual o ultimo slice (ordenar?)
        // acrescentar slice novo se for o caso
        
        List <OntClass> e_ontClasses = evolvedModel.listClasses().toList();
        LocalDateTime now      = LocalDateTime.now();  
        
              
        for(OntClass newCls : e_ontClasses)
        {
            String uri      = newCls.getURI();
            if(uri == null || Utilities.isInClassIgnoreList(uri) ) 
                continue;
            
           // System.out.println("NEW CLASS URI: " + uri);
            
            OntClass oldCls = ontologyModel.getOntClass(uri);
            
            if(oldCls==null || Utilities.isInIgnoreList(oldCls.getURI()))
                continue;
            
            //ignoremos as timeslices em si para nao andar a TS de TS
            if(isTimeSlice(oldCls) || isTimeSlice(newCls)) continue;
            
            
            // TODO
            // ver se a última versão da classe (newModel) é diferente do timeframe 
             
            // ver se o ultimo timeframe é diferente
            OntClass lastOldSlice = getLastTimeSlice(oldCls);
            OntClass lastNewSlice = getLastTimeSlice(newCls);
            
            lastNewSlice = newCls;
           
            if(lastOldSlice==null || lastNewSlice==null) continue; //porque é que este caso ocorreria?
           
            boolean newVersion = new ClassDiff().isNewVersion(lastOldSlice, lastNewSlice);
            
            System.out.println("\n\t "
                    + "== Source: " + uri 
                    + "\n\t == Comparing:" 
                    + "\n\t\t" + lastOldSlice.getURI() 
                    + "\n\t\t and " + lastNewSlice.getURI() + "\n\t\tResult: " + newVersion + "\n\t==");
           
            //newVersion = true;
            if(newVersion)
            {
                // format : TS__CLASSNAME__VERSIONNUMBER
                String oldURI = lastOldSlice.getURI();

                String olds     []= oldURI.split("#");
                String prefix     = olds[0];
                String className  = olds[1];
                int versionNumber = 0;
                
                // ja existe
                if(className.contains("TS__"))
                {
                    String[] split = className.split("__"); //  TS__CLASSNAME__VERSIONNUMBER
                    try
                    {
                        className = split[1];
                        versionNumber = Integer.parseInt(split[2]);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Error split/converting string " + oldURI + ". Error: " + e.getLocalizedMessage());
                    }
                }

                String prevURI = prefix + "#TS__" + className + "__" + versionNumber;
                versionNumber ++;
                
                String newURI = prefix + "#TS__" + className + "__" + versionNumber;
                
                TimeSliceCreator slicer = new TimeSliceCreator(lastNewSlice, versionNumber);
                slicer.setUp(ontologyModel, evolvedModel);
                slicer.execute();
                
                //ResourceUtils.renameResource(lastNewSlice, newURI);
                
                //a versao anterior foi modificada. copiar o que havia em histórico no modelo anterior
                
                OntologyUtils.copyClass(lastOldSlice, evolvedModel);
                
                lastOldSlice = evolvedModel.getOntClass(prevURI); // as alteraçoes doravante sao no novo modelo
                lastNewSlice = slicer.getSlice(); // as alteraçoes doravante sao no novo modelo

                addBefore(lastOldSlice, lastNewSlice);
                updateTemporalEQRestriction(lastOldSlice, slicer.getSliceBeginning());
              
                
            }
        }
     }
    
    private void updateTemporalEQRestriction(OntClass cls, Individual ind_end)
    {
        if(cls == null || ind_end == null) return;
        
        OntProperty beforeP = this.evolvedModel.getOntProperty(OntologyUtils.BEFORE_P);
        if(beforeP == null) beforeP = this.evolvedModel.createObjectProperty(OntologyUtils.BEFORE_P, false);
        
        
        List<OntClass> eqclasses = cls.listEquivalentClasses().toList();
    
        for(OntClass eq : eqclasses)
        {
            if(eq.isIntersectionClass())
            {
                IntersectionClass intersection = eq.asIntersectionClass();
            
                RDFList operands = intersection.getOperands();
                
                HasValueRestriction beforeRestriction = evolvedModel.createHasValueRestriction(null, beforeP, ind_end);
                operands.add(beforeRestriction);
                               
            }
        }
    
    }
    
    
    private void addLabel(OntClass cls, String label)
    {

        
        cls.addLabel(label, null);
    }
    
    private void addHasEnding(OntClass cls, LocalDateTime enddate)
    {
        
        OntProperty ontProperty = this.evolvedModel.getOntProperty(OntologyUtils.HAS_ENDING_P);

        if(ontProperty == null)
            ontProperty = this.evolvedModel.createObjectProperty(OntologyUtils.HAS_ENDING_P, false);
            
        OntClass instantClass = this.evolvedModel.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = this.evolvedModel.createClass(OntologyUtils.INSTANT_CLS);
        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(enddate), instantClass);
    
        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);
           
        cls.addSuperClass(createHasValueRestriction);
    }
    
    
    private void addBefore(OntClass cls, LocalDateTime enddate)
    {
        
        OntProperty ontProperty = this.evolvedModel.getOntProperty(OntologyUtils.BEFORE_P);

        if(ontProperty == null)
            ontProperty = this.evolvedModel.createObjectProperty(OntologyUtils.BEFORE_P, false);
            
        OntClass instantClass = this.evolvedModel.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = this.evolvedModel.createClass(OntologyUtils.INSTANT_CLS);
        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(enddate), instantClass);
    
        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);
           
        cls.addSuperClass(createHasValueRestriction);
    }
    
    private void replaceAfter(OntClass cls, LocalDateTime startdate)
    {
        List<OntClass> superClasses = cls.listSuperClasses(true).toList();
        
        if(superClasses.isEmpty()) // nao tem has begining tem de passar a ter
        {
            //OntologyUtils.addHasBeginning(cls);
            OntologyUtils.addAfter(cls);
        }
        else
            
        for(OntClass superClass : superClasses)
        {
            if(superClass.isRestriction())
            {
                if(superClass.asRestriction().isHasValueRestriction())
                {
                    HasValueRestriction hvr = superClass.asRestriction().asHasValueRestriction();
                    
                    if(hvr.getOnProperty().getURI().equalsIgnoreCase(OntologyUtils.AFTER_P))
                    {
                        
                        OntClass instantClass = this.evolvedModel.getOntClass(OntologyUtils.INSTANT_CLS);
                        if(instantClass == null)
                            instantClass = this.evolvedModel.createClass(OntologyUtils.INSTANT_CLS);
                        
                        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(startdate), instantClass);
            
                        hvr.setHasValue(date1);   
                    }
                }
            }
        }

    }
    
    
    private void replaceHasBeginning(OntClass cls, LocalDateTime startdate)
    {
        List<OntClass> superClasses = cls.listSuperClasses(true).toList();
        
        if(superClasses.isEmpty()) // nao tem has begining tem de passar a ter
        {
            OntologyUtils.addHasBeginning(cls);
        }
        else
            
        for(OntClass superClass : superClasses)
        {
            if(superClass.isRestriction())
            {
                if(superClass.asRestriction().isHasValueRestriction())
                {
                    HasValueRestriction hvr = superClass.asRestriction().asHasValueRestriction();
                    
                    if(hvr.getOnProperty().getURI().equalsIgnoreCase(OntologyUtils.HAS_BEGINNING_P))
                    {
                        
                        OntClass instantClass = this.evolvedModel.getOntClass(OntologyUtils.INSTANT_CLS);
                        if(instantClass == null)
                            instantClass = this.evolvedModel.createClass(OntologyUtils.INSTANT_CLS);
                        
                        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(startdate), instantClass);
            
                        hvr.setHasValue(date1);   
                    }
                }
            }
        }

    }

    private void addBefore(OntClass ontClass, OntClass newCls) 
    { 
        OntProperty ontProperty = this.evolvedModel.getOntProperty(OntologyUtils.BEFORE_P);

        if(ontProperty == null)
            ontProperty = this.evolvedModel.createObjectProperty(OntologyUtils.BEFORE_P, false);
        
        SomeValuesFromRestriction svfr = this.evolvedModel.createSomeValuesFromRestriction(null, ontProperty, newCls);
        ontClass.addSuperClass(svfr);
        
        ontProperty = this.evolvedModel.getOntProperty(OntologyUtils.AFTER_P);

        if(ontProperty == null)
            ontProperty = this.evolvedModel.createObjectProperty(OntologyUtils.AFTER_P, false);
        
        svfr = this.evolvedModel.createSomeValuesFromRestriction(null, ontProperty, ontClass);
        newCls.addSuperClass(svfr);
        
    }

    private List<OntClass> getTimeSlices(OntClass theOgClass) 
    {
        List<OntClass> timeSlices = new ArrayList<>();
        
        OntProperty sliceP      = theOgClass.getOntModel().getObjectProperty(OntologyUtils.HAS_SLICE_P);
        if(sliceP==null) sliceP = theOgClass.getOntModel().createOntProperty(OntologyUtils.HAS_SLICE_P);
        
        ExtendedIterator<OntClass> superClasses = theOgClass.listSuperClasses(true);
        
        for(OntClass superClass : superClasses.toList())
        {
            if(superClass.isRestriction())
            {
                Restriction superClsR = superClass.asRestriction();
                if(superClsR.isSomeValuesFromRestriction())
                {
                    SomeValuesFromRestriction sCls = superClsR.asSomeValuesFromRestriction();
                    if(sCls.getOnProperty().getURI().equals(sliceP.getURI()))
                    {
                        RDFNode valuesFrom = sCls.getSomeValuesFrom();
                        if(valuesFrom.canAs(OntClass.class))
                            timeSlices.add(valuesFrom.as(OntClass.class));
                    }
                }
                
            }
        }
                   
        return timeSlices;
        
    }

    private OntClass getLastTimeSlice(OntClass cls) 
    {
        List<OntClass> timeSlices = getTimeSlices(cls);
        
        OntProperty       beforeP = cls.getOntModel().getObjectProperty(OntologyUtils.BEFORE_P);
        if(beforeP==null) beforeP = cls.getOntModel().createOntProperty(OntologyUtils.BEFORE_P);
        
        if(timeSlices.isEmpty()) return null;
        
        OntClass lastSlice = timeSlices.get(0);
        // todas têm before menos a última
        
        for(OntClass timeSlice : timeSlices)
        {
            List<OntClass> superClasses = timeSlice.listSuperClasses(true).toList();
            List<OntClass> plc = new ArrayList<>();
            
            for(OntClass superClass : superClasses)
            {
                if(superClass.isRestriction())
                {
                    Restriction superClsR = superClass.asRestriction();
                    if(superClsR.isHasValueRestriction())
                    {
                        Restriction sCls = superClsR.asHasValueRestriction();
                        if(sCls.getOnProperty().getURI().equals(beforeP.getURI()))
                            plc.add(sCls);
                    }
                }
            }   
            
            //List<Statement> sliceList = beforeS.toList();
        
            if(plc.isEmpty())
                lastSlice = timeSlice;
        }
             
        if(isTimeSlice(lastSlice))
            return lastSlice;
        else
            return null;
        
    }

    private boolean isTimeSlice(OntClass cls)
    {
        String uri = cls.getURI();
        
        if(uri == null) return false;
        
        return uri.contains("TS__");        
    }

    private void copyInstances(OntModel ontologyModel, OntModel evolvedModel) 
    {
        List<Individual> individuals = ontologyModel.listIndividuals().toList();
    
        for(Individual i : individuals)
        {
            if(i.getURI()!=null && !i.getURI().contains(OntologyUtils.ONT_TIME_URL))
            {
                evolvedModel.createIndividual(i);
            }
            
//            
//            Statement stmtT = i.asResource().getStmtTerm();
//            
//            if( stmtT != null && stmtT.getSubject() != null && stmtT.getPredicate()!=null
//                    && stmtT.getObject() != null)
//                evolvedModel.add(i.asResource().getStmtTerm());
        }
        
    }
}