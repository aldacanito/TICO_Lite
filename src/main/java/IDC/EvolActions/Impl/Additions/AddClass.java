/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import Utils.Utilities;
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
    private boolean copy = true;
    
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
    
    public void setCopy(boolean copy)
    {  
        this.copy = copy;
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
        if(this.originalModel==null)
            Utils.Utilities.logError("Original Model is not instantiated", "ADDCLASS : EXECUTE");
        
        if(this.oldClass==null)
            this.oldClass = this.originalModel.getOntClass(URI);
        
        if(Utilities.isInIgnoreList(this.oldClass.getURI()))
            return;
        
        OntClass theSlice = null;
        if(this.evolvedModel.getOntClass(URI) == null) // preciso instanciar nova classe
        {   
            this.newClass = this.evolvedModel.createClass(URI);
            
            // cria os time slices
            
            TimeSliceCreator timeSlicer = new TimeSliceCreator(this.newClass);
            timeSlicer.execute();
            
            theSlice = timeSlicer.getSlice();
            
            // copia restriçoes da classe original para o slice
            if(copy)
                Utils.OntologyUtils.copyClassDetails(oldClass, theSlice);
        }
       
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
