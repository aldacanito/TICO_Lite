/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions.Restriction;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;

/**
 *
 * @author Alda
 */
public class AddAllValuesFromRestriction extends AddRestriction
{
    OntClass rangeClass;

    public AddAllValuesFromRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass) 
    {
        super(cls, onProperty, isEquivalent, isSubclass);
        
        if(cls.getURI()!=null && onProperty.getURI()!=null)
            System.out.println("Creating AllValuesFromRestriction for:"
                + "\n\t Class: " + cls.getURI() +
                "\t On Property: " + onProperty.getURI() +
                "\t Details: Is EQ? " + isEquivalent + ". Is Subclass? " + isSubclass + "." );
    }
    
    public AddAllValuesFromRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass, OntClass rangeClass) 
    {
        super(cls, onProperty, isEquivalent, isSubclass);
        this.rangeClass = rangeClass;
        
        if(cls.getURI()!=null && onProperty.getURI()!=null && rangeClass != null && rangeClass.getURI()!=null)
            System.out.println("Creating AllValuesFromRestriction for:"
                + "\n\t Class: " + cls.getURI() +
                "\t On Property: " + onProperty.getURI() +
                "\t Has Range: " + rangeClass.getURI() +
                "\t Details: Is EQ? " + isEquivalent + ". Is Subclass? " + isSubclass + "." );
    }
    
    
    public void setQualifiedRestrictionClass(OntClass rangeClass)
    {
        this.rangeClass = rangeClass;
    }
    
    @Override
    public void execute()
    {
        Restriction restriction = null;
        
        // TODO: TESTAR
        if(onProperty!=null && this.rangeClass!=null)
        {
            System.out.println("Adding CardinalityRestriction to " + this.ontClass.getURI() + " on Property " + onProperty.getURI());    
            
            restriction = this.getEvolvedModel().createAllValuesFromRestriction(null, onProperty, this.rangeClass);
        
            if(this.isEquivalent)
                this.ontClass.addEquivalentClass(restriction);

            if(this.isSubclass)
                this.ontClass.addSuperClass(restriction);
        }
  
    }
}
