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
        
//        String onto_path     = "Indexes/TestOnto/shizaTest_base.ttl" ;
//        String instance_path = "Indexes/TestOnto/shizaTest_newInstance.ttl" ;
        
        String onto_path     = "Indexes/Processed/ASIIO_semAtaque.ttl" ;
        String instance_path = "Indexes/Processed/small_dataset_withOnto.ttl" ;
        
        
        OntModel baseO = ModelFactory.createOntologyModel();
        OntModel baseI = ModelFactory.createOntologyModel();
          
        baseO.read(onto_path);
        baseI.read(instance_path);    
   
        Comparator comparator = new Comparator(baseO, baseI);
        
        comparator.run();
        
        String stats = comparator.printStats();
        System.out.println("Stats time: " + stats );
        
        Utilities.save("Indexes/NewModel/stats.txt", stats);
        
        OntologyUtils.writeModeltoFile(comparator.evolvedModel, "Indexes/NewModel/newModel.ttl");
     //   OntologyUtils.writeModeltoFile(ontologyModel, "Indexes/NewModel/newModel.ttl");
    }
    
    
}
