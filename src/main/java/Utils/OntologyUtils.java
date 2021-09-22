/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.CardinalityQRestriction;
import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.ComplementClass;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.IntersectionClass;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.QualifiedRestriction;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Literal;
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
        
        OntProperty newProperty = (OntProperty) newModel.createOntProperty(old_property.getURI());
        
        if(isFunctional)
            newProperty.convertToTransitiveProperty();
        if(inverseFunctionalProperty)
            newProperty.convertToInverseFunctionalProperty();
        if(symmetricProperty)
            newProperty.convertToSymmetricProperty();
        if(transitiveProperty)
            newProperty.convertToTransitiveProperty();
        
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
            OntProperty copyProperty = copyProperty(newModel, superProperty);
            newProperty.setSuperProperty(copyProperty);
        }
        
        List<? extends OntProperty> listSubProperties = old_property.listSubProperties().toList();
        for(OntProperty p : listSubProperties)
        {
            if(newModel.getProperty(p.getURI())!=null)
            {
                OntProperty copyProperty = copyProperty(newModel, p);
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
        
        stats += "\n\t\tSuperProperties: ";
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
        
        List<RDFNode> listComments = class2Copy.listComments(null).toList();
        for(RDFNode comment : listComments)
            newClass.addComment((Literal) comment);
    
        List<OntClass> listDisjointWith = class2Copy.listDisjointWith().toList();
        for(OntClass cls : listDisjointWith)
            if(!Utilities.isInIgnoreList(cls.getURI()))
                newClass.addDisjointWith(cls);
        
        List<OntClass> listEquivalentClasses = class2Copy.listEquivalentClasses().toList();
        for(OntClass cls : listEquivalentClasses)
        {   
            copyRestriction(cls, newClass, "EquivalentClass");
        }
        
        List<RDFNode> listLabels = class2Copy.listLabels(null).toList();
        for(RDFNode label : listLabels)
            newClass.addLabel((Literal) label);
        
        List<OntClass> listSubClasses = class2Copy.listSubClasses().toList();
        for(OntClass cls : listSubClasses)
        {
            if(hasRestriction(cls))
                copyRestriction(cls, newClass, "SubClass");
            else
                newClass.addSubClass(cls);
        }
        
        List<OntClass> listSuperClasses = class2Copy.listSuperClasses().toList();
        for(OntClass cls : listSuperClasses)
        {
            if(hasRestriction(cls))
                copyRestriction(cls, newClass, "SuperClass");
            else
                newClass.addSuperClass(cls);
        }
        
        return newClass;
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
    private static void copyRestriction(OntClass cls, OntClass newClass, String restrictionType) 
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
        
        if (old_restriction.isAllValuesFromRestriction()) 
        {
            Resource allValuesFrom = old_restriction.asAllValuesFromRestriction().getAllValuesFrom();

            //TEST?
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

        if (old_restriction.isCardinalityRestriction() || old_restriction.isMaxCardinalityRestriction() || old_restriction.isMinCardinalityRestriction())
        {            
            CardinalityRestriction cr = old_restriction.asCardinalityRestriction();
            int cardinality           = cr.getCardinality(); 
         
            if(cr.isMaxCardinalityRestriction())               
                new_restriction = newClass.getOntModel().createMaxCardinalityRestriction(
                            null, onProperty, cardinality);
            else if (cr.isMinCardinalityRestriction())
                new_restriction = newClass.getOntModel().createMinCardinalityRestriction
                        (null, onProperty, cardinality);
            else
                new_restriction = newClass.getOntModel().createCardinalityRestriction(
                            null, onProperty, cardinality);
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
        
        if(new_restriction!=null)
            addRestriction(restrictionType, newClass, new_restriction);
//        else //this does not work - copia restrição vazia, que depois dá erro
//            addRestriction(restrictionType, newClass, old_restriction);
        
    }
}
