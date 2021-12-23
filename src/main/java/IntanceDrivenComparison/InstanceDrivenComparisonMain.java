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
        Configs configs = new Configs();
        //startRounds();
        //compareAtaque_semAtaque();
        startTest();
        //startUseWeeks();
    }
    
    public static void startRounds()
    {
        String dir = "Indexes/Processed/with_instances/";
        // round one
        String onto_path      = dir + "base_asiio.ttl" ;
        String instance_path  = dir + "24-02-2020_inst.ttl" ;
        String print_path = dir + "round1.ttl";
        
       runComparator(onto_path, instance_path, print_path, 1);
        
//        // round two
        onto_path      = print_path;
        instance_path  = dir+ "28-07-2020_inst.ttl" ;
        print_path     =  dir + "round2.ttl";
        
        runComparator(onto_path, instance_path, print_path, 2);

////        // round three
        onto_path      = print_path;
        instance_path  = dir + "17-07-2021_inst.ttl" ;
        print_path     = dir + "round3.ttl";

        runComparator(onto_path, instance_path, print_path, 3);
   
    }
    
    public static void compareAtaque_semAtaque()
    {
        String dir = "Indexes/Processed/";
        String onto_path     = dir + "ASIIO_semAtaque.ttl" ;
        String instance_path = dir + "small_dataset_withOnto.ttl" ;
        String print_path    = dir + "result.ttl";
        
        runComparator(onto_path, instance_path, print_path, 0);
    }
    
    
    public static void startTest()
    {
        String dir            = "Indexes/TestOnto/";
        String onto_path      = dir + "shizaTest_base.ttl" ;
        String instance_path  = dir + "shizaTest_newInstance.ttl" ;
        String print_path = dir + "evolvedModel.ttl";
        
        runComparator(onto_path, instance_path, print_path, 0);
    }
    
        
    public static void runComparator(String onto_path, String instance_path,
                                     String print_path, int round)
    {
        OntModel baseO = OntologyUtils.readModel(onto_path);
        OntModel baseI = OntologyUtils.readModel(instance_path);

        Comparator comparator = new Comparator(baseO, baseI);
        
        comparator.run();
        
        String stats = comparator.printStats();
        System.out.println("Stats time: " + stats );
        
        Utilities.save("Indexes/TestOnto/stats_round"+round+".txt", stats);
        
        OntologyUtils.writeModeltoFile(comparator.evolvedModel, print_path);
    }
    
    
    public static void startUseWeeks()
    {
        String dir            = "Indexes/Datasets_Ataques/";
        String onto_path      = dir+ "assercoes_min.ttl" ;
        String instance_path  = dir+ "subset_20210408_20210415.ttl" ;
        String print_path     = dir+ "evolvedModel.ttl";
        int round             = 1;
        
        runComparator(onto_path, instance_path, print_path, round);
     
        // ROUND TWO
        onto_path      = print_path ;
        instance_path  = dir+ "subset_20210422_20210423.ttl" ;
        print_path     = dir+ "evolvedModel.ttl";
        round = 2;
        
        runComparator(onto_path, instance_path, print_path, round);
        
        // completar com mais semanas
        
    }
    
}
