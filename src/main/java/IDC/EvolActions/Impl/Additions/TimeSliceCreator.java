package IDC.EvolActions.Impl.Additions;

import IDC.Comparator;
import IDC.EvolActions.Interfaces.IAddTimeSlices;
import Utils.OntologyUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class TimeSliceCreator implements IAddTimeSlices
{
    private OntModel theModel, theTimeModel;
    private final OntClass toExpand;
    private OntClass theSlice;
    
    private final LocalDateTime start, end;
    private final String URI, sliceName;
    
    private Individual ind_start, ind_end;
   
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");  
    
    
    public TimeSliceCreator(OntClass toExpand)
    {
        this.theTimeModel = Comparator.temporal_instancesModel;
         
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        
        String parts []= this.URI.split("#");
        this.sliceName = parts[0] + "#TS__" + parts[1] + "__0";
       
        this.start = LocalDateTime.now();
        this.end   = null;
        
        theModel = toExpand.getOntModel();

    }
    
    public TimeSliceCreator(OntModel theModel, OntClass toExpand, int version)
    {
        this.theTimeModel = Comparator.temporal_instancesModel;
         
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        
        String parts []= this.URI.split("#");
        this.sliceName = parts[0] + "#TS__" + parts[1] + "__" + version;
       
        this.start = LocalDateTime.now();
        this.end   = null;
        
        this.theModel = theModel;
    }
    
    
    public TimeSliceCreator(OntClass toExpand, LocalDateTime start, LocalDateTime end, String sliceName)
    {   
        this.theTimeModel = Comparator.temporal_instancesModel;
         
        this.sliceName = sliceName;
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        this.start     = start;
        this.end       = end;
        
        theModel = toExpand.getOntModel();
    }
    


     public void execute()
    {
        // criar o time slice
        OntProperty beforeP = this.theModel.getOntProperty(OntologyUtils.BEFORE_P);
        if(beforeP == null) beforeP = this.theModel.createObjectProperty(OntologyUtils.BEFORE_P, false);
        
        OntProperty afterP = this.theModel.getOntProperty(OntologyUtils.AFTER_P);
        if(afterP == null) afterP = this.theModel.createObjectProperty(OntologyUtils.AFTER_P, false);
           
        OntProperty duringP = this.theModel.getOntProperty(OntologyUtils.DURING_P);
        if(duringP == null) duringP = this.theModel.createObjectProperty(OntologyUtils.DURING_P, false);
        
        OntProperty isSliceOfP = this.theModel.getOntProperty(OntologyUtils.IS_SLICE_OF_P);
        if(isSliceOfP == null) isSliceOfP = this.theModel.createObjectProperty(OntologyUtils.IS_SLICE_OF_P, false);
        
        OntProperty hasSliceP = this.theModel.getOntProperty(OntologyUtils.HAS_SLICE_P);
        if(hasSliceP == null) hasSliceP = this.theModel.createObjectProperty(OntologyUtils.HAS_SLICE_P, false);
       
        OntClass ontSlice = theModel.createClass(sliceName);
        
        getTimeModel();
        
        //criar intervalo
        
        Individual interval = createInterval(this.theModel);
        
        Individual ind_copy, ind_start_copy, ind_end_copy;
        ind_copy       = createInterval(this.getTimeModel());
        ind_start_copy = createStartIndividual(this.getTimeModel(), ind_copy);
        
        //adicionar hasBeginning e hasEnd ao intervalo
        
        ind_start = createStartIndividual(this.theModel, interval);
         
        RDFList listr = theModel.createList(toExpand);//evolvedModel.createList();
        listr.add(toExpand);
        
        if(end != null)
        {
            ind_end = this.createEndIndividual(theModel, interval);
            HasValueRestriction beforeRestriction = theModel.createHasValueRestriction(null, beforeP, ind_end);
            listr.add(beforeRestriction);
            
            ind_end_copy = this.createEndIndividual(this.getTimeModel(), ind_copy);
        }
        
        // temporal slice -> during interval
       
        HasValueRestriction duringRestriction = theModel.createHasValueRestriction(null, duringP, interval);
        ontSlice.addSuperClass(duringRestriction);
        
        // originalClass -> hasTemporalSlice

        toExpand.addSuperClass(theModel.createSomeValuesFromRestriction(null, hasSliceP, ontSlice));
             
        ontSlice.addSuperClass(theModel.createSomeValuesFromRestriction(null, isSliceOfP, toExpand));
        
        // after ind_start, before ind_end
        HasValueRestriction afterRestriction  = theModel.createHasValueRestriction(null, afterP, ind_start);  
        listr.add(afterRestriction);
        
        if(!listr.isEmpty())
        {
            IntersectionClass intersectionEQ = theModel.createIntersectionClass(null, listr);
            ontSlice.addEquivalentClass(intersectionEQ);
        }
        
        addLabel(ontSlice);
        
        theSlice = ontSlice;
        
    }
    
    
        private Individual createInterval(OntModel model)
    {
        String intervalName = "Interval_st_" + dtf2.format(start);
        
        if(end!=null) intervalName += "_" + dtf2.format(end) ;
            
        OntClass intervalClass = model.getOntClass(OntologyUtils.INTERVAL_CLS);
        if(intervalClass == null) intervalClass = model.createClass(OntologyUtils.INTERVAL_CLS);
        
        Individual interval  = model.createIndividual(intervalName,   intervalClass);
    
        return interval;
    }
    
    private Individual createStartIndividual(OntModel model, Individual interval)
    {
        OntProperty hasBeginningP = model.getOntProperty(OntologyUtils.HAS_BEGINNING_P);
        if(hasBeginningP == null) hasBeginningP = model.createObjectProperty(OntologyUtils.HAS_BEGINNING_P, false);
        
        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null) instantClass = model.createClass(OntologyUtils.INSTANT_CLS);
       
        Individual individual = model.createIndividual(dtf2.format(start), instantClass);
        interval.addProperty(hasBeginningP, individual);
        
        return individual;
    }
    
    private Individual createEndIndividual(OntModel model, Individual interval)
    {
        OntProperty hasEndingP = model.getOntProperty(OntologyUtils.HAS_ENDING_P);
        if(hasEndingP == null) hasEndingP = model.createObjectProperty(OntologyUtils.HAS_ENDING_P, false);
        
        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null) instantClass = model.createClass(OntologyUtils.INSTANT_CLS);
        
        Individual individual =  model.createIndividual(dtf2.format(end),   instantClass);
        interval.addProperty(hasEndingP,       ind_end);
        
        return individual;
    }
    
    
    private void addLabel(OntClass cls)
    {
        String parts []= this.sliceName.split("#");
        if(parts.length == 2)
            cls.addLabel(parts[1].replace("TS__", "TimeSlice ").replace("__", " "), null);
    }
    
    public OntClass getSlice()
    {
        return theSlice;
    }
    
    public Individual getSliceBeginning()
    {
        return this.ind_start;
    }
    
    
    public String getURI() 
    {
        return this.URI;
    }

    public OntModel getEvolvedModel() {
        return this.theModel;
    }

    public OntModel getTimeModel()
    {
        if(this.theTimeModel == null)
            this.theTimeModel = ModelFactory.createOntologyModel();
        
        return this.theTimeModel;
    }
    
}