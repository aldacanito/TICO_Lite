package Utils;

import IDC.ModelManager;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.reasoner.BaseInfGraph;

import java.util.*;

public class SPARQLUtils
{


    /**
     * Checks if Restriction r1 is a superclass of OntClass ontClass, using SPARQL.
     * @param ontClass
     * @param r1
     * @return TRUE if r1 is one of the ontClass' restrictions
     */
    public static boolean hasRestrictionSPARQL(OntClass ontClass, Restriction r1)
    {
        boolean ret = false;

        if(r1.isMaxCardinalityRestriction())
        {
            OntProperty prop = r1.getOnProperty();
            int cardinality  = r1.getCardinality(prop);

            //if( cardinality == 0 ) cardinality = 1; // todo figure why

            if(ontClass.getURI() != null)
                return hasCardinalitySPARQL(ontClass, prop, cardinality, "maxCardinality");
        } else if(r1.isMinCardinalityRestriction())
        {
            OntProperty prop = r1.getOnProperty();
            int cardinality  = r1.getCardinality(prop);

//            if( cardinality == 0 ) cardinality = 1;

            if(ontClass.getURI() != null)
                return hasCardinalitySPARQL(ontClass, prop, cardinality, "minCardinality");
        } else if(r1.isCardinalityRestriction())
        {
            OntProperty prop = r1.getOnProperty();
            int cardinality  = r1.getCardinality(prop);

            //if( cardinality == 0 ) cardinality = 1;

            if(ontClass.getURI() != null)
                return hasCardinalitySPARQL(ontClass, prop, cardinality, "cardinality");
        }

        if(r1.isHasValueRestriction())
        {
            HasValueRestriction hasValueRestriction = r1.asHasValueRestriction();
            RDFNode hasValue = hasValueRestriction.getHasValue();

            OntProperty prop = hasValueRestriction.getOnProperty();

            String range = null;
            if(hasValue.isResource())
                range = hasValue.asResource().getURI();

            return hasValuesFromSPARQL(ontClass, range, prop, "hasValue");
        } else if(r1.isAllValuesFromRestriction())
        {
            AllValuesFromRestriction r11 = r1.asAllValuesFromRestriction();
            Resource res                 = r11.getAllValuesFrom();
            OntProperty prop             = r11.getOnProperty();
            String range                 = res.getURI();

            return hasValuesFromSPARQL(ontClass, range, prop, "allValuesFrom");
        } else if(r1.isSomeValuesFromRestriction())
        {
            SomeValuesFromRestriction r11 = r1.asSomeValuesFromRestriction();
            Resource res                 = r11.getSomeValuesFrom();
            OntProperty prop             = r11.getOnProperty();
            String range                 = res.getURI();

            return hasValuesFromSPARQL(ontClass, range, prop, "someValuesFrom");
        }

        System.out.println("was not???  restricten????");
        return ret;
    }


    public static boolean hasValuesFromSPARQL(OntClass domain, String range, OntProperty property, String valueType)
    {
        OntModel model      = domain.getOntModel();
        String property_URI = property.getURI();
        String domain_URI   = domain.getURI();

        String queryString =
                " SELECT DISTINCT ?d ?value ?range WHERE\n" +
                        "{ \n" +
                        "  <"+ domain_URI + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?d  . \n" +
                        "  ?d <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Restriction> .\n" +
                        "  ?d <http://www.w3.org/2002/07/owl#onProperty> <" + property_URI + "> .  \n" +
                        "  ?d <http://www.w3.org/2002/07/owl#" + valueType + "> ";
        if(range!=null)
            queryString += " <"+ range +"> . ";
        else
            queryString += " ?range . ";


        queryString += " } " +
                "ORDER BY ASC(?d)" ;


        Query query = QueryFactory.create
                (
                        queryString
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                return true;
            }
        }
        catch(Exception e)
        {
            System.out.println("Error querying for Value Restrictions. Reason: " + e.getMessage());
        }

