package IDC.Metrics;

import Utils.SPARQLUtils;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.RDFNode;

import java.util.*;

public class IndividualMetrics
{
    Individual individual;
    List<OntClass> ontClasses;
    //USE URI
    private List<PropertyMetrics> propertyMetrics;

    public IndividualMetrics(Individual i)
    {
        this.individual = i;
        this.ontClasses = SPARQLUtils.listOntClassesSPARQL(i);
        this.propertyMetrics = new ArrayList<>();
    }

    public void addPropertyMetric(PropertyMetrics pm)
    {
        this.propertyMetrics.add(pm);
    }

    public List<PropertyMetrics> getAllPropertyMetrics()
    {
        return this.propertyMetrics;
    }

    public PropertyMetrics getPropertyMetricForProperty(String propertyURI)
    {
        for(PropertyMetrics pm : this.getAllPropertyMetrics())
        {
            if(pm.getURI().equalsIgnoreCase(propertyURI))
                return pm;
        }

        return null;
    }


    private void metrifyIndividual(Individual i)
    {
        Map<String, RDFNode> properties = SPARQLUtils.listPropertiesSPARQL(i);

        for(String property_uri : properties.keySet())
        {
            RDFNode object = properties.get(property_uri);

            PropertyMetrics pm = new PropertyMetrics(property_uri);

            // todo finish metrifying

        }

    }



}
