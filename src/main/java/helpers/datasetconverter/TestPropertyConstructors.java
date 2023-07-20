package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

public class TestPropertyConstructors
{

    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();


    static String prefix = "http://localhost/testing#";
    public static void main(String[] args)
    {
        Configs configs = new Configs();

        createSymmetricExamples  (instanceModel, 5);
        createIrreflexiveExamples(instanceModel, 5);
        createReflexiveExamples  (instanceModel, 5);
        createTransitiveExamples (instanceModel, 5);

        OntologyUtils.writeModeltoFile(instanceModel, "SimpleTestCase/testIndividualGeneration.ttl");

        runComparator("r1");

        OntologyUtils.writeModeltoFile(evolvingModel, "SimpleTestCase/testIndividualGeneration_evolved.ttl");

    }


    private static void createSymmetricExamples(OntModel theModel, int count)
    {
        OntClass cls1       = theModel.createClass(prefix + "SYM_CLS_" + RandomStringUtils.random(3, true, false));
        OntClass cls2       = theModel.createClass(prefix + "SYM_CLS_" + RandomStringUtils.random(3, true, false));

        String baseName    = prefix + "SYM_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "SYM_OBJPROP_" + RandomStringUtils.random(3, true, false);

        ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

        for(int i = 1; i <= count; i = i + 2 )
        {
            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
            i2.addProperty(prop, i1);
        }

    }

    private static void createIrreflexiveExamples(OntModel theModel, int count)
    {
        String baseName    = prefix + "IRR_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "IRR_OBJPROP_" + RandomStringUtils.random(3, true, false);

        ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

        for(int i = 1; i <= count; i++)
        {
            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            OntClass cls1 = theModel.createClass(prefix + "IRR_CLS_" + RandomStringUtils.random(3, true, false));
            OntClass cls2 = theModel.createClass(prefix + "IRR_CLS_" + RandomStringUtils.random(3, true, false));

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
        }

    }

    private static void createReflexiveExamples(OntModel theModel, int count)
    {
        OntClass cls       = theModel.createClass(prefix + "RR_CLS_" + RandomStringUtils.random(3, true, false));
        String baseName    = prefix + "RR_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "RR_OBJPROP_" + RandomStringUtils.random(3, true, false);

        ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

        for(int i = 1; i <= count; i++)
        {
            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls);
            Individual i2 = theModel.createIndividual(ind2_name, cls);

            i1.addProperty(oProp, i2);
        }

    }

    private static void createTransitiveExamples(OntModel theModel, int count)
    {
        OntClass cls       = theModel.createClass(prefix + "TRANS_CLS_" + RandomStringUtils.random(3, true, false));
        String baseName    = prefix + "TRANS_IND_"  + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "TRANS_OBJPROP_" + RandomStringUtils.random(3, true, false);

        ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

        for(int i = 1; i <= count; i++)
        {
            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls);
            Individual i2 = theModel.createIndividual(ind2_name, cls);

            i1.addProperty(oProp, i2);
        }
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
