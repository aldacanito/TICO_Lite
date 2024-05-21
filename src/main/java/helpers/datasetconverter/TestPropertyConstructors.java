package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.AnalyticUtils;
import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestPropertyConstructors
{

    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();
    static String base = "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\TICO\\";
    static String instanceModelFile = base + "\\Analytics\\testPropertyAxioms\\instances.ttl";
    static List<String> individual_uris;
    static String prefix = "http://localhost/testing#";

    public static void createExamples()
    {
        //createFunctionalExamples(instanceModel, 40);
        //createFunctionalCounterExamples(instanceModel, 60);

        //createTransitiveExamples (instanceModel, 50);
        //createTransitiveCounterExamples (instanceModel, 50);

        //createSymmetricExamples  (instanceModel, 50);
        //createSymmetricCounterExamples  (instanceModel, 50);

        //createAsymmetricExamples  (instanceModel, 50);
        //createAsymmetricCounterExamples  (instanceModel, 50);

        //createReflexiveExamples  (instanceModel, 50);
        //createReflexiveCounterExamples  (instanceModel, 50);

        createIrreflexiveExamples(instanceModel, 50);
        createIrreflexiveCounterExamples(instanceModel, 50);
        //
        ExtendedIterator<Individual> individualExtendedIterator = instanceModel.listIndividuals();

        for(Individual i : individualExtendedIterator.toList())
        {
            if(!i.getURI().isEmpty())
                individual_uris.add(i.getURI());
        }

        OntologyUtils.writeModeltoFile(instanceModel, "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\TICO\\Analytics\\testPropertyAxioms\\instances.ttl");

    }
    public static void main(String[] args)
    {
        Configs configs = new Configs();
        individual_uris = new ArrayList<String>();

        createExamples();

        runComparator("r1");

        //OntologyUtils.writeModeltoFile(evolvingModel, "SimpleTestCase/testIndividualGeneration_evolved.ttl");

    }

    private static void createFunctionalCounterExamples(OntModel theModel, int count)
    {

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "SOURCE_IND_" + RandomStringUtils.random(5, true, false);
        String baseRName   = prefix + "RANGE_IND_"  + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "OP_COUNTER";

        ObjectProperty prop1 = instanceModel.createObjectProperty(propertyURI + "_1");
        ObjectProperty prop2 = instanceModel.createObjectProperty(propertyURI + "_2");


        // two properties but they're not pointing at the same object
        for(int i = 0 ; i <= count / 2 ; i++)
        {
            String ind1_name = baseName  + "_" + RandomStringUtils.random(5, true, false);
            String ind2_name = baseRName + "_" + RandomStringUtils.random(5, true, false) ;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addDifferentFrom(i2);

            i1.addProperty(prop1, i1);
            i1.addProperty(prop1, i2);

/*
            String ind3_name = baseName  + "_" + RandomStringUtils.random(5, true, false);
            String ind4_name = baseRName + "_" + RandomStringUtils.random(5, true, false) ;

            Individual i3 = theModel.createIndividual(ind3_name, cls1);
            Individual i4 = theModel.createIndividual(ind4_name, cls1);

            i3.addDifferentFrom(i4);

            i1.addProperty(prop2, i3);
            i1.addProperty(prop2, i4);
*/
        }





    }
    private static void createFunctionalExamples(OntModel theModel, int count)
    {

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );
        OntClass cls3       = theModel.createClass(prefix + "CLS3" );

        String baseName     = prefix + "SOURCE_IND_" + RandomStringUtils.random(5, true, false);
        String baseRName    = prefix + "RANGE_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI  = prefix + "OP_E";

        ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

        // one property
        int halfCount = count / 2;
        for(int i = 0 ; i < halfCount ; i++)
        {
            String ind1_name = baseName  + "_" + RandomStringUtils.random(5, true, false);
            String ind2_name = baseRName + "_" + RandomStringUtils.random(5, true, false) ;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
        }

        // two properties, but they point to the same object
        for(int i = halfCount ; i <= count ; i++)
        {
            String ind1_name = baseName  + "_" + RandomStringUtils.random(5, true, false);
            String ind2_name = baseRName + "_" + RandomStringUtils.random(5, true, false) ;
            String ind3_name = baseRName + "_" + RandomStringUtils.random(5, true, false) ;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);
            Individual i3 = theModel.createIndividual(ind3_name, cls3);

            i3.addSameAs(i2);

            i1.addProperty(prop, i2);
            i1.addProperty(prop, i3);
        }

    }
    private static void createSymmetricExamples(OntModel theModel, int count)
    {

        /**
        OntClass cls1       = theModel.createClass(prefix + "SYM_CLS_" + RandomStringUtils.random(3, true, false));
        OntClass cls2       = theModel.createClass(prefix + "SYM_CLS_" + RandomStringUtils.random(3, true, false));

        String baseName    = prefix + "SYM_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "SYM_OBJPROP_" + RandomStringUtils.random(3, true, false);
        */

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IND_SYMM" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "SYMMETRICPROP";

        for(int i = 1; i <= count; i = i + 2 )
        {

            ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
            i2.addProperty(prop, i1);

/*
            if(new Random().nextInt(100)>=10)
            {
                String ind3_name =  prefix + "IND_" + RandomStringUtils.random(5, true, false);
                Individual i3 = theModel.createIndividual(ind3_name, cls2);
                i3.addProperty(prop, i1);
            }
*/

        }

    }

    private static void createSymmetricCounterExamples(OntModel theModel, int count)
    {

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IND_NOTSYMM" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "NOTSYMMETRICPROP";

        for(int i = 1; i <= count; i = i + 2 )
        {
            ObjectProperty prop  = instanceModel.createObjectProperty(propertyURI + "_1");
            ObjectProperty prop2 = instanceModel.createObjectProperty(propertyURI + "_2");

            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
            i2.addProperty(prop2, i1);

/*
            if(new Random().nextInt(100)>=10)
            {
                String ind3_name =  prefix + "IND_" + RandomStringUtils.random(5, true, false);
                Individual i3 = theModel.createIndividual(ind3_name, cls2);
                i3.addProperty(prop, i1);
            }
*/

        }

    }


    private static void createAsymmetricExamples(OntModel theModel, int count)
    {

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IND_ASYMM" + RandomStringUtils.random(5, true, false);
        String baseName1    = prefix + "IND_ASYMM" + RandomStringUtils.random(5, true, false);

        String propertyURI = prefix + "ASYMMETRICPROP";

        for(int i = 1; i <= count; i = i + 2 )
        {
            ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName1 + "_" + i;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);
        }

    }

    private static void createAsymmetricCounterExamples(OntModel theModel, int count)
    {
        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IND_NOTASYMM" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "NOT_ASYMMETRICPROP";

        for(int i = 1; i <= count; i = i + 2 )
        {
            ObjectProperty prop  = instanceModel.createObjectProperty(propertyURI + "_1");

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


        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IRR_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "IRREFLEXIVE_OP" ;

        for(int i = 1; i <= count; i++)
        {

            ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls2);

            i1.addProperty(prop, i2);

        }

    }

    private static void createIrreflexiveCounterExamples(OntModel theModel, int count)
    {
        /*
        String baseName    = prefix + "IRR_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "IRR_OBJPROP_" + RandomStringUtils.random(3, true, false);
        */

        OntClass cls1       = theModel.createClass(prefix + "CLS1" );
        OntClass cls2       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "NOT_IRR_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "NOT_IRREFLEXIVE" ;

        for(int i = 1; i <= count; i++)
        {

            ObjectProperty prop = instanceModel.createObjectProperty(propertyURI);

            String ind1_name = baseName + "_" + i;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);

            i1.addProperty(prop, i1);


        }

    }
    private static void createReflexiveExamples(OntModel theModel, int count)
    {
        OntClass cls1       = theModel.createClass(prefix + "CLS1" );

        String baseName    = prefix + "R_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "REFLEXIVE";

        for(int i = 1; i <= count; i++)
        {

            ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

            String ind1_name = baseName + "_" + i;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);

            i1.addProperty(oProp, i1);



        }

    }

    private static void createReflexiveCounterExamples(OntModel theModel, int count)
    {
        OntClass cls1       = theModel.createClass(prefix + "CLS1" );

        String baseName    = prefix + "NOT_R_IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "NOT_REFLEXIVE";

        for(int i = 1; i <= count; i++)
        {
            ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls1);
            Individual i2 = theModel.createIndividual(ind2_name, cls1);

            i1.addProperty(oProp, i2);

        }

    }
    private static void createTransitiveExamples(OntModel theModel, int count)
    {
        OntClass cls       = theModel.createClass(prefix + "CLS1" );

        String baseName    = prefix + "IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "TRANSPROP";

/**
        OntClass cls       = theModel.createClass(prefix + "TRANS_CLS_" + RandomStringUtils.random(3, true, false));
        String baseName    = prefix + "TRANS_IND_"  + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "TRANS_OBJPROP_" + RandomStringUtils.random(3, true, false);
*/

        for(int i = 1; i <= count; i++)
        {

            /*
            if(new Random().nextInt(100)>=50)
                propertyURI = prefix + "OP" + "1";
            else
                propertyURI = prefix + "OP" + "2";
            */

            ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

            int next = i+1;
            String ind1_name = baseName + "_" + i;
            String ind2_name = baseName + "_" + next;

            Individual i1 = theModel.createIndividual(ind1_name, cls);
            Individual i2 = theModel.createIndividual(ind2_name, cls);

            i1.addProperty(oProp, i2);

            //if(new Random().nextInt(100)>=10)
            //{
            String ind3_name =  prefix + "IND_" + RandomStringUtils.random(5, true, false);
            Individual i3 = theModel.createIndividual(ind3_name, cls);
            i1.addProperty(oProp, i3);
            //}

        }
    }

    private static void createTransitiveCounterExamples(OntModel theModel, int count)
    {
        OntClass cls       = theModel.createClass(prefix + "CLS2" );

        String baseName    = prefix + "IND_" + RandomStringUtils.random(5, true, false);
        String propertyURI = prefix + "NONTRANSOP";

/**
 OntClass cls       = theModel.createClass(prefix + "TRANS_CLS_" + RandomStringUtils.random(3, true, false));
 String baseName    = prefix + "TRANS_IND_"  + RandomStringUtils.random(5, true, false);
 String propertyURI = prefix + "TRANS_OBJPROP_" + RandomStringUtils.random(3, true, false);
 */

        for(int i = 1; i <= count; i++)
        {

            ObjectProperty oProp = instanceModel.createObjectProperty(propertyURI);

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
        String ontologyName = "testPropertyAxioms";
        int window_size = 100;

        AnalyticUtils.ONTO_NAME = ontologyName;
        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/"+ontologyName+"/Constructors" + window_size + "/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + ontologyName;
        Configs.windowSize = window_size;

        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

        OntModel ontModel = OntologyUtils.readModel(instanceModelFile, true);
        instanceModel.add(ontModel);

        Comparator comparator = new Comparator();

        //comparator.run();
        comparator.run(individual_uris);

        String stats = comparator.printStats();
        System.out.println(roundName + " stats time: " + stats );

    }

}
