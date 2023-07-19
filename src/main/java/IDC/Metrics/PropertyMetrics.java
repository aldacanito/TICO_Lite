/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Metrics;

import org.apache.jena.ontology.OntClass;

import java.util.HashMap;
import java.util.List;
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

    boolean isSymmetric = false;

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
        String print = "\nProperty Metrics for " + this.URI;
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

    /**
     * Gets the ratio between the amount of times a certain domain was associated with a property
     * and the total amount of times that property has been seen.
     * @param domainURI the URI of the Domain to check for
     * @return float percentage, 0 if not found
     */
    public float getDomainRatio(String domainURI)
    {
        if(this.getCount() == 0 || !this.getDomains().containsKey(domainURI)) return 0;
        return (float) this.getDomains().get(domainURI) / (float) this.getCount();
    }

    /**
     * Gets the ratio between the amount of times a certain range was associated with a property
     * and the total amount of times that property has been seen.
     * @param rangeURI the URI of the range to check for
     * @return float percentage, 0 if not available
     */
    public float getRangeRatio(String rangeURI)
    {
        if(this.getCount() == 0 || !this.getDomains().containsKey(rangeURI)) return 0;
        return (float) this.getDomains().get(rangeURI) / (float) this.getCount();
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

    public void copyRanges(Map<String, Integer> ranges)
    {
        for(String d_uri : ranges.keySet())
        {
            int new_count = ranges.get(d_uri);

            if(this.ranges.containsKey(d_uri))
            {
                int old_count = this.ranges.get(d_uri);
                this.ranges.put(d_uri, new_count + old_count);
            }
            else
            {
                this.ranges.put(d_uri, new_count);
            }
        }
    }
    public void copyDomains(Map<String, Integer> domains)
    {
        for(String d_uri : domains.keySet())
        {
            int new_count = domains.get(d_uri);

            if(this.domains.containsKey(d_uri))
            {
                int old_count = this.domains.get(d_uri);
                this.domains.put(d_uri, new_count + old_count);
            }
            else
            {
                this.domains.put(d_uri, new_count);
            }
        }
    }

    public void addDomain(OntClass domain)
    {
        if(domain.isURIResource())
           this.addDomain(domain.getURI());
    }


    public void addDomains(List<OntClass> domains)
    {
        for (OntClass d : domains)
            this.addDomain(d);
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

    public void setCount(int i)
    {
        this.count = i;
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
    

    public int getDomainCount(String domainURI)
    {
        Map<String, Integer> domains1 = this.getDomains();

        if(domains1.keySet().contains(domainURI))
            return domains1.get(domainURI);

        return 0;
    }

    public int getRangeCount(String rangeURI)
    {
        Map<String, Integer> r = this.getRanges();

        if(r.keySet().contains(rangeURI))
            return r.get(rangeURI);

        return 0;
    }

    
    
}
