/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Interfaces;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author shizamura
 */
public interface IEntityCompare 
{    
    public EvolutionaryAction compare(Model model, Object t0, Object t1);
    
}
