/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.EvolutionaryAction;
import java.util.ArrayList;
import java.util.Collections;
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

        Collections.sort(domains);
    }
    
    public void addRange(String rangeURI)
    {
        if(!ranges.contains(rangeURI))
            ranges.add(rangeURI);

        Collections.sort(domains);

    }
    
    public void addDisjointWith(String disjointWithURI)
    {
        if(!disjointWith.contains(disjointWithURI))
            disjointWith.add(disjointWithURI);

        Collections.sort(domains);

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
        if(!subPropertyOf.contains(propertyURI))
            this.subPropertyOf.add(propertyURI);

        Collections.sort(this.subPropertyOf);
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
        if(!subPropertyOf.contains(superProperty))
            this.superPropertyOf.add(superProperty);

        Collections.sort(this.superPropertyOf);
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
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
