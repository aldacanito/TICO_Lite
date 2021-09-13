/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddObjectProperty;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author shizamura
 */
public class AddObjectProperty extends AddProperty
{
    
    public AddObjectProperty(Triple thePropertyTriple)
    {
       super(thePropertyTriple);
    }
    
   
    @Override
    public void execute() 
    {
        ObjectProperty createObjectProperty = evolvedModel.createObjectProperty(thePropertyTriple.getPredicate().getURI());
    }
    
}
