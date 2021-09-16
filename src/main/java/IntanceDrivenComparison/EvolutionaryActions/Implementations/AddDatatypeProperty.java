/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddDatatypeProperty;
import Utils.OntologyUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

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
        
        // TODO VERIFICAR SE ISTO SE AGUENTA     
        OntologyUtils.copyProperty(evolvedModel, (OntProperty) thePropertyTriple.getPredicate());
    }
    
    public String toString()
    {
        String toPrint="ADD DATATYPE PROPERTY EVOLUTIONARY ACTION: " ;
        toPrint += OntologyUtils.propertyStats((OntProperty) thePropertyTriple.getPredicate());
        return toPrint;
    }
    
    
}
