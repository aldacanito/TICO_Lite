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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.CardinalityQRestriction;
import org.apache.jena.ontology.ComplementClass;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.MaxCardinalityQRestriction;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityQRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;


import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class OntologyUtils 
{
    public static final String INSTANT_CLS      = "http://www.w3.org/2006/time#Instant";
    public static final String HAS_ENDING_P     = "http://www.w3.org/2006/time#hasEnd";
    public static final String HAS_BEGINNING_P  = "http://www.w3.org/2006/time#hasBeginning";
    public static final String BEFORE_P         = "http://www.w3.org/2006/time#before";
    public static final String AFTER_P          = "http://www.w3.org/2006/time#after";
    public static final String INTERVAL_CLS     = "http://www.w3.org/2006/time#Interval";
    public static final String HAS_SLICE_P      = "http://www.w3.org/2006/time#hasTimeSlice";
    public static final String DURING_P         = "http://www.w3.org/2006/time#hasDuration";
    public static final String IS_SLICE_OF_P    = "http://www.w3.org/2006/time#isTimeSliceOf";
    public static final String ONT_TIME_URL     = "http://www.w3.org/2006/time";
    
    
    public static OntModel readModel(String filename) 
    {
    
        OntModel m = ModelFactory.createOntologyModel();  
        try 
        {
           m.read(new FileReader(filename, StandardCharsets.ISO_8859_1 ), "", "TTL");
        
        } catch (Exception e) 
        { 
           e.printStackTrace();
        }
        
        return m;
    }
    
    
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

    
    }
    
    
    
    public static List<OntClass> getTimeSlices(OntClass theOgClass) 
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

    public static OntClass getLastTimeSlice(OntClass cls) 
    {
        List<OntClass> timeSlices = getTimeSlices(cls);
        
        OntProperty       beforeP = cls.getOntModel().getObjectProperty(OntologyUtils.BEFORE_P);
        if(beforeP==null) beforeP = cls.getOntModel().createOntProperty(OntologyUtils.BEFORE_P);
        
        if(timeSlices.isEmpty()) return cls; 
        
        OntClass lastSlice = timeSlices.get(0);
        // todas têm before menos a última
        
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
        OntProperty newProperty = (OntProperty) newModel.createOntProperty(old_property.getURI());
        
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
        List<DatatypeProperty> listProperties = model.listDatatypeProperties().toList();
        for(DatatypeProperty dtp : listProperties)
            if(propertyURI.equalsIgnoreCase(dtp.getURI()))
                return dtp;
        
        return null;
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
            // TODO TESTAR
            EnumeratedClass asEnumeratedClass = cls.asEnumeratedClass();
            RDFList oneOf = asEnumeratedClass.getOneOf();
            EnumeratedClass enumClass = newClass.getOntModel().createEnumeratedClass(null, oneOf);
            
            addRestriction(restrictionType, newClass, enumClass);
        }
        
        if(cls.isIntersectionClass())
        {
            RDFList operands = cls.asIntersectionClass().getOperands();   
            IntersectionClass interClass = newClass.getOntModel().createIntersectionClass(null, operands);    
            addRestriction(restrictionType, newClass, interClass);
        }
        
        if(cls.isRestriction())
            copyRestrictionDetail(cls, newClass, restrictionType);
        
        if(cls.isUnionClass())
        {
            UnionClass asUnionClass = cls.asUnionClass();
            UnionClass newUnionClass = newClass.getOntModel().createUnionClass(null, asUnionClass.getOperands());
            addRestriction(restrictionType, newClass, newUnionClass);
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
        try
        {
           
            CardinalityQRestriction cqr =  old_restriction.as(CardinalityQRestriction.class);
            int cardinality = cqr.getCardinalityQ();
            OntClass hasClassQ = cqr.getHasClassQ().asClass();
            
            new_restriction = newClass.getOntModel().createCardinalityQRestriction
                    (null, onProperty, cardinality, hasClassQ);
        }
        catch(Exception e)
        {
            Utilities.logInfo("Could not cast restriction to CardinalityQRestriction");
        }
        
        int cardinality             = old_restriction.getCardinality(onProperty);
        

        try
        {
            ///MaxCardinalityRestriction cr   = old_restriction.convertToMaxCardinalityRestriction(cardinality);

            MaxCardinalityRestriction cr    = old_restriction.asMaxCardinalityRestriction();
            cardinality                 = cr.getMaxCardinality();
            boolean success = false;
            
            try
            {
                MaxCardinalityQRestriction cqr =  cr.as(MaxCardinalityQRestriction.class);
                cardinality = cqr.getMaxCardinalityQ();
                OntClass hasClassQ = cqr.getHasClassQ().asClass();

                new_restriction = newClass.getOntModel().createMaxCardinalityQRestriction
                        (null, onProperty, cardinality, hasClassQ);
                
                success = true;
            }
            catch(Exception e)
            {}
            
            if(!success)
                new_restriction = newClass.getOntModel().createMaxCardinalityRestriction
                        (null, onProperty, cardinality);
        }
        catch(Exception e)
        {
            Utilities.logInfo("Could not cast restriction to CardinalityRestriction");
        }
        
        
        try
        {
            //MinCardinalityRestriction cr   = old_restriction.convertToMinCardinalityRestriction(cardinality);

            MinCardinalityRestriction cr    = old_restriction.asMinCardinalityRestriction();
            cardinality                 = cr.getMinCardinality();
            boolean success = false;
            
            try
            {
                MinCardinalityQRestriction cqr =  cr.as(MinCardinalityQRestriction.class);
                cardinality = cqr.getMinCardinalityQ();
                OntClass hasClassQ = cqr.getHasClassQ().asClass();

                new_restriction = newClass.getOntModel().createMinCardinalityQRestriction
                        (null, onProperty, cardinality, hasClassQ);
                
                success = true;
            }
            catch(Exception e)
            {}
            
            if(!success)
                new_restriction = newClass.getOntModel().createMinCardinalityRestriction
                    (null, onProperty, cardinality);
        }
        catch(Exception e)
        {
            Utilities.logInfo("Could not cast restriction to CardinalityRestriction");
        }
        
        
        
        if(new_restriction!=null)
            addRestriction(restrictionType, newClass, new_restriction);
//        else //this does not work - copia restrição vazia, que depois dá erro
//            addRestriction(restrictionType, newClass, old_restriction);
        

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
}
