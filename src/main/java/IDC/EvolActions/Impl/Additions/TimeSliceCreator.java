/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.IAddTimeSlices;
import Utils.OntologyUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author shizamura
 */
public class TimeSliceCreator implements IAddTimeSlices
{

    private OntModel originalModel;
    private OntModel evolvedModel;
    private final OntClass toExpand;
    private OntClass theSlice;
    
    private final LocalDateTime start, end;
    private final String URI, sliceName;
    
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");  
    
    public TimeSliceCreator(OntClass toExpand)
    {
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        
        String parts []= this.URI.split("#");
        this.sliceName = parts[0] + "#TS__" + parts[1] + "__0";
       
        this.start = LocalDateTime.now();
        this.end   = null;
    }
    
    
    public TimeSliceCreator(OntClass toExpand, LocalDateTime start, LocalDateTime end, String sliceName)
    {   
        this.sliceName = sliceName;
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        this.start     = start;
        this.end       = end;
    }
    
    @Override
    public void execute()
    {
        // criar o time slice
        
        OntClass ontSlice = evolvedModel.createClass(sliceName);
        
        //criar intervalo
    
        String intervalName = "Interval_st_" + dtf2.format(start);
        
        if(end!=null) intervalName += "_" + dtf2.format(end) ;
            
        OntClass intervalClass = this.evolvedModel.getOntClass(OntologyUtils.INTERVAL_CLS);
        if(intervalClass == null) intervalClass = this.evolvedModel.createClass(OntologyUtils.INTERVAL_CLS);
        
        Individual interval  = this.evolvedModel.createIndividual(intervalName,   intervalClass);
       
        //adicionar hasBeginning e hasEnd ao intervalo
        
        OntProperty hasEndP = this.evolvedModel.getOntProperty(OntologyUtils.HAS_ENDING_P);
        if(hasEndP == null) hasEndP = this.evolvedModel.createObjectProperty(OntologyUtils.HAS_ENDING_P, false);
        
        OntProperty hasBeginningP = this.evolvedModel.getOntProperty(OntologyUtils.HAS_ENDING_P);
        if(hasBeginningP == null) hasBeginningP = this.evolvedModel.createObjectProperty(OntologyUtils.HAS_BEGINNING_P, false);
        
        OntClass instantClass = this.evolvedModel.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null) instantClass = this.evolvedModel.createClass(OntologyUtils.INSTANT_CLS);
       
        Individual ind_start = this.evolvedModel.createIndividual(dtf2.format(start), instantClass);
        interval.addProperty(hasBeginningP, ind_start);
        
        if(end != null)
        {
            Individual ind_end   = this.evolvedModel.createIndividual(dtf2.format(end),   instantClass);
            interval.addProperty(hasEndP,       ind_end);
        }
        
        // temporal slice -> during interval
       
        OntProperty duringP = this.evolvedModel.getOntProperty(OntologyUtils.DURING_P);
        if(duringP == null) duringP = this.evolvedModel.createObjectProperty(OntologyUtils.DURING_P, false);
        
        HasValueRestriction duringRestriction = evolvedModel.createHasValueRestriction(null, duringP, interval);
        ontSlice.addSuperClass(duringRestriction);
        
        // originalClass -> hasTemporalSlice
       
        OntProperty hasSliceP = this.evolvedModel.getOntProperty(OntologyUtils.HAS_SLICE_P);
        if(hasSliceP == null) hasSliceP = this.evolvedModel.createObjectProperty(OntologyUtils.HAS_SLICE_P, false);
        
        HasValueRestriction sliceRestriction = evolvedModel.createHasValueRestriction(null, hasSliceP, ontSlice);
        toExpand.addSuperClass(sliceRestriction);
        
        theSlice = ontSlice;
        
    }
    
    public OntClass getSlice()
    {
        return theSlice;
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


    



    
}
