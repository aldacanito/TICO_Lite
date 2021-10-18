
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
    HashMap<String, Integer> classProperties;
    List<String> functionalCandidates;
    
    public ClassPropertyMetrics(String EntityURI)
    {
        super(EntityURI);      
        classProperties        = new HashMap<>();
        functionalCandidates = new ArrayList<>();
        classMentions        = 0;
    }
    
    public ClassPropertyMetrics(OntClass cls)
    {
        super(cls.getURI());
        ontClass             = cls;        
        classProperties        = new HashMap<>();
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
    
    public void addProperty(String newPropertyURI)
    {
        int count = 1;
        if(classProperties.containsKey(newPropertyURI))
            count = (int) classProperties.get(newPropertyURI) + 1;
        
        classProperties.put(newPropertyURI, count);
        
        
    }
    
    public void addClassMention()
    {
        classMentions++;
    }
    
    public float getPropertyRatio(String propertyURI)
    {
        int propertyMentions = classProperties.get(propertyURI);
                
        if(propertyMentions!=0 && classMentions!=0)
            return (float) propertyMentions / (float) classMentions;
        else
            return 0;
    }
    
    
    public HashMap<String, Integer> getClassProperties()
    {
        return this.classProperties;
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
