package IDC.EvolActions.Impl.Additions.Restriction;

import IDC.ModelManager;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;

/**
 *
 * @author Alda
 */
public class AddSomeValuesFromRestriction extends AddRestriction
{


    public AddSomeValuesFromRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass)
    {
        super(cls, onProperty, isEquivalent, isSubclass);
    }
    
    public AddSomeValuesFromRestriction(OntClass cls, OntProperty onProperty, boolean isEquivalent, boolean isSubclass, OntClass rangeClass) 
    {
        super(cls, onProperty, isEquivalent, isSubclass);
        this.rangeClass = rangeClass;
    }
    



    @Override
    public void execute()
    {
        Restriction restriction = null;
                
        restriction = ModelManager.getManager().getEvolvingModel().createSomeValuesFromRestriction(null, onProperty, this.rangeClass);
        
        if(this.isEquivalent)
            this.ontClass.addEquivalentClass(restriction);
        
        if(this.isSubclass)
            this.ontClass.addSuperClass(restriction);
  
    }
    
    
}