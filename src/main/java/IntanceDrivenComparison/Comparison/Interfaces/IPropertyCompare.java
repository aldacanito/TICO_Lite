/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Interfaces;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Statement;


public interface IPropertyCompare extends IEntityCompare
{
    public EvolutionaryAction compare();
}
