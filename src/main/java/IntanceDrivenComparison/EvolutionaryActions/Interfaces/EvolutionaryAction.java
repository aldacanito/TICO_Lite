/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Interfaces;

import org.apache.jena.rdf.model.Model;

/**
 *
 * @author shizamura
 */
public interface EvolutionaryAction
{
    public void execute(Model originalModel, Model evolvedModel, Object t0);
}

