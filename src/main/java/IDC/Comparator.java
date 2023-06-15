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
import IDC.EvolActions.Impl.Additions.Restriction.*;

import IDC.Metrics.ClassPropertyMetrics;
import IDC.Metrics.EntityMetricsStore;
import IDC.Metrics.ExecutionHistory;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.query.*;
/**
 *
 * @author shizamura
 */
public class Comparator 
{
    EvolutionaryActionComposite executer, ontologyModelUpdater;
    List<ClassPropertyMetrics> clsPropMetrics ;

    List<ExecutionHistory> executionHistoryList;
    
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        
    public Comparator() 
    {

        this.executer              = new EvolutionaryActionComposite();
        this.ontologyModelUpdater  = new EvolutionaryActionComposite();
        
        Ontology evolvedOnt = ModelManager.getManager().getEvolvingModel().createOntology("");
        evolvedOnt.addImport(ModelManager.getManager().getEvolvingModel().createResource(OntologyUtils.ONT_TIME_URL));
     
        clsPropMetrics       = new ArrayList<>();
        executionHistoryList = new ArrayList<>();
    }


    public void run() 
    {

        // guidado pelas instancias
        // só funciona para as instâncias que tenham CLASSES associadas
        // reasoner desligado

        List <String> individuals_uris = OntologyUtils.getIndividualsSPARQL(ModelManager.getManager().getInstanceModel());

        for(String uri : individuals_uris)
        {

            try
            {
                Individual individual = ModelManager.getManager().getInstanceModel().getIndividual(uri);
                List<OntClass> listOntClasses = individual.listOntClasses(false).toList();

                //System.out.println("\t> " + uri);

                for (OntClass cls : listOntClasses)
                {
                    //TODO check anonymous classes!!!!

                    if (cls.getURI() != null && ModelManager.getManager().getEvolvingModel().getOntClass(cls.getURI()) == null)
                    {
                        if (Utilities.isInIgnoreList(cls.getURI()))
                            continue;

                        //System.out.println("Individual with new Class found!\n\t> " + uri + "\n\t\t> New OntClass: " + cls.getURI());
                        //ModelManager.getManager().getEvolvingModel().createClass(cls.getURI());
                        AddClass addClass = new AddClass(cls.getURI());
                        addClass.setCopy(false);
                        addClass.setStartEndInstance(individual, individual);
                        addClass.execute();
                    }
                }
            } catch (Exception e)
            {
                System.out.println("Exception handling individuals list. Reason: " + e.getMessage());
            }
        }

        System.out.println("Total Individuals before copy: " + individuals_uris.size() + ". Copying individual list...");

        int count = 0;
        for(String uri : individuals_uris)
        {
            try
            {
                Individual individual = ModelManager.getManager().getInstanceModel().getIndividual(uri);

                //System.out.println("\tCurrent individual: " + uri);

                List<OntClass> ontClasses = individual.listOntClasses(true).toList();

                StmtIterator listProperties = individual.listProperties();
                ModelManager.getManager().getEvolvingModel().add(listProperties);

                // try to get first class, if not available, try second. if not available, skip.

                OntClass ontClass = null;

                for (OntClass cls : ontClasses) {
                    try {
                        cls.getURI();
                        ontClass = cls;
                        break;
                    } catch (Exception e) {
                    }
                }

                if (ontClass == null)
                    continue;

                Individual new_ind = ModelManager.getManager().getEvolvingModel().createIndividual(individual.getURI(), ontClass);

                for (OntClass cls : ontClasses)
                    new_ind.addOntClass(cls);

                count++;
            }
            catch(Exception e)
            {
                System.out.println("Exception handling individuals list. Reason: " + e.getMessage());
            }
        }
        System.out.println("Finished copying individuals to evolved model. Total copies: " + count);
        //remover erros?

        List <String> evolving_uris = OntologyUtils.getIndividualsSPARQL(ModelManager.getManager().getEvolvingModel());

      //  cleanUnclearClasses(evolvedModel, listIndividuals);

        this.executer   = new EvolutionaryActionComposite();

        System.out.println("\nGoing through individuals of Evolved Model.\nTotal Individuals:" + evolving_uris.size());

        for(String uri : individuals_uris)
        {
            if(Utilities.isInIgnoreList(uri))
                continue;

            try
            {
                Individual individual = ModelManager.getManager().getEvolvingModel().getIndividual(uri);
                //System.out.println("\tCurrent Individual:" + individual.getURI());
                this.compareShapes(individual);
            }
            catch (Exception e)
            {
                System.out.println("Error handling evolving individuals. Reason: " + e.getMessage());
            }
        }

        executer.execute();

        // verificar se é preciso acrescentar validaçoes temporais em classes
        updateTemporalRestrictions();

    }
   
