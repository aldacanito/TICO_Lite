/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Alda
 */
public class AddClass implements IAddClass
{
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    private String URI;
    private OntClass newClass;
    private OntClass oldClass;
    
    private HashMap<OntClass, String> restrictions;
    
    private List<OntClass> disjoinWith;
    
    public AddClass(OntClass oldClass)
    {
        this.oldClass = oldClass;
        this.URI      = oldClass.getURI();
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
    public String toString()
    {
        String s = "> ADD CLASS : EVOLUTIONARY ACTION";
    
        if(this.URI!=null)
            s += "\n\t\t\t URI: " + this.getURI();
    
        if(this.restrictions!=null)
        {
            s+= "\n\t\t\t Restriction Count:" + this.restrictions.size();
            s+= "\n\t\t\t Restriction List:" ;
            
            Set<OntClass> keySet = this.restrictions.keySet();
            
            for(OntClass cls : keySet)
            {
                String rt = this.restrictions.get(cls);
                String rs = OntologyUtils.printRestriction(cls);
                
                s+= "\n\t\t\t\t Restriction Type: " + rt 
                        + "\n\t\t\t\t Details: " + rs;
            }
        }
        return s;
    }
    
    @Override
    public String getURI() 
    {
        return this.URI;
    }

    @Override
    public OntModel getEvolvedModel() {
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
        
        // TODO GARANTIR QUE NAO EXECUTA DUAS VEZES
        
        if(this.originalModel==null)
            Utils.Utilities.logError("Original Model is not instantiated", "ADDCLASS : EXECUTE");
        
        if(this.oldClass==null)
            this.oldClass = this.originalModel.getOntClass(URI);
        
        boolean create = false;
        if(this.evolvedModel.getOntClass(URI) != null) // class does not exist in the evolved model
            create = true;
        
        OntClass theSlice = null;
        if(create) // preciso instanciar nova classe
        {   
            // cria a nova classe no novo modelo com o URI (nao existe antes)
            
            this.newClass = this.evolvedModel.createClass(URI);
            
            // cria os time slices
            
            TimeSliceCreator timeSlicer = new TimeSliceCreator(this.newClass);
            timeSlicer.setUp(originalModel, evolvedModel);
            timeSlicer.execute();
            
            theSlice = timeSlicer.getSlice();
            
            // copia restriçoes da classe original para o slice
            if(this.restrictions!=null && theSlice!=null)
            {
                for(OntClass restriction : restrictions.keySet())
                {
                    String restrictionType = restrictions.get(restriction);   
                    Utils.OntologyUtils.copyRestriction(restriction, theSlice, restrictionType);
                }
            }
        }
        else // copia o que já existe
        {
           
        }
        
        //adicionar restrições
        // fazer esta parte sem ser copia!!!

       
        
        this.addDisjoints();
        
    }
    
    public void addDisjoints()
    {
        if(this.disjoinWith!=null)
        {
            for(OntClass disjoint : this.disjoinWith)
            {
                this.newClass.addDisjointWith(disjoint);
            }
        }
    }
    
}
