package IDC.EvolActions.Impl.Additions;

import IDC.EvolActions.Interfaces.IAddTimeSlices;
import IDC.ModelManager;
import Utils.OntologyUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;

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

    private OntProperty isSliceOfP, beforeP, afterP, duringP;
   
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");  
    
    
    public TimeSliceCreator(OntClass toExpand)
    {
        this.theTimeModel = ModelManager.getManager().getTemporal_instancesModel();         
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        
        String parts []= this.URI.split("#");
        this.sliceName = parts[0] + "#TS__" + parts[1] + "__0";
       
        this.start = LocalDateTime.now();
        this.end   = null;
        
        theModel = toExpand.getOntModel();
    }
    
    public TimeSliceCreator(OntClass toExpand, int version)
    {
        this.theTimeModel = ModelManager.getManager().getTemporal_instancesModel();
         
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        
        String parts []= this.URI.split("#");
        this.sliceName = parts[0] + "#TS__" + parts[1] + "__" + version;
       
        this.start = LocalDateTime.now();
        this.end   = null;
        
        this.theModel = ModelManager.getManager().getEvolvingModel();
    }
    
    
    public TimeSliceCreator(OntClass toExpand, Individual start, Individual end, String sliceName)
    {
        this.theTimeModel = ModelManager.getManager().getTemporal_instancesModel();
         
        this.sliceName = sliceName;
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        this.ind_start = start;
        this.ind_end   = end;
        
        this.start = null;
        this.end   = null;
        
        theModel = toExpand.getOntModel();
    }
    
    public TimeSliceCreator(OntClass toExpand, LocalDateTime start, LocalDateTime end, String sliceName)
    {   
        this.theTimeModel = ModelManager.getManager().getTemporal_instancesModel();
         
        this.sliceName = sliceName;
        this.toExpand  = toExpand;
        this.URI       = toExpand.getURI();
        this.start     = start;
        this.end       = end;
        
        theModel = toExpand.getOntModel();
    }
    

    public void execute()
    {
        setupPropertiesOnModel();

        if(this.ind_start!=null)
            this.createRestrictionsWithIndividuals();
        else
            this.createRestrictionsWithoutIndividuals();
    }
    
    
    public void createRestrictionsWithoutIndividuals()
    {
        OntClass ontSlice = theModel.createClass(sliceName);
        
        getTimeModel();
        
        //criar intervalo
        
        Individual interval = createInterval(this.theModel);
        Individual ind_copy, ind_start_copy, ind_end_copy;
        
        ind_copy       = createInterval(this.getTimeModel());
        ind_start_copy = createStartIndividual(this.getTimeModel(), ind_copy);
        
        //adicionar hasBeginning e hasEnd ao intervalo
        
        if(start != null)
            ind_start = createStartIndividual(theModel, interval);

        RDFList listr = theModel.createList(toExpand);//evolvedModel.createList();
        listr.add(toExpand);
        
        if(end != null)
        {
            
            ind_end = this.createEndIndividual(theModel, interval);
            HasValueRestriction beforeRestriction = theModel.createHasValueRestriction(null, this.beforeP, ind_end);
            listr.add(beforeRestriction);
            
            ind_end_copy = this.createEndIndividual(this.getTimeModel(), ind_copy);
        }
        
        // temporal slice -> during interval
       
        HasValueRestriction duringRestriction = theModel.createHasValueRestriction(null, this.duringP, interval);
        ontSlice.addSuperClass(duringRestriction);
        
        // originalClass -> hasTemporalSlice

//        toExpand.addSuperClass(theModel.createSomeValuesFromRestriction(null, hasSliceP, ontSlice));
             
        ontSlice.addSuperClass(theModel.createSomeValuesFromRestriction(null, this.isSliceOfP, toExpand));
        
        // after ind_start, before ind_end
        HasValueRestriction afterRestriction  = theModel.createHasValueRestriction(null, this.afterP, ind_start);
        listr.add(afterRestriction);
        
        if(!listr.isEmpty())
        {
            IntersectionClass intersectionEQ = theModel.createIntersectionClass(null, listr);
            ontSlice.addEquivalentClass(intersectionEQ);
        }
        
        addLabel(ontSlice);
        
        theSlice = ontSlice;
        
    }
    

    private void setupPropertiesOnModel()
    {
        this.beforeP = this.theModel.getOntProperty(OntologyUtils.BEFORE_P);
        if(beforeP == null)
            this.beforeP = this.theModel.createObjectProperty(OntologyUtils.BEFORE_P, false);

        this.afterP = this.theModel.getOntProperty(OntologyUtils.AFTER_P);
        if(afterP == null)
            this.afterP = this.theModel.createObjectProperty(OntologyUtils.AFTER_P, false);

        this.duringP = this.theModel.getOntProperty(OntologyUtils.DURING_P);
        if(duringP == null)
            this.duringP = this.theModel.createObjectProperty(OntologyUtils.DURING_P, false);

        this.isSliceOfP = this.theModel.getOntProperty(OntologyUtils.IS_SLICE_OF_P);
        if(isSliceOfP == null)
            this.isSliceOfP = this.theModel.createObjectProperty(OntologyUtils.IS_SLICE_OF_P, false);

    }
     
     
    private void createRestrictionsWithIndividuals()
    {
        OntClass ontSlice = theModel.createClass(sliceName);
        
        RDFList listr = theModel.createList(toExpand);
        listr.add(toExpand);
        
        Individual interval = createInterval(this.theModel);

        this.addEndIndividual(theModel, interval, ind_end);
        this.addStartIndividual(theModel, interval, ind_start);

        if(ind_end != null)
        {
            HasValueRestriction beforeRestriction = theModel.createHasValueRestriction(null, beforeP, ind_end);
            listr.add(beforeRestriction);
        }
        
        HasValueRestriction duringRestriction = theModel.createHasValueRestriction(null, duringP, interval);
        ontSlice.addSuperClass(duringRestriction);
        
 
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
       
    }
     
     
     
     
    private Individual createInterval(OntModel model)
    {
        OntClass intervalClass = model.getOntClass(OntologyUtils.INTERVAL_CLS);
        if(intervalClass == null) intervalClass = model.createClass(OntologyUtils.INTERVAL_CLS);
        
        OntProperty hasTimeStamp = model.getOntProperty(OntologyUtils.HAS_TIMESTAMP_P);
        if(hasTimeStamp == null) hasTimeStamp = model.createObjectProperty(OntologyUtils.HAS_TIMESTAMP_P, false);
        
        Individual interval;
        String intervalName = "Interval_st_";
        
        if(this.ind_start != null)
        {
            
            if(ind_start.hasProperty(hasTimeStamp))
            {
                String timestamp = ind_start.getPropertyValue(hasTimeStamp).asLiteral().getString();
                intervalName += timestamp;
            }
            else
            {
                String individualName = ind_start.getURI().split("#")[1];
                intervalName += individualName;
            
            }
            if(ind_end!=null)
            {
                if(ind_end.hasProperty(hasTimeStamp))
                {
                    String timestamp = ind_end.getPropertyValue(hasTimeStamp).asLiteral().getString();
                    intervalName += timestamp;
                }
                else
                {
                    String individualName = ind_end.getURI().split("#")[1];
                    intervalName += individualName;
                }
                
            }
        }
        else
        {
            intervalName += dtf2.format(start);
            if(end!=null) intervalName += "_" + dtf2.format(end) ;
        }
                
        interval  = model.createIndividual(intervalName,   intervalClass);
        return interval;
    }
    
    private void addStartIndividual(OntModel model, Individual interval, Individual start_ind)
    {
        OntProperty hasBeginningP = model.getOntProperty(OntologyUtils.HAS_BEGINNING_P);
        if(hasBeginningP == null) hasBeginningP = model.createObjectProperty(OntologyUtils.HAS_BEGINNING_P, false);
        
        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null) instantClass = model.createClass(OntologyUtils.INSTANT_CLS);
       
        interval.addProperty(hasBeginningP, start_ind);
    }
    
      private void addEndIndividual(OntModel model, Individual interval, Individual end_ind)
    {
        OntProperty hasEndingP = model.getOntProperty(OntologyUtils.HAS_ENDING_P);
        if(hasEndingP == null) hasEndingP = model.createObjectProperty(OntologyUtils.HAS_ENDING_P, false);
        
        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null) instantClass = model.createClass(OntologyUtils.INSTANT_CLS);
        
        interval.addProperty(hasEndingP,       end_ind);
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

    public OntModel getTimeModel()
    {
        if(this.theTimeModel == null)
            this.theTimeModel = ModelFactory.createOntologyModel();
        
        return this.theTimeModel;
    }
    
}