        return false;
    }


    public static boolean hasCardinalitySPARQL(OntClass ontClass, OntProperty property, int cardinality, String cardinalityType)
    {
        OntModel model      = ontClass.getOntModel();
        String property_URI = property.getURI();
        String class_URI    = ontClass.getURI();

        Query query = QueryFactory.create
                (
                        " SELECT DISTINCT ?d ?value WHERE\n" +
                                "{ \n" +
                                "  <"+ class_URI + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?d  . \n" +
                                "  ?d <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2002/07/owl#Restriction> .\n" +
                                "  ?d <http://www.w3.org/2002/07/owl#onProperty> <" + property_URI + "> .  \n" +
                                "  ?d <http://www.w3.org/2002/07/owl#" + cardinalityType + "> ?value . " +
                                "  filter(?value = " + cardinality + ") \n" +
                                "} ORDER BY ASC(?d)" +
                                ""
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                return true;
            }
        }
        catch(Exception e)
        {
            System.out.println("Error querying for Cardinality Restrictions. Reason: " + e.getMessage());
        }

        return false;
    }


    public static boolean testSupportReflexivenessSPARQL(Individual individual, String propertyURI)
    {
        boolean isReflexive     = false;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj \n" +
                                " WHERE \n" +
                                "  { " +
                                    "?obj <" + propertyURI + "> <" + individualURI + ">. " +

                                " FILTER (?obj = <" + individualURI + "> ) . \n" +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();
            isReflexive         = results.hasNext();
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isReflexive;
    }

    public static boolean testAgainstReflexivenessSPARQL(Individual individual, String propertyURI)
    {
        boolean isReflexive     = true;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj \n" +
                                " WHERE \n" +
                                "  { " +
                                " FILTER NOT EXISTS { ?obj <" + propertyURI + "> <" + individualURI + ">. } " +
                                " FILTER (?obj = <" + individualURI + "> ) . \n" +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();
            isReflexive         = results.hasNext();
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isReflexive;
    }


    public static boolean testSupportIrreflexivenessSPARQL(Individual individual, String propertyURI)
    {
        boolean isIrreflexive     = false;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " * \n" +
                                " WHERE \n" +
                                "  { " +
                                " FILTER NOT EXISTS { <" + individualURI + "> <" + propertyURI + "> <" + individualURI + ">. } " +
                                //" FILTER (?obj = <" + individualURI + "> ) . \n" +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();
            isIrreflexive         = results.hasNext();
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isIrreflexive;
    }

    public static boolean testAgainstIrreflexivenessSPARQL(Individual individual, String propertyURI)
    {
        boolean isIrreflexive   = true;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj \n" +
                                " WHERE \n" +
                                "  { " +
                                " <" + individualURI + "> <" + propertyURI + "> <" + individualURI + ">.  " +
                                //" FILTER (?obj = <" + individualURI + "> ) . \n" +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
                isIrreflexive = false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isIrreflexive;
    }

    public static boolean testSupportSymmetrySPARQL(Individual individual, String propertyURI)
    {
        boolean isSymmetric     = false;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj \n" +
                                " WHERE \n" +
                                "  { <"+individualURI+"> <" + propertyURI + "> ?obj . " +
                                "    ?obj <" + propertyURI + "> <"+individualURI+">  . " +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();
            isSymmetric  = results.hasNext();
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isSymmetric;
    }




    public static boolean testAgainstSymmetrySPARQL(Individual individual, String propertyURI)
    {
        boolean isNotSymmetric  = true;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj ?obj2 \n" +
                                " WHERE \n" +
                                "  { " +
                                "       <"+individualURI+"> <" + propertyURI + "> ?obj . " +
                                "       ?obj <" + propertyURI + "> ?obj2 . " +
                                "  FILTER(?obj2 != <"+individualURI+">) " +
                                "  FILTER(?obj2 != ?obj) " +
                                //"   FILTER NOT EXISTS { ?obj <" + propertyURI + "> <"+individualURI+"> } . " +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();
                Resource object2 = querySolution.getResource("?obj2");
                if(object2.getURI().equalsIgnoreCase(individualURI))
                {
                    isNotSymmetric = false;
                    break;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isNotSymmetric;
    }


    public static boolean testSupportAsymmetrySPARQL(Individual individual, String propertyURI)
    {
        boolean isAsymmetric    = true;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj ?obj2 \n" +
                                " WHERE \n" +
                                "  { " +
                                "       <"+individualURI+"> <" + propertyURI + "> ?obj . " +
                                "       ?obj <" + propertyURI + "> ?obj2 . " +
                                //"   FILTER NOT EXISTS { ?obj <" + propertyURI + "> <"+individualURI+">  . } " +
                                "  FILTER(?obj2 != <"+individualURI+">) " +
                                "  FILTER(?obj2 != ?obj) " +
                                "} \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            while (results.hasNext())
            {
                    QuerySolution querySolution = results.nextSolution();
                    Resource object2 = querySolution.getResource("?obj2");
                    if(object2.getURI().equalsIgnoreCase(individualURI))
                    {
                        isAsymmetric = false;
                        break;
                    }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isAsymmetric;
    }

    public static boolean testAgainstAsymmetrySPARQL(Individual individual, String propertyURI)
    {
        boolean isNotAsymmetric = false;
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                " ?obj \n" +
                                " WHERE \n" +
                                "  { <"+individualURI+"> <" + propertyURI + "> ?obj . " +
                                "    ?obj <" + propertyURI + "> <"+individualURI+">  . " +
                                "  } \n"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            isNotAsymmetric   = results.hasNext();
        }
        catch(Exception e)
        {
            System.out.println("Error testing symmetry. Reason: " + e.getMessage());
        }

        return isNotAsymmetric;
    }





    public static boolean testNegativeInverseFunctionalitySPARQL(Individual individual, String propertyURI, List<String> propertyValues, boolean sliding)
    {

        OntModel model          = individual.getOntModel();
        String individualURI    = individual.getURI();


        if(sliding)
            model  = ModelManager.getManager().getSlidingWindowModel();

        List<Boolean> test_results = new ArrayList<>();

        for(String propertyValue : propertyValues)
        {

            if(propertyValue==null || propertyValue.isEmpty() || propertyValue.contains("zygarde"))
                continue;

            boolean current_test = true;
            // get quantos sujeitos
            Query query = QueryFactory.create
                    (
                            "SELECT ?s1 ?s2 \n " +
                                    " WHERE \n " +
                                    " { \n " +
                                    "    ?s1 <" + propertyURI + "> <" + propertyValue + "> . \n  " +
                                    "    ?s2 <" + propertyURI + "> <" + propertyValue + "> . \n  " +
                                    " } "
                    );

            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                List<String> theResURIS = new ArrayList<String>();
                ResultSet results = qexec.execSelect();

                while (results.hasNext()) {
                    QuerySolution querySolution = results.nextSolution();

                    try
                    {
                        Resource object1 = querySolution.getResource("?s1");
                        Resource object2 = querySolution.getResource("?s2");
                        theResURIS.add(object1.getURI());
                        theResURIS.add(object2.getURI());
                    }
                    catch(Exception e)
                    {
                        System.out.println("Not a Resource. Trying Literal...");
                        Literal l1 = querySolution.getLiteral("?s1");
                        Literal l2 = querySolution.getLiteral("?s2");

                        theResURIS.add(l1.getValue().toString());
                        theResURIS.add(l2.getValue().toString());
                    }
                }

                Set<String> distinctURIsSet = new HashSet<>(theResURIS);
                List<String> distinctURIs   = new ArrayList<>(distinctURIsSet);

                for (int i = 0; i < distinctURIs.size(); i++)
                {
                    String s = distinctURIs.get(i);
                    if (!individualURI.equalsIgnoreCase(s)) {
                        current_test = false;
                    }
                }
            } catch (Exception e)
            {
                System.out.println("Error testing inverse functionality. Reason: " + e.getMessage());
            }

            test_results.add(current_test);

        }

        // all current tests must be false
        for(boolean b : test_results)
        {
            if(b) return false;
        }

        return true;

    }

    public static boolean testPositiveInverseFunctionalitySPARQL(Individual individual, String propertyURI, List<String> propertyValues, boolean sliding)
    {

        OntModel model          = individual.getOntModel();
        String individualURI    = individual.getURI();

        if(sliding)
            model  = ModelManager.getManager().getSlidingWindowModel();

        List<Boolean> test_results = new ArrayList<>();

       for(String propertyValue : propertyValues)
       {

           if(propertyValue==null || propertyValue.isEmpty())
               continue;

           boolean currentTest = false;
           Query query;
           // get quantos sujeitos
           boolean wellFormed = true;
           try {
               query = QueryFactory.create
                       (
                               "SELECT ?s1 \n " +
                                       " WHERE \n " +
                                       " { \n " +
                                       "    ?s1 <" + propertyURI + "> <" + propertyValue + "> . \n  " +
                                       " } "
                       );

           }
           catch(Exception e)
           {

               continue;
           }


           try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
               List<String> theResURIS = new ArrayList<String>();
               ResultSet results = qexec.execSelect();

               while (results.hasNext()) {
                   QuerySolution querySolution = results.nextSolution();

                   try
                   {
                       Resource object1 = querySolution.getResource("?s1");
                       theResURIS.add(object1.getURI());
                   }
                   catch(Exception e)
                   {
                       System.out.println("Not a Resource. Trying Literal...");
                       Literal l = querySolution.getLiteral("?s1");
                       theResURIS.add(l.getValue().toString());
                   }

               }

               Set<String> distinctURIsSet = new HashSet<>(theResURIS);
               distinctURIsSet.add(individualURI);
               List<String> distinctURIs = new ArrayList<>(distinctURIsSet);

               if (distinctURIs.size() == 1)
                   currentTest = true;

           } catch (Exception e) {
               System.out.println("Error testing inverse functionality. Reason: " + e.getMessage());
           }

           test_results.add(currentTest);
       }

       //all current tests must be true
        for(boolean b : test_results)
        {
            if(!b) return false;
        }

        return true;
    }

    public static boolean testPositiveFunctionalitySPARQL(Individual individual, String propertyURI, boolean sliding)
    {

        boolean isFunctional    = false;
        OntModel model          = individual.getOntModel();
        String individualURI    = individual.getURI();

        if(sliding)
            model  = ModelManager.getManager().getSlidingWindowModel();


        // get quantas propriedades
        Query query = QueryFactory.create
                (
                        "SELECT ?o1 \n " +
                                " WHERE \n " +
                                " { \n " +
                                "    <"+individualURI+"> <"+propertyURI+"> ?o1 . \n  " +
                                " } "
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            List<String> theResURIS = new ArrayList<String>();
            ResultSet results = qexec.execSelect();

            while (results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();

                try
                {
                    Resource object1 = querySolution.getResource("?o1");
                    theResURIS.add(object1.getURI());
                }
                catch(Exception e)
                {
                    System.out.println("Not a Resource. Trying Literal...");
                    Literal l = querySolution.getLiteral("?o1");
                    theResURIS.add(l.getValue().toString());
                }

            }
            Set<String> theResURISset = new HashSet<>(theResURIS);

            if(theResURISset.size() == 1)
                isFunctional = true;

        }
        catch(Exception e)
        {
            System.out.println("Error testing functionality. Reason: " + e.getMessage());
        }

        return isFunctional;
    }

    public static boolean testNegativeFunctionalitySPARQL(Individual individual, String propertyURI, boolean sliding)
    {

        boolean isFunctional    = true;
        OntModel model          = individual.getOntModel();
        String individualURI    = individual.getURI();

        if(sliding)
            model  = ModelManager.getManager().getSlidingWindowModel();

        // get quantas propriedades
        Query query = QueryFactory.create
                (
                        "SELECT ?o1 ?o2 \n " +
                                " WHERE \n " +
                                " { \n " +
                                "    <"+individualURI+"> <"+propertyURI+"> ?o1 . \n  " +
                                "    <"+individualURI+"> <"+propertyURI+"> ?o2 . \n  " +
                                " } "
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            List<String> theResURIS = new ArrayList<String>();
            ResultSet results = qexec.execSelect();

            while (results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();

                try
                {
                    Resource object1 = querySolution.getResource("?o1");
                    Resource object2 = querySolution.getResource("?o2");
                    theResURIS.add(object1.getURI());
                    theResURIS.add(object2.getURI());

                }
                catch(Exception e)
                {
                    System.out.println("Not a Resource. Trying Literal...");
                    Literal l1 = querySolution.getLiteral("?o1");
                    Literal l2 = querySolution.getLiteral("?o2");
                    theResURIS.add(l1.getValue().toString());
                    theResURIS.add(l2.getValue().toString());
                }

            }
            Set<String> theResURISset = new HashSet<>(theResURIS);

            if(theResURISset.size() > 1)
                isFunctional = false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing functionality. Reason: " + e.getMessage());
        }

        return isFunctional;


    }

    public static boolean testFunctionalitySPARQL(Individual individual, String propertyURI)
    {
        boolean isFunctional     = false;
        OntModel model          = individual.getOntModel();
        String individualURI    = individual.getURI();

        Query query = QueryFactory.create
                (
                    "SELECT ?b ?s ?s1 \n" +
                                "WHERE \n" +
                                "{ \n" +
                                "    <"+individualURI+"> <"+propertyURI+"> ?s . \n" +
                                "  OPTIONAL { \n" +
                                "    <"+individualURI+"> <"+propertyURI+"> ?s1 . \n" +
                                "    FILTER (?s != ?s1 ) } . \n" +
                                "} "
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            List<String> theResURIS = new ArrayList<String>();
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution querySolution = results.nextSolution();

            }
        }
        catch(Exception e)
        {
            System.out.println("Error testing functionality. Reason: " + e.getMessage());
        }

        return isFunctional;
    }

    public static boolean testTransitivenessSPARQL(Individual individual, String propertyURI, int levels)
    {
        if(levels == 0 )
            levels = 1;

        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();


        String queryString =
                "SELECT DISTINCT ?obj1 " ;

        for(int i = 2; i <=levels+1; i++)
            queryString+= " ?obj"+ i ;

        queryString += "\n WHERE \n" +
                "  { \n<" + individualURI + "> <" + propertyURI + "> ?obj1 . \n";

        int i = 1, i2 = 2;
        for(i = 1; i <=levels; i++)
        {
            i2 = i+1;
            queryString+= " ?obj"+ i + " <" + propertyURI + "> ?obj" + i2+ "  . \n" ;
        }

        for(i = 1; i <=levels; i++)
        {
            i2 = i+1;
            queryString+= " FILTER (?obj1 != ?obj" + i2+ " ) . \n" ;
            queryString+= " FILTER ( <" + individualURI + "> != ?obj" + i2+ " ) . \n" ;
        }

        queryString+= " } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj1 " ;

        for(i = 2; i <= levels+1; i++)
            queryString2+= " ?obj"+ i ;

        queryString2 += "\n WHERE { \n" ;

        int lasti = 1;
        for(i = 1; i <=levels; i++)
        {
            i2 = i+1;
            queryString2+= " ?obj"+ i + " <" + propertyURI + "> ?obj" + i2+ "  . \n" ;
            lasti = i2;
        }
        queryString2 += "   \n ?obj"+ lasti +" <" + propertyURI + ">  <" + individualURI + ">. \n";


        for(i = levels; i >= 1; i--)
        {
            queryString2 += " FILTER (?obj"+lasti+" != ?obj" + i+ " ) . \n" ;
            queryString2+= " FILTER ( <" + individualURI + "> != ?obj" + i+ " ) . \n" ;

        }

        queryString2 += " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";

        //Query query = QueryFactory.create(queryString);

        Query query = QueryFactory.create(finalQuery);

/*
        Transitive: if P(a,b) and P(b,c) then P(a,c)

            a can be in the beginning or ending of the query, in case we land somewhere in the middle

 */


        boolean isTransitive    = false;

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            List<String> theResURIS = new ArrayList<String>();
            ResultSet results   = qexec.execSelect();
            while(results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();

                for(i = 1; i <= levels+1; i++)
                {
                    String resN = "obj" + i;
                    Resource r1 = querySolution.getResource(resN);
                    theResURIS.add(r1.getURI());
                }

                boolean validAnswer = true;
                for (i = 0; i < theResURIS.size(); i++)
                {
                    for (int j = i+1; j < theResURIS.size(); j++)
                    {
                        if(theResURIS.get(i).equalsIgnoreCase(theResURIS.get(j)))
                        {
                            validAnswer = false;
                            break;
                        }
                    }
                    if(!validAnswer) break;
                }

                if(validAnswer)
                    isTransitive = true;
            }

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return isTransitive;
    }

    public static boolean testSupportTransitivenessSPARQL(Individual individual, String propertyURI)
    {
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        String queryString =
                "SELECT DISTINCT ?obj1 \n WHERE \n" +
                "  { \n" +
                        " <" + individualURI + "> <" + propertyURI + "> ?obj1 . \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2 . \n" +

                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                "  } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj1 \n WHERE " +
                "{ \n" +
                            " ?obj1 <" + propertyURI + "> ?obj2   . \n" +
                            " ?obj2 <" + propertyURI + "> <" + individualURI + ">  . \n" +
                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";


        Query query = QueryFactory.create(finalQuery);


        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();
                Resource object1 = querySolution.getResource("?obj1");

                return true;
            }
            else
                return false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return false;
    }


    public static Resource getTransitiveSequenceSPARQL(Individual individual, String propertyURI)
    {
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        String queryString =
                "SELECT DISTINCT ?obj2 \n WHERE \n" +
                        "  { \n" +
                        " <" + individualURI + "> <" + propertyURI + "> ?obj1 . \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2 . \n" +

                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        "  } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj2 \n WHERE " +
                        "{ \n" +
                        " ?obj2 <" + propertyURI + "> ?obj1   . \n" +
                        " ?obj1 <" + propertyURI + "> <" + individualURI + ">  . \n" +
                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";


        Query query = QueryFactory.create(finalQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
            {
                QuerySolution querySolution = results.nextSolution();
                Resource res = querySolution.getResource("?obj2");

                return res;
            }
            else
                return null;

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return null;
    }

    public static boolean testAgainstTransitivenessSPARQL(Individual individual, String propertyURI)
    {
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        String queryString =
                "SELECT DISTINCT ?obj1 \n WHERE \n" +
                        "  { \n" +
                        " <" + individualURI + "> <" + propertyURI + "> ?obj1 . \n" +

                        " FILTER NOT EXISTS { ?obj1 <" + propertyURI + "> ?obj2 } . \n" +
                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        "  } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj1 \n WHERE " +
                        "{ \n" +

                        " ?obj1 <" + propertyURI + "> <" + individualURI + ">  . \n" +
                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        " FILTER NOT EXISTS { ?obj1 <" + propertyURI + "> ?obj2 }   . \n" +
                        " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";


        Query query = QueryFactory.create(finalQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
                return true;
            else
                return false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return false;
    }

    public static boolean testSupportExplicitTransitivenessSPARQL(Individual individual, String propertyURI)
    {
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        String queryString =
                "SELECT DISTINCT ?obj1 \n WHERE \n" +
                        "  { \n" +

                        " <" + individualURI + "> <" + propertyURI + "> ?obj1 . \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2 . \n" +

                        " <" + individualURI + "> <" + propertyURI + "> ?obj2 . \n" +


                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        "  } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj1 \n WHERE " +
                        "{ \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2   . \n" +

                        " ?obj2 <" + propertyURI + "> <" + individualURI + ">  . \n" +
                        " ?obj1 <" + propertyURI + "> <" + individualURI + ">   . \n" +

                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";

        Query query = QueryFactory.create(finalQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
                return true;
            else
                return false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return false;
    }

    public static boolean testAgainstExplicitTransitivenessSPARQL(Individual individual, String propertyURI)
    {
        OntModel model          = ModelManager.getManager().getSlidingWindowModel();
        String individualURI    = individual.getURI();

        String queryString =
                "SELECT DISTINCT ?obj1 \n WHERE \n" +
                        "  { \n" +

                        " <" + individualURI + "> <" + propertyURI + "> ?obj1 . \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2 . \n" +

                        "FILTER NOT EXISTS { <" + individualURI + "> <" + propertyURI + "> ?obj2 . } \n" +


                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        "  } \n";

        String queryString2  =
                "SELECT DISTINCT ?obj1 \n WHERE " +
                        "{ \n" +
                        " ?obj1 <" + propertyURI + "> ?obj2   . \n" +

                        "  ?obj2 <" + propertyURI + "> <" + individualURI + ">  . \n" +

                        " FILTER NOT EXISTS { ?obj1 <" + propertyURI + "> <" + individualURI + "> }  . \n" +

                        " FILTER ( ?obj1 != ?obj2 ) . \n" +
                        " FILTER ( <" + individualURI + "> != ?obj1 ) . \n" +
                        " \n }";

        String finalQuery  = " SELECT * {\n "
                + "{ \n " + queryString + "\n }"
                + "OPTIONAL { \n " + queryString2 + "\n }"
                + "}";

        Query query = QueryFactory.create(finalQuery);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results   = qexec.execSelect();

            if(results.hasNext())
                return true;
            else
                return false;

        }
        catch(Exception e)
        {
            System.out.println("Error testing transitiveness. Reason: " + e.getMessage());
        }

        return false;
    }




    public static List<OntClass> getTimeSlicesSPARQL(OntClass ontClass)
    {
        List<OntClass> timeSlices = new ArrayList<>();
        OntModel model = ontClass.getOntModel();

        if(ontClass.getURI()==null)
            return timeSlices;

        Query query = QueryFactory.create
                (
                        "SELECT DISTINCT " +
                                " ?timeSlice ?b ?d \n" +
                                " WHERE \n" +
                                "  { ?timeSlice ?b ?d  .\n" +
                                "  ?d <http://www.w3.org/2002/07/owl#someValuesFrom> <"+ontClass.getURI()+">.\n" +
                                "  ?d <http://www.w3.org/2002/07/owl#onProperty> <http://www.w3.org/2006/time#isTimeSliceOf> .\n" +
                                "  FILTER (!isBlank(?timeSlice)) " +
                                "  } \n" +
                                " ORDER BY ASC(?timeSlice)"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution res = results.nextSolution();
                Resource theResource = res.getResource("timeSlice");

                if(theResource.isAnon() || theResource == null) continue;

                if(theResource.getURI().contains("TS__"))
                {
                    OntClass cls = model.getOntClass(theResource.getURI());
                    timeSlices.add(cls);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error obtaining TimeSlices. Reason: " + e.getMessage());
        }

        return timeSlices;
    }

    /**
     * Uses a SPARQL query to obtain an ordered list of URIs of all Individuals in an OntModel.
     * Ignores individuals in the Ignore List
     * @param model the OntModel to execute the search
     * @return List of URIs of Individuals
     */
    public static List<String> getIndividualsSPARQL(OntModel model)
    {
        //System.out.println("==SPARQL Listing individuals in the Model.==\n");

        List <String> individuals_uris = new ArrayList<>();

        Query query = QueryFactory.create
                (
                        "SELECT " +
                                //" DISTINCT " +
                                "?uri" +
                                " WHERE " +
                                "{" +
                                "?uri a ?t . " +

                                /*
                                " FILTER ( ?t != <" + OntologyUtils.INTERVAL_CLS + "> ) ." +
                                " FILTER ( ?t != <" + OntologyUtils.INSTANT_CLS + "> ) ." +

                                " FILTER ( ?t != <" + OntologyUtils.ONT_TIME_URL + "#DateTimeInterval> ) ." +
                                " FILTER ( ?t != <http://www.w3.org/2000/01/rdf-schema#Class> ) ." +
                                " FILTER ( ?t != <http://www.w3.org/2002/07/owl#Class> ) . " +
                                " FILTER ( ?t != <http://www.w3.org/2002/07/owl#DatatypeProperty> ) . " +
                                " FILTER ( ?t != <http://www.w3.org/2002/07/owl#ObjectProperty> ) . " +
                                */

                                "}"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution res    = results.nextSolution();
                Resource theResource = res.getResource("uri");

                // TODO decide what to do about anonymous classes!

                if(theResource.getURI()!=null && !model.getOntResource(theResource.getURI()).isProperty())
                {
                    String uri  = theResource.getURI();

                    if(
                            uri.startsWith("http://www.w3.org/2000/01/rdf-schema#")
                            || uri.startsWith("http://www.w3.org/2002/07/owl#")
                            || uri.contains( OntologyUtils.ONT_TIME_URL )
                            || uri.startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
                            || Utilities.isInIgnoreList(uri)
                    )
                        continue;

                    try
                    {
                        Individual individual = ModelManager.getManager().getInstanceModel().getIndividual(uri);

                        if(individual.canAs(OntClass.class) || individual.canAs(OntProperty.class) || individual.canAs(Ontology.class))
                            continue;

                        individuals_uris.add(uri);
                    }
                    catch(Exception e)
                    {
                        //System.out.println("URI " + uri + " is not an Individual of the Model.");
                    }

                }
            }
        }

        individuals_uris = new ArrayList<>(new HashSet<>(individuals_uris));
        Collections.sort(individuals_uris);

        System.out.println("\t\t== SPARQL Listing individuals in the Model. Count: "+ individuals_uris.size() +" ==");

        return individuals_uris;
    }

    /**
     * Uses SPARQL to retrieve a list of all OntClasses in an OntModel that are not in the Exclusion/Ignore List.
     * @param model The OntModel to execute the query
     * @return List of OntClasses
     */
    public static List<OntClass> listOntClassesSPARQL(OntModel model)
    {
        List<OntClass> clss   = new ArrayList<>();
        Query query = QueryFactory.create
                (
                        "SELECT DISTINCT " +
                                "?class " +
                                " WHERE " +
                                "{" +
                                "?a a ?class . " +
                                " FILTER ( ?class != <http://www.w3.org/2002/07/owl#NamedIndividual> ) . " +
                                "}" +
                                " ORDER BY ASC(?class) "
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution res    = results.nextSolution();
                Resource theResource = res.getResource("?class");

                if(!theResource.isURIResource()) continue;
                if(Utilities.isInIgnoreList(theResource.getURI())) continue;

                try
                {
                    OntClass cls = model.getOntClass(theResource.getURI());
                    clss.add(cls);
                }
                catch(Exception e)
                {
                    System.out.println("Error retrieving class with uri " + theResource.getURI() + " from Model." );
                }

            }
        }

        return clss;
    }

    /**
     * Lists the OntClasses of a given Individual
     * @param i the Individual whose OntModel the query will be run again
     * @return List of OntClasses which form the RDFS:TYPE of the Individual i
     */
    public static List<OntClass> listOntClassesSPARQL(Individual i)
    {
        String individual_uri = i.getURI();
        OntModel model        = i.getOntModel();
        List<OntClass> clss   = new ArrayList<>();

        if(individual_uri.contains("zygarde"))
            return clss;

        Query query = QueryFactory.create
                (
                        "SELECT DISTINCT " +
                                "?class " +
                                " WHERE " +
                                "{" +
                                "<"+ individual_uri + "> a ?class . " +
                                " FILTER ( ?class != <http://www.w3.org/2002/07/owl#NamedIndividual> ) . " +

                                "}"
                );

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution res    = results.nextSolution();
                Resource theResource = res.getResource("?class");

                if (!theResource.isURIResource())                  continue;
                if(Utilities.isInIgnoreList(theResource.getURI())) continue;

                try
                {
                    OntClass cls = model.getOntClass(theResource.getURI());
                    clss.add(cls);
                }
                catch(Exception e)
                {
                    System.out.println("Error retrieving class with uri " + theResource.getURI() + " from Model." );
                }

            }
        }

        //System.out.println("\t\t== SPARQL Listing Classes for individual " + individual_uri + " in the Model. Count: "+clss.size()+" ==\n");
        return clss;
    }


    /**
     * Uses SPARQL to identify the Properties associated with an Individual
     * @param i the Individual whose OntProperties we're looking for
     * @return Map of Property URI and RDFNODE value
     */
    public static List<Pair<String, RDFNode>> listPropertiesSPARQL(Individual i, boolean distinct)
    {
        String individual_uri               = i.getURI();
        OntModel model                      = i.getOntModel();
        List<Pair<String, RDFNode>> property_uris = new ArrayList<>();

        //System.out.println("==SPARQL Listing individual "+ individual_uri+"'s properties in the Model.==\n");

        String query_String =  "SELECT ";

        if(distinct)
            query_String += "DISTINCT ";

        query_String+= "?prop ?obj " +
                " WHERE " +
                "{ " +
                "<"+ individual_uri + "> ?prop ?obj. " +
                " }";

        Query query = QueryFactory.create(query_String);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model))
        {
            ResultSet results = qexec.execSelect();
            while (results.hasNext())
            {
                QuerySolution res = results.nextSolution();
                String prop       = res.getResource("prop").getURI();
                RDFNode obj_node  = res.get("obj");

                if(prop!=null && obj_node!= null)
                {
                    try
                    {
                        if(Utilities.isInIgnoreList(prop))
                            continue;

                        Pair<String, RDFNode> pap = Pair.of(prop, obj_node);

                        boolean insert = true;
                        for(Pair<String, RDFNode> old_node : property_uris)
                        {
                            boolean sameProp = old_node.getLeft().equalsIgnoreCase(pap.getLeft());
                            boolean sameVal  = false;

                            if(old_node.getRight().isURIResource() && pap.getRight().isURIResource())
                            {
                                String prev_URI = old_node.getRight().asResource().getURI();
                                String new_uri = pap.getRight().asResource().getURI();

                                if(prev_URI.equalsIgnoreCase(new_uri))
                                {
                                    sameVal = true;
                                }
                            }


                            if(sameProp && sameVal)
                            {
                                insert = false; break;
                            }


                        }
                        
                        if (insert) 
                            property_uris.add(pap);
                    }
                    catch(Exception e)
                    {
                        System.out.println("URI " + prop + " not a Property in the Model.");
                    }
                }
            }
        }

        //System.out.println("\t\t== SPARQL Listing Properties for individual "+ individual_uri+". Count: "+ property_uris.size()+" ==\n");

        return property_uris;
    }


    public static List<Pair<String, RDFNode>> listPropertiesSPARQL(Individual i)
    {
        return listPropertiesSPARQL(i, true);
    }


}
