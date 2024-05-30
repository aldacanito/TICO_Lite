
package IDC.Metrics;

import java.util.*;

import IDC.ModelManager;
import Utils.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.RDFNode;


public class ClassPropertyMetrics extends EntityMetrics
{


    //USE URI
    private OntClass ontClass;

    private List<IndividualMetrics> individualMetrics;
    private Individual first_mention, last_mention;

    private boolean metricsComputed = false;



    public ClassPropertyMetrics(String EntityURI)
    {
        super(EntityURI);      
        individualMetrics    = new ArrayList<>();

    }
    


    @Override
    public int getMentions()
    {
        return this.individualMetrics.size();
    }




    public void print()
    {
        String className = this.getURI();

        List<String> properties = this.getAllProperties();

        for(String p_uri : properties)
        {
            OntProperty p = this.ontClass.getOntModel().getOntProperty(p_uri);
            String fileName = Utils.AnalyticUtils.writeHeader(className, p, p_uri);

            String line = this.getPropertyMentions(p_uri)
                    + ";" + this.getDistinctPropertyMentions(p_uri)
                    + ";" + this.getPropertyRatio(p_uri)
                    + ";" + this.propertyMaxMentionsPerIndividual(p_uri)
                    + ";" + this.propertyMinMentionsPerIndividual(p_uri)
                    + ";" + this.propertyAvgMentionsPerIndividual(p_uri);

            line += (";" + this.isFunctionalCandidate(p_uri));


            if(!p.isDatatypeProperty())
            {
                line += (";" + this.getPropertyReflexiveCount(p_uri)     + ";" + this.getPropertyReflexiveRatio(p_uri));
                line += (";" + this.getPropertyIrreflexiveCount(p_uri)   + ";" + this.getPropertyIrreflexiveRatio(p_uri));
                line += (";" + this.getPropertySymmetryCount(p_uri)      + ";" + this.getPropertySymmetryRatio(p_uri));
                line += (";"        + this.getPropertyTransitiveCount(p_uri, 2) + ";" + this.getPropertyTransitiveRatio(p_uri, 2)
                        + ";"        + this.getPropertyTransitiveCount(p_uri, 3)      + ";" + this.getPropertyTransitiveRatio(p_uri, 3)
                );

            }

            Utils.Utilities.appendLineToFile(fileName, line);

        }

    }


    public int propertyMaxMentionsPerIndividual(String propertyURI)
    {
        int count = 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm==null) continue;
            int pmcount = pm.getCount();

