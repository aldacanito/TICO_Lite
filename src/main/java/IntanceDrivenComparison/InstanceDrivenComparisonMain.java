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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

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
        
        Model baseO = ModelFactory.createDefaultModel();
        Model baseI = ModelFactory.createDefaultModel();
    
        String path = "Indexes/Processed/ASIIO_semAtaque.ttl" ;
        baseO = ModelFactory.createOntologyModel();
        baseO.read(path);
        OntModel ontologyModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM, baseO );
        
        path = "Indexes/Processed/small_dataset_withOnto.ttl" ;
        baseI = ModelFactory.createOntologyModel();
        baseI.read(path);    
        OntModel instanceModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM, baseI );
   
        Comparator comparator = new Comparator(ontologyModel, instanceModel);
        comparator.run();
        
        String stats = comparator.printStats();
        System.out.println("Stats time: " + stats );
        
        Utilities.save("Indexes/NewModel/stats.txt", stats);
        
        OntologyUtils.writeModeltoFile(comparator.evolvedModel, "Indexes/NewModel/newModel.ttl");
    }
    
    
}
