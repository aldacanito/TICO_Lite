/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison;

import Utils.Configs;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;

/**
 *
 * @author shizamura
 */
public class InstanceDrivenComparisonMain 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        start();
    }
    
    public static void start()
    {
        Configs configs = new Configs();
        
        String onto_path      = "Indexes/TestOnto/shizaTest_base.ttl" ;
        String instance_path  = "Indexes/TestOnto/shizaTest_newInstance.ttl" ;
        String printModelPath = "Indexes/NewModel/newModel.ttl";
        String roundName      = "";
        // round one
        onto_path      = "Indexes/Processed/with_instances/base_asiio.ttl" ;
        instance_path  = "Indexes/Processed/with_instances/24-02-2020_inst.ttl" ;
        printModelPath = "Indexes/Processed/with_instances/round1.ttl";
        roundName = "_round1";
        
//        // round two
//        onto_path      = "Indexes/Processed/with_instances/round1.ttl" ;
//        instance_path  = "Indexes/Processed/with_instances/28-07-2020_inst.ttl" ;
//        printModelPath = "Indexes/Processed/with_instances/round2.ttl";
//        roundName = "_round2";
////        // round three
//        onto_path      = "Indexes/Processed/with_instances/round2.ttl" ;
//        instance_path  = "Indexes/Processed/with_instances/17-07-2021_inst.ttl" ;
//        printModelPath = "Indexes/Processed/with_instances/round3.ttl";
//        roundName = "_round3";
//        
        
//        onto_path     = "Indexes/Processed/ASIIO_semAtaque.ttl" ;
//        instance_path = "Indexes/Processed/small_dataset_withOnto.ttl" ;
//        instance_path = "Indexes/Processed/ASIIO.ttl" ;
        
//        OntModel baseO = ModelFactory.createOntologyModel();
//        OntModel baseI = ModelFactory.createOntologyModel();
          
        
        OntModel baseO = OntologyUtils.readModel(onto_path);
        OntModel baseI = OntologyUtils.readModel(instance_path);
        
        //baseO.read(onto_path);
        //baseI.read(instance_path);    
   
        Comparator comparator = new Comparator(baseO, baseI);
        
        comparator.run();
        
        String stats = comparator.printStats();
        System.out.println("Stats time: " + stats );
        
        Utilities.save("Indexes/NewModel/stats"+roundName+".txt", stats);
        
        OntologyUtils.writeModeltoFile(comparator.evolvedModel, printModelPath);
     //   OntologyUtils.writeModeltoFile(ontologyModel, "Indexes/NewModel/newModel.ttl");
    }
    
    
}
