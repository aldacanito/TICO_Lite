/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import IDC.EvolActions.Impl.Additions.AddClass;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import IDC.ModelManager;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;


import org.apache.jena.util.iterator.ExtendedIterator;

import javax.swing.*;

/**
 *
 * @author shizamura
 */
public class OntologyUtils 
{
    public static final String INSTANT_CLS      = "http://www.w3.org/2006/time#Instant";
    public static final String HAS_ENDING_P     = "http://www.w3.org/2006/time#hasEnd";
    public static final String HAS_BEGINNING_P  = "http://www.w3.org/2006/time#hasBeginning";
    public static final String HAS_TIMESTAMP_P  = "http://www.example.org/CM/CM_ontology.owl#hasTimeStamp";
    public static final String BEFORE_P         = "http://www.w3.org/2006/time#before";
    public static final String AFTER_P          = "http://www.w3.org/2006/time#after";
    public static final String INTERVAL_CLS     = "http://www.w3.org/2006/time#Interval";
    public static final String HAS_SLICE_P      = "http://www.w3.org/2006/time#hasTimeSlice";
    public static final String DURING_P         = "http://www.w3.org/2006/time#hasDuration";
    public static final String IS_SLICE_OF_P    = "http://www.w3.org/2006/time#isTimeSliceOf";
    public static final String ONT_TIME_URL     = "http://www.w3.org/2006/time";

    public static final String OWL_THING        = "http://www.w3.org/2002/07/owl#Thing";


