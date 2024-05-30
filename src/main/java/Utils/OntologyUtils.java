/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.*;

import org.apache.jena.reasoner.*;
import org.apache.jena.util.iterator.ExtendedIterator;


/**
 *
 * @author shizamura
 */
public class OntologyUtils 
{
    public static final String ONT_TIME_URL     = "http://www.w3.org/2006/time";

    public static final String OWL_THING        = "http://www.w3.org/2002/07/owl#Thing";

    public static final String C_FUNCTIONAL       = "FUNCTIONAL";

    public static final String C_INVERSE_FUNCTIONAL       = "INVERSE_FUNCTIONAL";
    public static final String C_REFLEXIVE        = "REFLEXIVE";
    public static final String C_IRREFLEXIVE      = "IRREFLEXIVE";
    public static final String C_SYMMETRIC        = "SYMMETRIC";

    public static final String C_ASYMMETRIC        = "ASYMMETRIC";
    public static final String C_TRANSITIVE2      = "TRANSITIVE_2_LEVELS";
    public static final String C_TRANSITIVE3      = "TRANSITIVE_3_LEVELS";
    public static final String NOT_C_FUNCTIONAL       = "!FUNCTIONAL";
    public static final String NOT_C_INVERSE_FUNCTIONAL       = "!INVERSE_FUNCTIONAL";
    public static final String NOT_C_REFLEXIVE        = "!REFLEXIVE";
    public static final String NOT_C_IRREFLEXIVE      = "!IRREFLEXIVE";
    public static final String NOT_C_SYMMETRIC        = "!SYMMETRIC";
    public static final String NOT_C_ASYMMETRIC        = "!ASYMMETRIC";
    public static final String NOT_C_TRANSITIVE2      = "!TRANSITIVE_2_LEVELS";
    public static final String NOT_C_TRANSITIVE3      = "!TRANSITIVE_3_LEVELS";





    public static OntModel readModel(String filename)
    {
        OntModel m = ModelFactory.createOntologyModel();  
        try 
        {
           m.read(new FileReader(filename, StandardCharsets.ISO_8859_1 ), "", "TTL");

           //Ontology theOntology = m.createOntology("");
           //theOntology.addImport(m.createResource(OntologyUtils.ONT_TIME_URL));
        
        } catch (Exception e) 
        { 
           e.printStackTrace();
        }
        
        return m;
    }

    public static OntModel readModel(String filename, boolean turtle)
    {
        OntModel m = ModelFactory.createOntologyModel();
        try
        {
            if(turtle)
                m.read(new FileReader(filename, StandardCharsets.ISO_8859_1 ), "", "TTL");
            else
                m.read(new FileReader(filename, StandardCharsets.ISO_8859_1 ), "", "RDF/XML");

            //Ontology theOntology = m.createOntology("");
            //theOntology.addImport(m.createResource(OntologyUtils.ONT_TIME_URL));

        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return m;

    }


    public static boolean checkEntailment(OntModel model, Resource s, Property p, Resource o)
    {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, model);

        inf.add(s, p, o);
        inf.rebind();

        ValidityReport rp = inf.validate();
        return rp.isValid();
    }


    public static void writeFullModel(OntModel theModel, String filename)
    {
        Utilities.logInfo("================\nStarting writing model to: " + filename);
        FileWriter out=null;
        try 
        {
            out = new FileWriter( filename );
        } 
        catch (IOException e) 
        {
            Utilities.logError("Error writing model to file. Reason: ", e.getMessage());
            e.printStackTrace();
        }
        try 
        {
            theModel.write( out, Configs.prop.getProperty("model_print") );
            Utilities.logInfo("Model written to file. Filename: " + filename);
        }
        finally 
        {
           try 
           {
               out.close();
           }
           catch (IOException closeException) 
           {
               // ignore
           }
        }
        
        Utilities.logInfo("================\nFinished writing model to: " + filename);
    
    }

    public static void writeModeltoFile(OntModel theModel, String filename)
    {
        writeFullModel(theModel, filename);
    }



    public static boolean removeIndividual(String individualURI, OntModel model)
    {
        try
        {
            Individual i = model.getIndividual(individualURI);

            /*
            List<Statement> propertyStmtList    = i.listProperties().toList();
            List<OntClass> ontClasses           = i.listOntClasses(false).toList();

            for(OntClass cls : ontClasses)
                i.removeOntClass(cls);
*/
           // i.removeProperties();
            i.remove();

        }
        catch (Exception e)
        {
            System.out.println("Error deleting individual " + individualURI + ". Reason:" + e.getMessage());
            return false;
        }


        return true;
    }

    public static Individual copyIndividual(Individual instance, OntModel model)
    {
        String iUri        = instance.getURI();
        Individual new_ind = model.createIndividual( iUri, instance.getOntClass(true));

        //copy all classes
        List<OntClass> oldClasses = instance.listOntClasses(true).toList();
        for(OntClass oldClass: oldClasses)
        {
            if(oldClass.getURI() != null)
            {
                model.createClass(oldClass.getURI());
                new_ind.addOntClass(oldClass);
            }

        }
        
        // copy all properties
        List<Statement> propertyStmtList = instance.listProperties().toList();
        for(Statement stmt : propertyStmtList)
        {
            Property predicate = stmt.getPredicate();
            RDFNode object     = stmt.getObject();
            
            Property newProperty = model.getProperty(predicate.getURI());
            if(newProperty==null)
                model.createProperty(predicate.getLocalName());
            
            if(object.isURIResource()) // objectProperty
            {
                Resource asResource     = object.asResource();
                OntResource ontResource = model.getOntResource(asResource.getURI());
                RDFNode newObj;
                
                if(ontResource==null) // nao existe no modelo novo
                    newObj = model.createIndividual(asResource.getURI(), null);
                else
                    newObj = ontResource;
                
                new_ind.addProperty(newProperty, newObj);
            }
            else if(object.isLiteral())
            {
                RDFDatatype datatype = object.asLiteral().getDatatype();
                String value         =  object.asLiteral().getString();
                new_ind.addProperty(newProperty, value, datatype);
            }
        }
        return new_ind;
    }
    
    
}
