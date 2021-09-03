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
    public IAddClass createAddClassAction()
    {
        return new AddClass();
    }
    
    public AddDatatypeProperty createAddDTPropertyAction()
    {
        return new AddDatatypeProperty();
    }
     
    public AddObjectProperty createAddObjectPropertyAction()
    {
        return new AddObjectProperty();
    }
}
