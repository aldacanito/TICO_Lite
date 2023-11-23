package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.Configs;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

public class RunPropertyConstructors
{

    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();

    public static void main(String[] args)
    {
        Configs configs = new Configs();





    }




    public static void runComparator(String roundName)
    {
        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);
        Comparator comparator = new Comparator();

        comparator.run();

        String stats = comparator.printStats();
        System.out.println(roundName + " stats time: " + stats );

    }




}
