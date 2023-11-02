package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;

public class TestConferenceInstances
{

    public static void main(String[] args)
    {
        Configs configs = new Configs();

        String folder = "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\Conference Subset";

        OntModel originalModel = OntologyUtils.readModel("C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\DatasetConverter\\OntoProcessMapping\\PopulatedConference\\conference_eswc.ttl");
        OntModel evolvingModel = OntologyUtils.readModel("C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\DatasetConverter\\OntoProcessMapping\\PopulatedConference\\conference_eswc.ttl");

        OntModel instanceModel = ModelFactory.createOntologyModel();


        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);



        File dir = new File(folder);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null)
        {
            for (File child : directoryListing)
            {

                try {
                    String fileName = child.getAbsolutePath();

                    if(fileName.contains(".rdf"))
                    {
                        OntModel ontModel = OntologyUtils.readModel(child.getAbsolutePath(), false);
                        instanceModel.add(ontModel);

                        System.out.println("Added instance file " + child.getAbsolutePath());
                    }
                }catch(Exception e)
                {
                    System.out.println("Exception reading model. Reason: " + e.getMessage());
                }

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }

        System.out.println("Finished reading instance files.");

        Comparator comparator = new Comparator();
        comparator.run();

        String stats = comparator.printStats();
        System.out.println(" stats time: " + stats );

    }




}
