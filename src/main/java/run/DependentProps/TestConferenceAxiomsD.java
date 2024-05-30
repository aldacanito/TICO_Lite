package run.DependentProps;

import IDC.Metrics.EntityMetricsStore;
import IDC.ModelManager;
import IDC.PropertyAxiomScoring;
import Utils.AnalyticUtils;
import Utils.Configs;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestConferenceAxiomsD
{
    static String base = "";
    static String datasetsFolder = base + "PopulatedDatasets\\";
    static int partition_size = 3000, window_size = 100; //change windows size

    /*
    List of properties to analyse.
    Comment the properties not to analyse (affects the support in the sliding window)
    */
    public static String conferenceProps [] =
            {
                    "http://conference#is_the_1th_part_of",
                    "http://conference#reviews",
                    "http://conference#was_a_program_committee_of",
                    "http://conference#was_a_steering_committee_of",
                    "http://conference#was_an_organizing_committee_of",
                    "http://conference#was_a_committee_chair_of",
                    "http://conference#was_a_track-workshop_chair_of",
                    "http://conference#belong_to_a_conference_volume",
                    "http://conference#belongs_to_a_review_reference",
                    "http://conference#has_a_program_committee",
                    "http://conference#has_a_steering_committee",
                    "http://conference#has_an_organizing_committee",
                    "http://conference#has_a_publisher",
                    "http://conference#has_a_review",
                    "http://conference#has_a_review_expertise",
                    "http://conference#has_a_submitted_contribution",
                    "http://conference#has_a_topic_or_a_submission_contribution",
                    "http://conference#has_a_track-workshop-tutorial_chair",
                    "http://conference#has_a_track-workshop-tutorial_topic",
                    "http://conference#has_an_abstract",
                    "http://conference#has_been_assigned_a_review_reference",
                    "http://conference#has_important_dates",
                    "http://conference#has_a_committee_chair"
            };

    static String cmtProps[] =
            {
                    "http://cmt#acceptedby",
                    "http://cmt#hasDecision",
                    "http://cmt#acceptedPaper",
                    "http://cmt#addedBy",
                    "http://cmt#addProgramCommitteeMember",
                    "http://cmt#adjustBid",
                    "http://cmt#adjustedBy",
                    "http://cmt#assignedByReviewer",
                    "http://cmt#assignExternalReviewer",
                    "http://cmt#hasAuthor",
                    "http://cmt#hasBid",
                    "http://cmt#readByMeta-Reviewer",
                    "http://cmt#rejectedBy",
                    "http://cmt#rejectPaper",
                    "http://cmt#writePaper",
                    "http://cmt#writtenBy",
                    "http://cmt#writeReview"
            };
    static String wineProps[] =
            {
                "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#adjacentRegion",
                "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#hasMaker",
                "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#locatedIn"
            };

    static String plantProps[] =
            {
                "http://purl.obolibrary.org/obo/BFO_0000051",
                "http://purl.obolibrary.org/obo/BFO_0000050",
                "http://purl.obolibrary.org/obo/part_of",
                "http://purl.obolibrary.org/obo/RO_0002202",
                "http://data.bioontology.org/metadata/obo/part_of"
            };


    public static void main(String [] args)
    {
        Configs c = new Configs();
        // uncomment the one you want to go
        //go("conference", conferenceProps, partition_size);
        //go("cmt", cmtProps, partition_size);
        //go("plant", plantProps, partition_size);
        //go("wine", wineProps, partition_size);

    }

    public static void go(String ontologyName, String[] props, int partitionSize)
    {
        PropertyAxiomScoring comparator = new PropertyAxiomScoring();

        AnalyticUtils.ONTO_NAME  = ontologyName;
        AnalyticUtils.CURRENT_FOLDER  = datasetsFolder + AnalyticUtils.ONTO_NAME + "/";
        AnalyticUtils.INSTANCE_FOLDER = AnalyticUtils.CURRENT_FOLDER + "/instances/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + AnalyticUtils.ONTO_NAME;
        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/" + AnalyticUtils.ONTO_NAME + "/Constructors" + window_size + "/";

        Utilities.importantProps = new HashMap<>();
        for (String propertyURI : props)
        {
            List<String> theProps = new ArrayList<>();
            theProps.add(propertyURI);
            Utilities.importantProps.put(ontologyName, theProps);
        }

        List<String> individuals_uris = Utilities.extractInstancesFromFile(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
        List<List<String>> partitions = Utilities.chopped(individuals_uris, partitionSize);

        //set up from TICO full
        OntModel originalModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
        OntModel evolvingModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
        OntModel instanceModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + "\\instances\\" + ontologyName + "_0.ttl");

        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

        Configs.windowSize = window_size;

        comparator.run(partitions.get(0));

        EntityMetricsStore.cleanStore();

    }



}
