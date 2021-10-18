/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

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

    public AddObjectProperty(String URI) 
    {
        super(URI);
    }
    
    public AddObjectProperty(String URI, boolean functional) 
    {
        super(URI, functional);
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