            if(pmcount > count)
                count = pmcount;
        }

        return count;
    }

    public int propertyMinMentionsPerIndividual(String propertyURI)
    {
        int count = Integer.MAX_VALUE;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm==null) continue;

            int pmcount = pm.getCount();

            if(pmcount < count)
                count = pmcount;
        }

        return count;
    }

    public float propertyAvgMentionsPerIndividual(String propertyURI)
    {
        int count = 0;
        int mentionedInIndividuals = 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm==null) continue;

            count += pm.getCount();
            mentionedInIndividuals++;
        }
        if(mentionedInIndividuals == 0) return 0;

        return (float) count / (float) mentionedInIndividuals;
    }


    public boolean isFunctionalCandidate(String propertyURI)
    {
        boolean functionalCandidate = true;

        // to be functional candidate it has to show only once per individual
        for(IndividualMetrics im : this.getIndividualMetrics())
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm== null || pm.getCount() > 1)
                return false;
        }

        return functionalCandidate;
    }






    public static void computeAllMetricsForIndividual(IndividualMetrics im, int indNumber)
    {
        Set<String> propertyURIs = new HashSet<>();

        propertyURIs.addAll(im.getProperties());

        for(String propertyURI : propertyURIs)
        {

            OntModel ontModel = ModelManager.getManager().getOriginalModel();

            OntProperty theProp = ontModel.getOntProperty(propertyURI);

            boolean isObjectP  = theProp.isObjectProperty();

            AxiomMetricsProcessor.computeFunctionality(propertyURI, im);

            if(isObjectP)
            {
                AxiomMetricsProcessor.computeInverseFunctionality(propertyURI, im);

                AxiomMetricsProcessor.computeTransitivenessSequence(propertyURI, im, true);
                AxiomMetricsProcessor.computeTransitivenessFull(propertyURI, im);

                if(indNumber>=37)
                    System.out.println("minimum threshold in");

                AxiomMetricsProcessor.computeSymmetry(propertyURI, im);
                AxiomMetricsProcessor.computeAsymmetry(propertyURI, im);

                AxiomMetricsProcessor.computeReflexiveness(propertyURI, im);
                AxiomMetricsProcessor.computeIrreflexiveness(propertyURI, im);
            }
        }

    }




    public static void static_printComputations2File(int ind_count)
    {
        Set<String> propertyURIs = new HashSet<>();

        for(IndividualMetrics im : EntityMetricsStore.getStore().getIndividualMetrics())
            propertyURIs.addAll(im.getProperties());

        AnalyticUtils.printAllComputations(propertyURIs, ind_count);

    }





    public void addClassMention(Individual mention)
    {
        this.last_mention = mention;
        IndividualMetrics im = new IndividualMetrics(mention);
        this.individualMetrics.add(im);

        this.print();
    }
    


    

    public int getPropertyMentions(String propertyURI)
    {
        int count = 0;
        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);

            if(pm!=null)
                count+= pm.getCount();
        }

        return count;
    }

    public List<String> getAllProperties()
    {
        List<String> properties = new ArrayList<>();
        for(IndividualMetrics im : this.individualMetrics)
        {
            List<PropertyMetrics> pm_list = im.getAllPropertyMetrics();
            for(PropertyMetrics pm : pm_list)
            {
                String property_uri = pm.getURI();

                if(!properties.contains(property_uri))
                    properties.add(property_uri);
            }
        }

        Collections.sort(properties);
        return properties;
    }



    public List<String> getAllDomainsOfProperty(String propertyURI)
    {
        Set<String> domains = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm!=null)
                domains.addAll(pm.getDomains().keySet());
        }
        return new ArrayList<>(domains);
    }


    public int getPropertySymmetryCount(String propertyURI)
    {
        int count = 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            Individual i = im.getIndividual();
            boolean isSymmetric = SPARQLUtils.testSupportSymmetrySPARQL(i, propertyURI);

            if (isSymmetric)
                count++;
        }

        return count;
    }

    public float getPropertySymmetryRatio(String propertyURI)
    {
        float symmetryRatio    = 0;
        int symmetricCount     = this.getPropertySymmetryCount(propertyURI);
        int propertyMentions   = getDistinctPropertyMentions(propertyURI);

        if(propertyMentions > 0)
            symmetryRatio = (float) symmetricCount / (float) propertyMentions;

        return symmetryRatio;
    }

    public List<String> getAllRangesOfProperty(String propertyURI)
    {
        Set<String> ranges = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);
            if(pm!=null)
                ranges.addAll(pm.getRanges().keySet());
        }
        return new ArrayList<>(ranges);
    }

    public int getDistinctPropertyMentions(String propertyURI)
    {
        int count = 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);

            if(pm!=null)
                count++;
        }

        return count;
    }


    public float getPropertyReflexiveRatio(String propertyURI)
    {
        float ratio             = 0;
        int count               = getPropertyReflexiveCount(propertyURI);
        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        int totalR              = ranges.size();

        if(totalR!=0)
            ratio = (float) count / (float) totalR;

        return ratio;
    }

    public int getPropertyReflexiveCount(String propertyURI)
    {
        int count = 0;

        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        List<String> domains    = getAllDomainsOfProperty(propertyURI);

        for(String range : ranges)
            if(domains.contains(range))
                count++;

        return count;
    }


    public float getPropertyTransitiveRatio(String propertyURI, int levels)
    {
        int mentions            = getPropertyMentions(propertyURI);
        int count               = getPropertyTransitiveCount(propertyURI, levels);
        float ratio             = 0;

        if(mentions!=0)
            ratio = (float) count / mentions;

        return ratio;
    }

    public int getPropertyTransitiveCount(String propertyURI, int levels)
    {
        int count = 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            Individual i = im.getIndividual();
            if (SPARQLUtils.testTransitivenessSPARQL(i, propertyURI, levels))
                count++;
        }

        return count;
    }

    public int getPropertyIrreflexiveCount(String propertyURI)
    {
        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        List<String> domains    = getAllDomainsOfProperty(propertyURI);
        int count               = 0;

        for(String range : ranges)
            if(!domains.contains(range))
                count++;


        return count;

    }

    public float getPropertyIrreflexiveRatio(String propertyURI)
    {
        int mentions            = getPropertyMentions(propertyURI);
        int count               = getPropertyIrreflexiveCount(propertyURI);
        float ratio             = 0;

        List<String> ranges     = getAllRangesOfProperty(propertyURI);
        int totalR              = ranges.size();

        if(totalR!=0)
            ratio = (float) count / (float) totalR;

        return ratio;
    }


    public List<String> getAllDatatypePropertiesURIs()
    {
        OntModel model = this.ontClass.getOntModel();
        List<String> properties = new ArrayList<>();

        for(String i : this.getAllProperties())
        {
            OntProperty ontProperty = model.getOntProperty(i);
            if(ontProperty != null)
                if(ontProperty.isDatatypeProperty())
                    properties.add(i);
        }

        return properties;
    }




    public List<String> getAllObjectPropertiesURIs()
    {
        OntModel model = this.ontClass.getOntModel();
        List<String> properties = new ArrayList<>();

        for(String i : this.getAllProperties())
        {
            OntProperty ontProperty = model.getOntProperty(i);
            if(ontProperty != null)
                if(ontProperty.isObjectProperty())
                    properties.add(i);
        }

        return properties;
    }




    /**
     * Percentage of times a certain property appears in the individuals
     * @param propertyURI
     * @return
     */
    public float getPropertyRatio(String propertyURI)
    {
        float perc = 0.0F;
        int total  = this.getDistinctPropertyMentions(propertyURI);

        if(individualMetrics.size()!=0)
            perc = (float) total / (float) this.individualMetrics.size();

        return perc;

    }

    public OntClass getOntClass()
    {
        return this.ontClass;
    }

    public void setOntClass(OntClass cls)
    {
        this.ontClass = cls;
    }

    /**
     * @return the IndividualMetrics
     */
    public List<IndividualMetrics> getIndividualMetrics() {
        return individualMetrics;
    }





    
}
