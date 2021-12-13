/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author shizamura
 */
public class EvolutionaryActionComposite implements EvolutionaryAction
{
    private OntModel originalModel;
    private OntModel evolvedModel;
    private String ontClassURI;
    List<EvolutionaryAction> actions;
    OntModel evolvingModel; 
    
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
    
    public void add(EvolutionaryAction action)
    {
        if(action==null)
            return;
        
        boolean repeated = false;
        for(EvolutionaryAction act : this.actions)
        {
            if(act.getURI()==null) continue;
            
            if(action.getURI().equalsIgnoreCase(act.getURI()))
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
            
            actions.add(action);
        }
    }

    @Override
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

    @Override
    public String getURI() 
    {
        return this.ontClassURI;
    }

    @Override
    public void setUp(OntModel originalModel, OntModel evolvedModel) 
    {
        this.originalModel = originalModel;
        this.evolvedModel  = evolvedModel;
    }

    @Override
    public void execute() 
    {
        this.execute(originalModel, evolvedModel);
    }
    
}
