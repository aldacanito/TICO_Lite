/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddObjectProperty;
import Utils.OntologyUtils;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

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
        evolvedModel.createObjectProperty(thePropertyTriple.getPredicate().getURI());
        OntologyUtils.copyProperty(evolvedModel, (OntProperty) thePropertyTriple.getPredicate());
    }
    
    public String toString()
    {
        String toPrint="ADD OBJECT PROPERTY EVOLUTIONARY ACTION: " ;
        toPrint += OntologyUtils.propertyStats((OntProperty) thePropertyTriple.getPredicate());
        return toPrint;
    }
    
}
