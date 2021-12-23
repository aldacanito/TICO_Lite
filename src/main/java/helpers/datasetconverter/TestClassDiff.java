/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.datasetconverter;

import IntanceDrivenComparison.Comparison.Implementations.Simple.ClassDiff;
import Utils.Configs;
import Utils.OntologyUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
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
    
    public static void main(String[] args) 
    {
        Configs configs = new Configs();
        
        OntClass cls1 = model.createClass("Class1");
        OntClass cls2 = model.createClass("Class2");
        
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
        
        addRestriction(cls2, "property2");
        addRestriction(cls1, "property3");
        newVersion = new ClassDiff ().isNewVersion(cls1, cls2);
        System.out.println("DEVE SER VERDADEIRO. É " + newVersion );
        
    }
    
    public static void addRestriction(OntClass cls, String prop)
    {
        OntProperty ontProperty = model.createObjectProperty(prop);
   
        MaxCardinalityRestriction restriction = model.createMaxCardinalityRestriction(null, ontProperty, 0);     
        cls.addSuperClass(restriction);
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
