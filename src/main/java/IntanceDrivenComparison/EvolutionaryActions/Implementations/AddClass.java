/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Alda
 */
public class AddClass implements IAddClass
{
    private OntClass newClass;
    private OntClass oldClass;
    
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    
    private HashMap<OntClass, String> restrictions;
    
    private String URI;
    //private String [] superClasses;
    //private String [] subClasses;
    //private String [] disjoinWith;
    
    public AddClass(OntClass oldClass)
    {
        this.oldClass = oldClass;
    }
    
    public AddClass(String URI)
    {  
        this.URI = URI;
    }
    
    public void addRestriction(String restrictionType, OntClass restriction)
    {
        restrictions.put(restriction, restrictionType);
    }
    
    
    @Override
    public String getURI() 
    {
        return this.URI;
    }

    @Override
    public OntModel getEvolvedModel() 
    {
        return this.evolvedModel;
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
        if(this.originalModel==null)
            Utils.Utilities.logError("Original Model is not instantiated", "ADDCLASS : EXECUTE");
        
        if(this.oldClass==null)
            this.oldClass = this.originalModel.getOntClass(URI);
        
        if(this.oldClass==null) // preciso instanciar nova classe
        {    
            this.newClass = this.evolvedModel.createClass(URI);
        }
        else // copia o que já existe
        {
            this.newClass = Utils.OntologyUtils.copyClass(oldClass, evolvedModel);
        }
        
        //adicionar restrições
        
        for(OntClass restriction : restrictions.keySet())
        {
            String restrictionType = restrictions.get(restriction);   
            Utils.OntologyUtils.copyRestriction(restriction, newClass, restrictionType);
        }
        
    }
    
    
    
}
