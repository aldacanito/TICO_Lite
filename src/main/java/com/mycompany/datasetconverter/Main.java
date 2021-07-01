/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.datasetconverter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

/**
 *
 * @author Alda
 */
public class Main 
{


    public static Model model = ModelFactory.createDefaultModel();
    public static ArrayList<String> assets = new ArrayList<String>();    
    public static ArrayList<String> moments = new ArrayList<String>();    
    
    
    public static void main(String[] args) 
    {
        
        // read file
        
        
       
        
        // Get JSON configuration
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent("Indexes/acs_mirror_alerts.json"));
    
        Map<String, Object> toMap = joAlerts.toMap();
        
//        Set<String> s = toMap.keySet();
        
        ArrayList<HashMap> aList = (ArrayList<HashMap>) toMap.get("list");
        
        
        
        // vai iterar as instancias
        Iterator i = aList.iterator();
        while (i.hasNext()) 
        {
           HashMap aa = (HashMap) i.next();
            
           
           // para o evento
           String alert_id = aa.get("alert_id").toString();
           
           String sensor            = aa.get("sensor").toString();
           String source_host_name  = aa.get("source_host_name").toString();
           
  
           String detect_time = aa.get("detect_time").toString();
           String type = aa.get("Type").toString();
           String alert_description = aa.get("alert_title").toString() + " - " + aa.get("alert_description").toString();
                      
           // para o alerta
           
           String severity = aa.get("severity").toString();
           
           createEvent(alert_id, sensor, source_host_name, detect_time, type, alert_description, severity);
           
           
        }
        
    }
    
    
    
    
    private static void createEvent(String eventId, String sensor, String source_host_name, 
                                    String start_time, String type, String description, String severity)
    {
        
        String extCriticality = "";
        switch(severity)
        {
            case "low":
                extCriticality = "Normal";
                break;
            case "medium":
                extCriticality = "Escalation";
                break;
            case "high":
                extCriticality = "Emergency";
                break;
        }
        
        String assetID = sensor + "_" + source_host_name;
        
        String insert =
            " PREFIX  asiio:        <http://www.gecad.isep.ipp.pt/ASIIO#>\n" +    
            " PREFIX  time:         <http://www.w3.org/2006/time#>\n" + 
            " PREFIX  ticket:       <http://purl.org/tio/ns.owl#>\n" + 
            " PREFIX  atmonto:      <https://data.nasa.gov/ontologies/atmontoCore/#>\n" + 
        
            " PREFIX  uco:          <https://raw.githubusercontent.com/Ebiquity/Unified-Cybersecurity-Ontology/master/uco2.ttl#>\n" + 
            "INSERT DATA {\n" +
            "  asiio:"+ eventId +"  a asiio:extEvent .\n" +
            "  asiio:"+ eventId +"  time:hasBeginning \""+ eventId + "_st\" .\n" +            
            "  asiio:"+ eventId +"  asiio:extHasCriticality asiio:" + extCriticality + " .\n" +
            "  asiio:"+ eventId +"  asiio:extType \"" + type + "\" .\n" +
            "  asiio:"+ eventId +"  asiio:extDescription \"" + description + "\" .\n";
        
        //create asset if necessary
        if(!assets.contains(source_host_name))
        {
            assets.add(source_host_name);
            
            insert += 
            "  asiio:"+ assetID +"  a asiio:extAsset .\n" +
            "  asiio:"+ assetID +"  asiio:extDescription \""+ sensor +" - "+ source_host_name +"\" .\n" ;
        }
         
        insert +=
              "  asiio:"+ eventId +"  time:extAffests asiio:"+ assetID + " .\n";
            
        String start_time_proc = start_time.replace(".", "").replace("-", "");
        
        //create time entity if necessary
        if(!moments.contains(start_time))
        {
            moments.add(start_time);

            insert += 
            "  asiio:"+ start_time_proc +"_et  a time:Instant .\n" +
            "  asiio:"+ start_time_proc +"_et  rdfs:label \""+ start_time +"\" .\n" ;
            
            insert += 
            "  asiio:"+ start_time_proc +"_st  a time:Instant .\n" +
            "  asiio:"+ start_time_proc +"_st  rdfs:label \""+ start_time +"\" .\n" ;
        }
        
        insert+=
            "  asiio:"+ eventId +"  time:hasEnd \""+ start_time_proc + "_et\" .\n" +
            "  asiio:"+ eventId +"  time:hasBeginning \""+ start_time_proc + "_st\" .\n";    
            
        insert+="}\"";
        
        
        
        //execute inserts
        UpdateRequest update = UpdateFactory.create(insert);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, "http://fusekidomain/ds/sparql");
        processor.execute();
    }
    
}
