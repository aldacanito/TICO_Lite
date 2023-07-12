package Utils;

import static Utils.Configs.NS_to_ignore;
import static Utils.Configs.class_to_ignore;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.log4j.BasicConfigurator;

public class Utilities 
{

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

    /**
     * Reads a text file content
     * 
     * @param filePath the text file path
     * @return the text file content
     */
//    public static String readTextFileContent(String filePath) {
//        String content = null;
//
//        if (Files.exists(Path.of(filePath))) {
//            try {
//                content = new String(Files.readAllBytes(Paths.get(filePath)));
//            } catch (IOException e) {
//                content = null;
//            }
//        }
//
//        return content;
//    }
    
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
