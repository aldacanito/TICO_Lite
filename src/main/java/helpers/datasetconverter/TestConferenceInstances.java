package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.AnalyticUtils;
import Utils.Configs;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.util.List;

public class TestConferenceInstances
{

    //static String base = "C:\\Users\\Alda\\Documents\\TICO\\";

    static String base = "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\TICO\\";
    static String datasetsFolder = base + "PopulatedDatasets/";

    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();

    public static void main(String[] args)
    {
        Configs configs = new Configs();

        Configs.windowSize = 50;

        //String [] datasets = {"cmt", "conference", "confOf", "ekaw"};


        AnalyticUtils.ONTO_NAME = "conference";

        String [] datasets = { AnalyticUtils.ONTO_NAME };

        List<String> individuals_uris = Utilities.extractInstancesFromFile(datasetsFolder);

        int partitionSize  = 3000;

        List<List<String>> partitions = Utilities.chopped(individuals_uris, partitionSize);

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

    public static void run(String ontologyName, List<String> inds, int windo_size)
    {
        AnalyticUtils.ONTO_NAME = ontologyName;

        AnalyticUtils.CURRENT_FOLDER = datasetsFolder + ontologyName + "/";
        AnalyticUtils.INSTANCE_FOLDER = AnalyticUtils.CURRENT_FOLDER + "/instances/";


        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/"+ontologyName+"/Constructors" + windo_size + "/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + ontologyName;

        originalModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
        evolvingModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + ontologyName + ".ttl");
        instanceModel = ModelFactory.createOntologyModel();

        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

        // read all instances files in the instance folder
        File dir                = new File(AnalyticUtils.INSTANCE_FOLDER);
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null)
        {
            for (File child : directoryListing)
            {
                try
                {
                    String fileName = child.getAbsolutePath();

                    if(fileName.contains(".ttl"))
                    {
                        OntModel ontModel = OntologyUtils.readModel(child.getAbsolutePath(), true);
                        instanceModel.add(ontModel);

                        System.out.println("Added instance file " + child.getAbsolutePath());
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Exception reading model. Reason: " + e.getMessage());
                }
            }
        }

        System.out.println("Finished reading instance files.");

        Comparator comparator = new Comparator();
        comparator.run(inds);

        String stats = comparator.printStats();
        System.out.println(" stats time: " + stats );
    }






}
