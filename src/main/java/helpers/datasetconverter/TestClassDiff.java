/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.datasetconverter;

import IDC.Comparison.Impl.Simple.ClassDiff;
import Utils.Configs;
import Utils.OntologyUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author shizamura
 */
public class TestClassDiff {

    /**
     * @param args the command line arguments
     */
    static OntModel model = ModelFactory.createOntologyModel();


    public static void test1()
    {
        OntClass cls1 = model.createClass("Class1");
        OntClass cls2 = model.createClass("Class2");
        OntClass cls3 = model.createClass("Class3");
        OntClass cls4 = model.createClass("Class4");

        addHasBeginning(cls1, "yadda1");
        addHasBeginning(cls2, "yadda2");

        boolean newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER FALSO. É " + newVersion );

        addRestriction(cls1, "property1");

        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER VERDADEIRO. É " + newVersion );

        addRestriction(cls2, "property1");
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER FALSO. É " + newVersion );

        cls1.addSuperClass(cls3);
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER VERDADEIRO. É " + newVersion );

        cls2.addSuperClass(cls3);
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER FALSO. É " + newVersion );

        cls1.addEquivalentClass(cls4);
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER VERDADEIRO. É " + newVersion );

        cls2.addEquivalentClass(cls4);
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER FALSO. É " + newVersion );


        addRestriction(cls2, "property2");
        addRestriction(cls1, "property3");
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER VERDADEIRO. É " + newVersion );


    }

    public static void testTimeSliceComparison()
    {
        boolean newVersion = false;
        String prefix = "http://localhost:3030/TimeSliceQueryTester/";


        OntClass cls1   = model.createClass(prefix + "Class1");
        OntClass cls2   = model.createClass(prefix + "Class2");
        OntClass cls3   = model.createClass(prefix + "Class3");
        OntClass cls4   = model.createClass(prefix + "Class4");
        OntClass tscls1 = model.createClass(prefix + "TS__Class1__1");

        addIsTimeSliceOf(tscls1, cls1);
        addBefore(tscls1, cls1);
        addHasDuration(tscls1, "yadda1");
        addHasBeginning(tscls1, "yadda1");

        addRestriction(cls1, prefix + "property1");
        addRestriction(tscls1, prefix + "property1");

        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("The two classes have the same cardinality restriction on property1. " +
                "Should be false. Is " + newVersion);

        addRestriction(cls1, prefix + "property2");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("Cls1 has a new Cardinality restriction on property2. " +
                "Should be true. Is " + newVersion);

        addRestriction(tscls1, prefix + "property2");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("Timeslice also has a new Cardinality restriction on property2. Should be false. Is " + newVersion);


        addValuesFromRestriction(tscls1, cls2, prefix + "property3", "someValuesFrom");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("TimeSlice has a someValuesFrom on property3. " +
                "Should be true. Is " + newVersion);


        addValuesFromRestriction(cls1, cls2, prefix + "property3", "someValuesFrom");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("Class1 also has a someValuesFrom on property3. Should be false. Is " + newVersion);

        addValuesFromRestriction(tscls1, cls2, prefix + "property4", "allValuesFrom");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("TimeSlice has an allValuesFrom on property4. " +
                "Should be true. Is " + newVersion);

        addValuesFromRestriction(cls1, cls2, prefix + "property4", "allValuesFrom");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("Class1 also has an allValuesFrom on property4. " +
                "Should be false. Is " + newVersion);

        addValuesFromRestriction(tscls1, cls2, prefix + "property5", "hasValue");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("TimeSlice has a hasValue on property5. Should be true. Is " + newVersion);

        addValuesFromRestriction(cls1, cls2, prefix + "property5", "hasValue");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("Class1 also has a hasValue on property5. Should be false. Is " + newVersion);

        addValuesFromRestriction(cls1, cls4, prefix + "property4", "allValuesFrom");
        addValuesFromRestriction(tscls1, cls3, prefix + "property4", "allValuesFrom");
        newVersion = new ClassDiff ().isNewVersion(cls1, tscls1);
        System.out.println("TimeSlice and Class1 have different ranges for property4. " +
                "Should be true. Is " + newVersion);



        //OntologyUtils.writeModeltoFile(model, "Indexes/TestOnto/testDiffFile.ttl");

    }

    public static void main(String[] args) 
    {
        Configs configs = new Configs();
        
        testTimeSliceComparison();
        //test1();
        
    }
    
    public static void addRestriction(OntClass cls, String prop)
    {
        OntProperty ontProperty = model.createObjectProperty(prop);

        MaxCardinalityRestriction restriction = model.createMaxCardinalityRestriction(null, ontProperty, 0);     
        cls.addSuperClass(restriction);
    }


    public static void addValuesFromRestriction(OntClass cls, OntClass range, String prop, String type)
    {
        OntProperty ontProperty = model.createObjectProperty(prop);

        switch(type)
        {
            case "someValuesFrom":
            {
                SomeValuesFromRestriction r = model.createSomeValuesFromRestriction(null, ontProperty, range);
                cls.addSuperClass(r);
                break;
            }
            case "allValuesFrom":
            {
                AllValuesFromRestriction r = model.createAllValuesFromRestriction(null, ontProperty, range);
                cls.addSuperClass(r);
                break;
            }
            case "hasValue":
            {
                HasValueRestriction r = model.createHasValueRestriction(null, ontProperty, range);
                cls.addSuperClass(r);
                break;
            }

        }


    }







    public static void addHasDuration(OntClass cls1, String date)
    {

        OntProperty ontProperty = model.getOntProperty(OntologyUtils.DURING_P);

        if(ontProperty == null)
            ontProperty = model.createObjectProperty(OntologyUtils.DURING_P, false);

        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = model.createClass(OntologyUtils.INSTANT_CLS);

        Individual date1 = model.createIndividual(date, instantClass);

        HasValueRestriction createHasValueRestriction = model.createHasValueRestriction(null, ontProperty, date1);

        cls1.addSuperClass(createHasValueRestriction);
    }

    public static void addIsTimeSliceOf(OntClass cls1, OntClass cls2)
    {

        OntProperty ontProperty = model.getOntProperty(OntologyUtils.IS_SLICE_OF_P);

        if(ontProperty == null)
            ontProperty = model.createObjectProperty(OntologyUtils.IS_SLICE_OF_P, false);

        HasValueRestriction createHasValueRestriction = model.createHasValueRestriction(null, ontProperty, cls2);
        cls1.addSuperClass(createHasValueRestriction);
    }

    public static void addBefore(OntClass cls1, OntClass cls2)
    {

        OntProperty ontProperty = model.getOntProperty(OntologyUtils.BEFORE_P);

        if(ontProperty == null)
            ontProperty = model.createObjectProperty(OntologyUtils.BEFORE_P, false);

        HasValueRestriction createHasValueRestriction = model.createHasValueRestriction(null, ontProperty, cls2);
        cls1.addSuperClass(createHasValueRestriction);
    }



    public static void addHasBeginning(OntClass cls, String date)
    {
        OntProperty ontProperty = model.getOntProperty(OntologyUtils.HAS_BEGINNING_P);

        if(ontProperty == null)
            ontProperty = model.createObjectProperty(OntologyUtils.HAS_BEGINNING_P, false);

        OntClass instantClass = model.getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = model.createClass(OntologyUtils.INSTANT_CLS);

        Individual date1 = model.createIndividual(date, instantClass);

        HasValueRestriction createHasValueRestriction = model.createHasValueRestriction(null, ontProperty, date1);

        cls.addSuperClass(createHasValueRestriction);

    }
    
}
