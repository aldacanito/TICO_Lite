/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
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
    
    public static void copyProperty(OntModel newModel, Property property) 
    {
        Utilities.logInfo("COPY PROPERTY METHOD [BASIC]. \tCopying: " + property.getURI());
        Property newProperty = (Property) newModel.createProperty(property.getURI());
        
        //TODO: add more se fizer sentido no futuro
    }
    
    
    /**
     * 
     * Clones Domain, Range and InverseOf from a given OntProperty
     * 
     * @param newModel Model in which to add the property
     * @param property OntProperty to clone
     * @return 
     */
     public static OntProperty copyProperty(OntModel newModel, OntProperty property) 
     {
        Utilities.logInfo("COPY ONTPROPERTY METHOD. \tCopying: " + property.getURI());
        
        OntProperty newProperty = (OntProperty) newModel.createOntProperty(property.getURI());
        
        ExtendedIterator<? extends OntResource> listDomain = property.listDomain();
        for(OntResource r : listDomain.toList())
            newProperty.addDomain(r);
        
        ExtendedIterator<? extends OntResource> listRange = property.listRange();
        for(OntResource r : listRange.toList())
            newProperty.addRange(r);
        
        ExtendedIterator<? extends OntProperty> listInverseOf = property.listInverseOf();
        for(OntProperty p : listInverseOf.toList())
            newProperty.addInverseOf(p);
        
        return newProperty;
     }
    
    public static String propertyStats(Node property)
    {
        String stats = "\nStats for Property " + property.getURI()+ "\n";
        stats += "Local name: " +  property.getLocalName();
        return stats;
    }
     
     public static String propertyStats(OntProperty property)
     {
         String stats = "\nStats for OntProperty " + property.getURI()+ "\n";
     
        stats += "\n\tDomains: ";
        ExtendedIterator<? extends OntResource> listDomain = property.listDomain();
        for(OntResource r : listDomain.toList())
            stats+= "\n\t\t-" + r.getLabel(null);
        
        stats += "\n\tRanges: ";
        ExtendedIterator<? extends OntResource> listRange = property.listRange();
        for(OntResource r : listRange.toList())
            stats+= "\n\t\t-" + r.getLabel(null);
        
        stats += "\n\tInverse of: ";
        ExtendedIterator<? extends OntProperty> listInverseOf = property.listInverseOf();
        for(OntProperty p : listInverseOf.toList())
            stats+= "\n\t\t-" + p.getLabel(null);
        
        stats+="\n\t === \n";
       
         return stats;
     }
     
     
     
     public static String classStats(OntClass ontClass)
     {
        String stats = "\nStats for OntClass "+ontClass.getURI()+"\n";
         
        stats += "\t Comments: " + ontClass.listComments(null).toList().size();
        stats += "\t Labels: " + ontClass.listLabels(null).toList().size();    
    
        stats += "\t Declared Properties: " + ontClass.listDeclaredProperties().toList().size();
        stats += "\t Disjoint Classes: " + ontClass.listDisjointWith().toList().size();
        stats += "\t Equivalent Classes: " + ontClass.listEquivalentClasses().toList().size();
        
        stats += "\t SubClasses: " + ontClass.listSubClasses().toList().size();
        stats += "\t SuperClasses: " + ontClass.listSuperClasses().toList().size();
          
        return stats;
     }
     
     
   /**
    * 
    * Creates a copy of a given OntClass into a new OntModel.
    * 
    * Copies: 
    *   - Declared Properties
    *   - Comments & Labels
    *   - DisjointWith
    *   - Equivalent Classes
    *   - SubClasses & SuperClasses (direct)
    * 
    * @param class2Copy The Class to be copied
    * @param newModel The Model in which to create the Class
    * @return The copied Class
    */
    public static OntClass copyClass(OntClass class2Copy, OntModel newModel)
    {
        Utilities.logInfo("COPY CLASSS METHOD. \tCopying: " + class2Copy.getURI());
        
        OntClass newClass = newModel.createClass(class2Copy.getURI());
    
        // get the class data
        
        ExtendedIterator<RDFNode> listComments = class2Copy.listComments(null);
        for(RDFNode comment : listComments.toList())
            newClass.addComment((Literal) comment);
    
        ExtendedIterator<OntProperty> listDeclaredProperties = class2Copy.listDeclaredProperties();
        for(OntProperty property : listDeclaredProperties.toList())
        {
            OntProperty prt = copyProperty(newModel, property);   
            prt.addDomain(class2Copy);
        }
        
        ExtendedIterator<OntClass> listDisjointWith = class2Copy.listDisjointWith();
        for(OntClass cls : listDisjointWith.toList())
            newClass.addDisjointWith(cls);
        
        ExtendedIterator<OntClass> listEquivalentClasses = class2Copy.listEquivalentClasses();
        for(OntClass cls : listEquivalentClasses.toList())
            newClass.addEquivalentClass(cls);
        
        ExtendedIterator<RDFNode> listLabels = class2Copy.listLabels(null);
        for(RDFNode label : listLabels.toList())
            newClass.addLabel((Literal) label);
        
        ExtendedIterator<OntClass> listSubClasses = class2Copy.listSubClasses();
        for(OntClass cls : listSubClasses.toList())
            newClass.addSubClass(cls);
        
        ExtendedIterator<OntClass> listSuperClasses = class2Copy.listSuperClasses();
        for(OntClass cls : listSuperClasses.toList())
            newClass.addSuperClass(cls);
        
        return newClass;
    }
    
    
    public static String printTriple(Triple t)
    {
        String subject      = t.getSubject().toString().split("#")[1];
        String predicate    = t.getPredicate().toString().split("#")[1];
        String object       = t.getObject().toString();
        
        try
        {
            object = object.split("#")[1];
        }
        catch(Exception e)
        {
            
        }
        
        String s = "Triple:\n";
                s+="\t S: #"    + subject ;
                s+=" P: #"      + predicate;
                s+=" O: #"      + object;
            
        return s;
    }
    
    
    public static boolean isProperty(Node predicate, OntModel ontModel)
    {
        boolean isObjectProperty = false;
        ExtendedIterator<OntProperty> listOntProperties = ontModel.listOntProperties();
    
        while(listOntProperties.hasNext())
        {
            OntProperty ob = listOntProperties.next();
            if(ob.getURI().equalsIgnoreCase(predicate.getURI()))
                return true;
        }
            
        return isObjectProperty;
    }
    
    public static boolean isProperty(OntProperty predicate, OntModel ontModel)
    {
        boolean isObjectProperty = false;
        ExtendedIterator<OntProperty> listOntProperties = ontModel.listOntProperties();
    
        while(listOntProperties.hasNext())
        {
            OntProperty ob = listOntProperties.next();
            if(ob.getURI().equalsIgnoreCase(predicate.getURI()))
                return true;
        }
            
        return isObjectProperty;
    }
    
    public static boolean isObjectProperty(Property predicate, OntModel ontModel)
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
    
    public static boolean isDatatypeProperty(Property predicate, OntModel ontModel)
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
