package Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;


import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import IDC.Metrics.ConstructorMetrics;
import IDC.Metrics.EntityMetricsStore;
import IDC.Metrics.PropertyMetrics;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.BasicConfigurator;

import static Utils.Configs.*;

public class Utilities 
{


    public static HashMap <String, List<String>> importantProps = new HashMap<>();
    public static boolean init = false;


    public static void initProps()
    {

        if(!init) {
            importantProps = new HashMap<>();

            List<String> props = new ArrayList<>();

            props.add("http://cmt#acceptedby");
            props.add("http://cmt#hasDecision");

            props.add("http://cmt#acceptedPaper");
            props.add("http://cmt#addedBy");
            props.add("http://cmt#addProgramCommitteeMember");
            props.add("http://cmt#adjustBid");
            props.add("http://cmt#adjustedBy");
            props.add("http://cmt#assignedByReviewer");
            props.add("http://cmt#assignExternalReviewer");
            props.add("http://cmt#hasAuthor");
            props.add("http://cmt#hasBid");

            props.add("http://cmt#readByMeta-Reviewer");

            props.add("http://cmt#rejectedBy");
            props.add("http://cmt#rejectPaper");
            props.add("http://cmt#writePaper");


            props.add("http://cmt#writtenBy");

            props.add("http://cmt#writeReview");


            importantProps.put("cmt", props);

            List<String> props2 = new ArrayList<>();

            props2.add("http://confOf#parallel_with");
            props2.add("http://confOf#follows");
            props2.add("http://confOf#hasCountry");

            props2.add("http://confOf#dealsWith");
            props2.add("http://confOf#employedBy");
            props2.add("http://confOf#expertOn");
            props2.add("http://confOf#hasCity");
            props2.add("http://confOf#hasAdministrativeEvent");

            props2.add("http://confOf#hasTopic");

            props2.add("http://confOf#reviewes");
            props2.add("http://confOf#writes");
            props2.add("http://confOf#writtenBy");

            importantProps.put("confOf", props2);

            List<String> props3 = new ArrayList<>();

            props3.add("http://ekaw#hasPart");
            props3.add("http://ekaw#partOf");

            props3.add("http://ekaw#partOfEvent");

            importantProps.put("ekaw", props3);

            List<String> props4 = new ArrayList<>();

            props4.add("http://conference#is_the_1th_part_of");
            props4.add("http://conference#reviews");
            props4.add("http://conference#was_a_program_committee_of");
            props4.add("http://conference#was_a_steering_committee_of");
            props4.add("http://conference#was_an_organizing_committee_of");
            props4.add("http://conference#was_a_committee_chair_of");
            props4.add("http://conference#was_a_track-workshop_chair_of");
            props4.add("http://conference#belong_to_a_conference_volume");
            props4.add("http://conference#belongs_to_a_review_reference");
            props4.add("http://conference#has_a_program_committee");
            props4.add("http://conference#has_a_steering_committee");
            props4.add("http://conference#has_an_organizing_committee");
            props4.add("http://conference#has_a_publisher");
            props4.add("http://conference#has_a_review");
            props4.add("http://conference#has_a_review_expertise");
            props4.add("http://conference#has_a_submitted_contribution");
            props4.add("http://conference#has_a_topic_or_a_submission_contribution");
            props4.add("http://conference#has_a_track-workshop-tutorial_chair");
            props4.add("http://conference#has_a_track-workshop-tutorial_topic");
            props4.add("http://conference#has_an_abstract");
            props4.add("http://conference#has_been_assigned_a_review_reference");
            props4.add("http://conference#has_important_dates");
            props4.add("http://conference#has_a_committee_chair");

            importantProps.put("conference", props4);


            List<String> props5 = new ArrayList<>();

            props5.add("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#adjacentRegion");
            props5.add("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#hasMaker");
            props5.add("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#locatedIn");

            importantProps.put("wine", props5);

            List<String> props6 = new ArrayList<>();

            props6.add("http://purl.obolibrary.org/obo/BFO_0000051");
            props6.add("http://purl.obolibrary.org/obo/BFO_0000050");
            props6.add("http://purl.obolibrary.org/obo/part_of");
            props6.add("http://purl.obolibrary.org/obo/RO_0002202");
            props6.add("http://data.bioontology.org/metadata/obo/part_of");

            importantProps.put("plant", props6);


            List<String> props7 = new ArrayList<>();
            props7.add("https://pokemon.com#bordersWith");

            importantProps.put("pokemon", props7);


            init = true;
        }

    }
    public static boolean isImportantProp( String propertyURI)
    {
        //initProps();

        if( importantProps.containsKey(AnalyticUtils.ONTO_NAME) )
        {
            List<String> theProps = importantProps.get(AnalyticUtils.ONTO_NAME);

            if(theProps.contains(propertyURI))
                return true;
        }

        return false;
    }


