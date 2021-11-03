/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Metrics;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shizamura
 */
public class PropertyMetrics
{
    private int count;
    protected String URI;
    private Map<String, Integer> ranges;
    private Map<String, Integer> domains;
    
    public PropertyMetrics(String URI)
    {
        count        = 1;
        this.URI     = URI;
        this.ranges  = new HashMap<>();
        this.domains = new HashMap<>();
    }
    
    public void addRange(String rangeURI)
    {
        if(this.getRanges().containsKey(rangeURI))
        {
            int c = this.getRanges().get(rangeURI);
            c++;
            this.getRanges().put(rangeURI, c);
        }
        else
        {
            this.getRanges().put(rangeURI, 1);
        }
       
    }
    
    public void addDomain(String domainURI)
    {
        if(this.getDomains().containsKey(domainURI))
        {
            int c = this.getDomains().get(domainURI);
            c++;
            this.getDomains().put(domainURI, c);
        }
        else
        {
            this.getDomains().put(domainURI, 1);
        }
    }
    
    public void addMention()
    {
        this.count++;
    }
    
    public String getURI()
    {
        return this.URI;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the ranges
     */
    public Map<String, Integer> getRanges() {
        return ranges;
    }

    /**
     * @return the domains
     */
    public Map<String, Integer> getDomains() {
        return domains;
    }
    
    
    
    
}
