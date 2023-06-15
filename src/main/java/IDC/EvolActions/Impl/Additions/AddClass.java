/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.IAddClass;
import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Alda
 */
public class AddClass implements IAddClass
{
   
    private String URI;
    private OntClass newClass;
    private OntClass oldClass;
    private boolean copy = true;
    
    private Individual start_ind, end_ind;
    
    private HashMap<OntClass, String> restrictions;
    
    private List<OntClass> disjoinWith;
    
    
    public AddClass(OntClass oldClass, Individual start_ind, Individual end_ind)
    {
        this.oldClass  = oldClass;
        this.URI       = oldClass.getURI();
    
        this.start_ind = start_ind;
        this.end_ind   = end_ind;
    }
    
    
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


    
 
    public void setStartEndInstance(Individual start_ind, Individual end_ind)
    {
        this.end_ind    = end_ind;
        this.start_ind  = start_ind;
        
    }
    
    
    @Override
    public void execute() 
    {        
        if(ModelManager.getManager().getOriginalModel()==null)
            Utils.Utilities.logError("Original Model is not instantiated", "ADDCLASS : EXECUTE");

        if(Utilities.isInIgnoreList(URI))
            return;

        if(this.oldClass==null)
            this.oldClass = ModelManager.getManager().getInstanceModel().getOntClass(URI);
        
        OntClass theSlice = null;
        if(ModelManager.getManager().getEvolvingModel().getOntClass(URI) == null) // preciso instanciar nova classe
        {   
            this.newClass = ModelManager.getManager().getEvolvingModel().createClass(URI);
            OntClass lastOldSlice = OntologyUtils.getLastTimeSlice(newClass);
            int version = OntologyUtils.getSliceNumber(lastOldSlice);
            
            String parts []= this.URI.split("#");
            String sliceName = parts[0] + "#TS__" + parts[1] + "__" + version;
            
            // cria os time slices
            TimeSliceCreator timeSlicer;
            
            if(this.start_ind != null)
                timeSlicer = new TimeSliceCreator(this.newClass, start_ind, end_ind, sliceName);
            else
                timeSlicer = new TimeSliceCreator(this.newClass, version);
            
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
