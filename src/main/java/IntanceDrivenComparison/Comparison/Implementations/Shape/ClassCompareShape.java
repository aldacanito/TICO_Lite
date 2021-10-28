/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Shape;

import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.Addition.AddDatatypeProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.Addition.AddObjectProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.Addition.Restriction.AddCardinalityRestriction;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.EvolutionaryActionComposite;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.Metrics.ClassPropertyMetrics;
import IntanceDrivenComparison.Metrics.EntityMetricsStore;
import Utils.Utilities;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

/**
 *
 * @author shizamura
 */
public class ClassCompareShape implements IClassCompare
{
//    protected OntClass ontClass;
    protected OntModel   ontModel;
    protected Individual instance;
    
    
    public ClassCompareShape(Individual ind, OntModel ontModel)
    {
        this.instance = ind;
        this.ontModel = ontModel;
    
    }
    
    
    @Override
    public EvolutionaryAction compare() 
    {
 //       ExtendedIterator classes = ontModel.listClasses();   
        Utilities.logInfo("ANALYSING INDIVIDUAL " + instance.getURI() + "...");
        
        List<Statement> properties  = instance.listProperties().toList();
        List<OntClass> ontClassList = instance.listOntClasses(true).toList();
        ClassPropertyMetrics cpm = null;
        
        for(OntClass cls : ontClassList)
        {
            String classURI = cls.getURI();
        
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
                
                if(object.isLiteral())
                    cpm.addDtProperty(predicateURI);
                if(object.isResource())
                    cpm.addObjProperty(predicateURI);
                    
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
            
        }

        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();
        composite.setUp(ontModel, ontModel); // start
        
        // run through all Classes and Properties and Check if EvolutionaryActions should be deployed
        if(cpm!=null)
            this.populateComposite(cpm, composite);
        
        //composite.execute();
        
        Utilities.logInfo("No Evolutionary Action created.");
        return composite;
    }

    
    private void populateObjProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
        OntClass ontClass = cpm.getOntClass();
    
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassObjProperties();
        
        for(String propertyURI : classProperties.keySet())
        {
            ObjectProperty onProperty = Utils.OntologyUtils.getObjectPropertyFromModel(ontModel, propertyURI);
            
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
            
            AddCardinalityRestriction rec = new AddCardinalityRestriction(ontClass, onProperty, isEquivalent, isSuperClass);
            
            // TODO decidir como refinar o qualified (preciso verificar os ranges TODOS
            if(mentions > Utils.Configs.subclass_threshold)
                if(isFunctional)
                    rec.setCardinalityType("Exactly", 1, isQualifiedR);
                else
                    rec.setCardinalityType("Min", 0, isQualifiedR);
           
        }
        
    }
    
    
    private void populateDtProperties(ClassPropertyMetrics cpm,  EvolutionaryActionComposite composite)
    {
         OntClass ontClass = cpm.getOntClass();
    
        List<String> functionalCandidates        = cpm.getFunctionalCandidates();
        HashMap<String, Integer> classProperties = cpm.getClassDtProperties();
        
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
        // run through all Classes and Properties and Check if EvolutionaryActions should be deployed
        this.populateObjProperties(cpm, composite);
        this.populateDtProperties(cpm, composite);
        
    }
    
    
}
