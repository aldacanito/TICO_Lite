/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Simple;

import IDC.EvolActions.Factories.EvolutionaryActionFactory;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.DatatypeProperty;
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
        try
        {
            DatatypeProperty predicate = statement.getPredicate().as(DatatypeProperty.class);
            if(!OntologyUtils.isProperty(predicate, ontModel))
            {
                Utilities.logInfo("ObjectProperty with URI "+ predicate.getURI() + " does not exist.");
                return EvolutionaryActionFactory.getInstance().createAddDTPropertyAction(predicate);
            }
        }
        catch(ClassCastException e)
        {
            Utilities.logError("Could not convert "+ statement.getPredicate().getURI() + " to DatatypeProperty.");
        }
        return null;
    }
}
