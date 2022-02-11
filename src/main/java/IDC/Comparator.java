package IDC;

import IDC.Comparison.Impl.Shape.ClassCompareShape;
import IDC.Comparison.Impl.Simple.ClassDiff;
import IDC.EvolActions.Impl.EvolutionaryActionComposite;
import IDC.Comparison.Interfaces.IClassCompare;
import IDC.Comparison.Interfaces.IPropertyCompare;
import IDC.EvolActions.Factories.ComparatorFactory;
import IDC.EvolActions.Impl.Additions.TimeSliceCreator;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import IDC.EvolActions.Interfaces.IAddClass;
import IDC.Metrics.ClassPropertyMetrics;
import IDC.Metrics.EntityMetricsStore;
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
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.ResourceUtils;
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
    
    public static OntModel temporal_instancesModel;
    
    EvolutionaryActionComposite executer, ontologyModelUpdater;
    List<ClassPropertyMetrics> clsPropMetrics ;
    

    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        
    public Comparator(OntModel ontologyModel, OntModel evolvedModel, OntModel instanceModel) 
    {
        Comparator.temporal_instancesModel = evolvedModel;
        
        this.ontologyModel = ontologyModel;
        this.instanceModel = instanceModel;
    
        this.evolvedModel  = evolvedModel; 
       
        this.executer      = new EvolutionaryActionComposite();
        this.ontologyModelUpdater      = new EvolutionaryActionComposite();
        
        Ontology evolvedOnt = this.evolvedModel.createOntology("");
        evolvedOnt.addImport(this.evolvedModel.createResource(OntologyUtils.ONT_TIME_URL));
     
        clsPropMetrics = new ArrayList<ClassPropertyMetrics>();
    }

   
    public void run() 
    {

        // guidado pelas instancias
        // só funciona para as instâncias que tenham CLASSES associadas
        // reasoner desligado
        ExtendedIterator<Individual> listIndividuals = instanceModel.listIndividuals();

        while(listIndividuals.hasNext())
        {
            Individual instance = listIndividuals.next();

            if(Utilities.isInIgnoreList(instance.getURI()))
                continue;
              
            this.compareShapes(instance);
        }
        
//        executer.execute(ontologyModel, ontologyModel);
        
        //evolvedModel.add(Comparator.temporal_instancesModel);
        
//        copyTimeEntities(Comparator.temporal_instancesModel, evolvedModel);
        
        Utils.OntologyUtils.writeModeltoFile(ontologyModel, "Indexes/TestOnto/ontologyModel1.ttl");

        
        executer.execute(ontologyModel, evolvedModel);

        // verificar se é preciso acrescentar validaçoes temporais em classes
        updateTemporalRestrictions();
       

        
    }
   
    private void copyTimeEntities(OntModel source, OntModel target)
    {
        OntClass instantClass = source.getOntClass(OntologyUtils.INSTANT_CLS);
        List<Individual> instants = source.listIndividuals(instantClass).toList();
    
        for(Individual instant : instants)
            OntologyUtils.copyIndividual(instant, evolvedModel);
        
        OntClass intervalClass = source.getOntClass(OntologyUtils.INTERVAL_CLS);
        List<Individual> intervals = source.listIndividuals(intervalClass).toList();
    
        for(Individual interval : intervals)
            OntologyUtils.copyIndividual(interval, evolvedModel);
    
    
    }
    
    
   
    private void compareShapes(Individual instance)
    {
        boolean ignore = Utilities.isInIgnoreList(instance.getURI());
        if(ignore) return;
       
        ClassCompareShape shapeC  = new ClassCompareShape(instance, ontologyModel);
        shapeC.setup(ontologyModel, evolvedModel);
        
        EvolutionaryAction compare = shapeC.compare();
        this.executer.add(compare);
    
    }
    
    
    private void compareProperties(Individual instance)
    {
        List<Statement> listProperties = instance.listProperties().toList();
               
        for(Statement stmt : listProperties)
        {
            Utilities.logInfo(OntologyUtils.printStatement(stmt));
            this.compareProperty(stmt);
        }
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

    private void updateTemporalRestrictions() 
    {
//        List <OntClass> e_ontClasses = ontologyModel.listClasses().toList();
        List <OntClass> e_ontClasses = evolvedModel.listClasses().toList();
        LocalDateTime now      = LocalDateTime.now();  
        
        for(OntClass newCls : e_ontClasses)
        {
            String uri      = newCls.getURI();
            if(uri == null || Utilities.isInClassIgnoreList(uri) ) 
                continue;
            
            System.out.println("NEW CLASS URI: " + uri);
            
            OntClass oldCls = ontologyModel.getOntClass(uri);
            
            if(oldCls==null || Utilities.isInIgnoreList(oldCls.getURI()))
                continue;
            
            //ignoremos as timeslices em si para nao andar a TS de TS
            if(OntologyUtils.isTimeSlice(oldCls) || OntologyUtils.isTimeSlice(newCls)) continue;
            
             
            // ver se o ultimo timeframe é diferente
            OntClass lastOldSlice = OntologyUtils.getLastTimeSlice(oldCls);
            OntClass lastNewSlice = newCls;
           
            if(lastOldSlice==null) continue; //porque é que este caso ocorreria?
           
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
                
                String prevURI = lastOldSlice.getURI();
                
                if(versionNumber!=0)
                    prevURI = prefix + "#TS__" + className + "__" + versionNumber;
                
                versionNumber ++;
                                
                TimeSliceCreator slicer = new TimeSliceCreator(evolvedModel, lastNewSlice, versionNumber);
                slicer.execute();

                lastNewSlice = slicer.getSlice(); // as alteraçoes doravante sao no novo modelo

                if(evolvedModel.getOntClass(prevURI)==null)
                    lastOldSlice = evolvedModel.createClass(prevURI);
                else
                    lastOldSlice = evolvedModel.getOntClass(prevURI);
                
                //copiar para o modelo novo
                OntologyUtils.copyClassDetails(ontologyModel.getOntClass(prevURI), lastOldSlice);

                lastOldSlice = evolvedModel.getOntClass(prevURI); // as alteraçoes doravante sao no novo modelo
                
                OntologyUtils.copyClassDetails(oldCls, lastNewSlice);
                
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
                try
                {
                    IntersectionClass intersection = eq.asIntersectionClass();

                    RDFList operands = intersection.getOperands();
                    
                    HasValueRestriction beforeRestriction = evolvedModel.createHasValueRestriction(null, beforeP, ind_end);
                    operands.add(beforeRestriction);
                }
                catch(Exception e)
                {
                    System.out.println("Not Intersection. Reason: " + e.getMessage());
                    cls.removeEquivalentClass(eq);
                }
                               
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
        
        if(ontClass.getURI().contains("TS__"))
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

    private void addClassRestrictions() 
    {       
        List <OntClass> e_ontClasses = evolvedModel.listClasses().toList();

        for(OntClass cls : e_ontClasses)
        {
            String classURI = cls.getURI();
             
            if(Utilities.isInIgnoreList(classURI))
                continue;
            
            ClassPropertyMetrics cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(classURI);  
            
            if(cpm==null) continue;
            
            ClassCompareShape.populateComposite(evolvedModel, cpm, executer);
        }
        
        ClassCompareShape.run(evolvedModel, executer);        
        executer.execute(ontologyModel, evolvedModel);
        
        
    
    }
}