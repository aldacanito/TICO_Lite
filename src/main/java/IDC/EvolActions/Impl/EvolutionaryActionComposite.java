/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl;

import IDC.EvolActions.Impl.Additions.AddClass;
import IDC.EvolActions.Impl.Additions.AddDatatypeProperty;
import IDC.EvolActions.Impl.Additions.AddObjectProperty;
import IDC.EvolActions.Impl.Additions.AddProperty;
import IDC.EvolActions.Impl.Additions.Restriction.AddAllValuesFromRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddCardinalityRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddSomeValuesFromRestriction;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import IDC.Metrics.ExecutionHistory;
import org.apache.jena.ontology.AllValuesFromRestriction;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.*;

/**
 *
 * @author shizamura
 */
public class EvolutionaryActionComposite implements EvolutionaryAction
{
    private String ontClassURI;
    private List<EvolutionaryAction> actions;
       
    @Override
    public String toString()
    {
        String evolutionaryOutputs = "List of Evolutionary Actions:\n";
        String toPrint = "\n\n\n======================================";
        
        toPrint += "\nEvolutionary Action Composite Stats\n";
        
        int count = 0;
        for(EvolutionaryAction action : actions)
        {
            if(action!=null)
            {
                count++;
                evolutionaryOutputs+= "\t" + action.toString() + "\n";
            }
        }   
        
        toPrint +="\tTotal evolutionary actions executed: " + count + "\n";
        toPrint += evolutionaryOutputs;
        toPrint += "======================================\n\n\n";
        return toPrint;
    }
    
    public EvolutionaryActionComposite()
    {
        actions = new ArrayList<>();
    }
    
    public boolean add(EvolutionaryAction action)
    {
        if(action==null || action.getURI()==null)
            return false;
        
        boolean repeated = false, added = false;

        if(action instanceof EvolutionaryActionComposite)
        {
            boolean addedAll = true;
            EvolutionaryActionComposite composite = (EvolutionaryActionComposite) action;

            List<EvolutionaryAction> toAdd = new ArrayList<>();
            for(EvolutionaryAction act : composite.getActions())
            {
                boolean repeated2 = false;
                for(EvolutionaryAction old_act : actions)
                    if(compare(act, old_act))
                        repeated2 = true;

                if(!repeated2)
                    actions.add(act);

                addedAll = addedAll & !repeated2;

            }

            return addedAll;
        }

        for(EvolutionaryAction act : this.actions)
        {
            if(act.getURI()==null) continue;

            if(contains(act, action))
            {
                repeated = true;
                break;
            }
        }

        if(!repeated)
        {
            if(this.getURI()==null || this.getURI().isEmpty())
                this.ontClassURI = action.getURI();
            else
                this.ontClassURI += " | " + action.getURI();

            return actions.add(action);
        }

        return added;
    }

    public boolean contains(EvolutionaryAction a, EvolutionaryAction b)
    {
        if(a instanceof EvolutionaryActionComposite)
            for(EvolutionaryAction action : ((EvolutionaryActionComposite) a).getActions())
                return contains(action, b);
        else //compare
            return compare(a, b);

        return false;
    }

