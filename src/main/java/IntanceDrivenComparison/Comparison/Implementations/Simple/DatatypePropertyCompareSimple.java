/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Simple;

import IntanceDrivenComparison.EvolutionaryActions.Factories.EvolutionaryActionFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;



public class DatatypePropertyCompareSimple extends PropertyCompareSimple
{
    public DatatypePropertyCompareSimple(Statement s, OntModel ontModel)
    {
        super(s, ontModel);
    }
    

    @Override
    public EvolutionaryAction compare() 
    {
        Property predic = statement.getPredicate();
        try
        {
            OntProperty predicate = (OntProperty) predic;
            boolean exists = OntologyUtils.isProperty(predicate, ontModel);

            if(!exists)
            {
                Utilities.logInfo("ObjectProperty with URI "+ predicate.getURI() + " does not exist.");
                return EvolutionaryActionFactory.getInstance().createAddDTPropertyAction(predicate);
            }
        }
        catch(ClassCastException e)
        {
            Utilities.logError("Could not convert "+ statement.getPredicate().getURI() + " to OntProperty.");
        }
        return null;
    }
}
