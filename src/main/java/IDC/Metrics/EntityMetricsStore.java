/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Metrics;

import Utils.Utilities;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shizamura
 */
public class EntityMetricsStore 
{
    private static EntityMetricsStore theInstance = new EntityMetricsStore();
    private List<ClassPropertyMetrics> theMetrics;
   
    private EntityMetricsStore()
    {
        theMetrics = new ArrayList<ClassPropertyMetrics>();
    }

    public static EntityMetricsStore getStore()
    {
        return theInstance;
    }

    public void addClassPropertyMetrics(ClassPropertyMetrics cpm)
    {
        if(!theMetrics.contains(cpm))
            theMetrics.add(cpm);
    }
   
    
    public ClassPropertyMetrics getMetricsByClassURI(String classURI)
    {
        ClassPropertyMetrics ret = null;
        for(ClassPropertyMetrics cpm : this.theMetrics)
        {
            if(cpm.getURI().equalsIgnoreCase(classURI))
                return cpm;
        }
    
        //Utilities.logInfo("Class Metrics for URI " + classURI + " are not in the Store.");
        return ret;
    }
    
}