    public static boolean compare(EvolutionaryAction a, EvolutionaryAction b)
    {
        if(a instanceof AddClass && b instanceof AddClass)
        {
            AddClass a1 = (AddClass) a;
            AddClass b1 = (AddClass) b;

            if(a1.getURI().equalsIgnoreCase(b1.getURI()))
                return true;
        }

        if(a instanceof AddObjectProperty && b instanceof AddObjectProperty)
        {
            AddObjectProperty a1 = (AddObjectProperty) a;
            AddObjectProperty b1 = (AddObjectProperty) b;

            boolean equalDisjoints = Utils.Utilities.listEqualsIgnoreOrder(a1.getDisjointWith(), b1.getDisjointWith());
            boolean equalRanges    = Utils.Utilities.listEqualsIgnoreOrder(a1.getRanges(), b1.getRanges());
            boolean equalDomains   = Utils.Utilities.listEqualsIgnoreOrder(a1.getDomains(), b1.getDomains());
            boolean equalSubProp   = Utils.Utilities.listEqualsIgnoreOrder(a1.getSubPropertyOf(), b1.getSubPropertyOf());
            boolean equalSupProp   = Utils.Utilities.listEqualsIgnoreOrder(a1.getSuperPropertyOf(), b1.getSuperPropertyOf());

            if(a1.getURI().equalsIgnoreCase(b1.getURI()))
                if(a1.isFunctional() == b1.isFunctional())
                    return equalDomains && equalDisjoints && equalRanges && equalSubProp && equalSupProp;
        }

        if(a instanceof AddDatatypeProperty && b instanceof AddDatatypeProperty)
        {
            AddDatatypeProperty a1 = (AddDatatypeProperty) a;
            AddDatatypeProperty b1 = (AddDatatypeProperty) b;

            boolean equalDisjoints = Utils.Utilities.listEqualsIgnoreOrder(a1.getDisjointWith(), b1.getDisjointWith());
            boolean equalRanges    = Utils.Utilities.listEqualsIgnoreOrder(a1.getRanges(), b1.getRanges());
            boolean equalDomains   = Utils.Utilities.listEqualsIgnoreOrder(a1.getDomains(), b1.getDomains());
            boolean equalSubProp   = Utils.Utilities.listEqualsIgnoreOrder(a1.getSubPropertyOf(), b1.getSubPropertyOf());
            boolean equalSupProp   = Utils.Utilities.listEqualsIgnoreOrder(a1.getSuperPropertyOf(), b1.getSuperPropertyOf());

            if(a1.getURI().equalsIgnoreCase(b1.getURI()))
                if(a1.isFunctional() == b1.isFunctional())
                    return equalDomains && equalDisjoints && equalRanges && equalSubProp && equalSupProp;
        }

        if(a instanceof AddAllValuesFromRestriction && b instanceof AddAllValuesFromRestriction)
        {
            AddAllValuesFromRestriction a1 = (AddAllValuesFromRestriction) a;
            AddAllValuesFromRestriction b1 = (AddAllValuesFromRestriction) b;

            if(a1.getRangeClass() == b1.getRangeClass())
            {
                if(a1.getRangeClass() == b1.getRangeClass())
                {
                    if (a1.getRangeClass()!=null)
                    {
                        if (a1.getRangeClass().getURI().equalsIgnoreCase(b1.getRangeClass().getURI()))
                            if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                                if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                                    return true;
                    } else if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                        if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                            return true;
                }
            }
        }

        if(a instanceof AddSomeValuesFromRestriction && b instanceof AddSomeValuesFromRestriction)
        {
            AddSomeValuesFromRestriction a1 = (AddSomeValuesFromRestriction) a;
            AddSomeValuesFromRestriction b1 = (AddSomeValuesFromRestriction) b;

            if(a1.getRangeClass() == b1.getRangeClass())
            {
                if (a1.getRangeClass()!=null)
                {
                    if (a1.getRangeClass().getURI().equalsIgnoreCase(b1.getRangeClass().getURI()))
                        if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                            if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                                return true;
                } else if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                    if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                        return true;
            }
        }

        if(a instanceof AddCardinalityRestriction && b instanceof AddCardinalityRestriction)
        {
            AddCardinalityRestriction a1 = (AddCardinalityRestriction) a;
            AddCardinalityRestriction b1 = (AddCardinalityRestriction) b;

            if(a1.getRangeClass() == b1.getRangeClass())
            {
                if(a1.getRangeClass() == null)
                {
                    if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                        if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                            if (a1.getCardinality() == b1.getCardinality())
                                return true;
                }
                else if (a1.getRangeClass().getURI().equalsIgnoreCase(b1.getRangeClass().getURI()))
                {
                    if (a1.getURI().equalsIgnoreCase(b1.getURI()))
                        if (a1.onProperty().getURI().equalsIgnoreCase(b1.onProperty().getURI()))
                            if (a1.getCardinality() == b1.getCardinality())
                                return true;
                }
            }
        }

        return false;
    }

    
    @Override
    public void execute() 
    {
        for(EvolutionaryAction action : actions)
        {
            try {
                action.execute();
            }catch(Exception e)
            {
                System.out.println("Error executing Evolutionary Action. Reason: " + e.getMessage());
            }
        }
    }

    @Override
    public String getURI() 
    {
        return this.ontClassURI;
    }

      public List<EvolutionaryAction> getActions()
      {
          return this.actions;
      }

    
    
}
