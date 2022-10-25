package IDC;

import IDC.Comparison.Impl.Shape.ClassCompareShape;
import IDC.Comparison.Impl.Simple.ClassDiff;
import IDC.EvolActions.Impl.EvolutionaryActionComposite;
import IDC.Comparison.Interfaces.IClassCompare;
import IDC.Comparison.Interfaces.IPropertyCompare;
import IDC.EvolActions.Factories.ComparatorFactory;
import IDC.EvolActions.Impl.Additions.AddClass;
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
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
        List <Individual> listIndividuals = instanceModel.listIndividuals().toList();

        System.out.println("Individuals before copy:");
        for(Individual individual : listIndividuals)
        {    
            //System.out.println("\t> " + instance.getURI());
            if(Utilities.isInIgnoreList(individual.getURI()))
                continue;
            
            List<OntClass> listOntClasses = individual.listOntClasses(false).toList();
            
            for(OntClass cls : listOntClasses)
            {
                if(cls.getURI()!= null && evolvedModel.getOntClass(cls.getURI()) == null)
                {
//                    evolvedModel.createClass(cls.getURI());
                    AddClass addClass = new AddClass(cls.getURI());
                    addClass.setCopy(false);
                    addClass.setStartEndInstance(individual, individual);
                    addClass.setUp(instanceModel, evolvedModel);
                    addClass.execute();
                }
            }
        }
        
        System.out.println("Total Individuals before copy: " + listIndividuals.size());
        
        System.out.println("Copying individual list...");
        listIndividuals = instanceModel.listIndividuals().toList();
        int count = 0;
        for(Individual individual : listIndividuals)
        {    
            if(Utilities.isInIgnoreList(individual.getURI()))
                continue;

            //System.out.println("\tCurrent individual: " + instance.getURI());

            List<OntClass> ontClasses = individual.listOntClasses(true).toList();
    
            StmtIterator listProperties = individual.listProperties();
            evolvedModel.add(listProperties);
            
            OntClass ontClass  = evolvedModel.getOntClass(individual.getOntClass(true).getURI());
            Individual new_ind = evolvedModel.createIndividual(individual.getURI(), ontClass);

            for(OntClass cls : ontClasses)
                new_ind.addOntClass(cls);
                      
            count++;
        }
        System.out.println("Finished copying individuals to evolved model. Total copies: " + count);
        //remover erros?
        
        listIndividuals = evolvedModel.listIndividuals().toList();
        
      //  cleanUnclearClasses(evolvedModel, listIndividuals);
        
        //Utils.OntologyUtils.writeModeltoFile(evolvedModel, "Indexes/TestOnto/middle_Allinstances.ttl");

        
        this.executer   = new EvolutionaryActionComposite();

        System.out.println("\nGoing through individuals of Evolved Model.\nTotal Individuals:" + listIndividuals.size());
        
        for(Individual instance : listIndividuals)
        {
            //System.out.println("\tCurrent Individual:" + instance.getURI());
            if(Utilities.isInIgnoreList(instance.getURI()))
                continue;
                                 
            this.compareShapes(instance);
        }

        executer.execute(ontologyModel, evolvedModel);
        
//        Utils.OntologyUtils.writeModeltoFile(instanceModel, "Indexes/TestOnto/middle_instance.ttl");
//        Utils.OntologyUtils.writeModeltoFile(ontologyModel, "Indexes/TestOnto/middle_original.ttl");
//        Utils.OntologyUtils.writeModeltoFile(evolvedModel,  "Indexes/TestOnto/middle_evolved.ttl");

        // verificar se é preciso acrescentar validaçoes temporais em classes
        updateTemporalRestrictions();

    }
   
    private void cleanUnclearClasses(OntModel model, List<Individual> listIndividuals)
    {
        System.out.println("\n\n==STARTING CLEAN UNCLEAR CLASSES ===");
        
        System.out.println("\n\nList obtained. Iterating...");
        for(Individual i : listIndividuals)
        {
            List<OntClass> listOntClasses = i.listOntClasses(false).toList();
        
            for(OntClass cls : listOntClasses)
            {
                try
                {
                    model.getOntClass(cls.getURI());
                }
                catch(Exception e)
                {
                    System.out.println("Class has no URI or otherwise does not"
                            + " exist in model. Exception Reason: " + e.getMessage());
                    cls.dropIndividual(i);
                }
            }
        }
        System.out.println("\n\n==FINISHED CLEAN UNCLEAR CLASSES ===");
    

    }

    private void updateTemporalRestrictions() 
    {
        //List <OntClass> e_ontClasses = ontologyModel.listClasses().toList();
        List <OntClass> e_ontClasses = evolvedModel.listClasses().toList();
        LocalDateTime now      = LocalDateTime.now();  
        
        System.out.println("\n========================================="
                + "\nNew Classes:");
        for(OntClass newCls : e_ontClasses)
        {
            String uri      = newCls.getURI();
            if(uri == null || Utilities.isInIgnoreList(uri) ) 
                continue;
        
            System.out.println("\t> URI: " + uri);
        }
        System.out.println("\n=========================================");
        
        for(OntClass newCls : e_ontClasses)
        {
            cleanSuperAndEquivalents(newCls);
            
            String uri      = newCls.getURI();
            if(uri == null || Utilities.isInIgnoreList(uri) ) 
                continue;
                        
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
//                OntologyUtils.copyClassDetails(ontologyModel.getOntClass(prevURI), lastNewSlice);

                lastOldSlice = evolvedModel.getOntClass(prevURI); // as alteraçoes doravante sao no novo modelo
                
                //OntologyUtils.copyClassDetails(oldCls, lastNewSlice);
                OntologyUtils.copyClassDetails(newCls, lastNewSlice);
                
                addBefore(lastOldSlice, lastNewSlice);
                updateTemporalEQRestriction(lastOldSlice, slicer.getSliceBeginning());
              
                
            }
        }
     }
    
    
    private void cleanSuperAndEquivalents(OntClass cls)
    {
        if(cls == null) return;
        List<OntClass> eqclasses = cls.listEquivalentClasses().toList();
        
        for(OntClass eq : eqclasses)
        {
            if(eq.isIntersectionClass())
            {
                try
                {
                    eq.asIntersectionClass().getOperands();  
                }
                catch(Exception e)
                {
                    System.out.println("Not Intersection. Reason: " + e.getMessage());
                    cls.removeEquivalentClass(eq);
                }
                               
            }
                        
            if(eq.isUnionClass())
            {
                try
                {
                    eq.asUnionClass().getOperands();  
                }
                catch(Exception e)
                {
                    System.out.println("Not Union. Reason: " + e.getMessage());
                    cls.removeEquivalentClass(eq);
                }                  
            }
        }
                
       // remover o que estiver em erro, siga
        eqclasses = cls.listSuperClasses().toList();
    
        for(OntClass eq : eqclasses)
        {
            if(eq.isUnionClass())
            {
                try
                {
                    eq.asUnionClass().getOperands();   
                }
                catch(Exception e)
                {
                    System.out.println("Not Union. Reason: " + e.getMessage());
                    cls.removeSuperClass(eq);
                }           
            }
            
            if(eq.isIntersectionClass())
            {
                try
                {
                    eq.asIntersectionClass().getOperands();   
                }
                catch(Exception e)
                {
                    System.out.println("Not Intersection. Reason: " + e.getMessage());
                    cls.removeSuperClass(eq);
                }           
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
    
        
 
    public String printStats() 
    {
        return this.executer.toString();
    }
    
}