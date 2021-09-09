/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class OntologyUtils 
{
    
    public static void writeModeltoFile(OntModel theModel, String filename)
    {
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
            theModel.write( out, "TTL" );
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

    
    }
    
    
    
    public static boolean isObjectProperty(Node predicate, OntModel ontModel)
    {
        boolean isObjectProperty = false;
        ExtendedIterator<ObjectProperty> listObjectProperties = ontModel.listObjectProperties();
    
        while(listObjectProperties.hasNext())
        {
            ObjectProperty ob = listObjectProperties.next();
            if(ob.getURI().equalsIgnoreCase(predicate.getURI()))
                return true;
        }
            
        return isObjectProperty;
    }
    
    public static boolean isDatatypeProperty(Node predicate, OntModel ontModel)
    {
        boolean isDatatypeProperty = false;
        ExtendedIterator<DatatypeProperty> listDatatypeProperties = ontModel.listDatatypeProperties();
    
        while(listDatatypeProperties.hasNext())
        {
            DatatypeProperty ob = listDatatypeProperties.next();
            if(ob.getURI().equalsIgnoreCase(predicate.getURI()))
                return true;
        }
            
        return isDatatypeProperty;
    }
    
}
