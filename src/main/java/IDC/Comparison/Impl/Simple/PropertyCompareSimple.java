/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Simple;

import IDC.Comparison.Interfaces.IPropertyCompare;
import IDC.EvolActions.Factories.EvolutionaryActionFactory;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class PropertyCompareSimple implements IPropertyCompare
{
    protected OntModel ontModel;
    protected Statement statement;
    
    public PropertyCompareSimple(Statement statement, OntModel ontModel)
    {
        this.ontModel  = ontModel;
        this.statement = statement;
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
                return EvolutionaryActionFactory.getInstance().createAddPropertyAction(predicate);

                //definir domain e range depois
            }
        }
        catch(ClassCastException e)
        {
            Utilities.logError("Could not convert "+ statement.getPredicate().getURI() + " to OntProperty.");
        }
        return null;
    }
    
    
       /*
    @Override
    public EvolutionaryAction compare() 
    {
        Triple t = (Triple) t0;
        boolean exists = OntologyUtils.isProperty(t.getPredicate(), model);
        
        if(!exists)
        {
            Utilities.logInfo("ObjectProperty with URI "+ t.getPredicate().getURI() + " does not exist.");
            return EvolutionaryActionFactory.getInstance().createAddPropertyAction(t);
        }
            
        return null;
    }*/

  
    
}

    

