/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Simple;

import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.EvolutionaryActionFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class ClassCompareSimple implements IClassCompare
{
 
    @Override
    public EvolutionaryAction compare(OntModel model, Object t0) 
    {
        
        OntClass ontClass = (OntClass) t0;
        ExtendedIterator classes = model.listClasses();
        
        boolean exists = false;
        while(classes.hasNext())
        {
            OntClass thisClass = (OntClass) classes.next();
             
             //para ja so comparar se a classe existe ou nao
            if(thisClass.getURI().equalsIgnoreCase(ontClass.getURI()))
            {
                exists = true;
                break;
            }
        }
        
        if(exists)
        {
            EvolutionaryAction action = EvolutionaryActionFactory.getInstance().createAddClassAction(ontClass);
            return action;
        }
       
        return null;
    }
}
