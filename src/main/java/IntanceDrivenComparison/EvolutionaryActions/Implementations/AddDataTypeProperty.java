/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Alda
 */
public class AddDatatypeProperty extends AddProperty
{
    
    public AddDatatypeProperty(String URI) {
        super(URI);
    }
    
    
    public AddDataTypeProperty(String URI, boolean functional) 
    {
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
    

    @Override
    public void execute()
    {
        OntProperty oldProperty = this.originalModel.getDatatypeProperty(URI);
        OntProperty newProperty = this.evolvedModel.getDatatypeProperty(URI);
           
        // tem algum caso em que é preciso apagar ?? ou não copiar ??
        
        if(newProperty == null) //propriedade nao existe, é preciso copiar
        {
            if(oldProperty == null) // propriedade totalmente Nova
            {
                newProperty = this.evolvedModel.createDatatypeProperty(URI);
            }
            else // copiar a que existe
            {
                newProperty = Utils.OntologyUtils.copyProperty(evolvedModel, oldProperty);
            }
        }
        
        if(this.functional)
            newProperty = newProperty.convertToTransitiveProperty();
    
        for(String domain : this.domains)
        {
            Resource resource = this.evolvedModel.getResource(domain);
            if(resource!=null)
                newProperty.addDomain(resource);
            else // domain não existe, tem de ser inventado
            {
            
            }
        }
        
        
        for(String range : this.ranges)
        {
            Resource resource = this.evolvedModel.getResource(range);
            if(resource!=null)
                newProperty.addDomain(resource);
            else // domain não existe, tem de ser inventado
            {
            
            }
        }
    }
    
    
}
