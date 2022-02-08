
package IDC.Metrics;

import Utils.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.jena.ontology.OntClass;



public class ClassPropertyMetrics extends EntityMetrics
{
    OntClass ontClass;
    int classMentions = 0;

    //USE URI
    HashMap<String, Integer> classObjProperties;
    HashMap<String, Integer> classDtProperties;
    List<String> functionalCandidates;
    
    private List<PropertyMetrics> propertyMetrics;
    
    public ClassPropertyMetrics(String EntityURI)
    {
        super(EntityURI);      
        classObjProperties   = new HashMap<>();
        functionalCandidates = new ArrayList<>();
        classMentions        = 0;
        propertyMetrics      = new ArrayList<>();
    }
    
    public ClassPropertyMetrics(OntClass cls)
    {
        super(cls.getURI());
        ontClass             = cls;        
        classObjProperties   = new HashMap<>();
        classDtProperties    = new HashMap<>();
        functionalCandidates = new ArrayList<>();
        classMentions        = 0;
        propertyMetrics      = new ArrayList<>();
    }
    
    @Override
    public String toString()
    {
        String print = "CLASS METRICS FOR CLASS "  + this.getURI();
        print += "\n\t Mentions: " + this.classMentions;
        
        Set<String> objP = classObjProperties.keySet();
        
        if(!objP.isEmpty())
            print += "\n\t Object Properties:";
        
        for(String key : objP)
        {
            int value = classObjProperties.get(key);
            print += "\n\t\t " + key +  ": " + value;
        }
        
        Set<String> dtP = classDtProperties.keySet();
        if(!objP.isEmpty()) print += "\n\t Datatype Properties:";
        
        for(String key : dtP)
        {
            int value = classDtProperties.get(key);
            print += "\n\t\t " + key +  ": " + value;
        }
        
       
        if(!this.functionalCandidates.isEmpty())
        {
            print += "\n\t Functional Candidates:";
            for(String s : this.functionalCandidates)
                print += "\n\t\t " + s;
        }
        
        for(PropertyMetrics pm : this.propertyMetrics)
            print += pm.toString();
        
    
        return print;
    }
    
    
    public PropertyMetrics getMetricsOfProperty(String propertyURI)
    {
        for(PropertyMetrics pm : this.propertyMetrics)
            if(pm.getURI().equalsIgnoreCase(propertyURI))
                return pm;
    
        return null;
    }
    
    public void updateFunctionalCandidate(String newPropertyURI)
    {
        if(functionalCandidates.contains(newPropertyURI))
            functionalCandidates.remove(newPropertyURI);
        else
            functionalCandidates.add(newPropertyURI);
    }
    
    public void addObjProperty(String newPropertyURI, String rangeURI)
    {
        int count = 1;
        
        if(Utilities.isInIgnoreList(newPropertyURI))
            return;
        
        
        if(classObjProperties.containsKey(newPropertyURI))
            count = (int) classObjProperties.get(newPropertyURI) + 1;
        
        classObjProperties.put(newPropertyURI, count);
        
        boolean containsPM = false;
        for(PropertyMetrics pm : this.getPropertyMetrics())
        {   
            if(pm.getURI().equalsIgnoreCase(newPropertyURI))
            {
                containsPM = true;
                pm.addRange(rangeURI);
                pm.addDomain(this.getURI());
            }
        }
        
        if(!containsPM)
        {   
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomain(this.getURI());
            pm.addRange(rangeURI);
            this.getPropertyMetrics().add(pm);
        }
        
    }
    
    
    
    public void addDtProperty(String newPropertyURI, String rangeType)
    {
        int count = 1;
        
        if(Utilities.isInIgnoreList(newPropertyURI))
            return;
        
        if(classDtProperties.containsKey(newPropertyURI))
            count = (int) classDtProperties.get(newPropertyURI) + 1;
        
        classDtProperties.put(newPropertyURI, count);
        
        boolean containsPM = false;
        for(PropertyMetrics pm : this.getPropertyMetrics())
        {   
            if(pm.getURI().equalsIgnoreCase(newPropertyURI))
            {
                containsPM = true;
                pm.addRange(rangeType);
                pm.addDomain(this.getURI());
            }
        }
        
        if(!containsPM)
        {   
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomain(this.getURI());
            pm.addRange(rangeType);
            this.getPropertyMetrics().add(pm);
        }
    }
    
    public void addClassMention()
    {
        classMentions++;
    }
    
    public float getPropertyRatio(String propertyURI)
    {
        int propertyMentions = classObjProperties.get(propertyURI);
                
        if(propertyMentions!=0 && classMentions!=0)
            return (float) propertyMentions / (float) classMentions;
        else
            return 0;
    }
    
     public HashMap<String, Integer> getClassDtProperties()
    {
        return this.classDtProperties;
    }
    
     
    public HashMap<String, Integer> getClassObjProperties()
    {
        return this.classObjProperties;
    }
    
    
    public List<String> getFunctionalCandidates()
    {
        return this.functionalCandidates;
    }
    
    public int getClassMentions()
    {
        return this.classMentions;
    }
    
    public OntClass getOntClass()
    {
        return this.ontClass;
    }

    /**
     * @return the propertyMetrics
     */
    public List<PropertyMetrics> getPropertyMetrics() {
        return propertyMetrics;
    }
    
}
