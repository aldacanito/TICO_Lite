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

    
    @Override
    public EvolutionaryAction compare() 
    {
 //       ExtendedIterator classes = ontModel.listClasses();   
//        Utilities.logInfo("ANALYSING INDIVIDUAL " + instance.getURI() + "...");
        
        List<Statement> properties  = instance.listProperties().toList();
        List<OntClass> ontClassList = instance.listOntClasses(true).toList();
        ClassPropertyMetrics cpm = null;
                
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();
        
        if(ontClassList==null) return composite;   
        
        composite.setUp(ontModel, evolvedModel); // start
        
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
        }
        
        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();
             
            if(Utilities.isInIgnoreList(classURI))
                continue;
            
            cpm = EntityMetricsStore.getStore()
                    .getMetricsByClassURI(classURI);  
            
            if(cpm==null) continue;
            
            this.populateComposite(cpm, composite);
        }

        
        //composite.execute();
        
        //Utilities.logInfo("No Evolutionary Action created.");
        return composite;
    }

    
    private void populateObjProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass = cpm.getOntClass();
        
        OntClass slice = OntologyUtils.getLastTimeSlice(ontClass);
    
        if(slice!=null)
            ontClass = slice;
        
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
                ontModel.createObjectProperty(propertyURI, false);
            
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
                else if(numRanges >= Utils.Configs.someValuesFrom_threshold)
                {
                    // some values from
                                      
                    String rangeURI     = ranges.keySet().iterator().next();
                    
                    if(rangeURI==null) continue;
                    
                    OntClass rangeClass = ontModel.getOntClass(rangeURI);
                    
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
    
    
    private void populateDtProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass = cpm.getOntClass();
    
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassDtProperties();
        
        if(classProperties==null) return;
        
        for(String propertyURI : classProperties.keySet())
        {
            DatatypeProperty onProperty = Utils.OntologyUtils.getDatatypePropertyFromModel(ontModel, propertyURI);
            
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
    private void populateComposite(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite) 
    {
        this.populateObjProperties(cpm, composite);
        this.populateDtProperties(cpm, composite);
        
        // run through all Classes and Properties and Check if EvolutionaryActions should be deployed

        OntClass ontClass = cpm.getOntClass();
        AddClass addCls = new AddClass(ontClass);
        
        composite.add(addCls);
        
    }
    
    
}