/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddObjectProperty;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;

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
        
        try
        {
            OntProperty property = (OntProperty) this.ontologyModel.getProperty(thePropertyTriple.getPredicate().getURI());
            evolvedModel.createObjectProperty(thePropertyTriple.getPredicate().getURI());
            OntologyUtils.copyProperty(evolvedModel, property);
    
        }
        catch(ClassCastException e)
        {  
            Utilities.logError("Error casting Property to OntProperty. Attempting Property cast...");
            
            Property property =  this.ontologyModel.getProperty(thePropertyTriple.getPredicate().getURI());
            evolvedModel.createObjectProperty(thePropertyTriple.getPredicate().getURI());
            OntologyUtils.copyProperty(evolvedModel, property);
            
        }
        
        
    }
    
    public String toString()
    {
        String toPrint="ADD OBJECT PROPERTY EVOLUTIONARY ACTION: " ;
     
        try
        {
            toPrint += OntologyUtils.propertyStats((OntProperty) thePropertyTriple.getPredicate());
        }
        catch(ClassCastException e)
        {
            Utilities.logError("Error Casting OntProperty. Attempting regular property...");
            toPrint += OntologyUtils.propertyStats(thePropertyTriple.getPredicate());
        }
       
        return toPrint;
    }
    
}
