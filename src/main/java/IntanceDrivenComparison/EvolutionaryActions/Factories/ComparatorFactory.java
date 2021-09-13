/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Factories;

import IntanceDrivenComparison.Comparison.Implementations.Simple.ClassCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.DatatypePropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.ObjectPropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import Utils.OntologyUtils;
import Utils.Utilities;

import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;

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

    
    public IPropertyCompare getPropertyComparator(Node predicate, OntModel ontModel) 
    {
        boolean objectProperty = OntologyUtils.isObjectProperty(predicate, ontModel);
        boolean datatypeProperty = OntologyUtils.isDatatypeProperty(predicate, ontModel);
        
        if(objectProperty)
            return new ObjectPropertyCompareSimple();
        
        if(datatypeProperty)
            return new DatatypePropertyCompareSimple();
            
        Utilities.logInfo("URI does not match any ObjectProperty or DatatypeProperty definitions in the model.");
        return null;
    }
    
}
