
package IDC.EvolActions.Impl.Additions.Restriction;

import IDC.ModelManager;
import Utils.Utilities;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;


import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;

/**
 *
 * @author Alda
 */
public class AddCardinalityRestriction extends AddRestriction
{
    private int      cardinality;
    private String   cardinalityType;
    private boolean  qualified;


    
    public AddCardinalityRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass) 
    {
        super(cls, onProperty, isEquivalent, isSubclass);
        
        setQualified(false);
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
        this.setCardinality(cardinality);
        this.setCardinalityType(type);
        this.setQualified(qualified);
    }
    
    /**
     * Defines the Range Class of a Qualified Restriction
     * @param rangeClass OntClass to act as range.
     */
    public void setQualifiedRestrictionClass(OntClass rangeClass)
    {
        this.setRangeClass(rangeClass);
    }
    
    
    @Override
    public void execute()
    {
        Restriction restriction = null;
        if(!isQualified())
        {
            switch(getCardinalityType())
            {
                case "min" : 
                //create min cardinality;
                    restriction = ModelManager.getManager().getEvolvingModel().createMinCardinalityRestriction(null, this.onProperty, getCardinality());
                    break;

                case "max" : 
                    //create max cardinality;
                    restriction = ModelManager.getManager().getEvolvingModel().createMaxCardinalityRestriction(null, this.onProperty, getCardinality());
                    break;
                default: 
                    // cardinality normal ??? num
                    restriction = ModelManager.getManager().getEvolvingModel().createCardinalityRestriction(null, this.onProperty, getCardinality());
                    break; 
            }
        }
        else
        {
            if(getRangeClass() == null || getRangeClass().getURI() == null || Utilities.isInIgnoreList(getRangeClass().getURI()))
                return;


            try {
                Individual i = getRangeClass().getOntModel().getIndividual(getRangeClass().getURI());
                return;
            } catch (Exception e)
            {
                System.out.println("Range Class is not individual! Proceed.");
            }


            switch(getCardinalityType())
            {
                case "min" : 
                    //create min cardinality;
                    restriction = ModelManager.getManager().getEvolvingModel().createMinCardinalityRestriction(null, this.onProperty, getCardinality());
                    restriction.removeAll( OWL.cardinality );

                    restriction.addLiteral(OWL2.qualifiedCardinality, getCardinality() );
                    restriction.addProperty( OWL2.onClass, getRangeClass() );
                    break;

                case "max" : 
                    //create max cardinality;

                    restriction = ModelManager.getManager().getEvolvingModel().createMaxCardinalityRestriction(null, this.onProperty, getCardinality());
                    restriction.removeAll( OWL.cardinality );

                    restriction.addLiteral(OWL2.qualifiedCardinality, getCardinality() );
                    restriction.addProperty( OWL2.onClass, getRangeClass() );
                    break;

                default: 
                    // cardinality normal ??? num
                    restriction = ModelManager.getManager().getEvolvingModel().createCardinalityRestriction(null, this.onProperty, getCardinality());
                    restriction.removeAll( OWL.cardinality );

                    restriction.addLiteral(OWL2.qualifiedCardinality, getCardinality() );
                    restriction.addProperty( OWL2.onClass, getRangeClass() );

                    break;
            }
        }
        
        if(this.isEquivalent)
            this.ontClass.addEquivalentClass(restriction);
        
        if(this.isSubclass)
            this.ontClass.addSuperClass(restriction);
        
    }


    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public String getCardinalityType() {
        return cardinalityType;
    }

    public void setCardinalityType(String cardinalityType) {
        this.cardinalityType = cardinalityType;
    }

    public boolean isQualified() {
        return qualified;
    }

    public void setQualified(boolean qualified) {
        this.qualified = qualified;
    }


}
