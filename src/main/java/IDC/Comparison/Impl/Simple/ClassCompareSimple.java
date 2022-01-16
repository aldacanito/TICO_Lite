/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Simple;

import IDC.Comparison.Interfaces.IClassCompare;
import IDC.EvolActions.Factories.EvolutionaryActionFactory;
import IDC.EvolActions.Interfaces.EvolutionaryAction;
import Utils.Utilities;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class ClassCompareSimple implements IClassCompare
{
    protected OntClass ontClass;
    protected OntModel ontModel;
    
    public ClassCompareSimple(OntClass ontClass, OntModel ontModel)
    {
        this.ontClass = ontClass;
        this.ontModel = ontModel;
    }
    
    @Override
    public EvolutionaryAction compare() 
    {
        ExtendedIterator classes = ontModel.listClasses();   
        Utilities.logInfo("COMPARING ONTCLASS " + ontClass.getURI() + " to existing classes...");
        
        boolean found = false;
        
        while(classes.hasNext())
        {
            OntClass thisClass = (OntClass) classes.next();
             
            String current_URI = thisClass.getURI();
            String compare_URI = ontClass.getURI();
                     
            Utilities.logInfo("\tCOMPARING: " + current_URI + " to " + compare_URI);
        
            // MAIS TARDE COMPARAR COM OUTRAS COISAS QUE NAO O URI

            if(current_URI==null) continue;
        
            if(current_URI.equalsIgnoreCase(compare_URI))
            {
                found = true;
                Utilities.logInfo("\tClass already on model. Skipping...");
                break;
            }
        }
        
        if(!found)    
        {
            Utilities.logInfo("\tClass not on model. Creating AddClass Action!");
            EvolutionaryAction action = EvolutionaryActionFactory.getInstance().createAddClassAction(ontClass);
            return action;
        }

        
        Utilities.logInfo("Class not found. No Evolutionary Action created.");
        return null;
    }
}
