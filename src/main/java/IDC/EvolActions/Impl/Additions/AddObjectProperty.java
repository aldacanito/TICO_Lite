/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import static Utils.OntologyUtils.copyProperty;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Alda
 */
public class AddObjectProperty extends AddProperty
{
    private boolean inverseFunctional   = false;
    private boolean transitive          = false;
    private boolean symmetric           = false;
    private boolean asymmetric          = false;
    private boolean reflexive           = false;
    private boolean irreflexive         = false;
    
    private List<OntProperty> inverseOf;

    public AddObjectProperty(String URI) 
    {
        super(URI);
        inverseOf = new ArrayList<>();
    }
    
    public AddObjectProperty(String URI, boolean functional) 
    {
        super(URI, functional);
        inverseOf = new ArrayList<>();
    }
    

    
    @Override
    public void execute()
    {
        OntProperty oldProperty = this.originalModel.getOntProperty(URI);
        OntProperty newProperty = this.evolvedModel.getOntProperty(URI);
           
        // tem algum caso em que é preciso apagar ?? ou não copiar ??
        
        if(newProperty == null) //propriedade nao existe, é preciso copiar
        {
            if(oldProperty == null) // propriedade totalmente Nova
            {
                newProperty = this.evolvedModel.createOntProperty(URI);
            }
            else // copiar a que existe
            {
                newProperty = Utils.OntologyUtils.copyProperty(evolvedModel, oldProperty);
            }
        }
       
        //verificar todas as declarações novas
    
        if(this.functional)
            newProperty = newProperty.convertToTransitiveProperty();
        if(this.inverseFunctional)
            newProperty = newProperty.convertToInverseFunctionalProperty();
        if(this.symmetric)
            newProperty = newProperty.convertToSymmetricProperty();
        if(this.transitive)
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
        
        for(OntProperty p : this.inverseOf)
                newProperty.addInverseOf(p);
        
        if(this.superPropertyOf != null)
        {
            for(String sPO : this.superPropertyOf)
            {
                if(sPO == null || sPO.isEmpty())
                    continue;
                if(this.evolvedModel.getProperty(sPO)!=null)
                {   
                    OntProperty oldSuperProperty = originalModel.getOntProperty(sPO);

                    OntProperty property = evolvedModel.getOntProperty(sPO);
                    if(property==null && oldSuperProperty!=null) // se a propriedade nao existe é preciso criar
                        property = copyProperty(evolvedModel, oldSuperProperty);

                    newProperty.addSuperProperty(property);
                }
            }
        }
        
        if(this.subPropertyOf!=null)
        {
            for(String sBO : this.subPropertyOf)
            {
                if(sBO == null || sBO.isEmpty())
                    continue;
                if(this.evolvedModel.getProperty(sBO)!=null)
                {   
                    OntProperty oldSubProperty = originalModel.getOntProperty(sBO);

                    OntProperty property = evolvedModel.getOntProperty(sBO);
                    if(property==null && oldSubProperty!=null) // se a propriedade nao existe é preciso criar
                        property = copyProperty(evolvedModel, oldSubProperty);

                    newProperty.addSubProperty(property);
                }
            }
        }
    }
    
    
    
    
    
    /**
     * @return the inverseFunctional
     */
    public boolean isInverseFunctional() {
        return inverseFunctional;
    }

    /**
     * @param inverseFunctional the inverseFunctional to set
     */
    public void setInverseFunctional(boolean inverseFunctional) {
        this.inverseFunctional = inverseFunctional;
    }

    /**
     * @return the transitive
     */
    public boolean isTransitive() {
        return transitive;
    }

    /**
     * @param transitive the transitive to set
     */
    public void setTransitive(boolean transitive) {
        this.transitive = transitive;
    }

    /**
     * @return the symmetric
     */
    public boolean isSymmetric() {
        return symmetric;
    }

    /**
     * @param symmetric the symmetric to set
     */
    public void setSymmetric(boolean symmetric) {
        this.symmetric = symmetric;
    }

    /**
     * @return the asymmetric
     */
    public boolean isAsymmetric() {
        return asymmetric;
    }

    /**
     * @param asymmetric the asymmetric to set
     */
    public void setAsymmetric(boolean asymmetric) {
        this.asymmetric = asymmetric;
    }

    /**
     * @return the reflexive
     */
    public boolean isReflexive() {
        return reflexive;
    }

    /**
     * @param reflexive the reflexive to set
     */
    public void setReflexive(boolean reflexive) {
        this.reflexive = reflexive;
    }

    /**
     * @return the irreflexive
     */
    public boolean isIrreflexive() {
        return irreflexive;
    }

    /**
     * @param irreflexive the irreflexive to set
     */
    public void setIrreflexive(boolean irreflexive) {
        this.irreflexive = irreflexive;
    }

    
    
}
