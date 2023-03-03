/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Copy;

import IDC.EvolActions.Interfaces.IAddProperty;
import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author shizamura
 */
public class CopyProperty implements IAddProperty
{
    protected Triple thePropertyTriple;
    protected OntProperty theProperty;
    protected boolean functional;
    
    
    public CopyProperty(Triple thePropertyTriple)
    {
       this.thePropertyTriple = thePropertyTriple;
    }
    
    public CopyProperty(OntProperty theProperty)
    {
       this.functional = false;
       this.theProperty = theProperty;
    }
    
    
    public CopyProperty(OntProperty theProperty, boolean functional)
    {
       this.functional = functional;
       this.theProperty = theProperty;
    }
    
    
    public void setFunctional(boolean functional)
    {
        this.functional = functional;
    }
    



    @Override
    public void execute() 
    {
        ModelManager.getManager().getEvolvingModel().createOntProperty(theProperty.getURI());
        
        if(!Utilities.isInIgnoreList(theProperty.getURI()))
            OntologyUtils.copyProperty(ModelManager.getManager().getEvolvingModel(), theProperty);
        
    }
    
    public String toString()
    {
        String toPrint="ADD PROPERTY EVOLUTIONARY ACTION: " ;
        toPrint += OntologyUtils.propertyStats(theProperty);
        return toPrint;
    }

    @Override
    public String getURI() 
    {
        return this.theProperty.getURI();
    }
  
    
}
