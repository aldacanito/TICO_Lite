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
public class AddRestriction implements IAddClass
{
    protected OntModel originalModel;
    protected OntModel evolvedModel;
    
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
    public String getURI() 
    {
        return this.URI;
    }

    @Override
    public OntModel getEvolvedModel() 
    {
        return this.evolvedModel;
    }

    @Override
    public void setUp(OntModel originalModel, OntModel evolvedModel) 
    {
        this.evolvedModel  = evolvedModel;
        this.originalModel = originalModel;
    }

    @Override
    public void execute() 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
