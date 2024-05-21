package helpers.datasetconverter;

import IDC.Comparator;
import IDC.Metrics.EntityMetricsStore;
import IDC.ModelManager;
import Utils.*;
import org.apache.jena.ontology.OntModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestConfProp
{

    //static String base = "C:\\Users\\Alda\\Documents\\TICO\\";

    static String base = "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\TICO\\";
    static String datasetsFolder = base + "PopulatedDatasets\\";

    static String props[] =
            {
                    "http://cmt#readByReviewer",

                   /* "http://conference#reviews",
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
                    */


            };


    public static void main(String[] args)
    {

        String ontologyName      = "cmt";
        AnalyticUtils.ONTO_NAME  = ontologyName;
        int partitionSize        = 3000;
        String[] datasets        = {AnalyticUtils.ONTO_NAME};

        AnalyticUtils.CURRENT_FOLDER  = datasetsFolder + ontologyName + "/";
        AnalyticUtils.INSTANCE_FOLDER = AnalyticUtils.CURRENT_FOLDER + "/instances/";

        for (String propertyURI : props)
        {
            Configs configs       = new Configs();
            List<String> theProps = new ArrayList<>();
            theProps.add(propertyURI);

            Utilities.importantProps = new HashMap<>();
            Utilities.importantProps.put(ontologyName, theProps);

            List<String> individuals_uris = Utilities.extractInstancesFromFile(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
            List<List<String>> partitions = Utilities.chopped(individuals_uris, partitionSize);

            OntModel originalModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
            OntModel evolvingModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
            OntModel instanceModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + "\\instances\\" + ontologyName + "_0.ttl");

            ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

            Configs.windowSize = 10;
            for(String dataset : datasets)
                run(dataset, partitions.get(0), 10);

            Configs.windowSize = 50;
            for(String dataset : datasets)
                run(dataset, partitions.get(0),50);

            Configs.windowSize = 100;
            for(String dataset : datasets)
                run(dataset, partitions.get(0), 100);

        }

    }

    public static void run(String ontologyName, List<String> inds, int windo_size) {
        AnalyticUtils.CURRENT_FOLDER = datasetsFolder + ontologyName + "/";
        AnalyticUtils.INSTANCE_FOLDER = AnalyticUtils.CURRENT_FOLDER + "/instances/";

        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/" + ontologyName + "/Constructors" + windo_size + "/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + ontologyName;

        Comparator comparator = new Comparator();
        comparator.run(inds);

        System.out.println("Finished window size " + windo_size);
        //String stats = comparator.printStats();
        //System.out.println(" stats time: " + stats);
        EntityMetricsStore.cleanStore();



    }





}
