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

import IDC.Metrics.*;
import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import Utils.Utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.Collection.*;

/**
 *
 * @author shizamura
 */
public class Comparator 
{
    EvolutionaryActionComposite executer, ontologyModelUpdater;
    List<ExecutionHistory> executionHistoryList;
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        
    public Comparator() 
    {

        this.executer              = new EvolutionaryActionComposite();
        this.ontologyModelUpdater  = new EvolutionaryActionComposite();
        
        Ontology evolvedOnt = ModelManager.getManager().getEvolvingModel().createOntology("");
        evolvedOnt.addImport(ModelManager.getManager().getEvolvingModel().createResource(OntologyUtils.ONT_TIME_URL));
     
        executionHistoryList = new ArrayList<>();
    }

    /**
     * Iterates through individual List and makes sure all their classes are in the Model. If not, creates
     * and executes an AddClass Evolutionary Action.
     * @param individuals_uris List of URIs of the Individuals to analyse.
     */
    private void checkNewClasses(List<String> individuals_uris)
    {
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

                        AddClass addClass = new AddClass(cls.getURI());
                        addClass.setCopy(false);
                        addClass.setStartEndInstance(individual, individual); // todo make sure this makes sense or replace with actual instants
                        addClass.execute();
                    }
                }
            } catch (Exception e)
            {
                System.out.println("Exception handling individuals list. Reason: " + e.getMessage());
            }
        }
    }

    /**
     * Copies the Individuals in the Individual Model to the Evolved Model. The remainder of the process won't work otherwise.
     * @param individuals_uris List of URIs of the Individuals to copy
     *                         TODO: make sure everything is actually being copied!!
     */
    public void copyIndividualsToEvolvedModel(List<String> individuals_uris)
    {
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
    }

    /**
     * Runs the Comparison algorithm over a list of individuals. Everything is done using the Evolved Model.
     * (assumes Individuals are in the Evolved Model!)
     * @param individuals_uris The URIs of the Individuals to analyse
     */
    public void runComparatorOnIndividuals(List<String> individuals_uris)
    {
        List <String> evolving_uris = SPARQLUtils.getIndividualsSPARQL(ModelManager.getManager().getEvolvingModel());

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
    }



    /**
     * Uses the ClassCompareShape comparator on an Individual to determine the evolutionary actions
     * that must be adderd to this.executer.
     * @param instance The Individual to analyse
     */
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




    /***
     * Guided by the instances! Only works for individuals with OntClasses associated to them.
     * Doesn't use reasoner.
     */
    public void run() 
    {
        List <String> individuals_uris = SPARQLUtils.getIndividualsSPARQL(ModelManager.getManager().getInstanceModel());

        int partitionSize = 10;

        List<List<String>> partitions = Utilities.chopped(individuals_uris, partitionSize);

        for(List<String> partition : partitions)
        {
            checkNewClasses(partition);
            copyIndividualsToEvolvedModel(partition);
            //  cleanUnclearClasses(evolvedModel, partition);
            runComparatorOnIndividuals(partition);
            updateTemporalRestrictions();         // check if the temporal restrictions on the classes are ok
            printEntityMetricsStats();
        }

    }

    private void printEntityMetricsStats()
    {
        List <OntClass> ontClasses = SPARQLUtils.listOntClassesSPARQL(ModelManager.getManager().getEvolvingModel());

        System.out.println("\n=========================================\nPRINTING ENTITY METRICS\n=========================================\n");

        for(OntClass newCls : ontClasses)
        {
            String uri                             = newCls.getURI();
            ClassPropertyMetrics metricsByClassURI = EntityMetricsStore.getStore().getMetricsByClassURI(uri);

            if(metricsByClassURI == null) continue;
            List<PropertyMetrics> propertyMetrics  = metricsByClassURI.getPropertyMetrics();

            if(propertyMetrics == null) continue;


            System.out.println("\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            System.out.println("\t++++++++++++++ " + uri + " ++++++++++++++");
            System.out.println("\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

            System.out.println("\tURI: " + uri + " >> Mentions: " +  metricsByClassURI.getMentions());
            System.out.println("\t> Property Metrics:");

            for(PropertyMetrics pm : propertyMetrics)
            {
                String p_uri = pm.getURI();
                Map<String, Integer> domains =  pm.getDomains();
                Map<String, Integer> ranges  =  pm.getRanges();


                System.out.println("\t\t>> Property URI: " + p_uri );
                System.out.println("\t\t\t>> Total Mentions: " + pm.getCount() + " | Property Ratio: " + metricsByClassURI.getPropertyRatio(p_uri));

                System.out.println("\t\t\t>> Domains:" );
                for(String d_uri : domains.keySet())
                    System.out.println("\t\t\t\t> " + d_uri + " | count: " + domains.get(d_uri) + " | ratio: " + pm.getDomainRatio(d_uri));

                System.out.println("\t\t\t>> Ranges:" );
                for(String r_uri : ranges.keySet())
                    System.out.println("\t\t\t\t> " + r_uri + " | count: " + ranges.get(r_uri) + " | ratio: " + pm.getRangeRatio(r_uri));

            }

            System.out.println("\t> Object Properties: ");
            HashMap<String, Integer> classObjProperties = metricsByClassURI.getClassObjProperties();
            for(String objP_uri : classObjProperties.keySet())
                System.out.println("\t\t\t> " + objP_uri + " | count: " + classObjProperties.get(objP_uri));


            System.out.println("\t> Datatype Properties: ");
            HashMap<String, Integer> classDtProperties = metricsByClassURI.getClassDtProperties();
            for(String dtP_uri : classDtProperties.keySet())
                System.out.println("\t\t\t> " + dtP_uri + " | count: " + classDtProperties.get(dtP_uri));

            System.out.println("\t> Functional Candidates: ");
            HashMap<String, Boolean> functionalCandidates = metricsByClassURI.getFunctionalCandidates();
            for(String fc_uri : functionalCandidates.keySet())
                System.out.println("\t\t\t> " + fc_uri + " | Is Functional: " + functionalCandidates.get(fc_uri));

            Individual first = metricsByClassURI.getFirstMention();
            Individual last  = metricsByClassURI.getLastMention();

            if(first!=null) System.out.println("\t\t> First Mentioned on Individual: " + first.getURI());
            if(last!=null)  System.out.println("\t\t> Last Mentioned on Individual: "  + last.getURI());

            System.out.println("\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        }
        System.out.println("\n=========================================");


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

    /**
     * Iterates through all newly created Classes and verifies if temporal restrictions are applicable.
     * In the case of TimeSlices, edits them to have starting and ending Instants.
     */
    private void updateTemporalRestrictions() 
    {
        List <OntClass> e_ontClasses1 = SPARQLUtils.listOntClassesSPARQL(ModelManager.getManager().getEvolvingModel());

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

            cleanSuperAndEquivalents(newCls);

            OntClass hasSlice = OntologyUtils.getLastTimeSlice(newCls);

            if(hasSlice != null && oldCls == null)
                OntologyUtils.copyClassDetails(newCls, hasSlice);

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
                    + " " + lastOldSlice.getURI()
                    + " & " + lastNewSlice.getURI() + "\n\t\tAre they Different? " + newVersion + "\n\t==");
           
            if(newVersion)
            {
                // format : TS__CLASSNAME__VERSIONNUMBER
                String oldURI = lastOldSlice.getURI();

                String olds     []= oldURI.split("#");
                String prefix     = olds[0];
                String className  = olds[1];
                int versionNumber = 0;
                
                // It's a timeSlice
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

                lastNewSlice = slicer.getSlice();

                if(ModelManager.getManager().getEvolvingModel().getOntClass(prevURI)==null)
                    lastOldSlice = ModelManager.getManager().getEvolvingModel().createClass(prevURI);
                else
                    lastOldSlice = ModelManager.getManager().getEvolvingModel().getOntClass(prevURI);
                
                // copies the restrictions from the OriginalModel's Original Class to the EvolvedModel's Class with the same name
                OntologyUtils.copyClassDetails(ModelManager.getManager().getOriginalModel().getOntClass(prevURI), lastOldSlice);

                // from here on we edit the EVOLVING model
                lastOldSlice = ModelManager.getManager().getEvolvingModel().getOntClass(prevURI);
                
                OntologyUtils.copyClassDetails(newCls, lastNewSlice);
                
                addBefore(lastOldSlice, lastNewSlice);
                updateTemporalEQRestriction(lastOldSlice, slicer.getSliceBeginning());

            }
        }
     }


    /**
     * Checks if the Restrictions in Super and Equivalent Classes are still entailed. Sometimes, there are errors in
     * the copying process, especially if the restriction involves operands
     * (e.g. missing classes that were not mentioned in any of the individuals analysed).
     * @param cls the OntClass to clean
     */
    private void cleanSuperAndEquivalents(OntClass cls)
    {
        if(cls == null) return;

        deepClean(cls,true);
        deepClean(cls, false);

    }

    /**
     * Checks if the Restrictions are still entailed. Sometimes, there are errors in
     * the copying process, especially if the restriction involves operands
     * (e.g. missing classes that were not mentioned in any of the individuals analysed).
     * @param cls the OntClass to clean
     * @param equivalent True if checking for Equivalent Classes, false if SuperClasses
     */
    private void deepClean(OntClass cls, boolean equivalent)
    {
        List<OntClass> ontClasses;

        if(equivalent)
            ontClasses = cls.listEquivalentClasses().toList();
        else
            ontClasses = cls.listSuperClasses().toList();

        for(OntClass eq : ontClasses)
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
                    if(!equivalent) cls.removeSuperClass(eq);
                    else            cls.removeEquivalentClass(eq);

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
                    if(!equivalent) cls.removeSuperClass(eq);
                    else            cls.removeEquivalentClass(eq);
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
    



    public String printStats() 
    {
        return this.executer.toString();
    }


}