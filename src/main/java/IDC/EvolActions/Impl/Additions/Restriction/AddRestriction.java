/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions.Restriction;

import IDC.EvolActions.Interfaces.IAddClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author Alda
 */
public abstract class AddRestriction implements IAddClass
{

    protected OntClass rangeClass;

    protected String range_URI;


    protected String URI;
    protected OntClass ontClass;
    
    protected boolean isEquivalent;
    protected boolean isSubclass;
    
    protected OntProperty onProperty;
    
    /**
     * Creates a new Restriction.
     * @param cls The Class the Restriction will be appended to
     * @param onProperty The Property associated with the Restriction
     * @param isEquivalent Should the Restriction be EQUIVALENT CLASS
     * @param isSubclass Should the Restriction be SUBCLASS OFF
     * 
     */
    public AddRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass)
    {
        this.isEquivalent       = isEquivalent;
        this.isSubclass         = isSubclass;
        this.ontClass           = cls;
        this.onProperty         = onProperty;
        this.URI                = cls.getURI();

    }
    
    
    @Override
    public String      getURI()         { return this.URI;     }
    public OntProperty onProperty()     { return onProperty;   }
    public boolean     isEquivalent()   { return isEquivalent; }
    public boolean     isSubclass()     { return isSubclass;   }

    @Override
    public void execute() 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public OntClass getRangeClass()
    {
        if(rangeClass != null)
            return rangeClass;
        else if(this.range_URI != null)
            return this.ontClass.getOntModel().getOntClass(this.range_URI);

        return null;
    }

    public void setRangeClass(OntClass rangeClass) {
        this.rangeClass = rangeClass;

        if(rangeClass!=null && !rangeClass.isAnon())
            this.range_URI = rangeClass.getURI();
    }

    public String getRange_URI()
    {
        return this.range_URI;
    }

    public void setRange_URI(String rURI)
    {
        this.range_URI = rURI;
    }
    
}
