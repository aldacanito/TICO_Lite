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
import Utils.OntologyUtils;
import Utils.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
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
//    protected OntClass ontClass;
    protected OntModel   ontModel;
    protected Individual instance;
    protected OntModel evolvedModel;
    
    public ClassCompareShape(Individual ind, OntModel ontModel)
    {
        this.instance = ind;
        this.ontModel = ontModel;
    }
    
    public void setup(OntModel o, OntModel e)
    {
        this.evolvedModel = e;
        this.ontModel = o;
    }

    
    public static EvolutionaryActionComposite run(OntModel ontModel)
    {
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();
        composite.setUp(ontModel, ontModel); // start
        
        return ClassCompareShape.run(ontModel, composite);
    
    }
    
    public static EvolutionaryActionComposite run(OntModel ontModel, EvolutionaryActionComposite composite)
    {
        ClassPropertyMetrics cpm = null;
        
        List<OntClass> ontClassList = ontModel.listClasses().toList();
        
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
            ClassCompareShape.populateComposite(ontModel, cpm, composite);
        
        }
        
        
        return composite;
    }
    
    
    @Override
    public EvolutionaryAction compare() 
    {
        Utilities.logInfo("ANALYSING INDIVIDUAL " + instance.getURI() + "...");
        
        List<Statement> properties  = instance.listProperties().toList();
        List<OntClass> ontClassList = instance.listOntClasses(true).toList();
                
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();
        
        if(ontClassList==null) 
            return composite;   
        
//        composite.setUp(ontModel, evolvedModel); // start
       composite.setUp(ontModel, ontModel);
       
        ClassPropertyMetrics cpm = null;
        
        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();
            
            if(Utilities.isInIgnoreList(classURI))
                continue;
            
            cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(classURI);
            
            if(cpm==null)
                cpm = new ClassPropertyMetrics(cls);
        
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
            ClassCompareShape.populateComposite(ontModel, cpm, composite);
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
                cpm = new ClassPropertyMetrics(cls);
        
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
            ClassCompareShape.populateComposite(ontModel, cpm, composite);
        }
        
        return composite;
    
    }
    
    
    public static void populateObjProperties(OntModel ontModel, ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass = cpm.getOntClass();
        //ontClass = ontModel.getOntClass(ontClass.toString()); // trocar pela versao da nova
        
//        OntClass slice = OntologyUtils.getLastTimeSlice(ontClass);
//    
//        if(slice!=null)
//            ontClass = slice;
        
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassObjProperties();
        //List<PropertyMetrics> propertyMetrics    = cpm.getPropertyMetrics();
        
        if(classProperties==null) return;
        
        for(String propertyURI : classProperties.keySet())
        {
            if(Utils.Utilities.isInIgnoreList(propertyURI))
                continue;
            
            OntProperty onProperty = ontModel.getOntProperty(propertyURI);
            if(onProperty==null) // just in case
                onProperty = ontModel.createObjectProperty(propertyURI, false);
            
            boolean isFunctional = false;
            boolean isQualifiedR = false;
            boolean isEquivalent = false;
            boolean isSuperClass = true;
            
            int mentions         = (int) classProperties.get(propertyURI);
            
            if(functionalCandidates.contains(propertyURI) && mentions > Utils.Configs.functional_threshold)
                isFunctional = true;
        
            AddObjectProperty add_objProperty = new AddObjectProperty(propertyURI, isFunctional);
            composite.add(add_objProperty);
            
            //adicionar na mesma a questao da restricao de equivalencia ao composite, depois ver se é para executar ou não
            
            if(mentions <= Utils.Configs.subclass_threshold)
                isSuperClass = false;
            
            if(mentions > Utils.Configs.equivalent_threshold)
                isEquivalent = true;
            
            
            if(isEquivalent || isSuperClass)
            {
                AddCardinalityRestriction rec = new AddCardinalityRestriction(ontClass, onProperty, isEquivalent, isSuperClass);

                // TODO decidir como refinar o qualified (preciso verificar os ranges TODOS
                if(mentions > Utils.Configs.subclass_threshold)
                    if(isFunctional)
                        rec.setCardinalityType("Exactly", 1, isQualifiedR);
                    else
                        rec.setCardinalityType("Min", 0, isQualifiedR);

                composite.add(rec);
            }

            // TODO ACRESCENTAR OUTROS TIPOS DE RESTRIÇAO AQUI
            //UTILIZAR O PROPERTYMETRICS
            PropertyMetrics pmetrics = cpm.getMetricsOfProperty(propertyURI);
            if(pmetrics!=null)
            {
                Map<String, Integer> ranges = pmetrics.getRanges();
                //all ranges must be the same
                // se houver mais que 1 range na lista ja nao vale
                int numRanges = ranges.keySet().size();
                
                if(numRanges == 1)
                {
                    //all values from
                    // OntClass cls, OntProperty onProperty, boolean isEquivalent,
                    //boolean isSubclass, OntClass rangeClass
                    
                    String rangeURI     = ranges.keySet().iterator().next();
                    
                    OntClass rangeClass = ontModel.getOntClass(rangeURI);
                    
                    if(rangeClass==null) // range é individual
                    {
                        Individual individual = ontModel.getIndividual(rangeURI);
                        
                        if(individual!=null)
                        {
                           List<OntClass> rangesL = individual.listOntClasses(true).toList();
                           
                           for(OntClass r : rangesL)
                           {
                                AddAllValuesFromRestriction aavfR = 
                                 new AddAllValuesFromRestriction(
                                         ontClass,
                                         onProperty,
                                         isEquivalent,
                                         isSuperClass,
                                         r
                                 );
                                composite.add(aavfR);
                           }
                        }
                    }
                    else
                    {
                        AddAllValuesFromRestriction aavfR = 
                                 new AddAllValuesFromRestriction(
                                         ontClass,
                                         onProperty,
                                         isEquivalent,
                                         isSuperClass,
                                         rangeClass
                                 );
                        composite.add(aavfR);
                    }
                }
                else if(numRanges >= Utils.Configs.someValuesFrom_threshold)
                {
                    // some values from
                                      
                    String rangeURI     = ranges.keySet().iterator().next();
                    
                    if(rangeURI==null) continue;
                    
                    OntClass rangeClass = ontModel.getOntClass(rangeURI);
                    
                    if(rangeClass==null) // range é individual
                    {
                        Individual individual = ontModel.getIndividual(rangeURI);
                        
                        if(individual!=null)
                        {
                           List<OntClass> rangesL = individual.listOntClasses(true).toList();
                           
                           for(OntClass r : rangesL)
                           {
                                AddSomeValuesFromRestriction aavfR = 
                                 new AddSomeValuesFromRestriction(
                                         ontClass,
                                         onProperty,
                                         isEquivalent,
                                         isSuperClass,
                                         r
                                 );
                                composite.add(aavfR);
                           }
                        }
                    }
                    else
                    {
                        AddSomeValuesFromRestriction asvfR = 
                            new AddSomeValuesFromRestriction(
                                    ontClass,
                                    onProperty,
                                    isEquivalent,
                                    isSuperClass,
                                    rangeClass
                            );
                    composite.add(asvfR);
                    }                 
                }
            }
        }
    }
    
    
    public static void populateDtProperties(OntModel ontModel, ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass = cpm.getOntClass();
    
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassDtProperties();
        
        if(classProperties==null) return;
        
        for(String propertyURI : classProperties.keySet())
        {
           // DatatypeProperty onProperty = Utils.OntologyUtils.getDatatypePropertyFromModel(ontModel, propertyURI);
            
            if(Utilities.isInIgnoreList(propertyURI))
                continue;
            
            DatatypeProperty onProperty = ontModel.getDatatypeProperty(propertyURI);
            if(onProperty==null) // just in case
                onProperty = ontModel.createDatatypeProperty(propertyURI, false);
            
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
            
            if(mentions <= Utils.Configs.subclass_threshold)
                isSuperClass = false;
            
            if(mentions > Utils.Configs.equivalent_threshold)
                isEquivalent = true;
            
            AddCardinalityRestriction rec = new AddCardinalityRestriction(ontClass, onProperty, isEquivalent, isSuperClass);
            
            // TODO decidir como refinar o qualified (preciso verificar os ranges TODOS
            if(mentions > Utils.Configs.subclass_threshold)
                if(isFunctional)
                    rec.setCardinalityType("Exactly", 1, isQualifiedR);
                else
                    rec.setCardinalityType("Min", 0, isQualifiedR);
           
        }
        
    }
    
    public static void populateComposite(OntModel ontModel, ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite) 
    {
        ClassCompareShape.populateObjProperties(ontModel, cpm, composite);
        ClassCompareShape.populateDtProperties(ontModel, cpm, composite);
       
        OntClass ontClass = cpm.getOntClass();
        AddClass addCls = new AddClass(ontClass);
        
        composite.add(addCls);
        
    }
    
    
}
