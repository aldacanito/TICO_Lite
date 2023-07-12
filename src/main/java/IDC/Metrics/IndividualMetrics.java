package IDC.Metrics;

import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import Utils.Utilities;
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
        this.metrify();
    }

    public Individual getIndividual()
    {
        return this.individual;
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


    private void metrify()
    {
        Map<String, RDFNode> properties = SPARQLUtils.listPropertiesSPARQL(this.individual);

        for(String property_uri : properties.keySet())
        {
            RDFNode object_node     = properties.get(property_uri);
            PropertyMetrics pm = new PropertyMetrics(property_uri);

            pm.addDomains(this.ontClasses);

            if(object_node.isResource())
            {
                if(object_node.canAs(OntClass.class)) // range is a class
                    this.addObjProperty(property_uri, object_node.asResource().getURI());
                else
                {
                    boolean didIt = false;
                    for(OntClass sCls : this.ontClasses)
                    {
                        if(!Utilities.isInIgnoreList(sCls.getURI()))
                        {
                            this.addObjProperty(property_uri, sCls.getURI());
                            didIt = true;
                        }
                    }

                    if(!didIt)
                        this.addObjProperty(property_uri, OntologyUtils.OWL_THING);
                }

            }
            else if(object_node.isLiteral())
                this.addDtProperty(property_uri, object_node.asLiteral().getDatatypeURI());

            // todo finish metrifying

            this.propertyMetrics.add(pm);
        }

    }


    public void addObjProperty(String newPropertyURI, String rangeURI)
    {
        int count = 1;

        if(Utilities.isInIgnoreList(newPropertyURI))
            return;

        boolean containsPM = false;
        for(PropertyMetrics pm : this.getAllPropertyMetrics())
        {
            if(pm.getURI().equalsIgnoreCase(newPropertyURI))
            {
                containsPM = true;
                pm.addRange(rangeURI);
                pm.addDomains(this.ontClasses);
            }
        }

        if(!containsPM)
        {
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomains(this.ontClasses);
            pm.addRange(rangeURI);
            this.getAllPropertyMetrics().add(pm);
        }

    }



    public void addDtProperty(String newPropertyURI, String rangeType)
    {
        int count = 1;

        if(Utilities.isInIgnoreList(newPropertyURI))
            return;

        boolean containsPM = false;
        for(PropertyMetrics pm : this.getAllPropertyMetrics())
        {
            if(pm.getURI().equalsIgnoreCase(newPropertyURI))
            {
                containsPM = true;
                pm.addRange(rangeType);
                pm.addDomains(this.ontClasses);
            }
        }

        if(!containsPM)
        {
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomains(this.ontClasses);
            pm.addRange(rangeType);
            this.getAllPropertyMetrics().add(pm);
        }
    }

}
