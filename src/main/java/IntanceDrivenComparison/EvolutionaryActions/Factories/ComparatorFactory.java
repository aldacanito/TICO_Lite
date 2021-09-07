/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Factories;

import IntanceDrivenComparison.Comparison.Implementations.Simple.ClassCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.ObjectPropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;

/**
 *
 * @author shizamura
 */
public class ComparatorFactory 
{
    private static ComparatorFactory theFactory = new ComparatorFactory();
   
    private ComparatorFactory()
    {
        //TODO CHECK CONFIGS
    }
    
    public static ComparatorFactory getInstance()
    {
        return theFactory;
    }
    
    //TODO alterar para ser dinamico
    public IClassCompare getClassComparator()
    {
        IClassCompare compare = new ClassCompareSimple();
        
        return compare;
    }

    public IPropertyCompare getPropertyComparator(boolean objectProperty) 
    {
        IPropertyCompare compare;
        if(objectProperty)
            compare = new ObjectPropertyCompareSimple();
        else
            compare = new DatatypePropertyCompare();
        
        return compare;
    
    }
    
}
