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
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;

/**
 *
 * @author shizamura
 */
public class ObjectPropertyCompareSimple extends PropertyCompareSimple
{
    public ObjectPropertyCompareSimple(Statement s, OntModel ontModel)
    {
        super(s, ontModel);
    }
    
    @Override
    public EvolutionaryAction compare() 
    {
        try
        { 
            ObjectProperty predicate = statement.getPredicate().as(ObjectProperty.class);
            if(!OntologyUtils.isObjectProperty(predicate, ontModel))
            {
                Utilities.logInfo("ObjectProperty with URI "+ predicate.getURI() + " does not exist.");
                return EvolutionaryActionFactory.getInstance().createAddObjectPropertyAction(predicate);
            }
        }
        catch(ClassCastException e)
        {
            Utilities.logError("Property with URI "+ statement.getPredicate().getURI() + " cannot be cast to ObjectProperty.");
        }
            
        return null;
    }
        
    /*
    @Override
    public EvolutionaryAction compare(OntModel model, Object t0) 
    {
        Triple t = (Triple) t0;
        
        boolean exists = OntologyUtils.isObjectProperty(t.getPredicate(), model);
        
        if(!exists)
        {
            Utilities.logInfo("ObjectProperty with URI "+ t.getPredicate().getURI() + " does not exist.");
            
            return EvolutionaryActionFactory.getInstance().createAddObjectPropertyAction(t);
            
            
            //definir domain e range depois
        }
        
        return null;
    }*/
}
