

package Utils;

import java.io.File;
import java.io.FileInputStream;
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

    private static final String config_file = "config.properties";
    public static Properties prop;
    public static String[] NS_to_ignore;
    public static List<String> class_to_ignore;
    
    public static int functional_threshold;
    public static int subclass_threshold;
    public static int equivalent_threshold;
    public static int someValuesFrom_threshold;

    public static int windowSize = 100;
    
    
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
            
            this.prop.setProperty("verbose",                    "true");
            this.prop.setProperty("model_print",                "TTL"); // alternatively RDF/XML
            
            this.prop.store(outp, "default configuration, auto-generated");
            
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
