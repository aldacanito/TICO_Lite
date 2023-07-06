package helpers.datasetconverter;

import IDC.Comparator;
import IDC.ModelManager;
import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;

public class BasicEvolvingTest
{
    static OntModel originalModel = ModelFactory.createOntologyModel();
    static OntModel evolvingModel = ModelFactory.createOntologyModel();
    static OntModel instanceModel = ModelFactory.createOntologyModel();


    static String prefix = "http://localhost/testing#";
    public static void main(String[] args)
    {
        Configs configs = new Configs();
        setupModels1();
        runComparator();
    }



    public static OntModel setUpOriginalModel()
    {
        OntModel theModel = ModelFactory.createOntologyModel();

        OntClass cls1 = theModel.createClass(prefix + "Class1");
        OntClass cls2 = theModel.createClass(prefix + "Class2");

        OntProperty ontProperty = theModel.createObjectProperty(prefix + "ObjectProperty1", false);
        HasValueRestriction createHasValueRestriction = theModel.createHasValueRestriction(null, ontProperty, cls2);
        cls1.addSuperClass(createHasValueRestriction);

        DatatypeProperty dtProperty = theModel.createDatatypeProperty(prefix + "DatatypeProperty1", false);
        Literal l = theModel.createTypedLiteral(24);
        HasValueRestriction hasValueRestriction = theModel.createHasValueRestriction(null, dtProperty, l);

        cls2.addSuperClass(hasValueRestriction);

        return theModel;
    }

    public static OntModel setupInstanceModel()
    {
        OntModel theModel = ModelFactory.createOntologyModel();
        OntClass cls3     = theModel.createClass(prefix + "Class3");

        Individual i1 = theModel.createIndividual(prefix + "i1", cls3);
        Individual i2 = theModel.createIndividual(prefix + "i2", cls3);
        Individual i3 = theModel.createIndividual(prefix + "i3", cls3);

        DatatypeProperty dtProperty2 = theModel.createDatatypeProperty(prefix + "DatatypeProperty2", false);
        Literal l2 = theModel.createTypedLiteral(2);
        Literal l3 = theModel.createTypedLiteral(3);


        i1.addLiteral(dtProperty2, l2);
        i2.addLiteral(dtProperty2, l2);
        i3.addLiteral(dtProperty2, l3);

        return theModel;
    }


    public static OntModel setupSecondInstanceModel()
    {
        OntModel theModel = ModelFactory.createOntologyModel();
        OntClass cls2     = theModel.createClass(prefix + "Class2");

        OntClass cls3     = theModel.createClass(prefix + "Class3");
        OntClass cls4     = theModel.createClass(prefix + "Class4");

        Individual i1 = theModel.createIndividual(prefix + "i1", cls3);
        Individual i2 = theModel.createIndividual(prefix + "i2", cls3);
        Individual i3 = theModel.createIndividual(prefix + "i3", cls3);

        ObjectProperty obProperty2 = theModel.createObjectProperty(prefix + "ObjectProperty2", false);
        ObjectProperty obProperty3 = theModel.createObjectProperty(prefix + "ObjectProperty3", false);

        i1.addProperty(obProperty3, cls4);
        i2.addProperty(obProperty3, cls4);
        i3.addProperty(obProperty2, cls3);

        return theModel;
    }

    public static void setupRound3()
    {

    }



    public static void setupModels1()
    {
        evolvingModel = setUpOriginalModel();
        originalModel = setUpOriginalModel();
        instanceModel = setupInstanceModel();
        instanceModel = setupSecondInstanceModel();
    }

    public static void runComparator()
    {
        ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);
        Comparator comparator = new Comparator();

        comparator.run();

        String stats = comparator.printStats();
        System.out.println("Stats time: " + stats );

        //System.out.println(evolvingModel.toString());

        OntologyUtils.writeModeltoFile(ModelManager.getManager().getEvolvingModel(), "SimpleTestCase/result.ttl");
    }
}