    /**
     * Gets the Version of an OntModel
     * @param model
     * @return one of "original", "evolving" or "individuals"
     */
    public static String getModelVersion(OntModel model)
{
    String version = "original";

    try
    {
        OntResource versionInfoR = model.getOntResource("VersionInfo");
        Statement version1 = versionInfoR.getProperty(model.getAnnotationProperty("version"));
        Literal lit = version1.getLiteral();
        version = lit.toString();
    }
    catch(Exception e)
    {}


    return version;
}
    public static OntModel readModel(String filename, String baseURI)
    {

        OntModel m = ModelFactory.createOntologyModel();
        try
        {
            m.read(new FileReader(filename, StandardCharsets.ISO_8859_1 ), baseURI, "TTL");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return m;
    }

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
    
    
    public static void writeInstanceModel(String filename)
    {
        OntModel theModel = OntologyUtils.readModel(filename);
        
        filename = filename.replace(".ttl", "");
        filename += "_instances.ttl";
        
        List<OntClass> toList = theModel.listClasses().toList();
       
        
        for(OntClass ontClass : toList)
        {
            ontClass.remove();
        }
       
        OntologyUtils.writeFullModel(theModel, filename);
        
    }
    
    public static void writeClassesModel(String filename)
    {
        System.out.println("Write Classes Model begin.");
        OntModel theModel = OntologyUtils.readModel(filename);
        
        filename = filename.replace(".ttl", "");
        filename += "_classes.ttl";
        
        List<Individual> toList = theModel.listIndividuals().toList();        
        for(Individual i : toList)
        {   
            if(i.getOntClass().getURI().equalsIgnoreCase(INSTANT_CLS))
                System.out.println("Instant individual: " + i.getURI());
            
            if(i.getOntClass().getURI().equalsIgnoreCase(INTERVAL_CLS))
                System.out.println("Interval individual: " + i.getURI());
            

            if(!Utilities.isInIgnoreList(i.getURI()))
                 i.remove();
        }
       
        OntologyUtils.writeFullModel(theModel, filename);
        System.out.println("Write Classes Model finished.");
        
    }
    
    public static void writeClassesModel(OntModel theModel, String filename)
    {
        System.out.println("Write Classes Model begin.");

        filename = filename.replace(".ttl", "");
        filename += "_classes.ttl";
        
        List<Individual> toList = theModel.listIndividuals().toList();
        System.out.println("Individuals List obtained");        

        for(Individual i : toList)
        {
            /**
            if(i.getOntClass().getURI().equalsIgnoreCase(INSTANT_CLS))
            {
                System.out.println("Instant individual: " + i.getURI());
                continue;
            }
            
            if(i.getOntClass().getURI().equalsIgnoreCase(INTERVAL_CLS))
            {
                System.out.println("Interval individual: " + i.getURI());
                continue;
            }
            **/

            if(!Utilities.isInIgnoreList(i.getURI()))
                 i.remove();
        }
       
        OntologyUtils.writeFullModel(theModel, filename);
        System.out.println("Write Classes Model finished.");
        
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
   //     writeInstanceModel(filename);
        //writeClassesModel(theModel, filename);
    }


    public static List<OntClass> getTimeSlices(OntClass ontClass)
    {
        return SPARQLUtils.getTimeSlicesSPARQL(ontClass);
    }
    public static List<OntClass> getTimeSlicesJENA(OntClass theOgClass)
    {
        List<OntClass> timeSlices = new ArrayList<>();
        
        OntProperty sliceP      = theOgClass.getOntModel().getObjectProperty(OntologyUtils.HAS_SLICE_P);
        if(sliceP==null) sliceP = theOgClass.getOntModel().createOntProperty(OntologyUtils.HAS_SLICE_P);
        
        ExtendedIterator<OntClass> superClasses = theOgClass.listSuperClasses(true);
        
        for(OntClass superClass : superClasses.toList())
        {
            if(superClass.isRestriction())
            {
                Restriction superClsR = superClass.asRestriction();
                if(superClsR.isSomeValuesFromRestriction())
                {
                    SomeValuesFromRestriction sCls = superClsR.asSomeValuesFromRestriction();
                    if(sCls.getOnProperty().getURI().equals(sliceP.getURI()))
                    {
                        RDFNode valuesFrom = sCls.getSomeValuesFrom();
                        if(valuesFrom.canAs(OntClass.class))
                            timeSlices.add(valuesFrom.as(OntClass.class));
                    }
                }
                
            }
        }
                   
        return timeSlices;
        
    }

    public static int getSliceNumber(OntClass theSlice)
    {
        String oldURI = theSlice.getURI();

        String olds     []= oldURI.split("#");
        String className  = olds[1];
        int versionNumber = 0;

        // ja existe
        if(className.contains("TS__"))
        {
            String[] split = className.split("__"); //  TS__CLASSNAME__VERSIONNUMBER
            try
            {
                versionNumber = Integer.parseInt(split[2]);
            }
            catch(Exception e)
            {
                System.out.println("Error split/converting string " + oldURI + ". Error: " + e.getLocalizedMessage());
                versionNumber = 0;
            }
        }

        versionNumber++;
        return versionNumber;
    }
    
    
    public static Individual getBeforeInstant(Individual instance)
    {
        OntModel model = instance.getOntModel();
        
        OntProperty       beforeP = instance.getOntModel().getObjectProperty(OntologyUtils.BEFORE_P);
        if(beforeP==null) beforeP = instance.getOntModel().createOntProperty(OntologyUtils.BEFORE_P);
        
        OntProperty       duringP = instance.getOntModel().getObjectProperty(OntologyUtils.DURING_P);
        if(duringP==null) duringP = instance.getOntModel().createOntProperty(OntologyUtils.DURING_P);
        
        String ind_uri = null;
        
        Statement stmt = instance.getProperty(beforeP);
                
        if(stmt == null)
        {
            //procurar o during
            
            stmt = instance.getProperty(duringP);
        
            if(stmt!=null && stmt.getObject().isResource())
            {
                Resource res    = stmt.getObject().asResource();
                ind_uri         = res.getURI();
                Statement stmt2 = res.getProperty(beforeP);
                
                if(stmt2!=null && stmt2.getObject().isResource())
                    ind_uri = stmt.getObject().asResource().getURI();
            }   
            
        }
        else if(stmt.getObject().isResource())
            ind_uri = stmt.getObject().asResource().getURI();
        
        
        if(ind_uri == null)
            return null;
        
        return model.getIndividual(ind_uri);
    }
    
     public static Individual getAfterInstant(Individual instance)
    {
        OntModel model = instance.getOntModel();
        
        OntProperty       afterP = instance.getOntModel().getObjectProperty(OntologyUtils.AFTER_P);
        if(afterP==null) afterP = instance.getOntModel().createOntProperty(OntologyUtils.AFTER_P);
        
        OntProperty       duringP = instance.getOntModel().getObjectProperty(OntologyUtils.DURING_P);
        if(duringP==null) duringP = instance.getOntModel().createOntProperty(OntologyUtils.DURING_P);
        
        String ind_uri = null;
        
        Statement stmt = instance.getProperty(afterP);
        
        if(stmt == null)
        {
            //procurar o during
            
            stmt = instance.getProperty(duringP);
        
            if(stmt!=null && stmt.getObject().isResource())
            {
                Resource res    = stmt.getObject().asResource();
                ind_uri         = res.getURI();
                Statement stmt2 = res.getProperty(afterP);
                
                if(stmt2!=null && stmt2.getObject().isResource())
                    ind_uri = stmt.getObject().asResource().getURI();
            }   
            
        }
        else if(stmt.getObject().isResource())
            ind_uri = stmt.getObject().asResource().getURI();
        
        if(ind_uri == null)
            return null;
        
        return model.getIndividual(ind_uri);
    }
    
    
    
    
    public static OntClass getLastTimeSlice(OntClass cls) 
    {
        List<OntClass> timeSlices = getTimeSlices(cls);

        if(timeSlices.isEmpty())
            return cls;

        return timeSlices.get(timeSlices.size()-1);
        /*
        for(OntClass timeSlice : timeSlices)
        {
            List<OntClass> superClasses = timeSlice.listSuperClasses(true).toList();
            List<OntClass> plc = new ArrayList<>();
            
            for(OntClass superClass : superClasses)
            {
                if(superClass.isRestriction())
                {
                    Restriction superClsR = superClass.asRestriction();
                    if(superClsR.isHasValueRestriction())
                    {
                        Restriction sCls = superClsR.asHasValueRestriction();
                        if(sCls.getOnProperty().getURI().equals(beforeP.getURI()))
                            plc.add(sCls);
                    }
                }
            }   
            
            //List<Statement> sliceList = beforeS.toList();
        
            if(plc.isEmpty())
                lastSlice = timeSlice;
        }
             
        if(isTimeSlice(lastSlice))
            return lastSlice;
        else
            return cls;
        */
        
    }

    public static boolean isTimeSlice(OntClass cls)
    {
        String uri = cls.getURI();
        
        if(uri == null) return false;
        
        return uri.contains("TS__");        
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
     * @param old_property OntProperty to clone
     * @return 
     */
     public static OntProperty copyProperty(OntModel newModel, OntProperty old_property) 
     {
        Utilities.logInfo("COPY ONTPROPERTY METHOD. \tCopying: " + old_property.getURI());
        
        boolean isFunctional = old_property.isFunctionalProperty();
        boolean inverseFunctionalProperty = old_property.isInverseFunctionalProperty();
        boolean symmetricProperty = old_property.isSymmetricProperty();
        boolean transitiveProperty = old_property.isTransitiveProperty();
        
        //check if property exists

         OntProperty newProperty;

         if(old_property.isDatatypeProperty())
            newProperty = (OntProperty) newModel.createDatatypeProperty(old_property.getURI(), false);
         else if(old_property.isObjectProperty())
             newProperty = (OntProperty) newModel.createObjectProperty(old_property.getURI(), false);
         else
             newProperty = (OntProperty) newModel.createDatatypeProperty(old_property.getURI(), false);
        
        if(isFunctional)
            newProperty = newProperty.convertToTransitiveProperty();
        if(inverseFunctionalProperty)
            newProperty = newProperty.convertToInverseFunctionalProperty();
        if(symmetricProperty)
            newProperty = newProperty.convertToSymmetricProperty();
        if(transitiveProperty)
            newProperty = newProperty.convertToTransitiveProperty();
        
        List<? extends OntResource> listDomain = old_property.listDomain().toList();
        for(OntResource r : listDomain)
            newProperty.addDomain(r);
        
        List<? extends OntResource> listRange = old_property.listRange().toList();
        for(OntResource r : listRange)
            newProperty.addRange(r);
        
        List<? extends OntProperty> listInverseOf = old_property.listInverseOf().toList();
        for(OntProperty p : listInverseOf)
                newProperty.addInverseOf(p);
        
        OntProperty superProperty = old_property.getSuperProperty();
        if(superProperty!=null && newModel.getProperty(superProperty.getURI())!=null)
        {
            OntProperty property = newModel.getOntProperty(superProperty.getURI());
            if(property==null)
                property = copyProperty(newModel, superProperty);
            
            newProperty.setSuperProperty(property);
        }
        
        List<? extends OntProperty> listSubProperties = old_property.listSubProperties().toList();
        for(OntProperty p : listSubProperties)
        {
            if(newModel.getProperty(p.getURI())!=null)
            {
                OntProperty copyProperty = newModel.getOntProperty(p.getURI());
                if(copyProperty==null)
                    copyProperty = copyProperty(newModel, p);
                newProperty.addSubProperty(copyProperty);
            }
        }
        
        
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
         String stats = "\n\tStats for OntProperty " + property.getURI()+ "\n";
     
        stats += "\n\t\tSuperProperties: ";
        ExtendedIterator<? extends OntResource> listSUP = property.listSuperProperties();
        for(OntResource r : listSUP.toList())
            if(!Utilities.isInIgnoreList(r.getURI()))
                stats+= "\n\t\t\t- " + r.getURI();
        
        stats += "\n\t\tSubProperties: ";
        ExtendedIterator<? extends OntResource> listSUB = property.listSubProperties();
        for(OntResource r : listSUB.toList())
            if(!Utilities.isInIgnoreList(r.getURI()))
                stats+= "\n\t\t\t- " + r.getURI();
         
        stats += "\n\t\tDomains: ";
        ExtendedIterator<? extends OntResource> listDomain = property.listDomain();
        for(OntResource r : listDomain.toList())
            if(!Utilities.isInIgnoreList(r.getURI()))
                stats+= "\n\t\t\t- " + r.getURI();
        
        stats += "\n\t\tRanges: ";
        ExtendedIterator<? extends OntResource> listRange = property.listRange();
        for(OntResource r : listRange.toList())
            if(!Utilities.isInIgnoreList(r.getURI()))
                stats+= "\n\t\t\t- " + r.getURI();
        
        stats += "\n\t\tInverse of: ";
        ExtendedIterator<? extends OntProperty> listInverseOf = property.listInverseOf();
        for(OntProperty p : listInverseOf.toList())
            if(!Utilities.isInIgnoreList(p.getURI()))
                stats+= "\n\t\t\t- " + p.getURI();
        
        stats+="\n\t\t === \n";
       
         return stats;
     }

     public static String classStats(OntClass ontClass)
     {
        String stats = "\n\tStats for OntClass "+ontClass.getURI()+"\n";
         
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
      * Finds a class in an OntModel. Returns null if there is no class with the URI.
      * @param model The Model in which to look for the OntClass
      * @param URI URI of the Class
      * @return OntClass with the same URI as provided, null if it doesn't exist in the OntModel.
      */
     public static OntClass getClassFromModel(OntModel model, String URI)
     {
        List<OntClass> listClasses = model.listClasses().toList();
        for(OntClass cls : listClasses)
            if(cls.getURI().equalsIgnoreCase(URI))
                return cls;
       
        return null;
     }
     
     /**
      * Finds a class in an OntModel. Returns null if there is no such OntClass.
      * @param model The model in which to look for the class
      * @param toFind OntClass to look after. Comparison is done through the getURI() method.
      * @return OntClass if a class with the same URI exists, null otherwise.
      */
     public static OntClass getClassFromModel(OntModel model, OntClass toFind)
     {
        List<OntClass> listClasses = model.listClasses().toList();
        for(OntClass cls : listClasses)
            if(cls.getURI().equalsIgnoreCase(toFind.getURI()))
                return cls;
       
        return null;
     }
     
     
     
     /**
      * Looks for a Object Property in a given Model. Comparison is done by comparing the URIs from the properties.
      * @param model The Model to search in
      * @param property The ObjectProperty to look for 
      * @return The ObjectProperty if it exists, null otherwise.
      */
     public static ObjectProperty getObjectPropertyFromModel(OntModel model, ObjectProperty property)
     {
        return getObjectPropertyFromModel(model, property.getURI());
     }
     
     /**
      * Looks for a Object Property in a given Model. Comparison is done by comparing the URIs from the properties.
      * @param model The Model to search in
      * @param propertyURI The URI to look for 
      * @return The ObjectProperty if it exists, null otherwise.
      */
     public static ObjectProperty getObjectPropertyFromModel(OntModel model, String propertyURI)
     {
        List<ObjectProperty> listProperties = model.listObjectProperties().toList();
        for(ObjectProperty dtp : listProperties)
            if(propertyURI.equalsIgnoreCase(dtp.getURI()))
                return dtp;
        
        return null;
     }
     
     
     
     /**
      * Looks for a Datatype Property in a given Model. Comparison is done by comparing the URIs from the properties.
      * @param model The Model to search in
      * @param property The DatatypeProperty to look for 
      * @return The DatatypeProperty if it exists, null otherwise.
      */
     public static DatatypeProperty getDatatypePropertyFromModel(OntModel model, DatatypeProperty property)
     {
        return getDatatypePropertyFromModel(model, property.getURI());
     }
     
     /**
      * Looks for a Datatype Property in a given Model. Comparison is done by comparing the URIs from the properties.
      * @param model The Model to search in
      * @param propertyURI The URI to look for 
      * @return The DatatypeProperty if it exists, null otherwise.
      */
     public static DatatypeProperty getDatatypePropertyFromModel(OntModel model, String propertyURI)
     {
         try
         {
             return model.getDatatypeProperty(propertyURI);
         }
         catch(Exception e)
         {
             System.out.println("Problem retrieving DataType Property with URI " + propertyURI + " from Model. Reason: " + e.getMessage());
             return null;
         }
     }
     
     
     
     
     public static String getBasePrefix(OntModel model)
     {
        Map<String, String> nsPrefixMap = model.getNsPrefixMap();
        String basePrefix = nsPrefixMap.get(""); //base
                //OntClass newClass2 = newModel.createClass(basePrefix + equivalentClassURI);
         
        return basePrefix;
     
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
        
        String class2CopyURI = class2Copy.getURI();
        
        if(class2Copy.isAnon())
                class2CopyURI = class2Copy.toString();

        OntClass newClass = newModel.createClass(class2CopyURI);
    
        
        // get the class data
        
        copyClassDetails(class2Copy, newClass);
        return newClass;
    }
    
    
    public static OntClass copyClassDetails(OntClass class2Copy, String newClassURI)
    {   
        OntClass newClass = class2Copy.getOntModel().createClass(newClassURI);
        return copyClassDetails(class2Copy, newClass);
    }
    
    
    /**
     * Copies all properties of a Class to another one.
     * @param source The source OntClass
     * @param target The target OntClass
     * @return 
     */
    public static OntClass copyClassDetails(OntClass source, OntClass target)
    {
        if(source == null)
            return target;

        List<RDFNode> listComments = source.listComments(null).toList();
        for(RDFNode comment : listComments)
            target.addComment((Literal) comment);
    
        List<OntClass> listDisjointWith = source.listDisjointWith().toList();
        for(OntClass cls : listDisjointWith)
            if(!Utilities.isInIgnoreList(cls.getURI()))
                target.addDisjointWith(cls);
        
        List<OntClass> listEquivalentClasses = source.listEquivalentClasses().toList();
        for(OntClass cls : listEquivalentClasses)
        {   
            copyRestriction(cls, target, "EquivalentClass");
        }
        
        List<RDFNode> listLabels = source.listLabels(null).toList();
        for(RDFNode label : listLabels)
            target.addLabel((Literal) label);
        
        List<OntClass> listSubClasses = source.listSubClasses().toList();
        for(OntClass cls : listSubClasses)
        {
            if(hasRestriction(cls))
                copyRestriction(cls, target, "SubClass");
            else
                target.addSubClass(cls);
        }
        
        List<OntClass> listSuperClasses = source.listSuperClasses().toList();
        for(OntClass cls : listSuperClasses)
        {
            String uri = cls.getURI();
            
            if(uri!=null && Utilities.isInIgnoreList(uri))
                continue;
            
            if(hasRestriction(cls))
                copyRestriction(cls, target, "SuperClass");
            else
                target.addSuperClass(cls);
        }
        
        return target;
    }
    
    
    public static String printTriple(Triple t)
    {
        String subject      = t.getSubject().toString().split("#")[1];
        String predicate    = t.getPredicate().toString().split("#")[1];
        String object       = t.getObject().toString();
        
        return printSPO(subject, predicate, object);
    }



    public static String printStatement(Statement t)
    {
        String subject      = t.getSubject().toString().split("#")[1];
        String predicate    = t.getPredicate().toString().split("#")[1];
        String object       = t.getObject().toString();
        
        return printSPO(subject, predicate, object);
    }
    
    public static String printSPO(String subject, String predicate, String object)
    {
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
    
    
    public static void addHasBeginning(OntClass cls)
    {
        OntProperty ontProperty = cls.getOntModel().getOntProperty(OntologyUtils.HAS_BEGINNING_P);

        if(ontProperty == null)
            ontProperty = cls.getOntModel().createObjectProperty(OntologyUtils.HAS_BEGINNING_P, false);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        LocalDateTime now = LocalDateTime.now();  

        OntClass instantClass = cls.getOntModel().getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = cls.getOntModel().createClass(OntologyUtils.INSTANT_CLS);

        Individual date1 = cls.getOntModel().createIndividual(dtf2.format(now), instantClass);

        //date1.addLabel(dtf.format(now), null);

        System.out.println("PRITNING AGORA O BOENCO");

        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);

        cls.addSuperClass(createHasValueRestriction);    
    
    }
    
     public static void addAfter(OntClass cls)
    {
        OntProperty ontProperty = cls.getOntModel().getOntProperty(OntologyUtils.AFTER_P);

        if(ontProperty == null)
            ontProperty = cls.getOntModel().createObjectProperty(OntologyUtils.AFTER_P, false);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");  
        LocalDateTime now = LocalDateTime.now();  

        OntClass instantClass = cls.getOntModel().getOntClass(OntologyUtils.INSTANT_CLS);
        if(instantClass == null)
            instantClass = cls.getOntModel().createClass(OntologyUtils.INSTANT_CLS);

        Individual date1 = cls.getOntModel().createIndividual(dtf2.format(now), instantClass);

        //date1.addLabel(dtf.format(now), null);

        System.out.println("PRITNING AGORA O BOENCO");

        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);

        cls.addSuperClass(createHasValueRestriction);    
    
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

    private static boolean hasRestriction(OntClass cls)
    {
        return cls.isEnumeratedClass() || cls.isIntersectionClass() || cls.isUnionClass()
                || cls.isRestriction() ||  cls.isComplementClass();
    }
    
    private static void addRestriction(String restrictionType, OntClass newClass, OntClass restriction)
    {
        switch(restrictionType)
            {
                case "EquivalentClass":
                    newClass.addEquivalentClass(restriction); 
                    break;
                case "SubClass":
                    newClass.addSubClass(restriction);
                    //newClass.addEquivalentClass(restriction);
                    break;
                case "SuperClass":
                    newClass.addSuperClass(restriction);
                    //newClass.addEquivalentClass(restriction);
                    break;
                default:
                    
                    break;
            }
    }
    
    /**
     * 
     * @param cls The class to copy restriction from
     * @param newClass The destination class
     * @param restrictionType the type of restriction to consider. One of:
     *  SuperClassOf
     *  SubClassOf
     * @return 
     */
    public static void copyRestriction(OntClass cls, OntClass newClass, String restrictionType) 
    {
        
        if(cls.isEnumeratedClass())
        {
            EnumeratedClass asEnumeratedClass = cls.asEnumeratedClass();
            RDFList oneOf = asEnumeratedClass.getOneOf();
            EnumeratedClass enumClass = newClass.getOntModel().createEnumeratedClass(null, oneOf);
            
            addRestriction(restrictionType, newClass, enumClass);
        }
        
        if(cls.isIntersectionClass())
        {
            try
            {
                RDFList operands = cls.asIntersectionClass().getOperands();   
                IntersectionClass interClass = newClass.getOntModel().createIntersectionClass(null, operands);    
                addRestriction(restrictionType, newClass, interClass);
            }
            catch(Exception e)
            {
                System.out.println("Failed to convert node to Intersection class. Not copying.");
            }
        }
        
        if(cls.isRestriction())
            copyRestrictionDetail(cls, newClass, restrictionType);
        
        if(cls.isUnionClass())
        {
            try
            {
                UnionClass asUnionClass = cls.asUnionClass();
                UnionClass newUnionClass = newClass.getOntModel().createUnionClass(null, asUnionClass.getOperands());
                addRestriction(restrictionType, newClass, newUnionClass);
            }
            catch(Exception e)
            {
                System.out.println("Failed to convert node to Union class. Not copying.");
                
            }
        }
        
        if(cls.isComplementClass())
        {
            ComplementClass createComplementClass = newClass.getOntModel().createComplementClass(null, cls);         
            addRestriction(restrictionType, newClass, createComplementClass);
        }
        

    }

    public static void copyRestrictionDetail(OntClass cls, OntClass newClass, String restrictionType)
    {
        Restriction old_restriction = cls.asRestriction();
        Restriction new_restriction = null; 
        OntProperty onProperty      = old_restriction.getOnProperty();
        
        if(onProperty.getURI().contains(OntologyUtils.ONT_TIME_URL))
            return;
        
        
        if (old_restriction.isAllValuesFromRestriction()) 
        {
            Resource allValuesFrom = old_restriction.asAllValuesFromRestriction().getAllValuesFrom();
            AllValuesFromRestriction createAllValuesFromRestriction = 
                    newClass.getOntModel().createAllValuesFromRestriction(null, onProperty, allValuesFrom);
               
            new_restriction = createAllValuesFromRestriction;
        }

        if (old_restriction.isSomeValuesFromRestriction())
        {
            
            Resource someValuesFrom = old_restriction.asSomeValuesFromRestriction().getSomeValuesFrom();
            SomeValuesFromRestriction createSomeValuesFromRestriction = 
                    newClass.getOntModel().createSomeValuesFromRestriction(null, onProperty, someValuesFrom);

            new_restriction = createSomeValuesFromRestriction;
        }

        if(old_restriction.isHasValueRestriction())
        {   
            HasValueRestriction hasValueRestriction = old_restriction.asHasValueRestriction();
            RDFNode hasValue = hasValueRestriction.getHasValue();
            new_restriction = newClass.getOntModel().createHasValueRestriction(null, onProperty, hasValue);
        }
        
        // I don't know if this ever works
        if(old_restriction.canAs(CardinalityQRestriction.class))
        {
           
            CardinalityQRestriction cqr =  old_restriction.as(CardinalityQRestriction.class);
            int cardinality = cqr.getCardinalityQ();
            OntClass hasClassQ = cqr.getHasClassQ().asClass();
            
            new_restriction = newClass.getOntModel().createCardinalityQRestriction
                    (null, onProperty, cardinality, hasClassQ);
        }
        
        
        int cardinality;
        
        if(old_restriction.canAs(CardinalityRestriction.class))
        {
            CardinalityRestriction cr = old_restriction.asCardinalityRestriction();
            cardinality                 = cr.getCardinality();
            
             new_restriction = newClass.getOntModel().createCardinalityRestriction
                        (null, onProperty, cardinality);
        
        }
        
        if(old_restriction.canAs(MaxCardinalityRestriction.class))
        {
            MaxCardinalityRestriction cr = old_restriction.asMaxCardinalityRestriction();
            cardinality                  = cr.getMaxCardinality();

            if(cr.canAs(MaxCardinalityQRestriction.class))
            {
                MaxCardinalityQRestriction cqr =  cr.as(MaxCardinalityQRestriction.class);
                cardinality = cqr.getMaxCardinalityQ();
                OntClass hasClassQ = cqr.getHasClassQ().asClass();

                new_restriction = newClass.getOntModel().createMaxCardinalityQRestriction
                        (null, onProperty, cardinality, hasClassQ);

            }
            else
                new_restriction = newClass.getOntModel().createMaxCardinalityRestriction
                        (null, onProperty, cardinality);
        }

        if(old_restriction.canAs(MinCardinalityRestriction.class))
        {
            MinCardinalityRestriction cr = old_restriction.asMinCardinalityRestriction();
            cardinality                  = cr.getMinCardinality();

            if(cr.canAs(MinCardinalityQRestriction.class))
            {
                MinCardinalityQRestriction cqr =  cr.as(MinCardinalityQRestriction.class);
                cardinality = cqr.getMinCardinalityQ();
                OntClass hasClassQ = cqr.getHasClassQ().asClass();

                new_restriction = newClass.getOntModel().createMinCardinalityQRestriction
                        (null, onProperty, cardinality, hasClassQ);
            }
            else
                new_restriction = newClass.getOntModel().createMinCardinalityRestriction
                    (null, onProperty, cardinality);
        }

        
        if(new_restriction!=null)
            addRestriction(restrictionType, newClass, new_restriction);


    }

    public static String printRestriction(OntClass cls) 
    {
        String ret = "";
        
        if(!cls.isRestriction()) return "";

        Restriction r = cls.asRestriction();
        
        if(r.isAllValuesFromRestriction())
        {
            ret += "All Values From | On Property: " + r.asAllValuesFromRestriction().getOnProperty() 
                    + " | Value: " + r.asAllValuesFromRestriction().getAllValuesFrom().toString();
        }
        
        if(r.isCardinalityRestriction())
        {
            ret += "Cardinality Restriction | On Property: " + r.asCardinalityRestriction().getOnProperty() 
                    + " | Cardinality: " + r.asCardinalityRestriction().getCardinality();
        }
        
        if(r.isMinCardinalityRestriction())
        {
            ret += "Min Cardinality Restriction | On Property: " + r.asMinCardinalityRestriction().getOnProperty() 
                    + " | Min Cardinality: " + r.asMinCardinalityRestriction().getMinCardinality();
        }
        if(r.isMaxCardinalityRestriction())
        {
            ret += "Max Cardinality Restriction | On Property: " + r.asMaxCardinalityRestriction().getOnProperty() 
                    + " | Max Cardinality: " + r.asMaxCardinalityRestriction().getMaxCardinality();
        }
        
        if(r.isHasValueRestriction())
        {
            ret += "Has Value Restriction | On Property: " + r.asHasValueRestriction().getOnProperty() 
                    + " | Value: " + r.asHasValueRestriction().getHasValue().toString();
        }
        
        if(r.isSomeValuesFromRestriction())
        {
            ret += "Some Values From Restriction | On Property: " + r.asSomeValuesFromRestriction().getOnProperty() 
                    + " | Value: " + r.asSomeValuesFromRestriction().getSomeValuesFrom().toString();
        }
        
      
        return ret;
    }


    public static Individual getIndividual(String URI, OntModel ontModel) 
    {
        String parts []= URI.split("#");

        List<String> individualUris = SPARQLUtils.getIndividualsSPARQL(ontModel);
    
        System.out.println("==Looking for individual: " + URI);
        for(String indURI : individualUris)
        {
            System.out.println("\tParsing ... " + indURI);

            if(parts.length == 2 && indURI.contains(parts[1]))
                return ontModel.getIndividual(indURI);

            if(indURI.equalsIgnoreCase(URI))
                return ontModel.getIndividual(indURI);
        
        }
    
       return null;
    }

    public static Individual copyIndividual(Individual instance, OntModel model)
    {
        Individual new_ind = model.createIndividual(instance.getOntClass(true));
        
        List<Statement> propertyStmtList = instance.listProperties().toList();            
            
        //copy all classes
        
        List<OntClass> ontClasses = instance.listOntClasses(true).toList();
        
        for(OntClass cls : ontClasses)
        {
            if(cls.getURI()!=null && model.getOntClass(cls.getURI())==null)
            {
                model.createClass(cls.getURI());
                new_ind.addOntClass(cls);
            }
        }
        
        // copy all properties
        for(Statement stmt : propertyStmtList)
        {
            Property predicate = stmt.getPredicate();
            RDFNode object     = stmt.getObject();
            
            Property newProperty = model.getProperty(predicate.getURI());
            if(newProperty==null)
                model.createProperty(predicate.getLocalName());
            
            if(object.isURIResource()) // objectProperty
            {
                Resource asResource = object.asResource();
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