    private static boolean relevantIndividual(String line)
    {

        //if( line.contains("paper") )
        //    return true;


      //  if( line.toLowerCase().contains("region") )
        //    return true;



        if(line.contains("person") || line.contains("paper") )
            return true;

        if(AnalyticUtils.ONTO_NAME.equalsIgnoreCase("conference"))
            if(line.contains("abstr") ||  line.contains("Date") )
                return true;

        if(AnalyticUtils.ONTO_NAME.equalsIgnoreCase("confOf"))
            if(line.contains("event") || line.contains("workshop") || line.contains("Evt")  || line.contains("tutorial")  || line.contains("topic"))
                return true;

        if(AnalyticUtils.ONTO_NAME.equalsIgnoreCase("ekaw"))
            if(line.contains("presentation") || line.contains("proceedings") || line.contains("topic")  || line.contains("tutorial")  || line.contains("conference"))
                return true;


        return false;
    }


    public static void generateInstanceListFile(String fileName, OntModel model)
    {
        List<Individual> inds = model.listIndividuals().toList();

        for(Individual i : inds)
        {
            if(i.getURI() != null && !i.getURI().isEmpty() && !i.getURI().isEmpty())
                Utilities.appendLineToFile(fileName, i.getURI());
        }
    }

    public static List<String> extractInstancesFromFile(String datasetFolder)
    {
        //String file_content = readFileContent(AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_instance.ttl");

        List<String> individuals_uris = new ArrayList<>();

        String filename = AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_0.ttl";

//        String filename = datasetFolder + "/" + AnalyticUtils.ONTO_NAME + "//" + AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_0.ttl";

        if(!filename.contains("plant") && !filename.contains("wine"))
        {
            String file_content = readFileContent(filename);
            String lines[] = file_content.split("\\r?\\n");

            for (String line : lines) {
                if (line.contains("-instances") && line.startsWith("<") && relevantIndividual(line)) {
                    line = line.replace("<", "").replace(">", "");
                    individuals_uris.add(line);
                }
            }
        }
        else
        {
            filename = datasetFolder + "/" + AnalyticUtils.ONTO_NAME + "//" + AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_instances.txt";
            String file_content = readFileContent(filename);
            String lines[] = file_content.split("\\r?\\n");

            for (String line : lines) {
                if (!line.isEmpty()) {
                    individuals_uris.add(line);
                }
            }


        }

        individuals_uris = new ArrayList<>(new HashSet<>(individuals_uris));
        //Collections.sort(individuals_uris);

        Collections.shuffle(individuals_uris);

        return individuals_uris;
    }

    public static List<String> extractInstancesFromFile()
    {
        //String file_content = readFileContent(AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_instance.ttl");

        String file_content = readFileContent(AnalyticUtils.INSTANCE_FOLDER + "/" + AnalyticUtils.ONTO_NAME + "_0.ttl");
        String lines []     = file_content.split("\\r?\\n");
        List<String> individuals_uris = new ArrayList<>();

        for(String line : lines)
        {
            if(line.contains("-instances") && line.startsWith("<") && relevantIndividual(line))
            {
                line = line.replace("<", "").replace(">", "");
                individuals_uris.add(line);
            }
        }

        individuals_uris = new ArrayList<>(new HashSet<>(individuals_uris));
        //Collections.sort(individuals_uris);

        Collections.shuffle(individuals_uris);

        return individuals_uris;
    }




    public static boolean deleteFile(String filename)
    {
        File myObj = new File(filename);
        if (myObj.delete()) {
            System.out.println("Deleted the file: " + myObj.getName());
            return true;
        } else {
            System.out.println("Failed to delete the file.");
            return false;
        }
    }


