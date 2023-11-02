
package IDC.Metrics;

import java.util.*;

import Utils.AnalyticUtils;
import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import Utils.Utilities;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.*;


public class ClassPropertyMetrics extends EntityMetrics
{


    //USE URI
    private OntClass ontClass;

    private List<IndividualMetrics> individualMetrics;
    private Individual first_mention, last_mention;

    private boolean metricsComputed = false;

    private HashMap<String, List<String>> seenRanges;
    
    public ClassPropertyMetrics(String EntityURI)
    {
        super(EntityURI);      
        //propertyMetrics      = new ArrayList<>();
        individualMetrics    = new ArrayList<>();
        seenRanges = new HashMap<>();
    }
    
    public ClassPropertyMetrics(String EntityURI, Individual first_mention)
    {
        super(EntityURI);
        //propertyMetrics      = new ArrayList<>();
        individualMetrics    = new ArrayList<>();
        seenRanges = new HashMap<>();


        this.first_mention   = first_mention;
        this.last_mention    = first_mention;

        this.addClassMention(first_mention);
    }

    @Override
    public int getMentions()
    {
        return this.individualMetrics.size();
    }

    @Override
    public String toString()
    {
        String print = "";

        print += "\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n";
        print += ("\t++++++++++++++ " + this.getURI() + " ++++++++++++++\n");

        print += ("\n\tClass " + this.getURI() + " is mentioned " +  this.getMentions() + " times.\n");
        print += ("\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

        print += ("\n\t> Property Metrics:");

        List<String> properties = this.getAllProperties();

        for(String p_uri : properties)
        {
            String type = "Object Property";
            OntProperty p = this.ontClass.getOntModel().getOntProperty(p_uri);

            if(p.isDatatypeProperty())
                type = "Datatype Property";


            print += ("\n\t\t>> URI " + p_uri + " is a " + type );

            print += ("\n\t\t\t>> Total Mentions: "                              + this.getPropertyMentions(p_uri)
                                    + "\n\t\t\t>> Distinct Mentions: "           + this.getDistinctPropertyMentions(p_uri)
                                    + "\n\t\t\t>> Frequency: "                   + this.getPropertyRatio(p_uri)
                                    + "\n\t\t\t>> Max Mentions per Individual: " + this.propertyMaxMentionsPerIndividual(p_uri)
                                    + "\n\t\t\t>> Min Mentions per Individual: " + this.propertyMinMentionsPerIndividual(p_uri)
                                    + "\n\t\t\t>> AVG Mentions per Individual: " + this.propertyAvgMentionsPerIndividual(p_uri)
            );

            print += ("\n\t\t\t>> Is Functional? " + this.isFunctionalCandidate(p_uri));


            if(!p.isDatatypeProperty())
            {
                print += ("\n\t\t\t>> Reflexiviness > count: "   + this.getPropertyReflexiveCount(p_uri)     + " > ratio: " + this.getPropertyReflexiveRatio(p_uri));
                print += ("\n\t\t\t>> Irreflexiviness > count: " + this.getPropertyIrreflexiveCount(p_uri)   + " > ratio: " + this.getPropertyIrreflexiveRatio(p_uri));
                print += ("\n\t\t\t>> Symmetry > count: "        + this.getPropertySymmetryCount(p_uri)      + " > ratio: " + this.getPropertySymmetryRatio(p_uri));
                print += ("\n\t\t\t>> Transtiviness:"
                        + "\n\t\t\t\t> 2 level count: "        + this.getPropertyTransitiveCount(p_uri, 2)      + " > ratio: " + this.getPropertyTransitiveRatio(p_uri, 2)
                        + "\n\t\t\t\t> 3 level count: "        + this.getPropertyTransitiveCount(p_uri, 3)      + " > ratio: " + this.getPropertyTransitiveRatio(p_uri, 3)
                );

            }

            print += ("\n\t\t\t>> Domains:" );
            for(String d_uri : this.getAllDomainsOfProperty(p_uri))
                print += ("\n\t\t\t\t> " + d_uri + " | frequency: " + this.propertyDomainRatio(p_uri, d_uri));

            print += ("\n\t\t\t>> Ranges:" );
            for(String r_uri : this.getAllRangesOfProperty(p_uri))
                print += ("\n\t\t\t\t> " + r_uri + " | frequency: " + this.propertyRangeRatio(p_uri, r_uri));

        }

        Individual first = this.getFirstMention();
        Individual last  = this.getLastMention();

        if(first!=null) print += ("\n\t\t> First Mentioned on Individual: " + first.getURI());
        if(last!=null)  print += ("\n\t\t> Last Mentioned on Individual: "  + last.getURI());

        print += ("\n\t+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");

        return print;
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


    public String printComputations()
    {
        String ret = "===\nRetrieving Constructor Metrics...\n";

        if(!metricsComputed)
            computeAllMetrics();

        Set<String> propertyURIs = new HashSet<>();

        for(IndividualMetrics im : this.individualMetrics)
            propertyURIs.addAll(im.getProperties());

        for(String propertyURI : propertyURIs)
        {
            PropertyMetrics pm        = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
            for(ConstructorMetrics cm : pm.getConstructors())
            {
                ret += cm.toString();
            }
        }

        ret+="====\n";
        return ret;
    }



    public void computeAllMetricsForIndividual(IndividualMetrics im)
    {
        Set<String> propertyURIs = new HashSet<>();

        propertyURIs.addAll(im.getProperties());



        for(String propertyURI : propertyURIs)
        {

            if(propertyURI.contains("issues") )
                System.out.println("THIS IS IT!");

            computeFunctionality(propertyURI, im);
            computeSymmetry(propertyURI, im);
            computeReflexiveness(propertyURI, im);
            computeIrreflexiveness(propertyURI, im);
            computeTransitiveness(propertyURI, 2, im);
            computeTransitiveness(propertyURI, 3, im);
            computeAsymmetry(propertyURI, im);
            computeInverseFunctionality(propertyURI, im);
        }

    }

    public void computeAllMetrics()
    {
        resetComputations();

        Set<String> propertyURIs = new HashSet<>();

        for(IndividualMetrics im : this.individualMetrics)
            propertyURIs.addAll(im.getProperties());

        for(String propertyURI : propertyURIs)
        {
            computeFunctionality(propertyURI);
            computeSymmetry(propertyURI);
            computeReflexiveness(propertyURI);
            computeIrreflexiveness(propertyURI);
            computeTransitiveness(propertyURI, 2);
            computeTransitiveness(propertyURI, 3);
            computeAsymmetry(propertyURI);
            computeInverseFunctionality(propertyURI);
        }

        metricsComputed = true;
    }

    public void printComputations2File()
    {
        String ret = "";

        if(!metricsComputed)
            computeAllMetrics();

        Set<String> propertyURIs = new HashSet<>();

        for(IndividualMetrics im : EntityMetricsStore.getStore().getIndividualMetrics())
            propertyURIs.addAll(im.getProperties());

        AnalyticUtils.printAllComputations(propertyURIs);

    }

    public void resetComputations()
    {
        Set<String> propertyURIs = new HashSet<>();

        for(IndividualMetrics im : this.individualMetrics)
            propertyURIs.addAll(im.getProperties());

        for(String propertyURI : propertyURIs)
        {
            PropertyMetrics pm = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);

            List<ConstructorMetrics> constructors = pm.getConstructors();
            for(ConstructorMetrics cm : constructors)
                cm.clean();

            String propertyPart = propertyURI.split("#")[1];
            String fileName   = "Analytics/Constructors/" + propertyPart + "_constructors" + ".csv";

            //Utilities.deleteFile(fileName);
        }



    }


    public ConstructorMetrics computeInverseFunctionality(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm    = pm.getConstructorMetrics(OntologyUtils.C_INVERSE_FUNCTIONAL);

        PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else
        {
            Set<String> ranges_PURI = pm1.getRanges().keySet();
            List<String> total_seen_ranges_of_PURI = this.seenRanges.get(propertyURI);

            for (String range : ranges_PURI)
            {
                if (total_seen_ranges_of_PURI!=null && total_seen_ranges_of_PURI.contains(range))
                    cm.addSupport();
                else
                {
                    if(total_seen_ranges_of_PURI == null)
                        total_seen_ranges_of_PURI = new ArrayList<>();

                    total_seen_ranges_of_PURI.add(range);
                    cm.addAgainst();
                }
            }

            if (total_seen_ranges_of_PURI.size() == 50)
                total_seen_ranges_of_PURI.remove(0);

        }
        return cm;
    }

    public ConstructorMetrics computeInverseFunctionality(String propertyURI)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm    = pm.getConstructorMetrics(OntologyUtils.C_INVERSE_FUNCTIONAL);

        for(IndividualMetrics im : this.getIndividualMetrics())
        {
            PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else {

            Set<String> ranges_PURI = pm1.getRanges().keySet();
            List<String> total_seen_ranges_of_PURI = this.seenRanges.get(propertyURI);


                for (String range : ranges_PURI)
                {
                    if (total_seen_ranges_of_PURI!=null && total_seen_ranges_of_PURI.contains(range))
                        cm.addSupport();
                    else
                    {
                        if(total_seen_ranges_of_PURI == null)
                            total_seen_ranges_of_PURI = new ArrayList<>();

                        total_seen_ranges_of_PURI.add(range);
                        cm.addAgainst();
                    }
                }

                if (total_seen_ranges_of_PURI.size() == 50)
                    total_seen_ranges_of_PURI.remove(0);

            }
        }

        return cm;
    }