    private void cleanAnonymousErrorClasses(OntModel model, List<String> individuals_uris)
    {
        System.out.println("\n\n==STARTING CLEAN ANONYMOUS ERROR CLASSES ===");
        
        System.out.println("\n\nList obtained. Iterating...");
        for(String uri : individuals_uris) {
            try {
                Individual i = ModelManager.getManager().getInstanceModel().getIndividual(uri);

                List<OntClass> listOntClasses = i.listOntClasses(false).toList();

                for (OntClass cls : listOntClasses)
                {
                    try
                    {
                        model.getOntClass(cls.getURI()); // cleans anon
                    }
                    catch (Exception e)
                    {
                        System.out.println("Class has no URI or otherwise does not"
                                + " exist in model. Exception Reason: " + e.getMessage());
                        cls.dropIndividual(i);
                    }
                }
            }catch (Exception e)
            {
                System.out.println("Error cleaning anonymous class. Reason: " + e.getMessage());
            }
        }

        System.out.println("\n\n==FINISHED CLEAN ANON CLASSES ===");
    

    }

    private void updateTemporalRestrictions() 
    {
        //List <OntClass> e_ontClasses = ModelManager.getManager().getOriginalModel().listClasses().toList();

        List <OntClass> e_ontClasses  = ModelManager.getManager().getEvolvingModel().listClasses().toList();
        List <OntClass> e_ontClasses1 = OntologyUtils.listOntClassesSPARQL(ModelManager.getManager().getEvolvingModel());

        System.out.println("\n=========================================\nNew Classes:");
        for(OntClass newCls : e_ontClasses1)
        {
            String uri      = newCls.getURI();
            System.out.println("\t> URI: " + uri);
        }
        System.out.println("\n=========================================");
        
        for(OntClass newCls : e_ontClasses1)
        {

            String uri      = newCls.getURI();
            OntClass oldCls = ModelManager.getManager().getOriginalModel().getOntClass(uri);
            
            if(oldCls==null || Utilities.isInIgnoreList(oldCls.getURI()))
                continue;
            
            //ignoremos as timeslices em si para nao andar a TS de TS
            if(OntologyUtils.isTimeSlice(oldCls) || OntologyUtils.isTimeSlice(newCls)) continue;

            cleanSuperAndEquivalents(newCls);

            // ver se o ultimo timeframe é diferente
            OntClass lastOldSlice = OntologyUtils.getLastTimeSlice(oldCls);
            OntClass lastNewSlice = newCls;
           
            if(lastOldSlice==null) continue; //porque é que este caso ocorreria?
           
            boolean newVersion = new ClassDiff().isNewVersion(lastOldSlice, lastNewSlice);
            
            System.out.println("\n\t "
                    + "== Source: " + uri 
                    + "\n\t == Comparing:" 
                    + " " + lastOldSlice.getURI()
                    + " & " + lastNewSlice.getURI() + "\n\t\tAre they Different? " + newVersion + "\n\t==");
           
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
                    ExtendedIterator<? extends OntClass> operandsL = intersection.listOperands();

                    if(operandsL.toList() != null && operandsL.toList().size() != 0) {
                        RDFList operands = intersection.getOperands();
                        HasValueRestriction beforeRestriction = ModelManager.getManager().getEvolvingModel().createHasValueRestriction(null, beforeP, ind_end);
                        operands.add(beforeRestriction);
                    }
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
        if(Utilities.isInIgnoreList(instance.getURI())) return;

        try
        {
            ClassCompareShape shapeC   = new ClassCompareShape(instance);
            EvolutionaryAction compare = shapeC.compare();
            this.executer.add(compare);
        }
        catch(Exception e)
        {
            System.out.println("Problem comparing shapes. Reason: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

 
    public String printStats() 
    {
        return this.executer.toString();
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


}