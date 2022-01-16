

package IDC.Metrics;

/**
 *
 * calculate metrics for individual Entities (classes, properties, etc)
 */
public class EntityMetrics 
{
    private String URI;
    private int mentions = 1;
    
    public EntityMetrics(String entityURI)
    {
        this.URI = entityURI;
    }
    
    
    
    /**
     * @return the classURI
     */
    public String getURI() {
        return URI;
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

    
    
    
}
