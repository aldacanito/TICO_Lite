/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.ModelManager;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Alda
 */
public class AddDatatypeProperty extends AddProperty
{
    
    public AddDatatypeProperty(String URI) {
        super(URI);
    }
    
    public AddDatatypeProperty(String URI, boolean functional) 
    {
        super(URI, functional);
    }
   
    

    @Override
    public void execute()
    {
        Utils.Utilities.logInfo("Executing Evolutionary Action Add Datatype Property for Property with URI " + this.getURI());
   
        DatatypeProperty dtp = Utils.OntologyUtils.getDatatypePropertyFromModel(ModelManager.getManager().getEvolvingModel(), URI);
        
        if(dtp!=null) // property Exists. modify?
        {}
        else
        {}
        
        OntProperty oldProperty = ModelManager.getManager().getOriginalModel().getDatatypeProperty(URI);
        OntProperty newProperty = ModelManager.getManager().getEvolvingModel().getDatatypeProperty(URI);
           
        // tem algum caso em que é preciso apagar ?? ou não copiar ??
        
        if(newProperty == null) //propriedade nao existe, é preciso copiar
        {
            if(oldProperty == null) // propriedade totalmente Nova
            {
                newProperty = ModelManager.getManager().getEvolvingModel().createDatatypeProperty(URI);
            }
            else // copiar a que existe
            {
                //newProperty = ModelManager.getManager().getEvolvingModel().createDatatypeProperty(URI, false);
                newProperty = Utils.OntologyUtils.copyProperty(ModelManager.getManager().getEvolvingModel(), oldProperty);
            }
        }
        
        if(this.functional)
            newProperty = newProperty.convertToFunctionalProperty();
        else
        {
            newProperty.removeProperty(RDF.type, OWL.FunctionalProperty);
        }
    
        for(String domain : this.domains)
        {
            Resource resource = ModelManager.getManager().getEvolvingModel().getResource(domain);
            if(resource!=null)
                newProperty.addDomain(resource);
            else // domain não existe, tem de ser inventado
            {
            
            }
        }

        for(String range : this.ranges)
        {
            Resource resource = ModelManager.getManager().getEvolvingModel().getResource(range);
            if(resource!=null)
                newProperty.addDomain(resource);
            else // domain não existe, tem de ser inventado
            {
            
            }
        }
    }
    
    
}
