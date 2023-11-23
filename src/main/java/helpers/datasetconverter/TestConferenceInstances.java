package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.AnalyticUtils;
import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;

public class TestConferenceInstances
{

    static String base = "C:\\Users\\Alda\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\DatasetConverter\\";
    static String datasetsFolder = base + "PopulatedDatasets/";

    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();

    public static void main(String[] args)
    {
        Configs configs = new Configs();

        //String [] datasets = {"cmt", "conference", "confOf", "edas", "ekaw", "gmo"};

        String [] datasets = {"cmt"};

        for(String dataset : datasets)
            run(dataset);

    }

    public static void run(String ontologyName)
    {
        String folder = datasetsFolder + ontologyName + "/";
        String instancesFolder = folder + "/instances/";

        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/"+ontologyName+"/Constructors/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + ontologyName;

        originalModel = OntologyUtils.readModel(folder + ontologyName + ".ttl");
        evolvingModel = OntologyUtils.readModel(folder + ontologyName + ".ttl");
        instanceModel = ModelFactory.createOntologyModel();

        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

        // read all instances files in the instance folder
        File dir                = new File(instancesFolder);
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
        comparator.run();

        String stats = comparator.printStats();
        System.out.println(" stats time: " + stats );
    }




}
