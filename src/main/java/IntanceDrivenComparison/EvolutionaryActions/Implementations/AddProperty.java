/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddProperty;
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
public class AddProperty implements IAddProperty
{
    protected Triple thePropertyTriple;
    protected OntModel ontologyModel;
    protected OntModel evolvedModel;
    protected OntProperty theProperty;
    protected boolean functional;
    
    
    public AddProperty(Triple thePropertyTriple)
    {
       this.thePropertyTriple = thePropertyTriple;
    }
    
    public AddProperty(OntProperty theProperty)
    {
       this.functional = false;
       this.theProperty = theProperty;
    }
    
    
    public AddProperty(OntProperty theProperty, boolean functional)
    {
       this.functional = functional;
       this.theProperty = theProperty;
    }
    
    
    public void setFunctional(boolean functional)
    {
        this.functional = functional;
    }
    
    @Override
    public OntModel getEvolvedModel() 
    {
        return this.evolvedModel;
    }

    @Override
    public void setUp(OntModel originalModel, OntModel evolvedModel) 
    {
        this.ontologyModel = originalModel;
        this.evolvedModel  = evolvedModel;
        
        if(this.evolvedModel == null || this.evolvedModel.isEmpty())
            this.evolvedModel = ontologyModel;
    }


    @Override
    public void execute() 
    {
        evolvedModel.createOntProperty(theProperty.getURI());
        
        if(!Utilities.isInIgnoreList(theProperty.getURI()))
            OntologyUtils.copyProperty(evolvedModel, theProperty);
       
        
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
