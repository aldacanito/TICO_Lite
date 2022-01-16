/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    
    
    @Override
    public String toString()
    {
        String print = "Property Metrics for " + this.URI;
        print += "\n\t Count: " + this.count;
        
        if(!ranges.isEmpty())
        {
            print += "\n\t Ranges:";
            Set<String> keys = ranges.keySet();
            for(String key : keys)
            {
                Integer value = ranges.get(key);
                print += key + ": " + value;
            }    
        }
        
        if(!domains.isEmpty())
        {
            print += "\n\t Domains:";
            Set<String> keys = domains.keySet();
            for(String key : keys)
            {
                Integer value = domains.get(key);
                print += key + ": " + value;
            }    
        }
        
        return print;
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
