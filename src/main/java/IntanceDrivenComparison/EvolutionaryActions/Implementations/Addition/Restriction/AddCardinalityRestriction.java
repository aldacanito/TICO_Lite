
package IntanceDrivenComparison.EvolutionaryActions.Implementations.Addition.Restriction;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;

/**
 *
 * @author Alda
 */
public class AddCardinalityRestriction extends AddRestriction
{
    int      cardinality;
    String   cardinalityType;
    boolean  qualified;
    OntClass rangeClass;
    
    public AddCardinalityRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass) 
    {
        super(cls, onProperty, isEquivalent, isSubclass);
        qualified = false;
    }
    
    /**
     * Defines details about the Cardinality Restriction
     * 
     * @param type "Min", "Max" or "Exactly", for the type of cardinality to apply
     * @param cardinality The cardinality.
     * @param qualified If this is meant as a qualified cardinality restriction (must invoke setQualifiedRestrictionClass to decide the range afterwards)
     */
    public void setCardinalityType(String type, int cardinality, boolean qualified)
    {
        this.cardinality     = cardinality;
        this.cardinalityType = type;
        this.qualified       = qualified;
    }
    
    /**
     * Defines the Range Class of a Qualified Restriction
     * @param rangeClass OntClass to act as range.
     */
    public void setQualifiedRestrictionClass(OntClass rangeClass)
    {
        this.rangeClass = rangeClass;
    }
    
    
    @Override
    public void execute()
    {
        Restriction restriction = null;
        if(!qualified)
        {
            switch(cardinalityType)
            {
                case "min" : 
                //create min cardinality;
                    restriction = this.getEvolvedModel().createMinCardinalityRestriction(null, this.onProperty, cardinality);
                    break;

                case "max" : 
                    //create max cardinality;
                    restriction = this.getEvolvedModel().createMaxCardinalityRestriction(null, this.onProperty, cardinality);
                    break;
                default: 
                    // cardinality normal ??? num
                    restriction = this.getEvolvedModel().createCardinalityRestriction(null, this.onProperty, cardinality);
                    break; 
            }
        }
        else
        {
            switch(cardinalityType)
            {
                case "min" : 
                    //create min cardinality;
                    restriction = this.getEvolvedModel().createMinCardinalityQRestriction(null, this.onProperty, cardinality, rangeClass);
                    break;
                case "max" : 
                    //create max cardinality;
                    restriction = this.getEvolvedModel().createMaxCardinalityQRestriction(null, this.onProperty, cardinality, rangeClass);
                    break;
                default: 
                    // cardinality normal ??? num
                    restriction = this.getEvolvedModel().createCardinalityQRestriction(null, this.onProperty, cardinality, rangeClass);
                    break; 
            }
        }
        
        if(this.isEquivalent)
            this.ontClass.addEquivalentClass(restriction);
        
        if(this.isSubclass)
            this.ontClass.addSuperClass(restriction);
        
    }
    
    
}
