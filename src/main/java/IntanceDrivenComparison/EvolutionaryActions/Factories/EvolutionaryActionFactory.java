/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Factories;

import IntanceDrivenComparison.EvolutionaryActions.Implementations.CopyClass;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.CopyDatatypeProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.CopyObjectProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.CopyProperty;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import Utils.Utilities;
import org.apache.jena.ontology.OntClass;

import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author shizamura
 */
public class EvolutionaryActionFactory 
{
    private static EvolutionaryActionFactory theFactory = new EvolutionaryActionFactory();
    
    private EvolutionaryActionFactory()
    {
        //TODO FIGURE OUT CONFIGURATION FILES ETC
    }
    
    public static EvolutionaryActionFactory getInstance()
    {
        return theFactory;
    }
    
    // TODO IMPLEMENTAR MAIS ESCOLHAS
    public IAddClass createAddClassAction(OntClass theClassToAdd)
    {
        return new CopyClass(theClassToAdd);
    }
    
    public CopyDatatypeProperty createAddDTPropertyAction(Triple t)
    {
        return new CopyDatatypeProperty(t);
    }
     
    public CopyObjectProperty createAddObjectPropertyAction(Triple t)
    {
        return new CopyObjectProperty(t);
    }
    
    public CopyProperty createAddPropertyAction(Triple t)
    {
        Utilities.logInfo("Creating Add Property Evolutionary Action...");
        return new CopyProperty(t);
    }


    public CopyDatatypeProperty createAddDTPropertyAction(OntProperty t)
    {
        return new CopyDatatypeProperty(t);
    }
     
    public CopyObjectProperty createAddObjectPropertyAction(OntProperty t)
    {
        return new CopyObjectProperty(t);
    }
    
    public CopyProperty createAddPropertyAction(OntProperty t)
    {
        Utilities.logInfo("Creating Add Property Evolutionary Action...");
        return new CopyProperty(t);
    }

    
}
