/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import org.apache.jena.ontology.DatatypeProperty;

/**
 *
 * @author Alda
 */
public class AddDatatypeProperty extends AddProperty
{
    
    public AddDatatypeProperty(String URI) {
        super(URI);
    }
    
    public AddDatatypeProperty(String URI, boolean functional) {
        super(URI, functional);
    }
    
    public void execute()
    {
        Utils.Utilities.logInfo("Executing Evolutionary Action Add Datatype Property for Property with URI " + this.getURI());
   
        DatatypeProperty dtp = Utils.OntologyUtils.getDatatypePropertyFromModel(this.evolvedModel, URI);
        
        if(dtp!=null) // property Exists. modify?
        {}
        else
        {}

        
       
    }
    

    
}
