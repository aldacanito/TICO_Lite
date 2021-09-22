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
    
    public AddObjectProperty(OntProperty thePropertyTriple)
    {
       super(thePropertyTriple);
    }
    
   
    @Override
    public void execute() 
    {
        
        try
        {
            OntologyUtils.copyProperty(evolvedModel, theProperty);
    
        }
        catch(ClassCastException e)
        {  
            Utilities.logError("Error casting Property "+ theProperty.getURI() +" to OntProperty. Attempting Property cast...");
            
            Property property =  this.ontologyModel.getProperty(theProperty.getURI());
            evolvedModel.createObjectProperty(theProperty.getURI());
            OntologyUtils.copyProperty(evolvedModel, property);
            
        }
        
        
    }
    
    public String toString()
    {
        String toPrint="ADD OBJECT PROPERTY EVOLUTIONARY ACTION: " ;
     
        toPrint += OntologyUtils.propertyStats(theProperty);
       
        return toPrint;
    }
    
}
