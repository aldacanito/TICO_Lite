/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddProperty;
import Utils.OntologyUtils;
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
      
    public AddProperty(Triple thePropertyTriple)
    {
       this.thePropertyTriple = thePropertyTriple;
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
        evolvedModel.createOntProperty(thePropertyTriple.getPredicate().getURI());
        OntologyUtils.copyProperty(evolvedModel, (OntProperty) thePropertyTriple.getPredicate());
    }
    
    public String toString()
    {
        String toPrint="ADD PROPERTY EVOLUTIONARY ACTION: " ;
        toPrint += OntologyUtils.propertyStats((OntProperty) thePropertyTriple.getPredicate());
        return toPrint;
    }
  
    
}
