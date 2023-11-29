package Utils;

import IDC.Metrics.ConstructorMetrics;
import IDC.Metrics.EntityMetricsStore;
import IDC.Metrics.PropertyMetrics;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntProperty;

import java.io.File;
import java.util.List;
import java.util.Set;

public class AnalyticUtils
{
    public static String CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/Constructors/";
    public static String ANALYTICS_FOLDER = "Analytics/";
    public static String ONTO_NAME = "cmt";

    public static String CURRENT_FOLDER =  "/";
    public static String INSTANCE_FOLDER = "/instances/";

    public static String getPropertyNameforPath(String propertyURI)
    {
        if(propertyURI.contains("#"))
            propertyURI     = propertyURI.split("#")[1];
        else if (propertyURI.contains("/"))
        {
            String parts []= propertyURI.split("/");
            propertyURI = parts[parts.length - 1];
        }
        return propertyURI;
    }
    public static String getConstructorFilename(String constructor, String propertyURI)
    {
        return CONSTRUCTOR_ANALYTICS_FOLDER + getPropertyNameforPath(propertyURI) + "_" + constructor + ".csv";
    }

    public static String writePropertyHeader(String constructor, String propertyURI)
    {
        String filename = getConstructorFilename(constructor, propertyURI);
        String header   = "support;against;neutral\n";
        Utilities.createFile(filename);
        return filename;
    }

    public static String writeHeader(String className, OntProperty p, String p_uri)
    {
        className = getPropertyNameforPath(className);
        p_uri     = getPropertyNameforPath(p_uri);

        String filename = ANALYTICS_FOLDER + className + "_" + p_uri + ".csv";

        String header = "TMen;DMen;Freq;MaxMenI;MinMenI;AVGMenI;Funct;";

        if(!p.isDatatypeProperty())
            header +="R_Count;R_Ratio;IR_Count;IR_Ratio;Sym_Count;Sym_Ratio;TR2_Count;TR2_Ratio;TR3_Count;TR3_Ratio";

        header+="\n";

        Utilities.createFile(filename);

        return filename;
    }

    public static void deleteAnalytics()
    {
        try
        {
            FileUtils.forceDelete(new File(CONSTRUCTOR_ANALYTICS_FOLDER));
        }
        catch(Exception e)
        {
            System.out.println("Error deleting analytics. Reason:" + e.getMessage());
        }

    }

    public static void printAllComputations(Set<String> propertyURIs)
    {
        for(String propertyURI : propertyURIs)
        {
            String propertyPart = getPropertyNameforPath(propertyURI);

            System.out.println("\n=======================\nPrinting computations for " + propertyPart);
            String fileName   = CONSTRUCTOR_ANALYTICS_FOLDER + "Constructors_totals_" + propertyPart + ".csv";
            Utilities.createFile(fileName);

            PropertyMetrics pm = EntityMetricsStore.getStore().getMetricsByPropertyURI(propertyURI);
            List<ConstructorMetrics> constructorMetrics = pm.getConstructors();

            String line  = "";

            for(ConstructorMetrics cm : constructorMetrics)
            {
                int support        = cm.getSupport();
                int against        = cm.getAgainst();
                int neutral        = cm.getNeutral();

                line += support + ";" + against + ";" + neutral + ";";
            }

            Utils.Utilities.appendLineToFile(fileName, line);
            System.out.println("\n=======================\nFinished printing computations for " + propertyPart + ". File: " + fileName);

        }
    }

}
