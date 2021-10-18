/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alda
 */
public class AddProperty 
{
    private String URI;
    private String subPropertyOf;
    private String superPropertyOf;
    private List<String> domains;
    private List<String> ranges;
    private List<String> disjointWith;
    
    boolean functional = false;
    
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
    public String getSubPropertyOf() {
        return subPropertyOf;
    }

    /**
     * @param subPropertyOf the subPropertyOf to set
     */
    public void setSubPropertyOf(String subPropertyOf) {
        this.subPropertyOf = subPropertyOf;
    }

    /**
     * @return the superPropertyOf
     */
    public String getSuperPropertyOf() {
        return superPropertyOf;
    }

    /**
     * @param superPropertyOf the superPropertyOf to set
     */
    public void setSuperPropertyOf(String superPropertyOf) {
        this.superPropertyOf = superPropertyOf;
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
}
