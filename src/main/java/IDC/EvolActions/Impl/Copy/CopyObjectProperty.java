/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Copy;

import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author shizamura
 */
public class CopyObjectProperty extends CopyProperty
{
    boolean isFunctional = false;
    
    public CopyObjectProperty(Triple thePropertyTriple)
    {
       super(thePropertyTriple);
    }
    
    public CopyObjectProperty(OntProperty thePropertyTriple)
    {
       super(thePropertyTriple);
    }
    
    
    public CopyObjectProperty(OntProperty thePropertyTriple, boolean isFunctional)
    {
       super(thePropertyTriple);
       this.isFunctional = isFunctional;
    }
    
   
    @Override
    public void execute() 
    {
        
        try
        {
            OntologyUtils.copyProperty(ModelManager.getManager().getEvolvingModel(), theProperty);
    
        }
        catch(ClassCastException e)
        {  
            Utilities.logError("Error casting Property "+ theProperty.getURI() +" to OntProperty. Attempting Property cast...");
            
            Property property =  ModelManager.getManager().getOriginalModel().getProperty(theProperty.getURI());
            ModelManager.getManager().getEvolvingModel().createObjectProperty(theProperty.getURI());
            OntologyUtils.copyProperty(ModelManager.getManager().getEvolvingModel(), property);
            
        }
        
        
    }
    
    public String toString()
    {
        String toPrint="ADD OBJECT PROPERTY EVOLUTIONARY ACTION: " ;
     
        toPrint += OntologyUtils.propertyStats(theProperty);
       
        return toPrint;
    }
    
}
