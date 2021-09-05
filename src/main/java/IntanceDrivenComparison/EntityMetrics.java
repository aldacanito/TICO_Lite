

package IntanceDrivenComparison;

/**
 *
 * calculate metrics for individual Entities (classes, properties, etc)
 */
public class EntityMetrics 
{
    private String classURI, type;
    private int mentions = 0;
    
    public EntityMetrics(String entityURI, String type)
    {
        this.classURI = entityURI;
        this.type     = type;
    }
    
    
    
    /**
     * @return the classURI
     */
    public String getClassURI() {
        return classURI;
    }

    /**
     * @param classURI the classURI to set
     */
    public void setClassURI(String classURI) {
        this.classURI = classURI;
    }

    
    public void mention()
    {
        this.mentions++;
    }
    
    /**
     * @return the mentions
     */
    public int getMentions() {
        return mentions;
    }

    /**
     * @param mentions the mentions to set
     */
    public void setMentions(int mentions) {
        this.mentions = mentions;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    
    
    
}
