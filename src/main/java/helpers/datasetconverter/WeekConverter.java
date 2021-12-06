/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers.datasetconverter;

import Utils.Utilities;
import static helpers.datasetconverter.Main.assets;
import static helpers.datasetconverter.Main.events;
import static helpers.datasetconverter.Main.logger;
import static helpers.datasetconverter.Main.model;
import static helpers.datasetconverter.Main.moments;
import static helpers.datasetconverter.Main.s_relatedAlerts;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.json.JSONObject;

/**
 *
 * @author Alda
 */
public class WeekConverter {

    public static Model model               = ModelFactory.createDefaultModel();
    public static ArrayList<String> assets  = new ArrayList<String>();    
    public static ArrayList<String> moments = new ArrayList<String>();    
    public static ArrayList<String> events  = new ArrayList<String>();    
    
    public static List<String> s_relatedAlerts = new ArrayList<String>();
    
    public static String directory_base = "Indexes/JSON/";
    public static String directory_new  = directory_base + "Recentes/";
    public static String directory_old  = directory_base + "Antigos/";
    
    public static boolean addList = true;
    
    
    static Logger logger = Logger.getLogger("errors");  
    
    static Date [] antes = { 
            new GregorianCalendar(2020, 00, 1).getTime(), 
            new GregorianCalendar(2021, 03, 8).getTime()};
    
    static Date [] scen_1_2 = { 
            new GregorianCalendar(2021, 03, 8).getTime(), 
            new GregorianCalendar(2021, 03, 15).getTime()
        };
    
    static Date [] scen_5 = { 
            new GregorianCalendar(2021, 02, 16).getTime(), 
            new GregorianCalendar(2021, 02, 17).getTime()
        };
    
    static Date [] zagreb = { 
            new GregorianCalendar(2021, 03, 22).getTime(), 
            new GregorianCalendar(2021, 03, 23).getTime()
        };
    
    static Date [] milao = { 
            new GregorianCalendar(2021, 8, 7).getTime(), 
            new GregorianCalendar(2021, 8, 9).getTime()
        };
    
    static Date [] atenas = { 
            new GregorianCalendar(2021, 03, 25).getTime(), 
            new GregorianCalendar(2021, 03, 26).getTime()
        };
    

    
    public static void main(String[] args) throws IOException 
    {
        directory_old  = directory_new;

            
        s_relatedAlerts = new ArrayList<String>();
        FileHandler fh = new FileHandler("log.log");  

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
        
       
        // tudo antes do primeiro cenário
        createDataset( antes[0], antes[1] );
       
        // entre scenarios 1 e 2
        createDataset(  scen_1_2[0], scen_1_2[1]);
        
        // scenario 5
        createDataset( scen_5[0], scen_5[1] );
        
        // simulacao zagreb
        createDataset(  zagreb[0], zagreb[1] );
        
        // simulacao atenas
        createDataset( atenas[0], atenas[1]);
        
        // simulacao milao
        createDataset( milao[0], milao[1]);
        
        
    }
    
    private static void createDataset(Date start, Date end)
    {
      
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            
        model   = ModelFactory.createDefaultModel();
        assets  = new ArrayList<String>();    
        moments = new ArrayList<String>();    
        events  = new ArrayList<String>();    

        s_relatedAlerts = new ArrayList<String>();
    
        //os meses começam a contar do zero porque quem desenvolveu isto é maluco
        String s_start = fmt.format(start.getTime());
        String s_end   = fmt.format(end.getTime());
            
        String filename = "subset_" + s_start + "_" + s_end + "";
    
        readIncidents(true, start, end);
        readRelatedAlerts(start, end);
    
        try
        {
              FileWriter out = new FileWriter( directory_base + filename + ".ttl");
              model.write( out, "TTL" );
        }
        catch(Exception e)
        {
            System.out.println("E "+e.getMessage());
        }
        
    }
    
    
    private static void readRelatedAlerts(Date start, Date end)
    {
       
        String path = directory_old + "acs_mirror_alerts.json";
        // read file
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent(path, addList));
    
        Map<String, Object> toMap = joAlerts.toMap();
        ArrayList<HashMap> aList  = (ArrayList<HashMap>) toMap.get("list");
        
