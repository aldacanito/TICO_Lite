

package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Properties prop;
    
    public Configs()
    {
        this.prop = new Properties();
        this.readProperties();
    }
    
    public void readProperties()
    {
        try 
        {
            FileInputStream ip = new FileInputStream(this.config_file);
            prop.load(ip);
            
        } catch (Exception ex)
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
            
            this.prop.store(outp, "default configuration, auto-generated");
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Configs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
