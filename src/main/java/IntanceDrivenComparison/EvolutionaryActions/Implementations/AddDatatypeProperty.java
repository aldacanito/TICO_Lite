/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddDatatypeProperty;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author shizamura
 */
public class AddDatatypeProperty extends AddProperty
{

    public AddDatatypeProperty(Triple t) 
    {
        super(t);
    }
    
    @Override
    public void execute() 
    {
        evolvedModel.createDatatypeProperty(thePropertyTriple.getPredicate().getURI());
    }
    
}
