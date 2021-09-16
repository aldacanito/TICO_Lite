

package IntanceDrivenComparison;

import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.ComparatorFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class Comparator 
{
    OntModel ontologyModel;
    OntModel instanceModel;
    OntModel evolvedModel;
    EvolutionaryActionExecuter executer;
    
    public Comparator(OntModel ontologyModel, OntModel instanceModel) 
    {
        this.ontologyModel = ontologyModel;
        this.instanceModel = instanceModel;
        this.evolvedModel  = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.executer      = new EvolutionaryActionExecuter();
    }

    
    private boolean validateModels()
    {
        if(ontologyModel.isEmpty() || instanceModel.isEmpty() || !instanceModel.listIndividuals().hasNext())
        {
            Utilities.logInfo( "Models have not been instantiated properly." );
            return false;
        }
   
        Utilities.logInfo( "Models have been instantiated properly." );
        return true;
    }
    
    public void run() 
    {
        // end app if no models are valid
        if(!validateModels())
            return;
        
        // guidado pelas instancias
        ExtendedIterator<Individual> listIndividuals = instanceModel.listIndividuals();
        
        
        Utilities.logInfo("Listing all individuals... ");
        
        while(listIndividuals.hasNext())
        {
            Individual instance = listIndividuals.next();
           
            Utilities.logInfo("Current Individual: " + instance.getURI());
     
            this.compareClasses(instance);
     
            Utilities.logInfo("Iterating through the properties of Individual " + instance.getURI());
        
            StmtIterator listProperties = instance.listProperties();
            while(listProperties.hasNext())
            {
                Statement stmt  = listProperties.next();
                Triple t        = stmt.asTriple();
                
                Utilities.logInfo(OntologyUtils.printTriple(t));
              
                
                this.compareProperty(t);
            }
            
        }
        
        executer.execute(ontologyModel, evolvedModel);
        
    }

    
    private void compareProperty(Triple t)
    {
        IPropertyCompare comparator = ComparatorFactory.getInstance().getPropertyComparator(t.getPredicate(), this.ontologyModel);
        
        if(comparator != null)
        {
            EvolutionaryAction compare = comparator.compare(this.ontologyModel, t);
            this.executer.add(compare);
        }      
    }
    
    
    /*
    * Verifica se as classes da instânica existem. Se não, adiciona-as.
    */
    private void compareClasses(Individual instance) 
    {
        Utilities.logInfo("COMPARE CLASSES for instance " 
                + instance.getURI()
                + ".\n Iterating through its classes...");
        // procura todas as classes a que a instancia possa pertencer
        
        ExtendedIterator<OntClass> listOntClasses = instance.listOntClasses(true);
        for(OntClass cls : listOntClasses.toList())
        {
            IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(); 
            IAddClass createAddClassAction = (IAddClass) classComparator.compare(this.ontologyModel, cls);
            
            if(createAddClassAction!=null)
                this.executer.add(createAddClassAction);  
            
        }
    }

    public String printStats() 
    {
        return this.executer.toString();
    }
}
