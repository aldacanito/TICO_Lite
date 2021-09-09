/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Interfaces;

import org.apache.jena.ontology.OntModel;

/**
 *
 * @author shizamura
 */
public interface EvolutionaryAction
{
    public OntModel getEvolvedModel();
    public void setUp(OntModel originalModel, OntModel evolvedModel);
    public void execute();
    
}

