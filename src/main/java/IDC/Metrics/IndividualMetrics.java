package IDC.Metrics;

import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import Utils.Utilities;
import org.apache.commons.lang3.tuple.Pair;
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


    public boolean isSymmetric(String propertyURI)
    {
        return SPARQLUtils.testSymmetrySPARQL(this.individual, propertyURI);
    }

    private void metrify()
    {
        List<Pair<String, RDFNode>> properties = SPARQLUtils.listPropertiesSPARQL(this.individual, false);

        for(Pair<String, RDFNode> tuple : properties)
        {
            String property_uri     = tuple.getLeft();
            RDFNode object_node     = tuple.getRight();
            PropertyMetrics pm      = new PropertyMetrics(property_uri);

            pm.addDomains(this.ontClasses);
            //this.addPropertyMetric(pm);

            if(object_node.isResource())
            {
                String object_uri = object_node.asResource().getURI();

                OntClass pot = this.individual.getOntModel().getOntClass(object_uri);

                if(pot!=null) // range is a class
                    this.addObjProperty(property_uri, object_node.asResource().getURI());
                else // range is an individual
                {
                    boolean didIt = false;

                    try {

                        Individual i = this.individual.getOntModel().getIndividual(object_uri);
                        List<OntClass> i_classes = SPARQLUtils.listOntClassesSPARQL(i);

                        for (OntClass sCls : i_classes) {
                            if (!Utilities.isInIgnoreList(sCls.getURI())) {
                                this.addObjProperty(property_uri, sCls.getURI());
                                didIt = true;
                                break;
                            }
                        }
                    } catch (Exception e)
                    {
                        System.out.println("Could not convert NODE to Individual.");
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

    private void addPropertyMetric(PropertyMetrics new_pm)
    {
        String pm_uri = new_pm.getURI();
        boolean added = false;

        for(PropertyMetrics pm : this.getAllPropertyMetrics())
        {
            if(pm.getURI().equalsIgnoreCase(pm_uri))
            {
                pm.setCount(pm.getCount() + new_pm.getCount());

                pm.copyDomains(new_pm.getDomains());
                pm.copyRanges (new_pm.getRanges ());

                added = true;
                break;
            }
        }

        if(!added)
        {
            this.propertyMetrics.add(new_pm);
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

                count = pm.getCount();
                count++;
                pm.setCount(count);
            }
        }

        if(!containsPM)
        {
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomains(this.ontClasses);
            pm.addRange(rangeURI);
            pm.setCount(1);
            addPropertyMetric(pm);

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

                count = pm.getCount();
                count++;
                pm.setCount(count);
            }
        }

        if(!containsPM)
        {
            PropertyMetrics pm = new PropertyMetrics(newPropertyURI);
            pm.addDomains(this.ontClasses);
            pm.addRange(rangeType);
            addPropertyMetric(pm);
        }
    }

}
