/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Shape;

import IDC.Comparison.Interfaces.IClassCompare;
import IDC.EvolActions.Impl.Additions.AddClass;
import IDC.EvolActions.Impl.Additions.AddDatatypeProperty;
import IDC.EvolActions.Impl.Additions.AddObjectProperty;
import IDC.EvolActions.Impl.Additions.Restriction.AddAllValuesFromRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddCardinalityRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddSomeValuesFromRestriction;
import IDC.EvolActions.Impl.EvolutionaryActionComposite;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import IDC.Metrics.ClassPropertyMetrics;
import IDC.Metrics.EntityMetricsStore;
import IDC.Metrics.PropertyMetrics;
import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author shizamura
 */
public class ClassCompareShape implements IClassCompare
{    
    protected Individual instance;


    public ClassCompareShape(Individual ind)
    {
        this.instance = ind;
    }
    

    
    public static EvolutionaryActionComposite run(EvolutionaryActionComposite composite)
    {
        ClassPropertyMetrics cpm = null;
        
        List<OntClass> ontClassList = ModelManager.getManager().getOriginalModel().listClasses().toList();
        //List<OntClass> ontClassList = ModelManager.getManager().getEvolvingModel().listClasses().toList();

        for(OntClass cls : ontClassList)
        {
            
            if(cls.getURI()==null || Utils.Utilities.isInClassIgnoreList(cls.getURI()))
                continue;
            
            cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(cls.getURI());
            
            if(cpm==null)
            {
                System.out.println("CPM was null for URI " + cls.getURI() );
                continue;
            }
            
            System.out.println("populating composite with CPM for URI: " + cls.getURI());
            ClassCompareShape.populateComposite(cpm, composite);
        
        }
        
        
        return composite;
    }


    private EvolutionaryAction compareSPARQL()
    {
        //Utilities.logInfo("ANALYSING INDIVIDUAL " + instance.getURI() + "...");

        Map<String, RDFNode> property_URIs    = OntologyUtils.listPropertiesSPARQL(instance);
        List<OntClass> ontClassList           = OntologyUtils.listOntClassesSPARQL(instance);
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();

        if(ontClassList==null)
            return composite;

        ClassPropertyMetrics cpm;

        Individual before = OntologyUtils.getBeforeInstant(instance);
        Individual after  = OntologyUtils.getAfterInstant(instance);

        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();

            if(Utilities.isInIgnoreList(classURI) || classURI == null)
                continue;

            cpm = EntityMetricsStore.getStore().getMetricsByClassURI(classURI);

            // get before e after da classe se existirem, senao deixa os individuals a null

            if(cpm==null)
                cpm = new ClassPropertyMetrics(cls, after);

            if(before!=null)
                cpm.addClassMention(before);
            else
                cpm.addClassMention();

            HashMap<String, Integer> repeated = new HashMap<>();

            if(!property_URIs.isEmpty())
                for(String predicateURI : property_URIs.keySet())
                {
                    if(Utilities.isInIgnoreList(predicateURI)) continue;

                    RDFNode object_node = property_URIs.get(predicateURI);

                    if(object_node.isResource())
                        cpm.addObjProperty(predicateURI, object_node.asResource().getURI());
                    else if(object_node.isLiteral())
                        cpm.addDtProperty(predicateURI, object_node.asLiteral().getDatatypeURI());

                    int count = repeated.getOrDefault(predicateURI, 0);
                    count++;

                    repeated.put(predicateURI, count);



                }

            for(String predicateURI : repeated.keySet())
                if(repeated.getOrDefault(predicateURI, 0) != 0)
                    cpm.updateFunctionalCandidate(predicateURI);

            EntityMetricsStore.getStore().addClassPropertyMetrics(cpm);
            ClassCompareShape.populateComposite(cpm, composite);
        }

