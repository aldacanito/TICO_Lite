/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddProperty;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Alda
 */
public class AddProperty implements EvolutionaryAction
{
    String URI;
    List<String> subPropertyOf;
    List<String> superPropertyOf;
    List<String> domains;
    List<String> ranges;
    List<String> disjointWith;
    
    OntModel originalModel;
    OntModel evolvedModel;
   
    protected boolean functional = false;
    
    //equivalent to?
    
    
    public AddProperty(String URI)
    {
        this.URI     = URI;
        domains      = new ArrayList<>();
        ranges       = new ArrayList<>();
        disjointWith = new ArrayList<>();
    }
    
    public AddProperty(String URI, boolean functional)
    {
        this.URI = URI;
        this.functional = functional;
        
        domains      = new ArrayList<>();
        ranges       = new ArrayList<>();
        disjointWith = new ArrayList<>();
    }
    
    public void setFunctional(boolean functional)
    {
        this.functional = functional;
    }
    
    public boolean isFunctional()
    {
        return this.functional;
    }

    
    public void addDomain(String domainURI)
    {
        if(!domains.contains(domainURI))
            domains.add(domainURI);
    }
    
    public void addRange(String rangeURI)
    {
        if(!ranges.contains(rangeURI))
            ranges.add(rangeURI);
    }
    
    public void addDisjointWith(String disjointWithURI)
    {
        if(!disjointWith.contains(disjointWithURI))
            disjointWith.add(disjointWithURI);
    }
    
    /**
     * @return the URI
     */
    public String getURI() {
        return URI;
    }

    /**
     * @param URI the URI to set
     */
    public void setURI(String URI) {
        this.URI = URI;
    }

    /**
     * @return the subPropertyOf
     */
    public List<String> getSubPropertyOf() {
        return subPropertyOf;
    }

    /**
     * @param subPropertyOf the subPropertyOf to set
     */
    public void setSubPropertyOf(List<String> subPropertyOf) {
        this.subPropertyOf = subPropertyOf;
    }
    
    public void addSubPropertyOf(String propertyURI)
    {
        this.subPropertyOf.add(propertyURI);
    }

    /**
     * @return the superPropertyOf
     */
    public List<String> getSuperPropertyOf() {
        return superPropertyOf;
    }

    /**
     * @param superPropertyOf the superPropertyOf to set
     */
    public void setSuperPropertyOf(List<String> superPropertyOf) {
        this.superPropertyOf = superPropertyOf;
    }

    public void addSuperPropertyOf(String superProperty)
    {
        this.superPropertyOf.add(superProperty);
    }
    
    
    /**
     * @return the domains
     */
    public List<String> getDomains() {
        return domains;
    }

    /**
     * @return the ranges
     */
    public List<String> getRanges() {
        return ranges;
    }

    /**
     * @return the disjointWith
     */
    public List<String> getDisjointWith() {
        return disjointWith;
    }

    @Override
    public OntModel getEvolvedModel() {
        return this.evolvedModel;
    }

    @Override
    public void setUp(OntModel originalModel, OntModel evolvedModel) 
    {
     
        this.evolvedModel = evolvedModel;
        this.originalModel = originalModel;
        
        if(evolvedModel == null)
            this.evolvedModel = this.originalModel;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
