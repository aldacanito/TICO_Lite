/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Shape;

import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.EvolutionaryActionFactory;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.EvolutionaryActionComposite;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.Metrics.ClassPropertyMetrics;
import IntanceDrivenComparison.Metrics.EntityMetricsStore;
import Utils.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;

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
                
                cpm.addProperty(predicateURI);
                
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
            this.populateComposite(cpm);
        
        //composite.execute();
        
        Utilities.logInfo("No Evolutionary Action created.");
        return composite;
    }

    private void populateComposite(ClassPropertyMetrics cpm) 
    {
        // run through all Classes and Properties and Check if EvolutionaryActions should be deployed
        OntClass ontClass = cpm.getOntClass();
        int classMentions = cpm.getClassMentions();
        List<String> functionalCandidates = cpm.getFunctionalCandidates();
        
        HashMap<String, Integer> classProperties = cpm.getClassProperties();
        
        for(String propertyURI : classProperties.keySet())
        {
            boolean isFunctional = false;
            if(functionalCandidates.contains(propertyURI))
                isFunctional = true;
        
            int mentions = (int) classProperties.get(propertyURI);
              
            
            // preciso ter thresholds, uma classe ou restriçao só é criada se for mencionada x vezes
            
        }
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
