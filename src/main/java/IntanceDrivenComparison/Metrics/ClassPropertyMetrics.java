
package IntanceDrivenComparison.Metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.ontology.OntClass;



public class ClassPropertyMetrics extends EntityMetrics
{
    OntClass ontClass;
    int classMentions = 0;

    //USE URI
    HashMap<String, Integer> classObjProperties;
    HashMap<String, Integer> classDtProperties;
    List<String> functionalCandidates;
    
    public ClassPropertyMetrics(String EntityURI)
    {
        super(EntityURI);      
        classObjProperties   = new HashMap<>();
        functionalCandidates = new ArrayList<>();
        classMentions        = 0;
    }
    
    public ClassPropertyMetrics(OntClass cls)
    {
        super(cls.getURI());
        ontClass             = cls;        
        classObjProperties        = new HashMap<>();
        functionalCandidates = new ArrayList<>();
        classMentions        = 0;
    }
    
    public void updateFunctionalCandidate(String newPropertyURI)
    {
        if(functionalCandidates.contains(newPropertyURI))
            functionalCandidates.remove(newPropertyURI);
        else
            functionalCandidates.add(newPropertyURI);
    }
    
    public void addObjProperty(String newPropertyURI)
    {
        int count = 1;
        if(classObjProperties.containsKey(newPropertyURI))
            count = (int) classObjProperties.get(newPropertyURI) + 1;
        
        classObjProperties.put(newPropertyURI, count);
    }
    
    
    public void addDtProperty(String newPropertyURI)
    {
        int count = 1;
        if(classDtProperties.containsKey(newPropertyURI))
            count = (int) classDtProperties.get(newPropertyURI) + 1;
        
        classDtProperties.put(newPropertyURI, count);
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
    
}