    public ConstructorMetrics computeFunctionality(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_FUNCTIONAL);

        PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else
        {
            if (pm1.getCount() == 1)
                cm.addSupport();
            else
                cm.addAgainst();
        }

        return cm;
    }


    public ConstructorMetrics computeFunctionality(String propertyURI)
    {
        PropertyMetrics pm       = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_FUNCTIONAL);

        for(IndividualMetrics im : this.getIndividualMetrics())
        {
            PropertyMetrics pm1 = im.getPropertyMetricForProperty(propertyURI);

            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else {
                if (pm1.getCount() > 1)
                    cm.addSupport();
                else
                    cm.addAgainst();
            }
        }

        return cm;
    }

    public ConstructorMetrics computeSymmetry(String propertyURI, IndividualMetrics im)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_SYMMETRIC);
        Individual i          = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else {

            if (SPARQLUtils.testSymmetrySPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();
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

    public ConstructorMetrics computeAsymmetry(String propertyURI, IndividualMetrics im)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_ASYMMETRIC);
        Individual i          = im.getIndividual();

        if(!im.hasProperty(propertyURI))
            cm.addNeutral();
        else {

            if (!SPARQLUtils.testSymmetrySPARQL(i, propertyURI))
                cm.addSupport();
            else
                cm.addAgainst();
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

    public ConstructorMetrics computeTransitiveness(String propertyURI, int levels, IndividualMetrics im)
    {

        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = null;
        Individual i          = im.getIndividual();

        if(levels == 2)          cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE2);
        else if(levels == 3)     cm = pm.getConstructorMetrics(OntologyUtils.C_TRANSITIVE3);

        if(cm!=null)
        {
            if(!im.hasProperty(propertyURI))
                cm.addNeutral();
            else
            {
                if (SPARQLUtils.testTransitivenessSPARQL(i, propertyURI, levels))
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

    public ConstructorMetrics computeReflexiveness(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_REFLEXIVE);

        Set<String> ranges     = im.getPropertyMetricForProperty(propertyURI).getRanges().keySet();
        Set<String> domains    = im.getPropertyMetricForProperty(propertyURI).getDomains().keySet();

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

    public ConstructorMetrics computeIrreflexiveness(String propertyURI, IndividualMetrics im)
    {
        PropertyMetrics pm    = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
        ConstructorMetrics cm = pm.getConstructorMetrics(OntologyUtils.C_IRREFLEXIVE);

        Set<String> ranges     = im.getPropertyMetricForProperty(propertyURI).getRanges().keySet();
        Set<String> domains    = im.getPropertyMetricForProperty(propertyURI).getDomains().keySet();

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


    public void addClassMention(Individual mention)
    {
        this.last_mention = mention;
        IndividualMetrics im = new IndividualMetrics(mention);
        this.individualMetrics.add(im);

        this.print();
    }
    
    public Individual getFirstMention()
    {
        return this.first_mention;
    }
    
    public Individual getLastMention()
    {
        return this.last_mention;
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

    public float propertyDomainRatio(String propertyURI, String domainURI)
    {
        int domainCount         = 0;
        float propertyMentions  = getDistinctPropertyMentions(propertyURI);

        if(propertyMentions==0) return 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);

            if(pm!=null)
                domainCount += pm.getDomainCount(domainURI);
        }

        return (float) domainCount / propertyMentions;
    }

    public float propertyRangeRatio(String propertyURI, String rangeURI)
    {
        int domainCount = 0;
        float propertyMentions = getDistinctPropertyMentions(propertyURI);

        if(propertyMentions==0) return 0;

        for(IndividualMetrics im : this.individualMetrics)
        {
            PropertyMetrics pm = im.getPropertyMetricForProperty(propertyURI);

            if(pm!=null)
                domainCount += pm.getRangeCount(rangeURI);
        }

        return (float) domainCount / propertyMentions;
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
            boolean isSymmetric = SPARQLUtils.testSymmetrySPARQL(i, propertyURI);

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

    public List<DatatypeProperty> getAllDatatypeProperties()
    {
        OntModel model = this.ontClass.getOntModel();
        List<DatatypeProperty> properties = new ArrayList<>();

        for(String i : this.getAllProperties())
        {
            OntProperty ontProperty = model.getOntProperty(i);
            if(ontProperty != null)
                if(ontProperty.isDatatypeProperty())
                    properties.add(ontProperty.asDatatypeProperty());
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

    public List<ObjectProperty> getAllObjectProperties()
    {
        OntModel model = this.ontClass.getOntModel();
        List<ObjectProperty> properties = new ArrayList<>();

        for(String i : this.getAllProperties())
        {
            OntProperty ontProperty = model.getOntProperty(i);
            if(ontProperty != null)
                if(ontProperty.isObjectProperty())
                    properties.add(ontProperty.asObjectProperty());
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





    /**
     * @return the propertyMetrics
     */
//    public List<PropertyMetrics> getPropertyMetrics() {
//        return propertyMetrics;
//    }
    
}
