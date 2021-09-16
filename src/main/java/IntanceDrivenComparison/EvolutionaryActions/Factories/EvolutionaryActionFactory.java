/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Factories;

import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddClass;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddDatatypeProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddObjectProperty;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddProperty;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import Utils.Utilities;
import org.apache.jena.ontology.OntClass;

import org.apache.jena.graph.Triple;

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
        return new AddClass(theClassToAdd);
    }
    
    public AddDatatypeProperty createAddDTPropertyAction(Triple t)
    {
        return new AddDatatypeProperty(t);
    }
     
    public AddObjectProperty createAddObjectPropertyAction(Triple t)
    {
        return new AddObjectProperty(t);
    }
    
    public AddProperty createAddPropertyAction(Triple t)
    {
        Utilities.logInfo("Creating Add Property Evolutionary Action...");
        return new AddObjectProperty(t);
    }

    
}
