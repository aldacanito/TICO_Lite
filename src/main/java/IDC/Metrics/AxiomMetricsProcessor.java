package IDC.Metrics;

import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AxiomMetricsProcessor
{

    public static ConstructorMetrics computeFunctionality(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm     = pm.getConstructorMetrics(OntologyUtils.C_FUNCTIONAL);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_FUNCTIONAL);

        Individual i          = im.getIndividual();

        if (!im.hasProperty(propertyURI))
        {
            cm.addNeutral();
            not_cm.addNeutral();
        }
        else
        {
            if (SPARQLUtils.testPositiveFunctionalitySPARQL(i, propertyURI, true))
                cm.addSupport();
            else
                cm.addAgainst();

            if (!SPARQLUtils.testNegativeFunctionalitySPARQL(i, propertyURI, true))
                not_cm.addSupport();
            else
                not_cm.addAgainst();
        }


        /**
         if(!im.hasProperty(propertyURI))
         cm.addNeutral();
         else
         {

         if (pm1.getCount() == 1)
         cm.addSupport();
         else
         if(pm1.getCount() > 1)
         {
         boolean functionalCandidate = true;
         Map<String, List<String>> ranges = pm1.getRanges();
         // <CLASS URI>, List <IND 1, IND 2...>

         // within same class all must be the same value
         for(String classURI1 : ranges.keySet())
         {
         Set<String> range_values_distinct = new HashSet<>(ranges.get(classURI1));

         if(range_values_distinct.size() != 1 && ranges.get(classURI1).size() != 1) // if any is different then it's not candidate
         {
         functionalCandidate = false;
         break;
         }
         else
         {
         for (String classURI2 : ranges.keySet())
         {
         // same URI shows up in different classes
         if (classURI1 != classURI2)
         {
         List<String> range_values1 = ranges.get(classURI1);
         List<String> range_values2 = ranges.get(classURI2);

         Set<String> bigSet = new HashSet<>(range_values1);
         bigSet.addAll(range_values2);

         if(bigSet.size() > 0 && bigSet.size() != 1)
         {
         functionalCandidate = false;
         break;
         }
         }
         }
         }
         if(!functionalCandidate) break;
         }

         if(!functionalCandidate)
         cm.addAgainst();
         else
         cm.addSupport();
         }
         else
         cm.addSupport();
         }
         */
        return cm;
    }
    public static ConstructorMetrics computeInverseFunctionality(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm    = pm.getConstructorMetrics(OntologyUtils.C_INVERSE_FUNCTIONAL);
        ConstructorMetrics not_cm    = pm.getConstructorMetrics(OntologyUtils.NOT_C_INVERSE_FUNCTIONAL);

        // Inverse Functionality: The Range determines the individual

        // IF : if  P(A,B) & P(C,B) -> A=C
        // need to take notice of both ranges and individual uris
        /*
             LIST TUPLE( < URI, PROP, RANGE_VALUE > ) ->
                -> If more one pair only: good
                -> more pairs: must all have same URI for the same value!!!!

         */

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else
        {
            PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

            Map<String, List<String>> ranges = pm1.getRanges();
            Set<String> ranges_pURI          = ranges.keySet();

            for(String range : ranges_pURI)
            {
                List<String> range_values = ranges.get(range);

                if (!im.hasProperty(propertyURI))
                {
                    cm.addNeutral(); not_cm.addNeutral();
                }
                else
                {
                    if (SPARQLUtils.testPositiveInverseFunctionalitySPARQL(im.getIndividual(), propertyURI, range_values, true))
                    {
                        cm.addSupport();
                    }
                    else
                    {
                        cm.addAgainst();
                    }

                    if (SPARQLUtils.testNegativeInverseFunctionalitySPARQL(im.getIndividual(), propertyURI, range_values,  true))
                    {
                        not_cm.addSupport();
                    }
                    else
                    {
                        not_cm.addAgainst();
                    }
                }


            }

            /*
            boolean if_candidate = true;
            String domain_value              = im.getIndividual().getURI();

            for(String range : ranges_pURI)
            {
                List<String> range_values = ranges.get(range);

                for(String range_value : range_values)
                {

                    List<Triple<String, String, String>> seenTriplesOfProperty = EntityMetricsStore.getStore().getSeenTriplesOfProperty(propertyURI);

                    for(Triple<String, String, String> triple : seenTriplesOfProperty)
                    {
                        String tuple_domain_uri  = triple.getLeft();
                        String tuple_range_value = triple.getRight();

                        if(tuple_range_value.equalsIgnoreCase(range_value) && !tuple_domain_uri.equalsIgnoreCase(domain_value))
                        {
                            if_candidate = false;
                            break;
                        }

                    }

                    Triple<String, String, String> currentTriple = Triple.of(domain_value, propertyURI, range_value);
                    EntityMetricsStore.getStore().addTriple(currentTriple);

                    if(!if_candidate) break;
                }

            }


            if(if_candidate) cm.addSupport();
            else             cm.addAgainst();*/

        }



        return cm;
    }

    public static ConstructorMetrics computeTransitivenessSequence(String propertyURI, IndividualMetrics im, boolean checkEntailment)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        Individual i          = im.getIndividual();

        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE2);
        ConstructorMetrics not_cm    = pm.getConstructorMetrics(OntologyUtils.NOT_C_TRANSITIVE2);

        if(cm!=null)
        {
            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else
            {
                if (SPARQLUtils.testSupportTransitivenessSPARQL(i, propertyURI))
                {
                    // check entailment too
                    if(checkEntailment)
                    {
                        Resource o = SPARQLUtils.getTransitiveSequenceSPARQL(i, propertyURI);

                        if(o!=null)
                        {
                            OntModel o_model   = ModelManager.getManager().getOriginalModel();
                            OntModel s_model   = ModelManager.getManager().getSlidingWindowModel();

                            Property prop = o_model.getOntProperty(propertyURI);

                            boolean entailed = OntologyUtils.checkEntailment(s_model, i.asResource(), prop, o);

                            if(entailed)
                                cm.addSupport();
                            else
                                cm.addAgainst();
                        }
                        else cm.addAgainst();

                    }
                    else
                        cm.addSupport();
                }
                else
                {
                    cm.addAgainst();
                }

                if (!SPARQLUtils.testAgainstTransitivenessSPARQL(i, propertyURI))
                {
                    not_cm.addSupport();
                }
                else
                {
                    not_cm.addAgainst();
                }


            }
        }

        return cm;
    }

    public static ConstructorMetrics computeTransitivenessFull(String propertyURI, IndividualMetrics im)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        Individual i          = im.getIndividual();

        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE3);
        ConstructorMetrics not_cm    = pm.getConstructorMetrics(OntologyUtils.NOT_C_TRANSITIVE3);

        if(cm!=null)
        {
            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else
            {
                if (SPARQLUtils.testSupportExplicitTransitivenessSPARQL(i, propertyURI))
                {
                    cm.addSupport();
                }
                else
                {
                    cm.addAgainst();
                }

                if (!SPARQLUtils.testAgainstExplicitTransitivenessSPARQL(i, propertyURI))
                {
                    not_cm.addSupport();
                }
                else
                {
                    not_cm.addAgainst();
                }


            }
        }

        return cm;
    }
    public static ConstructorMetrics computeSymmetry(String propertyURI, IndividualMetrics im)
    {

        PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm     = pm.getConstructorMetrics(OntologyUtils.C_SYMMETRIC);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_SYMMETRIC);
        Individual i              = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else {

            if (SPARQLUtils.testSupportSymmetrySPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();

            if (!SPARQLUtils.testAgainstSymmetrySPARQL(i, propertyURI))
                not_cm.addSupport();
            else
                not_cm.addAgainst();
        }

        return cm;
    }

    public static ConstructorMetrics computeAsymmetry(String propertyURI, IndividualMetrics im)
    {

        PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm     = pm.getConstructorMetrics(OntologyUtils.C_ASYMMETRIC);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_ASYMMETRIC);
        Individual i              = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else {

            if (!SPARQLUtils.testSupportSymmetrySPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();

            if (SPARQLUtils.testAgainstSymmetrySPARQL(i, propertyURI))
                not_cm.addSupport();
            else
                not_cm.addAgainst();

            /*
            if (!SPARQLUtils.testSupportAsymmetrySPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();

            if (SPARQLUtils.testAgainstAsymmetrySPARQL(i, propertyURI))
                not_cm.addSupport();
            else
                not_cm.addAgainst();
       */ }

        return cm;
    }
    public static ConstructorMetrics computeReflexiveness(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_REFLEXIVE);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_REFLEXIVE);
        Individual i          = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else
        {

            if (SPARQLUtils.testSupportReflexivenessSPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();

            if (!SPARQLUtils.testAgainstReflexivenessSPARQL(i, propertyURI))
                not_cm.addSupport();
            else
                not_cm.addAgainst();
        }

        return cm;
    }

    public static ConstructorMetrics computeIrreflexiveness(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm     = pm.getConstructorMetrics(OntologyUtils.C_IRREFLEXIVE);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_IRREFLEXIVE);
        Individual i              = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else
        {

            if (SPARQLUtils.testSupportIrreflexivenessSPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();

            if (!SPARQLUtils.testAgainstIrreflexivenessSPARQL(i, propertyURI))
                not_cm.addSupport();
            else
                not_cm.addAgainst();
        }


        return cm;
    }






    /*
    // todo check ????
    public ConstructorMetrics computeInverseFunctionality(String propertyURI)
    {
        PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm     = pm.getConstructorMetrics(OntologyUtils.C_INVERSE_FUNCTIONAL);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_INVERSE_FUNCTIONAL);

        for(IndividualMetrics im : this.getIndividualMetrics())
        {
            PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else
            {

                Map<String, List<String>> ranges = pm1.getRanges();
                Set<String> ranges_pURI          = ranges.keySet();

                for(String range : ranges_pURI)
                {
                    List<String> range_values = ranges.get(range);

                    if (!im.hasProperty(propertyURI))
                    {
                        cm.addNeutral(); not_cm.addNeutral();
                    }
                    else
                    {
                        if (SPARQLUtils.testPositiveInverseFunctionalitySPARQL(im.getIndividual(), propertyURI, range_values, true))
                            cm.addSupport();
                        else
                            cm.addAgainst();

                        if (SPARQLUtils.testNegativeInverseFunctionalitySPARQL(im.getIndividual(), propertyURI, range_values, true))
                            not_cm.addSupport();
                        else
                            not_cm.addAgainst();
                    }


                }
            }


        }
        return cm;
    }

    public ConstructorMetrics computeFunctionality(String propertyURI)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm    = pm.getConstructorMetrics(OntologyUtils.C_FUNCTIONAL);
        ConstructorMetrics not_cm = pm.getConstructorMetrics(OntologyUtils.NOT_C_FUNCTIONAL);


        for(IndividualMetrics im : this.getIndividualMetrics())
        {
            Individual i = im.getIndividual();

            if (!im.hasProperty(propertyURI))
            {
                cm.addNeutral();
                not_cm.addNeutral();
            }
            else
            {
                if (SPARQLUtils.testPositiveFunctionalitySPARQL(i, propertyURI, true))
                    cm.addSupport();
                else
                    cm.addAgainst();

                if (!SPARQLUtils.testNegativeFunctionalitySPARQL(i, propertyURI, true))
                    not_cm.addSupport();
                else
                    not_cm.addAgainst();
            }
        }
        return cm;
    }

    public ConstructorMetrics computeSymmetry(String propertyURI)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_SYMMETRIC);

        for(IndividualMetrics im : this.individualMetrics)
        {
            Individual i = im.getIndividual();

            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else {

                if (SPARQLUtils.testSymmetrySPARQL(i, propertyURI))
                    cm.addSupport();
                else
                    cm.addAgainst();
            }
        }

        return cm;
    }

    public ConstructorMetrics computeAsymmetry(String propertyURI)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_ASYMMETRIC);

        for(IndividualMetrics im : this.individualMetrics)
        {
            Individual i = im.getIndividual();

            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else {

                if (!SPARQLUtils.testSymmetrySPARQL(i, propertyURI))
                    cm.addSupport();
                else
                    cm.addAgainst();
            }
        }

        return cm;
    }

    public ConstructorMetrics computeTransitiveness(String propertyURI, int levels)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = null;

        if(levels == 2)          cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE2);
        else if(levels == 3)     cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE3);

        if(cm!=null)
        {
            for(IndividualMetrics im : this.individualMetrics)
            {
                Individual i = im.getIndividual();

                if(!im.hasProperty(propertyURI))
                    cm.addNeutral();
                else {
                    if (SPARQLUtils.testTransitivenessSPARQL(i, propertyURI, levels))
                        cm.addSupport();
                    else
                        cm.addAgainst();
                }

            }
        }

        return cm;
    }

    public ConstructorMetrics computeReflexiveness(String propertyURI)
    {
        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_REFLEXIVE);

        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        List<String> domains    = getAllDomainsOfProperty(propertyURI);

        for(String range : ranges)
        {
            if (domains.contains(range))
                cm.addSupport();
            else
                cm.addAgainst();
        }

        if(ranges.isEmpty()  || domains.isEmpty())
        {
            cm.getNeutral(); return cm;
        }

        return cm;
    }

    public ConstructorMetrics computeIrreflexiveness(String propertyURI)
    {
        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_IRREFLEXIVE);

        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        List<String> domains    = getAllDomainsOfProperty(propertyURI);

        if(ranges.isEmpty()  || domains.isEmpty())
        {
            cm.getNeutral(); return cm;
        }

        for(String range : ranges)
        {
            if (!domains.contains(range))
                cm.addSupport();
            else
                cm.addAgainst();
        }

        return cm;
    }
*/
}
