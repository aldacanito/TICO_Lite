/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.datasetconverter;


import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
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
    public static ArrayList<String> events = new ArrayList<String>();    
    
    
    static Logger logger = Logger.getLogger("errors");  
        
    public static void main(String[] args) throws IOException 
    {
        FileHandler fh = new FileHandler("log.log");  

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  

        readAlerts();
        readIncidents();

       //escrever no ficheiro no fim
      // model.write( System.out, "TTL" );

      try
      {
       FileWriter out = new FileWriter( "Indexes/processed.ttl" );
       
       model.write( out, "TTL" );
      }catch(Exception e)
      {
          System.out.println("E "+e.getMessage());
      }
    }
    
    
    private static void readIncidents()
    {
         // read file
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent("Indexes/acs_mirror_real_incidents.json"));
    
        Map<String, Object> toMap = joAlerts.toMap();
        ArrayList<HashMap> aList = (ArrayList<HashMap>) toMap.get("list");
        
        // vai iterar as instancias
        Iterator i = aList.iterator();
        while (i.hasNext()) 
        {
            HashMap instance = (HashMap) i.next();
            ArrayList<String> relatedAlerts = new ArrayList<String>();
            
            String id = "", external_identifier = "", description ="", 
                   kind ="", type="", severity="", status="", timestamp="";
            
            if(instance.containsKey("identifier")) id   = instance.get("identifier").toString();
            if(instance.containsKey("external_identifier")) external_identifier   = instance.get("external_identifier").toString();
            if(instance.containsKey("description")) description   = instance.get("description").toString();
            if(instance.containsKey("kind")) kind   = instance.get("kind").toString();
            if(instance.containsKey("type")) type   = instance.get("type").toString();
            if(instance.containsKey("severity")) severity   = instance.get("severity").toString();
            if(instance.containsKey("status")) status   = instance.get("status").toString();
            if(instance.containsKey("timestamp")) timestamp  = instance.get("timestamp").toString();
            if(instance.containsKey("related_alerts"))  relatedAlerts   = (ArrayList<String>) instance.get("related_alerts");            
            
            if(type.equalsIgnoreCase("incident") && !id.isBlank() && !timestamp.isBlank())
                createRealIncident(id, external_identifier, description, kind, type, severity, status, timestamp, relatedAlerts);
            else if(!external_identifier.isBlank())
                    createEventfromIncident(id, external_identifier, description, kind, type, severity, status, timestamp);
            
        }
    }
         
    private static void readAlerts()
    {
        // read file
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent("Indexes/acs_mirror_alerts.json"));
    
        Map<String, Object> toMap = joAlerts.toMap();
        ArrayList<HashMap> aList = (ArrayList<HashMap>) toMap.get("list");
        
        // vai iterar as instancias
        Iterator i = aList.iterator();
        while (i.hasNext()) 
        {
           HashMap aa = (HashMap) i.next();
             
           String alert_id = "", sensor ="", source_host_name ="", detect_time ="",
                  type ="", alert_description ="", severity = "";
           
           if(aa.containsKey("alert_id"))
                alert_id          = aa.get("alert_id").toString();
           if(aa.containsKey("sensor"))
                sensor            = aa.get("sensor").toString();
           if(aa.containsKey("source_host_name"))
                source_host_name  = aa.get("source_host_name").toString();
           if(aa.containsKey("detect_time"))
                detect_time       = aa.get("detect_time").toString();
           if(aa.containsKey("Type"))
                type              = aa.get("Type").toString();
           if(aa.containsKey("alert_title") && aa.containsKey("alert_description") )
                alert_description = aa.get("alert_title").toString() + " - " + aa.get("alert_description").toString();              
           if(aa.containsKey("severity"))
                severity          = aa.get("severity").toString();
           
           if(!alert_id.isBlank() && !sensor.isBlank() && !source_host_name.isBlank())
               createEvent(alert_id, sensor, source_host_name, detect_time, type, alert_description, severity);
                     
        }

    }
    
    private static String getCriticality(String severity)
    {
        switch(severity)
        {
            case "low":     return "extNormalCriticality";
            case "medium":  return "extEscalationCriticality";      
            case "high":    return "extEmergencyCriticality";
        }
    
        return severity;
    }
    
    private static void createEvent(String eventId, String sensor, String source_host_name, 
                                    String start_time, String type, String description, String severity)
    {
        
        // até eu arranjar uma maneira em condiçoes de fazer escape a esta gente toda é à força bruta mesmo
        String assetID = sensor.replace(" ", "_").replace(".", "_").replace("(", "").replace(")", "")
                .replace("{", "").replace("}", "").replace("\"", "")
                + "_" + source_host_name.replace(" ", "_").replace(".", "_").replace("(", "").replace(")", "")
                .replace("{", "").replace("}", "").replace("\"", "");
        
        description = description.replace("\"", "\'");
        source_host_name = source_host_name.replace("\"", "\'");
        
        String insert =
            " PREFIX  asiio:        <http://www.gecad.isep.ipp.pt/ASIIO#>\n" +    
            " PREFIX  time:         <http://www.w3.org/2006/time#>\n" + 
            " PREFIX  ticket:       <http://purl.org/tio/ns.owl#>\n" + 
            " PREFIX  atmonto:      <https://data.nasa.gov/ontologies/atmontoCore/#>\n" + 
            " PREFIX  rdf:          <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
            " PREFIX  rdfs:         <http://www.w3.org/2000/01/rdf-schema#>\n" + 
            " PREFIX  uco:          <https://raw.githubusercontent.com/Ebiquity/Unified-Cybersecurity-Ontology/master/uco2.ttl#>\n" + 
            
            "INSERT DATA {\n" +
            "  asiio:"+ eventId +"  a asiio:extEvent .\n" +
            "  asiio:"+ eventId +"_alert  a asiio:extAlert .\n" +
            "  asiio:"+ eventId +"_alert  asiio:extIsTriggeredBy asiio:" + eventId + " .\n";
        
        if(!severity.isBlank())
            insert += "  asiio:"+ eventId +"_alert  asiio:extHasCriticality asiio:" + getCriticality(severity) + " .\n";
            
        insert+=
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
              "  asiio:"+ eventId +"  asiio:extAffests asiio:"+ assetID + " .\n";
            
        if(!start_time.isBlank())
            insert+=getStartEndDateInserts(eventId, start_time);
        
        insert+="}";
        
        
        try
        {
            UpdateAction.parseExecute( insert, model );
        }
        catch(Exception e)
        {
            logger.info("===================");
            logger.info("ERROR: CREATE EVENT");
            logger.info("Error creating instance for id " + eventId);
            logger.info("Reason " + e.getLocalizedMessage());
            logger.info("Generated Query " + insert);
        }
        //execute inserts no fuseki
        //UpdateRequest update = UpdateFactory.create(insert);
        //UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, "http://fusekidomain/ds/sparql");
        //processor.execute();
    }

    private static String getStartEndDateInserts(String id, String date)
    {
        String insert           = "";
        String start_time_proc  = date.replace(".", "").replace("-", "").replace("+", "").replace(":", "");
        
        //create time entity if necessary
        if(!moments.contains(date))
        {
            moments.add(date);

            insert += 
            "  asiio:"+ start_time_proc +"_et  a time:Instant .\n" +
            "  asiio:"+ start_time_proc +"_et  rdfs:label \""+ date +"\" .\n" ;
            
            insert += 
            "  asiio:"+ start_time_proc +"_st  a time:Instant .\n" +
            "  asiio:"+ start_time_proc +"_st  rdfs:label \""+ date +"\" .\n" ;
        }
        
        insert+=
            "  asiio:"+ id +"  time:hasEnd asiio:"+ start_time_proc + "_et .\n" +
            "  asiio:"+ id +"  time:hasBeginning asiio:"+ start_time_proc + "_st .\n";    
            
        return insert;
    }
    
    private static void createRealIncident(String id, String external_identifier, String description, String kind, String type, String severity, String status, String timestamp, ArrayList<String> relatedAlerts) 
    {
        
        String insert =
            " PREFIX  asiio:        <http://www.gecad.isep.ipp.pt/ASIIO#>\n" +    
            " PREFIX  time:         <http://www.w3.org/2006/time#>\n" + 
            " PREFIX  ticket:       <http://purl.org/tio/ns.owl#>\n" + 
            " PREFIX  atmonto:      <https://data.nasa.gov/ontologies/atmontoCore/#>\n" + 
            " PREFIX  rdf:          <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
            " PREFIX  rdfs:         <http://www.w3.org/2000/01/rdf-schema#>\n" + 
            " PREFIX  uco:          <https://raw.githubusercontent.com/Ebiquity/Unified-Cybersecurity-Ontology/master/uco2.ttl#>\n"; 
            
        insert+=
            "INSERT DATA {\n" +
            "  asiio:"+ id +"  a asiio:extIncident .\n" ;
        
        if(!severity.isBlank())
            insert += "  asiio:"+ id +"  asiio:extHasCriticality asiio:" + getCriticality(severity) + " .\n";
            
        insert += "  asiio:"+ id +"  asiio:extDescription \"" + description + "\" .\n";
                    
        insert+=getStartEndDateInserts(id, timestamp);
        
        //ver o tipo de ataque conforme a data 
        try
        {
            
            // kind é sempre INTRUSION mas nao devia ser!!!!!
            Date date = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss").parse(timestamp);  
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            
            //confirmar se é 5 ou 4 o mes
            Date zagreb = new GregorianCalendar(2021, 03, 23).getTime();
            Date atenas = new GregorianCalendar(2021, 03, 26).getTime();
            Date milao1 = new GregorianCalendar(2021, 03, 27).getTime();
            Date milao2 = new GregorianCalendar(2021, 03, 28).getTime();
            
            Date scenario1 = new GregorianCalendar(2021, 03, 15).getTime();
            Date scenario2 = new GregorianCalendar(2021, 03, 13).getTime();
            Date scenario12 = new GregorianCalendar(2021, 03, 8).getTime();
            
            Date scenario5 = new GregorianCalendar(2021, 03, 17).getTime();
            Date trainee1 = new GregorianCalendar(2021, 03, 23).getTime();
            Date trainee2 = new GregorianCalendar(2021, 03, 25).getTime();
            
            String attackType = "extAttack";
            
            // vou fazer classificaçao multipla até prova em contrário
            String date1 = fmt.format(date);
            String date2 = fmt.format(scenario1);
            if(date1.equals(date2))
            {
                insert += "  asiio:"+ id +"_attack  a asiio:extAtenas .\n";
                insert += "  asiio:"+ id +"_attack  a asiio:extScenario1 .\n";
                
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extAtenas .\n" ;
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extScenario1 .\n" ;
            }
            
            if(fmt.format(date).equals(fmt.format(scenario2)))
            {    
                insert += "  asiio:"+ id +"_attack  a asiio:extAtenas .\n";
                insert += "  asiio:"+ id +"_attack  a asiio:extScenario2 .\n";
                
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extScenario1 .\n" ;
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extAtenas .\n" ;
            }
            
            if(fmt.format(date).equals(fmt.format(scenario12)))
            {
                insert += "  asiio:"+ id +"_attack  a asiio:extScenario1 .\n";
                insert += "  asiio:"+ id +"_attack  a asiio:extScenario2 .\n";
                insert += "  asiio:"+ id +"_attack  a asiio:extAtenas .\n";
                
                /*
                insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extAtenas .\n" ;
                insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extScenario1 .\n" ;
                insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extScenario2 .\n" ;
                */            
            }
            
            if(fmt.format(date).equals(fmt.format(scenario5)))
            {
                insert += "  asiio:"+ id +"_attack  a asiio:extScenario5 .\n";
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extScenario5 .\n" ;
            }
            
            date2 = fmt.format(trainee1);
            String date3 = fmt.format(trainee2);
            if(date1.equals(date2) || date1.equals(date3))
            {
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extTrainee .\n" ;
                insert += "  asiio:"+ id +"_attack  a asiio:extTrainee .\n";
            }
            
            
            date2 = fmt.format(zagreb);
            if(date1.equals(date2))
            {
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extZagreb .\n" ;
                insert += "  asiio:"+ id +"_attack  a asiio:extZagreb .\n";
            }   
            if(fmt.format(date).equals(fmt.format(atenas)))
            {
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extAtenas .\n" ;
                insert += "  asiio:"+ id +"_attack  a asiio:extAtenas .\n";
            }
            
            if(fmt.format(date).equals(fmt.format(milao1)) || fmt.format(date).equals(fmt.format(milao2)))
            {
                insert += "  asiio:"+ id +"_attack  a asiio:extMilao .\n";
                //insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:extMilao .\n" ;
            }
            
            insert+=getStartEndDateInserts(id+"_attack", timestamp);
            
            // acrescentar ataque à lista de eventos associados ao incidente
            String attack = id + "_attack";
            insert += "  asiio:"+ id +"  asiio:extConsistsOf asiio:" + attack + " .\n";
            
            // TODO trocar o type por outro tipo de ataque ou subclasse de ataque
            insert += "  asiio:"+ id +"_attack  a asiio:"+ attackType +" .\n";
            
            type = type.replace("\\", "");
            
            if(!type.isBlank())
                insert+= "  asiio:"+ id +"_attack  asiio:extAttackType asiio:ext"+type+" .\n" ;
            
        }
        catch(Exception e)
        {
            System.out.println("erro " +e);
        }
        
        // related events nao é preciso criar os eventos, o outro processo trata disso
        // basta associar ao Incidente
        for(String event : relatedAlerts)
        {
           event = event.replace("\\", "");
            
           if(event.isBlank())
              continue;
           
           insert += "  asiio:"+ id +"  asiio:extConsistsOf asiio:" + event + " .\n";
           insert += "  asiio:"+ event +"  a asiio:extEvent .\n";
        }
        
        insert+="}";
       
        //inserir no modelo jena
        
        try
        {
            UpdateAction.parseExecute( insert, model );
        }
        catch(Exception e)
        {
            
            logger.info("===================");
            logger.info("ERROR: CREATE INCIDENT");
            logger.info("Error creating instance for id " + id);
            logger.info("Reason " + e.getLocalizedMessage());
            logger.info("Generated Query " + insert);
        }
    }
    
    
    // TODO verificar se aqui vai ser só inserir ou fazer update??
    private static void createEventfromIncident(String id, String external_identifier, String description, String kind, String type, String severity, String status, String timestamp) 
    {
        String eventId = external_identifier;
        
        String insert =
            " PREFIX  asiio:        <http://www.gecad.isep.ipp.pt/ASIIO#>\n" +    
            " PREFIX  time:         <http://www.w3.org/2006/time#>\n" + 
            " PREFIX  ticket:       <http://purl.org/tio/ns.owl#>\n" + 
            " PREFIX  atmonto:      <https://data.nasa.gov/ontologies/atmontoCore/#>\n" + 
            " PREFIX  rdf:          <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
            " PREFIX  rdfs:         <http://www.w3.org/2000/01/rdf-schema#>\n" + 
            " PREFIX  uco:          <https://raw.githubusercontent.com/Ebiquity/Unified-Cybersecurity-Ontology/master/uco2.ttl#>\n" ; 
            
        insert+= "INSERT DATA {\n" ;
        
        if(!events.contains(external_identifier))
        {
            insert+=
            "  asiio:"+ eventId +"  a asiio:extEvent .\n" +
                
            "  asiio:"+ eventId +"_alert  a asiio:extAlert .\n" +
            "  asiio:"+ eventId +"_alert  asiio:extIsTriggeredBy asiio:" + eventId + " .\n";
           
            if(!severity.isBlank())
                insert+= "  asiio:"+ eventId +"_alert  asiio:extHasCriticality asiio:" + getCriticality(severity) + " .\n";
            if(!type.isBlank())
                insert+= "  asiio:"+ eventId +"  asiio:extType \"" + type + "\" .\n";
            if(!description.isBlank())
                insert+= "  asiio:"+ eventId +"  asiio:extDescription \"" + description + "\" .\n" ;
            if(!status.isBlank())
                insert+="  asiio:"+ eventId +"  asiio:extStatus \"" + status + "\" .\n";
    
            if(!timestamp.isBlank())
                insert+=getStartEndDateInserts(eventId, timestamp);
        }
        
        insert+="}";
        
        
        try
        {
            UpdateAction.parseExecute( insert, model );
        }
        catch(Exception e)
        {
            logger.info("===================");
            logger.info("ERROR: CREATE EVENT FROM INCIDENT");
            logger.info("Error creating instance for id " + id);
            logger.info("Reason " + e.getLocalizedMessage());
            logger.info("Generated Query " + insert);
        }
    
    }
    
}
