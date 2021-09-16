/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Simple;

import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.EvolutionaryActionFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class PropertyCompareSimple implements IPropertyCompare
{
     @Override
    public EvolutionaryAction compare(OntModel model, Object t0) 
    {
        Triple t = (Triple) t0;
        
        boolean exists = OntologyUtils.isProperty(t.getPredicate(), model);
        
        if(!exists)
        {
            Utilities.logInfo("ObjectProperty with URI "+ t.getPredicate().getURI() + " does not exist.");
            
            return EvolutionaryActionFactory.getInstance().createAddPropertyAction(t);
        }
            
        return null;
    }
}

    

