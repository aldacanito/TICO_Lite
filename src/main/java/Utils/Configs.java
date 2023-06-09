

package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Colocar configurações e variáveis finais aqui
 * 
 */
public class Configs 
{
    public static final String ASIIO_NS = "http://www.gecad.isep.ipp.pt/ASIIO#";
    private static final String config_file = "config.properties";
    public static Properties prop;
    public static String[] NS_to_ignore;
    public static List<String> class_to_ignore;
    
    public static int functional_threshold;
    public static int subclass_threshold;
    public static int equivalent_threshold;
    public static int someValuesFrom_threshold;
    
    
    public Configs()
    {
        this.prop = new Properties();
        this.readProperties();
        NS_to_ignore = new String[0];
        class_to_ignore = new ArrayList();
        
        String namespace_ignore = (String) this.prop.get("namespace_ignore");
        
        if(namespace_ignore!=null && !namespace_ignore.isEmpty())
            NS_to_ignore = namespace_ignore.split(";");
        
    }
    
    
    public void readProperties()
    {
        try 
        {
            FileInputStream ip = new FileInputStream(this.config_file);
            prop.load(ip);
            
            String verbose = (String) prop.getOrDefault("verbose", "true");
            Utilities.verbose = Boolean.parseBoolean(verbose);
            
//            this.functional_threshold = Integer.parseInt( (String) prop.getOrDefault("functional_threshold", "10"));
//            this.subclass_threshold   = Integer.parseInt( (String) prop.getOrDefault("subclass_threshold",   "10"));
//            this.equivalent_threshold = Integer.parseInt( (String) prop.getOrDefault("equivalent_threshold", "50"));
//            this.someValuesFrom_threshold = Integer.parseInt( (String) prop.getOrDefault("someValyesFrom_threshold", "15"));
            
            this.functional_threshold = Integer.parseInt( (String) prop.getOrDefault("functional_threshold", "0"));
            this.subclass_threshold   = Integer.parseInt( (String) prop.getOrDefault("subclass_threshold",   "0"));
            this.equivalent_threshold = Integer.parseInt( (String) prop.getOrDefault("equivalent_threshold", "02"));
            this.someValuesFrom_threshold = Integer.parseInt( (String) prop.getOrDefault("someValuesFrom_threshold", "0"));
      
        } 
        catch (Exception ex)
        {
            Utilities.getLogger().log(Level.SEVERE, null, ex);
            createSamplePropertiesFile(); // se nao existe cria um novo
        }
          
    }
    
    
    private void createSamplePropertiesFile()
    {
        try 
        {
            File f = new File(this.config_file);
            if(!f.exists())
                    f.createNewFile();

            FileOutputStream outp = new FileOutputStream(this.config_file);
                
            this.prop = new Properties();
            
            this.prop.setProperty("compareClassImplementation", "Comparison.Implementations.Simple.ClassCompareSimple.java");
            this.prop.setProperty("compareOPImplementation",    "Comparison.Implementations.Simple.ObjectPropertyCompareSimple.java");
            this.prop.setProperty("compareDPImplementation",    "Comparison.Implementations.Simple.DatatypePropertyCompareSimple.java");
            
            this.prop.setProperty("addClassImplementation",     "EvolutionaryActions.Implementations.Simple.AddClass.java");
            this.prop.setProperty("addOPImplementation",        "EvolutionaryActions.Implementations.Simple.AddObjectProperty.java");
            this.prop.setProperty("addDPImplementation",        "EvolutionaryActions.Implementations.Simple.AddDatatypeProperty.java");
            
            this.prop.setProperty("deleteClassImplementation",  "EvolutionaryActions.Implementations.Simple.DeleteClass.java");
            this.prop.setProperty("deleteOPImplementation",     "EvolutionaryActions.Implementations.Simple.DeleteObjectProperty.java");
            this.prop.setProperty("deletePImplementation",      "EvolutionaryActions.Implementations.Simple.DeleteDatatypeProperty.java");
            
            this.prop.setProperty("namespace_ignore",           "http://www.example.org/Pianism/OntoPianismIndividuals.owl#erpTimeInstant;http://www.w3.org/2002/07/owl;http://www.w3.org/2006/time;http://purl.org/dc/terms;http://www.w3.org/1999/02/22-rdf-syntax-ns;http://www.w3.org/2000/01/rdf-schema;");
            this.prop.setProperty("verbose",                    "true");
            
            this.prop.setProperty("model_print",                "TTL"); // alternatively RDF/XML
            
            
            this.prop.setProperty("functional_threshold",       "10"); 
            this.prop.setProperty("subclass_threshold",         "10"); 
            this.prop.setProperty("equivalent_threshold",       "50"); 
            
            
            this.prop.store(outp, "default configuration, auto-generated");
            
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
