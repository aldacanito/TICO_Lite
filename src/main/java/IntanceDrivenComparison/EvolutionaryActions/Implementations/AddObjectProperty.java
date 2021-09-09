/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddObjectProperty;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author shizamura
 */
public class AddObjectProperty implements IAddObjectProperty
{
    private Triple thePropertyTriple;
    private OntModel ontologyModel;
    private OntModel evolvedModel;
    
    
    public AddObjectProperty(Triple thePropertyTriple)
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
        ObjectProperty createObjectProperty = evolvedModel.createObjectProperty(thePropertyTriple.getPredicate().getURI());
    }
    
}