        return composite;
    }


    
    @Override
    public EvolutionaryAction compare()
    {
        return compareSPARQL();
    }


    public static void populateObjProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass    = cpm.getOntClass();
        OntModel originModel = ontClass.getOntModel();

        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassObjProperties();

        if(classProperties==null) return;
        
        for(String propertyURI : classProperties.keySet())
        {
            if(Utils.Utilities.isInIgnoreList(propertyURI)) continue;

            PropertyMetrics pmetrics = cpm.getMetricsOfProperty(propertyURI);
            int mentions = classProperties.get(propertyURI);

            OntProperty onProperty = ModelManager.getManager().getOriginalModel().getOntProperty(propertyURI);
            if(onProperty==null) // just in case
                onProperty = ModelManager.getManager().getOriginalModel().createObjectProperty(propertyURI, false);

            boolean isFunctional = (functionalCandidates.contains(propertyURI) && mentions > Utils.Configs.functional_threshold);
            boolean isQualifiedR = (pmetrics.getRanges().size() == 1);
            boolean isEquivalent = (mentions > Utils.Configs.equivalent_threshold);
            boolean isSuperClass = (mentions >= Utils.Configs.subclass_threshold);

            AddObjectProperty add_objProperty = new AddObjectProperty(propertyURI, isFunctional);
            composite.add(add_objProperty);

            System.out.println("\t> Property " + propertyURI + " accessed."
                              +"\n\t\t> Mentions: " + mentions);
            System.out.println("\t\t> Is SuperClass: " + isSuperClass + "\n\t\t > Is EquivalentClass: " + isEquivalent +
                               "\n\t\t > Is Functional: " + isFunctional);

            if(isEquivalent || isSuperClass)
            {
                if(mentions > Utils.Configs.subclass_threshold)
                {
                    AddCardinalityRestriction rec = new AddCardinalityRestriction(ontClass, onProperty, isEquivalent, isSuperClass);
                    rec.setQualified(isQualifiedR);

                    if(isQualifiedR) // find and instantiate (if needed) the range class
                    {
                        String range_URI = pmetrics.getRanges().keySet().iterator().next(); // get the first element risps
                        OntClass range   = ModelManager.getManager().getEvolvingModel().getOntClass(range_URI);

                        if(range == null)
                        {
                            composite.add(new AddClass(range_URI));
                            rec.setRange_URI(range_URI); // todo ver se é isto ou se é preciso executar a acçao imediatamente
                        }
                        else
                            rec.setRangeClass(range);

                    }

                    if (isFunctional)
                    {
                        rec.setCardinalityType("Exactly", 1, isQualifiedR);
                        if(composite.add(rec))
                            System.out.println("\t\t\t Cardinality Restriction Added!");
                    }
                }
            }

            // TODO ACRESCENTAR OUTROS TIPOS DE RESTRIÇAO AQUI


            if(pmetrics!=null)
            {
                Map<String, Integer> ranges = pmetrics.getRanges();
                //all ranges must be the same
                // se houver mais que 1 range na lista ja nao vale
                int numRanges = ranges.keySet().size();
                
                if(numRanges == 1)
                {
                    //all values from

                    String rangeURI     = ranges.keySet().iterator().next();
                    OntClass rangeClass = originModel.getOntClass(rangeURI);
                    
                    if(rangeClass==null) // range é individual
                    {
                        Individual individual = originModel.getIndividual(rangeURI);
                        
                        if(individual!=null)
                        {
                            List<OntClass> rangesL = OntologyUtils.listOntClassesSPARQL(individual);

                           for(OntClass r : rangesL)
                           {
                                if(r.getURI()!=null && Utilities.isInClassIgnoreList(r.getURI()))
                                    continue;

//                               System.out.println("\t\t\t Adding Some Values From:"  +
//                                       "\n\t\t\t\t Class: "    + ontClass.getURI()   +
//                                       "\n\t\t\t\t Property: " + onProperty.getURI() +
//                                       "\n\t\t\t\t Range: "    + r.getURI());

                               AddAllValuesFromRestriction aavfR =
                                     new AddAllValuesFromRestriction(
                                             ontClass,
                                             onProperty,
                                             isEquivalent,
                                             isSuperClass,
                                             r
                                     );

                               if(composite.add(aavfR))
                                   System.out.println("\t\t\t Some Values From Added.");

                           }
                        }
                    }
                    else
                    {
//                        System.out.println("\t\t\t Adding All Values From:"  +
//                                "\n\t\t\t\t Class: "    + ontClass.getURI()   +
//                                "\n\t\t\t\t Property: " + onProperty.getURI() +
//                                "\n\t\t\t\t Range: "    + rangeClass.getURI());

                        AddAllValuesFromRestriction aavfR = 
                                 new AddAllValuesFromRestriction(
                                         ontClass,
                                         onProperty,
                                         isEquivalent,
                                         isSuperClass,
                                         rangeClass
                                 );
                        if(composite.add(aavfR))
                          System.out.println("\t\t\t All Values From Added.");

                    }
                }
                else if(numRanges >= Utils.Configs.someValuesFrom_threshold)
                {
                    // some values from
                                      
                    String rangeURI     = ranges.keySet().iterator().next();
                    
                    if(rangeURI==null) continue;

                    OntClass rangeClass = originModel.getOntClass(rangeURI);

                    if(rangeClass==null) // range é individual
                    {
                        Individual individual = originModel.getIndividual(rangeURI);

                        if(individual!=null)
                        {
                            List<OntClass> rangesL = OntologyUtils.listOntClassesSPARQL(individual);

                            for(OntClass r : rangesL)
                           {
                               if(r.getURI()!=null && Utilities.isInClassIgnoreList(r.getURI()))
                                    continue;

//                               System.out.println("\t\t\t Adding Some Values From:"  +
//                                       "\n\t\t\t\t Class: "    + ontClass.getURI()   +
//                                       "\n\t\t\t\t Property: " + onProperty.getURI() +
//                                       "\n\t\t\t\t Range: "    + r.getURI());

                               AddSomeValuesFromRestriction aavfR =
                                     new AddSomeValuesFromRestriction(
                                             ontClass,
                                             onProperty,
                                             isEquivalent,
                                             isSuperClass,
                                             r
                                     );

                               if(composite.add(aavfR))
                                   System.out.println("\t\t\t Some Values From Added.");
                           }
                        }
                    }
                    else
                    {
//                        System.out.println("\t\t\t Adding Some Values From:"  +
//                                "\n\t\t\t\t Class: "    + ontClass.getURI()   +
//                                "\n\t\t\t\t Property: " + onProperty.getURI() +
//                                "\n\t\t\t\t Range: "    + rangeClass.getURI());

                        AddSomeValuesFromRestriction asvfR = 
                            new AddSomeValuesFromRestriction(
                                    ontClass,
                                    onProperty,
                                    isEquivalent,
                                    isSuperClass,
                                    rangeClass
                            );

                        if(composite.add(asvfR))
                            System.out.println("\t\t\t Some Values From Added.");

                    }                 
                }
            }
        }

        System.out.println("\t== Finished Populate Object Properties Composite");

    }
    
    
    public static void populateDtProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {

        OntClass ontClass = cpm.getOntClass();
    
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassDtProperties();
        
        if(classProperties==null) return;
        
        for(String propertyURI : classProperties.keySet())
        {
            if(Utilities.isInIgnoreList(propertyURI))
                continue;
            
            DatatypeProperty onProperty = ModelManager.getManager().getOriginalModel().getDatatypeProperty(propertyURI);
            if(onProperty==null) // just in case
                onProperty = ModelManager.getManager().getOriginalModel().createDatatypeProperty(propertyURI, false);
            
            boolean isFunctional = false;
            boolean isQualifiedR = false;
            boolean isEquivalent = false;
            boolean isSuperClass = true;
            
            int mentions         = (int) classProperties.get(propertyURI);
            
            if(functionalCandidates.contains(propertyURI) && mentions > Utils.Configs.functional_threshold)
                isFunctional = true;
        
            AddDatatypeProperty add_dtProperty = new AddDatatypeProperty(propertyURI, isFunctional);
            composite.add(add_dtProperty);
            
            //adicionar na mesma a questao da restricao de equivalencia ao composite, depois ver se é para executar ou não
            
            if(mentions <= Utils.Configs.subclass_threshold)   isSuperClass = false;
            if(mentions >  Utils.Configs.equivalent_threshold) isEquivalent = true;

            // TODO decidir como refinar o qualified (preciso verificar os ranges TODOS
            if(mentions > Utils.Configs.subclass_threshold)
            {
                AddCardinalityRestriction rec = new AddCardinalityRestriction(ontClass, onProperty, isEquivalent, isSuperClass);

                if(isFunctional)
                {
                    rec.setCardinalityType("Exactly", 1, isQualifiedR);
                    composite.add(rec);
                }

            }
            
        }

        System.out.println("\t== Finished Populate DataType Properties Composite");

    }
    
    public static void populateComposite(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite) 
    {

        OntClass ontClass    = cpm.getOntClass();

        if(ontClass.isAnon() || Utils.Utilities.isInIgnoreList(ontClass.getURI()))
            return;

        System.out.println("\n=========================================");

        System.out.println("Populating Properties Composite for class: " + ontClass.getURI());

        ClassCompareShape.populateObjProperties(cpm, composite);
        ClassCompareShape.populateDtProperties(cpm, composite);

        AddClass addCls = new AddClass(ontClass);
        
        composite.add(addCls);

        System.out.println("=========================================\n");
    }







    private EvolutionaryAction compareJENA()
    {
        //Utilities.logInfo("ANALYSING INDIVIDUAL " + instance.getURI() + "...");

        List<Statement> properties  = instance.listProperties().toList();
        List<OntClass> ontClassList = instance.listOntClasses(true).toList();

        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();

        if(ontClassList==null)
            return composite;

        ClassPropertyMetrics cpm = null;

        Individual before = OntologyUtils.getBeforeInstant(instance);
        Individual after  = OntologyUtils.getAfterInstant(instance);

        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();

            if(Utilities.isInIgnoreList(classURI) || classURI == null)
                continue;

            cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(classURI);

            // get before e after da classe se existirem, senao deixa os individuals a null

            if(cpm==null)
                cpm = new ClassPropertyMetrics(cls, after);

            if(before!=null)
                cpm.addClassMention(before);
            else
                cpm.addClassMention();

            HashMap<String, Integer> repeated = new HashMap<>();

            if(properties!=null)
                for(Statement stmt : properties)
                {
                    String predicateURI = stmt.getPredicate().getURI();

                    boolean ignore = Utilities.isInIgnoreList(instance.getURI());
                    if(ignore) continue;

                    RDFNode object = stmt.getObject();

                    if(object.isLiteral()) // DT PROP
                    {
                        Literal asLiteral = object.asLiteral();
                        cpm.addDtProperty(predicateURI, asLiteral.getDatatypeURI());
                    }
                    if(object.isResource()) // OBJ PROP
                    {
                        Resource asResource = object.asResource();
                        cpm.addObjProperty(predicateURI, asResource.getURI());
                    }
                    int count = repeated.getOrDefault(predicateURI, 0);
                    count++;

                    repeated.put(predicateURI, count);

                }

            for(String predicateURI : repeated.keySet())
                if(repeated.getOrDefault(predicateURI, 0) != 0)
                    cpm.updateFunctionalCandidate(predicateURI);

            EntityMetricsStore.getStore().addClassPropertyMetrics(cpm);
            ClassCompareShape.populateComposite(cpm, composite);
        }

        return composite;
        // return fill(composite, instance, ontClassList, properties, this.evolvedModel);
    }

    private static EvolutionaryActionComposite fill(EvolutionaryActionComposite composite,
                                                    Individual instance,
                                                    List<OntClass> ontClassList,
                                                    List<Statement> properties,
                                                    OntModel ontModel)
    {
        ClassPropertyMetrics cpm = null;

        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();

            if(Utilities.isInIgnoreList(classURI))
                continue;

            cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(classURI);

            if(cpm==null)
                cpm = new ClassPropertyMetrics(cls, null);

            cpm.addClassMention();

            HashMap<String, Integer> repeated = new HashMap<>();

            if(properties!=null)
                for(Statement stmt : properties)
                {
                    String predicateURI = stmt.getPredicate().getURI();

                    boolean ignore = Utilities.isInIgnoreList(instance.getURI());
                    if(ignore) continue;

                    RDFNode object = stmt.getObject();

                    if(object.isLiteral()) // DT PROP
                    {
                        Literal asLiteral = object.asLiteral();
                        cpm.addDtProperty(predicateURI, asLiteral.getDatatypeURI());
                    }
                    if(object.isResource()) // OBJ PROP
                    {
                        Resource asResource = object.asResource();
                        cpm.addObjProperty(predicateURI, asResource.getURI());
                    }
                    int count = repeated.getOrDefault(predicateURI, 0);
                    count++;

                    repeated.put(predicateURI, count);

                }

            for(String predicateURI : repeated.keySet())
            {
                int count = repeated.getOrDefault(predicateURI, 0);
                if(count!=0)
                    cpm.updateFunctionalCandidate(predicateURI);
            }

            // run through all Classes and Properties and Check if EvolutionaryActions should be deployed
            //if(cpm!=null)

            EntityMetricsStore.getStore().addClassPropertyMetrics(cpm);
            ClassCompareShape.populateComposite(cpm, composite);
        }

        return composite;

    }



    public static EvolutionaryActionComposite run()
    {
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();

        return ClassCompareShape.run(composite);

    }


}
