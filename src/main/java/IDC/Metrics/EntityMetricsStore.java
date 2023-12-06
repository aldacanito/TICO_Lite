/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Metrics;

import Utils.Configs;
import Utils.Utilities;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.ontology.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shizamura
 */
public class EntityMetricsStore 
{
    private static EntityMetricsStore theInstance = new EntityMetricsStore();
    private List<ClassPropertyMetrics> theMetrics;
    private List<IndividualMetrics> individualMetrics;

    private List<PropertyMetrics> propertyMetrics;

    private List<Triple<String, String, String>> seenTriples;



    private EntityMetricsStore()
    {
        theMetrics        = new ArrayList<ClassPropertyMetrics>();
        individualMetrics = new ArrayList<>();
        propertyMetrics   = new ArrayList<>();
        seenTriples       = new ArrayList<>();
    }

    public static EntityMetricsStore getStore()
    {
        return theInstance;
    }

    public void addClassPropertyMetrics(ClassPropertyMetrics cpm)
    {
        if(!theMetrics.contains(cpm))
            theMetrics.add(cpm);
    }


    public List<Triple<String,String,String>> getAllSeenTriples()
    {
        return this.seenTriples;
    }


    public List<Triple<String, String, String>> getSeenTriplesOfProperty(String propertyURI)
    {
        List<Triple<String, String, String>> triplesOf = new ArrayList<>();
        for(Triple<String, String, String> triple : this.seenTriples)
        {
            if(triple.getMiddle().equalsIgnoreCase(propertyURI))
                triplesOf.add(triple);

        }

        return triplesOf;
    }

    public void addTriple(String domain, String prop, String range)
    {
        Triple<String, String, String> t = Triple.of(domain, prop, range);
        this.addTriple(t);
    }

    /**
     * ensure you only keep Configs.windowSize many triples with the same property.
     * @param t
     */
    public void addTriple(Triple<String, String, String> t)
    {
        List<Triple<String,String,String>> similar_triples = this.getSeenTriplesOfProperty(t.getMiddle());

        if (similar_triples.size() == Configs.windowSize)
        {
            this.seenTriples.remove(similar_triples.get(0));
        }

        this.seenTriples.add(t);
    }


    public void addPropertMetrics(PropertyMetrics pm)
    {
        this.propertyMetrics.add(pm);
    }

    public List<PropertyMetrics> getPropertyMetrics()
    {
        return this.propertyMetrics;
    }

    public List<IndividualMetrics> getIndividualMetrics()
    {
        return this.individualMetrics;
    }

    public PropertyMetrics getPropertyMetricsByURI(String URI)
    {
        for(PropertyMetrics pm : this.propertyMetrics)
            if(pm.getURI().equalsIgnoreCase(URI))
                return pm;

        return null;
    }


    public IndividualMetrics getMetricsForIndividual(Individual i)
    {
        for(IndividualMetrics im : individualMetrics)
            if(im.getIndividual().getURI().equalsIgnoreCase(i.getURI()))
                return im;

        return null;
    }

    public IndividualMetrics addIndividualMetrics(Individual i)
    {
        IndividualMetrics im = new IndividualMetrics(i);
        this.addIndividualMetrics(im);

        return im;
    }


    public void addIndividualMetrics(IndividualMetrics im)
    {
        if(individualMetrics != null)
        {
            boolean contains = false;
            for(IndividualMetrics im1 : this.individualMetrics)
                if(im1.getIndividual().getURI().equalsIgnoreCase(im.getIndividual().getURI()))
                {    contains = true; break; }

            if(!contains)
                this.individualMetrics.add(im);
        }
    }


    public PropertyMetrics getMetricsByPropertyURI(String propURI)
    {
        PropertyMetrics met = null;

        for(PropertyMetrics pm: this.propertyMetrics)
        {
            if(pm.getURI().equalsIgnoreCase(propURI))
            {
                met = pm; break;
            }
        }

        if(met == null)
        {
            met = new PropertyMetrics(propURI);
            this.propertyMetrics.add(met);
        }

        return met;
    }

    
    public ClassPropertyMetrics getMetricsByClassURI(String classURI)
    {
        ClassPropertyMetrics ret = null;

        for(ClassPropertyMetrics cpm : this.theMetrics)
        {
            if(cpm.getURI().equalsIgnoreCase(classURI))
            {
               ret = cpm; break;
            }
        }

        if(ret == null)
            ret = new ClassPropertyMetrics(classURI);

        //Utilities.logInfo("Class Metrics for URI " + classURI + " are not in the Store.");
        return ret;
    }
    
}