    public static void createFile(String filename)
    {
        File file = new File(filename);
        File dir  = file.toPath().getParent().toFile();

        if(!dir.exists()) dir.mkdirs();

        boolean fe = !file.exists() && !file.isDirectory()  ;

        if(!file.isFile())
        {
            try
            {
                FileOutputStream outputStream         = new FileOutputStream(filename);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                BufferedWriter bufferedWriter         = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write("");
                bufferedWriter.close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }



    public static void appendLineToFile(String filename, String line)
    {
        try
        {
            line += "\n";
            Files.write(Paths.get(filename), line.getBytes(), StandardOpenOption.APPEND);

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }








    // chops a list into non-view sublists of length L
    public static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
    
    public static void addToClassIgnoreList(String i)
    {
        if(i == null || i.isBlank() || i.isBlank())
            return;
    
        class_to_ignore.add(i);
        
    }
    
    public static boolean isInClassIgnoreList(String i)
    {
        if(i == null || i.isBlank() || i.isBlank())
            return false;
        
        return class_to_ignore.contains(i);
    }


    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2)
    {
        if(list1 == null && list2 == null) return true;
        if(list1 == null && list2 != null) return false;
        if(list1 != null && list2 == null) return false;

        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }


    public static boolean isInIgnoreList(String i)
    {
        
        boolean ret = false;
        if(i == null || i.isBlank() || i.isBlank())
            return ret;
        
        for(String ignore : NS_to_ignore)
        {
            if(i.contains(ignore))
                return true;
        }
   
        return ret;
    }

    // Generic method to check for duplicates in an array
    public static <T> boolean checkForDuplicates(T... array)
    {
        // for every array element, check if it is found afterward in the array
        for (int i = 0; i < array.length; i++)
        {
            for (int j = i + 1; j < array.length; j++)
            {
                if (array[i] != null && array[i].equals(array[j])) {
                    return true;
                }
            }
        }

        // no duplicate is found
        return false;
    }
    
    /** LOG UTILITIES **/
    
   
    private static final Logger logger = Logger.getLogger("InstanceDrivenComparison"); 
    private boolean logger_Active = false;
    protected static boolean verbose = false;
    
    
    
    public static void logInfo(String message)
    {
        if(verbose)
            System.out.println("LOG | INFO |  Message: " + message);
        logger.info(message);
    }
    
    public static void logError(String message)
    {
        if(verbose)
            System.out.println("LOG | ERROR |  Message: " + message);
        logger.severe(message);
    }
    
    public static void logError(String message, String stacktrace)
    {
        if(verbose)
            System.out.println("LOG | ERROR |  Message: " + message + "\n STACKTRACE: " + stacktrace);
        logger.severe(message);
    }
    
           

    public static Logger getLogger() 
    {
        BasicConfigurator.configure();
        String fileName = "logs/ComparisonLog" + LocalDate.now().toString() + ".log";
        FileHandler fh;  
        try 
        {
            fh = new FileHandler(fileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (IOException | SecurityException ex) 
        {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        return logger;
    }
    
    
    /** FILE UTILITIES **/
    
    
    /**
     * Saves a String content into a file.
     * 
     * @param filename the complete filename, including the path and file extension.
     * @param content  the file content
     */
    public static void save(String filename, String content) {
        // Create folder if it not exists
        File dir = new File(filename).toPath().getParent().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Write file
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(content);

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a text file content from the class path resources folder
     * 
     * @param resourceFilePath the file path to read
     * @return the file content; null if does not exists
     */
    public static String readResourcesTextFileContent(String resourceFilePath) {
        String content = "", line;

        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(
                    Utilities.class.getClassLoader().getResourceAsStream(resourceFilePath)));

            while ((line = reader.readLine()) != null) {
                content += line + "\r\n";
            }
        } catch (NullPointerException e) {
            // ignored
        } catch (IOException e) {
            // ignored
        }

        return content;
    }


    public static String readFileContent(String filePath)
    {
        String content = "";

        if (Files.exists(Path.of(filePath)))
        {
            try
            {
                content += new String(Files.readAllBytes(Paths.get(filePath)));
            } catch (IOException e) {
                content = null;
            }
        }
        return content;
    }


    public static String readTextFileContent(String filePath, boolean addList) {
        String content = "";

        if (Files.exists(Path.of(filePath))) {
            try {
                if(addList)
                    content += "{ \"list\" : ";
                    
                content += new String(Files.readAllBytes(Paths.get(filePath)));
    
                if(addList)
                    content += "}";
            
            } catch (IOException e) {
                content = null;
            }
        }

        return content;
    }







//    
//    
//    /** JSON UTILITIES **/
//   
//    /**
//     * Validates if a given string is a valid JSON object
//     * 
//     * @param json the JSON string to be checked
//     * @return true if it is a valid JSON object; false otherwise
//     */
//    public static boolean isValidJSONObject(String json) {
//        boolean valid = true;
//        try {
//            new JSONObject(json);
//        } catch (JSONException e) {
//            valid = false;
//        }
//        return valid;
//    }
//
//    /**
//     * Validates if a given string is a valid JSON array
//     * 
//     * @param json the JSON string to be checked
//     * @return true if it is a valid JSON array; false otherwise
//     */
//    public static boolean isValidJSONArray(String json) {
//        boolean valid = true;
//        if (json == null) {
//            valid = false;
//        } else {
//            try {
//                new JSONArray(json);
//            } catch (JSONException e) {
//                valid = false;
//            }
//        }
//        return valid;
//    }
//
//    /**
//     * Validates a given JSON schema.
//     * 
//     * @param schema the JSON schema to be validated
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONSchemaStr(String schema) {
//        boolean valid = false;
//
//        try {
//            JSONObject rawSchema = new JSONObject(schema);
//            SchemaLoader.load(rawSchema);
//            valid = true;
//        } catch (Exception e) {
//            // ignored
//        }
//
//        return valid;
//    }
//
//    /**
//     * Validates a given JSON schema by file path.
//     * 
//     * @param pathSchema the path of the JSON schema to be validated
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONSchemaFile(String pathSchema) {
//        boolean valid = false;
//
//        try {
//            valid = isValidJSONSchemaStr(new String(Files.readAllBytes(Paths.get(pathSchema))));
//        } catch (IOException e) {
//            // ignored
//        }
//
//        return valid;
//    }
//
//    /**
//     * Validates a JSON string for a given JSON schema.
//     * 
//     * @param json   the JSON string to be validated
//     * @param schema the JSON schema to validate the JSON string
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONStrSchemaStr(String json, String schema) {
//        boolean valid = false;
//
//        try {
//            JSONObject rawSchema = new JSONObject(schema);
//            Schema sch = SchemaLoader.load(rawSchema);
//            if (isValidJSONObject(json)) {
//                sch.validate(new JSONObject(json));
//            } else {
//                sch.validate(new JSONArray(json));
//            }
//            valid = true;
//        } catch (Exception e) {
//            // ignored
//        }
//
//        return valid;
//    }
//
//    /**
//     * Validates a JSON file for a given JSON schema file.
//     * 
//     * @param pathJson   the file path for JSON to be validated
//     * @param pathSchema the file path for JSON schema to validate the JSON file
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONFileSchemaFile(String pathJson, String pathSchema) {
//        boolean valid = false;
//
//        try {
//            valid = isValidJSONStrSchemaStr(new String(Files.readAllBytes(Paths.get(pathJson))),
//                    readResourcesTextFileContent(pathSchema));
//        } catch (IOException e) {
//            // ignored
//        }
//
//        return valid;
//    }
//
//    /**
//     * Validates a JSON string for a given JSON schema file.
//     * 
//     * @param json       the JSON string to be validated
//     * @param pathSchema the file path for JSON schema to validate the JSON string
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONStrSchemaFile(String json, String pathSchema) {
//        return isValidJSONStrSchemaStr(json, readResourcesTextFileContent(pathSchema));
//    }
//
//    /**
//     * Validates a JSON file for a given JSON schema.
//     * 
//     * @param pathJson the file path for JSON to be validated
//     * @param schema   the JSON schema to validate the JSON string
//     * @return true if valid; false otherwise.
//     */
//    public static boolean isValidJSONFileSchemaStr(String pathJson, String schema) {
//        boolean valid = false;
//
//        try {
//            valid = isValidJSONStrSchemaStr(new String(Files.readAllBytes(Paths.get(pathJson))), schema);
//        } catch (IOException e) {
//            // ignored
//        }
//
//        return valid;
//    }
//
//    /**
//     * Gets a JSON path query result from a JSON string
//     * 
//     * @param json  the JSON string to be considered
//     * @param query the JSON path query
//     * @return the query result; null if any exception is thrown
//     */
//    public static String getJSONPath(String json, String query) {
//        Configuration conf = Configuration.defaultConfiguration();
//        conf.addOptions(Option.ALWAYS_RETURN_LIST);
//        conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
//        conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
//
//        Object obj = null;
//
//        try {
//            obj = JsonPath.using(conf).parse(json).read(query);
//        } catch (PathNotFoundException e) {
//            obj = null;
//        }
//
//        return obj.toString();
//    }
//
//	/**
//	 * Auxiliary control class
//	 */
//	public static class MyCondVar {
//		private boolean value = false;
//
//		public synchronized void waitOn() throws InterruptedException {
//			while (!value) {
//				wait();
//			}
//		}
//
//		public synchronized void signal() {
//			value = true;
//			notifyAll();
//		}
//	}

}
