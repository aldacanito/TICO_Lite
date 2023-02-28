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
import IDC.ModelManager;
/**
 *
 * @author shizamura
 */
public class Comparator 
{
    EvolutionaryActionComposite executer, ontologyModelUpdater;
    List<ClassPropertyMetrics> clsPropMetrics ;
    
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        
    public Comparator() 
    {

        this.executer      = new EvolutionaryActionComposite();
        this.ontologyModelUpdater      = new EvolutionaryActionComposite();
        
        Ontology evolvedOnt = ModelManager.getManager().getEvolvingModel().createOntology("");
        evolvedOnt.addImport(ModelManager.getManager().getEvolvingModel().createResource(OntologyUtils.ONT_TIME_URL));
     
        clsPropMetrics = new ArrayList<ClassPropertyMetrics>();
    }

   
    public void run() 
    {

        // guidado pelas instancias
        // só funciona para as instâncias que tenham CLASSES associadas
        // reasoner desligado
        List <Individual> listIndividuals = ModelManager.getManager().getInstanceModel().listIndividuals().toList();

        System.out.println("Individuals before copy:");
        for(Individual individual : listIndividuals)
        {    
            //System.out.println("\t> " + instance.getURI());
            if(Utilities.isInIgnoreList(individual.getURI()))
                continue;
            
            List<OntClass> listOntClasses = individual.listOntClasses(false).toList();
            
            for(OntClass cls : listOntClasses)
            {
                if(cls.getURI()!= null && ModelManager.getManager().getEvolvingModel().getOntClass(cls.getURI()) == null)
                {
//                    ModelManager.getManager().getEvolvingModel().createClass(cls.getURI());
                    AddClass addClass = new AddClass(cls.getURI());
                    addClass.setCopy(false);
                    addClass.setStartEndInstance(individual, individual);
                    addClass.execute();
                }
            }
        }
        
        System.out.println("Total Individuals before copy: " + listIndividuals.size());
        
        System.out.println("Copying individual list...");
        listIndividuals = ModelManager.getManager().getInstanceModel().listIndividuals().toList();
        int count = 0;
        for(Individual individual : listIndividuals)
        {    
            if(Utilities.isInIgnoreList(individual.getURI()))
                continue;

            //System.out.println("\tCurrent individual: " + instance.getURI());

            List<OntClass> ontClasses = individual.listOntClasses(true).toList();
    
            StmtIterator listProperties = individual.listProperties();
            ModelManager.getManager().getEvolvingModel().add(listProperties);
            
            OntClass ontClass  = ModelManager.getManager().getEvolvingModel().getOntClass(individual.getOntClass(true).getURI());
            Individual new_ind = ModelManager.getManager().getEvolvingModel().createIndividual(individual.getURI(), ontClass);

            for(OntClass cls : ontClasses)
                new_ind.addOntClass(cls);
                      
            count++;
        }
        System.out.println("Finished copying individuals to evolved model. Total copies: " + count);
        //remover erros?
        
        listIndividuals = ModelManager.getManager().getEvolvingModel().listIndividuals().toList();
        
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

        executer.execute();
        
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
        //List <OntClass> e_ontClasses = ModelManager.getManager().getOriginalModel().listClasses().toList();
        List <OntClass> e_ontClasses = ModelManager.getManager().getEvolvingModel().listClasses().toList();
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
                        
            OntClass oldCls = ModelManager.getManager().getOriginalModel().getOntClass(uri);
            
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
                                
                TimeSliceCreator slicer = new TimeSliceCreator(lastNewSlice, versionNumber);
                slicer.execute();

                lastNewSlice = slicer.getSlice(); // as alteraçoes doravante sao no novo modelo

                if(ModelManager.getManager().getEvolvingModel().getOntClass(prevURI)==null)
                    lastOldSlice = ModelManager.getManager().getEvolvingModel().createClass(prevURI);
                else
                    lastOldSlice = ModelManager.getManager().getEvolvingModel().getOntClass(prevURI);
                
                //copiar para o modelo novo
                OntologyUtils.copyClassDetails(ModelManager.getManager().getOriginalModel().getOntClass(prevURI), lastOldSlice);
//                OntologyUtils.copyClassDetails(ModelManager.getManager().getOriginalModel().getOntClass(prevURI), lastNewSlice);

                lastOldSlice = ModelManager.getManager().getEvolvingModel().getOntClass(prevURI); // as alteraçoes doravante sao no novo modelo
                
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
        
        OntProperty beforeP = ModelManager.getManager().getEvolvingModel().getOntProperty(OntologyUtils.BEFORE_P);
        if(beforeP == null) beforeP = ModelManager.getManager().getEvolvingModel().createObjectProperty(OntologyUtils.BEFORE_P, false);

        List<OntClass> eqclasses = cls.listEquivalentClasses().toList();
    
        for(OntClass eq : eqclasses)
        {
            if(eq.isIntersectionClass())
            {
                try
                {
                    IntersectionClass intersection = eq.asIntersectionClass();
                    RDFList operands = intersection.getOperands();
                    HasValueRestriction beforeRestriction = ModelManager.getManager().getEvolvingModel().createHasValueRestriction(null, beforeP, ind_end);
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
        
        OntProperty ontProperty = ModelManager.getManager().getEvolvingModel().getOntProperty(OntologyUtils.HAS_ENDING_P);

        if(ontProperty == null)
            ontProperty = ModelManager.getManager().getEvolvingModel().createObjectProperty(OntologyUtils.HAS_ENDING_P, false);
            
        OntClass instantClass = ModelManager.getManager().getEvolvingModel().getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = ModelManager.getManager().getEvolvingModel().createClass(OntologyUtils.INSTANT_CLS);
        Individual date1 = ModelManager.getManager().getEvolvingModel().createIndividual(dtf2.format(enddate), instantClass);
    
        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);
           
        cls.addSuperClass(createHasValueRestriction);
    }
    
    
    private void addBefore(OntClass cls, LocalDateTime enddate)
    {
        
        OntProperty ontProperty = ModelManager.getManager().getEvolvingModel().getOntProperty(OntologyUtils.BEFORE_P);

        if(ontProperty == null)
            ontProperty = ModelManager.getManager().getEvolvingModel().createObjectProperty(OntologyUtils.BEFORE_P, false);
            
        OntClass instantClass = ModelManager.getManager().getEvolvingModel().getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = ModelManager.getManager().getEvolvingModel().createClass(OntologyUtils.INSTANT_CLS);
        Individual date1 = ModelManager.getManager().getEvolvingModel().createIndividual(dtf2.format(enddate), instantClass);
    
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
                        
                        OntClass instantClass = ModelManager.getManager().getEvolvingModel().getOntClass(OntologyUtils.INSTANT_CLS);
                        if(instantClass == null)
                            instantClass = ModelManager.getManager().getEvolvingModel().createClass(OntologyUtils.INSTANT_CLS);
                        
                        Individual date1 = ModelManager.getManager().getEvolvingModel().createIndividual(dtf2.format(startdate), instantClass);
            
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
                        
                        OntClass instantClass = ModelManager.getManager().getEvolvingModel().getOntClass(OntologyUtils.INSTANT_CLS);
                        if(instantClass == null)
                            instantClass = ModelManager.getManager().getEvolvingModel().createClass(OntologyUtils.INSTANT_CLS);
                        
                        Individual date1 = ModelManager.getManager().getEvolvingModel().createIndividual(dtf2.format(startdate), instantClass);
            
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
            OntProperty ontProperty = ModelManager.getManager().getEvolvingModel().getOntProperty(OntologyUtils.BEFORE_P);

            if(ontProperty == null)
                ontProperty = ModelManager.getManager().getEvolvingModel().createObjectProperty(OntologyUtils.BEFORE_P, false);

            SomeValuesFromRestriction svfr = ModelManager.getManager().getEvolvingModel().createSomeValuesFromRestriction(null, ontProperty, newCls);
            ontClass.addSuperClass(svfr);

            ontProperty = ModelManager.getManager().getEvolvingModel().getOntProperty(OntologyUtils.AFTER_P);

            if(ontProperty == null)
                ontProperty = ModelManager.getManager().getEvolvingModel().createObjectProperty(OntologyUtils.AFTER_P, false);

            svfr = ModelManager.getManager().getEvolvingModel().createSomeValuesFromRestriction(null, ontProperty, ontClass);
            newCls.addSuperClass(svfr);
        }
        
    }

    private void addClassRestrictions() 
    {       
        List <OntClass> e_ontClasses = ModelManager.getManager().getEvolvingModel().listClasses().toList();

        for(OntClass cls : e_ontClasses)
        {
            String classURI = cls.getURI();
             
            if(Utilities.isInIgnoreList(classURI))
                continue;
            
            ClassPropertyMetrics cpm = EntityMetricsStore.getStore().getMetricsByClassURI(classURI);  
            
            if(cpm==null) continue;
            
            ClassCompareShape.populateComposite(cpm, executer);
        }
        
        ClassCompareShape.run(executer);        
        executer.execute();
        
        
    
    }
    
      
    private void compareProperty(Statement t)
    {
        boolean ignore = Utilities.isInIgnoreList(t.getPredicate().getURI());
        
        if(ignore) return;
        IPropertyCompare comparator = ComparatorFactory.getInstance().getPropertyComparator(t, ModelManager.getManager().getOriginalModel());
        
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
            
            IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(cls); 
            
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
       
        ClassCompareShape shapeC  = new ClassCompareShape(instance);

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