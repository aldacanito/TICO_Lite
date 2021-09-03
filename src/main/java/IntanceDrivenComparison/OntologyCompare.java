/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison;

import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddClass;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.AddObjectProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author shizamura
 */
public class OntologyCompare {
    
    private static final String ASIIO_NS = "http://www.gecad.isep.ipp.pt/ASIIO#";
    private OntModel ontologyModel;
    private OntModel instanceModel;
    
    private HashMap toEvolve;
    
    
    public OntologyCompare(String ontologyFile, String instancesFile)
    {
        toEvolve = new HashMap(); // provavelmente vou ter de definir os metodos que definem ACÇOES evolutivas
        
        
        Model baseO = ModelFactory.createDefaultModel();
        Model baseI = ModelFactory.createDefaultModel();
    
        String path = "Indexes/Processed/ASIIO.ttl" ;
        baseO = ModelFactory.createOntologyModel();
        baseO.read(path);
        ontologyModel = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, baseO );
        
        path = "Indexes/Processed/17072021.ttl" ;
        baseI = ModelFactory.createOntologyModel();
        baseI.read(path);    
        instanceModel = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM_RDFS_INF, baseI );
    
    }
    
    public void compareInstances()
    {
        List<OntClass> originalClasses = ontologyModel.listClasses().toList();
        
        //get all classes from the new ontology
        for ( Individual ind : instanceModel.listIndividuals().toList() ) 
        {
            System.out.println( ind );
            
            //check if the classes exist in the original ontology
            OntClass klass = ind.getOntClass();
            
            if(!originalClasses.contains(klass))
                evolveClass(ind);
            else
                compareWithPreviousDefinition(klass);
        }   
    }
   
    public HashMap getMetricsForClass(String theClass)
    {
        HashMap theMetrics = new HashMap();
    
        
        
        return theMetrics;
    }

    private void evolveClass(Individual newInd) 
    {
        //Como defino a acçao
        
        //acçao pode ser um composito de varias acçoes / lista
        
        // cada acçao deve ter a sua propria classe que a execute, mesmo que para ja a execuçao seja dizer "faz isto"
        
        // ver se a classe nao existe -> add class
        // se as propriedades nao correspondem a classe -> addOP ou addDP
        
        OntClass klass = newInd.getOntClass();
        
        
        toEvolve.put(klass, new AddClass()); // se está aqui é porque a classe nao existe
        
        Resource individual = (Resource) newInd.asResource();
        
        StmtIterator propertiesIt = individual.listProperties();
        
        while ( propertiesIt.hasNext()) 
        {
            Statement prop = (Statement) propertiesIt.next();    
            Triple propT = prop.asTriple();
            
            Node predicate = propT.getPredicate();
            Node object = propT.getObject();
            
            if(!ontologyModel.getProperty(predicate.getURI()).equals(predicate.getURI()))
                toEvolve.put(prop, new AddObjectProperty());
        
        }
    
    }

    private void compareWithPreviousDefinition(OntClass klass) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
