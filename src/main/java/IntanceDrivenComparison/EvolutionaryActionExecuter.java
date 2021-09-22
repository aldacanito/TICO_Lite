/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author shizamura
 */
public class EvolutionaryActionExecuter 
{
    List<EvolutionaryAction> actions;
    OntModel evolvingModel; 
    
    public String toString()
    {
        String evolutionaryOutputs = "List of Evolutionary Actions:\n";
        String toPrint = "\n\n\n======================================";
        
        toPrint += "\nEvolutionary Action Executer Stats\n";
        
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
    
    public EvolutionaryActionExecuter()
    {
        actions = new ArrayList<EvolutionaryAction>();
    }
    
    public void add(EvolutionaryAction action)
    {
        if(action==null)
            return;
        
        boolean repeated = false;
        for(EvolutionaryAction act : this.actions)
        {
            if(action.getURI().equalsIgnoreCase(act.getURI()))
            {    
                repeated = true;
                break;
            }
        }
            
        if(!repeated)
            actions.add(action);
    }

    public OntModel getEvolvedModel()
    {
        return evolvingModel;
    }
    
    public void execute(OntModel originalModel, OntModel evolvedModel) 
    {
        evolvingModel = evolvedModel;
        //nao uses lambda!!
        for(EvolutionaryAction action : actions)
        {
            //depois vai ser preciso garantir qualquer coisa da ordem da execuçao
            if(action!=null)
            {
                action.setUp(originalModel, evolvingModel);
                action.execute();

                evolvingModel = action.getEvolvedModel();
            }
        }
    }
    
}