        // vai iterar as instancias
        Iterator i = aList.iterator();
        while (i.hasNext()) 
        {
           HashMap aa = (HashMap) i.next();
             
           String alert_id = "", sensor ="", source_host_name ="", detect_time ="",
                  type ="", alert_description ="", severity = "";
           
           if(aa.containsKey("alert_id"))
                alert_id          = aa.get("alert_id").toString();
           
           if(!s_relatedAlerts.contains(alert_id))
               continue;
           
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
           
           if(!detect_time.isBlank())
           {
                try
                {
                    Date eventDate = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss").parse(detect_time);  
                    if(eventDate.before(start) || eventDate.after(end))
                        return;
               }
               catch(Exception e)
               {
                   System.out.println("Could not parse datetime " + detect_time +". Reason: " + e.getMessage());
                   return;
               }
           }
           if(!alert_id.isBlank() && !sensor.isBlank() && !source_host_name.isBlank())
               createEvent(alert_id, sensor, source_host_name, detect_time, type, alert_description, severity, start, end);
                     
        }    
    }
    
    
    private static void readIncidents(boolean attacksOnly, Date start, Date end)
    {
         // read file
        String path = directory_old + "acs_mirror_real_incidents.json";
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent(path, addList));

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
            
            s_relatedAlerts.addAll(relatedAlerts);
            
            //id = escape(id, true);
            external_identifier = escape(external_identifier, true);
            description = escape(description);
            kind = escape(kind);
            type = escape(type, true);
            severity = escape(severity);
            status = escape(status, true);
            
            if(type.equalsIgnoreCase("incident") && !id.isBlank() && !timestamp.isBlank())
                createRealIncident(attacksOnly, id, external_identifier, description, kind, type, severity, status, timestamp, relatedAlerts, start, end);
            //else if(!external_identifier.isBlank())
            //        createEventfromIncident(id, external_identifier, description, kind, type, severity, status, timestamp);
            
        }
    }
         
    private static void readAlerts(Date start, Date end)
    {
        // read file
        String path = directory_old + "acs_mirror_alerts.json";
        JSONObject joAlerts = new JSONObject(Utilities.readTextFileContent(path, addList));
    
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
               createEvent(alert_id, sensor, source_host_name, detect_time, type, alert_description, severity, start, end);
                     
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
                                    String start_time, String type, String description, String severity,
                                    Date start, Date end)
    {
        
        Date theTime = null;
        
        try
        {
            if(!start_time.isBlank())
            {
                theTime = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss").parse(start_time);
            
                if(theTime.after(end) || theTime.before(start))
                    return;
            }
        }
        catch(Exception e)
        {
            System.out.println("Error converting timestamp: " + e.getMessage());
        }
        
        
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
            //"  asiio:"+ start_time_proc +"_et  a time:Instant .\n" +
            //"  asiio:"+ start_time_proc +"_et  rdfs:label \""+ date +"\" .\n" ;
            
            insert += 
            "  asiio:"+ start_time_proc +"_st  a time:Instant .\n" +
            "  asiio:"+ start_time_proc +"_st  rdfs:label \""+ date +"\" .\n" ;
        }
        
        insert+=
            //"  asiio:"+ id +"  time:hasEnd asiio:"+ start_time_proc + "_et .\n" +
            "  asiio:"+ id +"  time:hasBeginning asiio:"+ start_time_proc + "_st .\n";    
            
        return insert;
    }
    
    private static void createRealIncident(boolean attacksOnly, String id, String external_identifier, 
            String description, String kind, String type, String severity, String status, 
            String timestamp, ArrayList<String> relatedAlerts,
            Date start, Date end) 
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

        Date eventDate = null;
        try
        {
            eventDate = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:mm:ss").parse(timestamp);  

            if(eventDate.before(start) || eventDate.after(end))
                return;
        
        } catch(Exception e) 
        {
            System.out.println("Could not parse date.");
        }    

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String eventDateSTR  = "";
        if(eventDate!=null)
        {
            eventDateSTR  = fmt.format(eventDate);
            String extras = adicionarExtras(eventDate, id);

            insert += extras;
        }   
        
        //ver o tipo de ataque conforme a data 
        try
        {
            
            // kind é sempre INTRUSION mas nao devia ser!!!!!

            //os meses começam a contar do zero porque quem desenvolveu isto é maluco
            String zagreb = fmt.format(new GregorianCalendar(2021, 03, 23).getTime());
            String atenas = fmt.format(new GregorianCalendar(2021, 03, 26).getTime());
            String milao1 = fmt.format(milao[0].getTime());
            String milao2 = fmt.format(milao[1].getTime());
            
            String scenario1_1 = fmt.format(new GregorianCalendar(2021, 03, 15).getTime());
            String scenario1_2 = fmt.format(new GregorianCalendar(2021, 03, 8).getTime());
            
            String scenario2_1  = fmt.format(new GregorianCalendar(2021, 03, 13).getTime());
            String scenario2_2 = fmt.format(new GregorianCalendar(2021, 03, 8).getTime());
            
            String scenario5    = fmt.format(new GregorianCalendar(2021, 02, 17).getTime());
           
            String trainee2     = fmt.format(new GregorianCalendar(2021, 01, 25).getTime());
            
            /*
            if(!eventDateSTR.equals(zagreb) & !eventDateSTR.equals(atenas) & !eventDateSTR.equals(milao1) & !eventDateSTR.equals(milao2)
                    & !eventDateSTR.equals(scenario1_1) & !eventDateSTR.equals(scenario1_2) & !eventDateSTR.equals(scenario2_1)
                    & !eventDateSTR.equals(scenario2_2) & !eventDateSTR.equals(scenario5) & !eventDateSTR.equals(trainee2))
                 
                return;
            */
            
          
            
            
            //String attackType = "extAttack";
            
            // acrescentar ataque à lista de eventos associados ao incidente caso necessario
           
            String createAttackType =
                    "  asiio:"+ id +"  asiio:extConsistsOf asiio:" + id + "_attack .\n" +
                    "  asiio:"+ id +"_attack  a asiio:extAttack .\n" +
                    "  asiio:"+ id +"_attack  asiio:extHas asiio:"+ id +"_attackType .\n" +
                    
                    " asiio:"+ id +"_attackType a asiio:extAttackType .\n" ;
            
            // type é sempre incident então para já é redundante
            /* type = type.replace("\\", "");
            
            if(!type.isBlank())
                createAttackType += "  asiio:"+ id +"_attackType a asiio:ext"+type+" .\n" ; */
            
            
            // vou fazer classificaçao multipla até prova em contrário
           
            if(eventDateSTR.equals(zagreb)) //evento zagreb
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extZagreb . asiio:extZagreb rdfs:subClassOf asiio:extAttackType .\n";
            
            if(eventDateSTR.equals(atenas)) //eventos Atenas
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extAtenas . asiio:extAtenas rdfs:subClassOf asiio:extAttackType .\n";
            
            if(eventDateSTR.equals(milao1) | eventDateSTR.equals(milao2) ) //eventoa Milao
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extMilao . asiio:extMilao rdfs:subClassOf asiio:extAttackType .\n";
            
            if(eventDateSTR.equals(scenario1_1) | eventDateSTR.equals(scenario1_2)) //evento scenario 1
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extScenario1 . asiio:extScenario1 rdfs:subClassOf asiio:extAttackType .\n";
            
            if(eventDateSTR.equals(scenario2_1) | eventDateSTR.equals(scenario2_2)) //evento scenario 2
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extScenario2 . asiio:extScenario2 rdfs:subClassOf asiio:extAttackType .\n";
            
            if(eventDateSTR.equals(scenario5)) //evento scenario 5
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extScenario5 . asiio:extScenario5 rdfs:subClassOf asiio:extAttackType .\n";
            
             if(eventDateSTR.equals(trainee2)) //evento trainee?
                insert += createAttackType + "  asiio:"+ id +"_attackType  a asiio:extTrainee . asiio:extTrainee rdfs:subClassOf asiio:extAttackType .\n";
            
            
            insert+=getStartEndDateInserts(id+"_attack", timestamp);
            
            //TODO verificar se durante o dia conseguimos identificar classes extra de ataques...
            // TODO trocar o type por outro tipo de ataque ou subclasse de ataque
           // insert += "  asiio:"+ id +"_attack  a asiio:"+ attackType +" .\n";
            
           
            
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
    
    
    private static String escape(String str)
    {
        return escape(str, false);
    }
    
    private static String escape(String str, boolean space)
    {
        str = str.replace("\\", "").replace("\"", "'");
       
        str = str.replace(".", "_").replace("(", "").replace(")", "")
                .replace("{", "[").replace("}", "]").replace("\"", "");
        
        if(space)
            str = str.replace(" ", "_");
    
        return str;
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

    private static String adicionarExtras(Date eventDate, String eventId) 
    {
        String ret = "";

         // versao inicial, sem nada
        if(eventDate.after(antes[0]) && eventDate.before(antes[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:week '0' .\n";  
            ret+= " asiio:"+eventId +" asiio:weekZero \"yes\" .\n";
        }
          
        if(eventDate.after(scen_5[0]) && eventDate.before(scen_5[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:generatedIn \"Cenario 5\" .\n";
            ret+=  "  asiio:"+ eventId +"  asiio:week '1' .\n";  
            
            ret+= " asiio:"+eventId +" asiio:weekZero \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekOne \"yes\" .\n";
        }
          
        if(eventDate.after(scen_1_2[0]) && eventDate.before(scen_1_2[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:generatedIn \"Cenarios 1 ou 2\" .\n";
            ret+=  "  asiio:"+ eventId +"  asiio:week '2' .\n";
            
            ret+= " asiio:"+eventId +" asiio:weekZero \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekOne \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekTwo \"yes\" .\n";
            
        }
        
        if(eventDate.after(zagreb[0]) && eventDate.before(zagreb[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:generatedIn \"Zagreb\" .\n";
            ret+=  "  asiio:"+ eventId +"  asiio:week '3' .\n";  
            
            ret+= " asiio:"+eventId +" asiio:weekZero \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekOne \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekTwo \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekThree \"yes\" .\n";
        }

        if(eventDate.after(atenas[0]) && eventDate.before(atenas[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:generatedIn \"Atenas\" .\n";
            ret+=  "  asiio:"+ eventId +"  asiio:week '4' .\n"; 
            
            ret+= " asiio:"+eventId +" asiio:weekZero \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekOne \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekTwo \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekThree \"no\" .\n";
            ret+= " asiio:"+eventId +" asiio:weekFour \"yes\" .\n";
        }

        if(eventDate.after(milao[0]) && eventDate.before(milao[1]))
        {
            ret+=  "  asiio:"+ eventId +"  asiio:generatedIn \"Milao\" .\n";
            ret+=  "  asiio:"+ eventId +"  asiio:week '5' .\n";  
        }
      

    
    
        return ret;
    }
    
    
}
