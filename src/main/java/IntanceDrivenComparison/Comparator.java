

package IntanceDrivenComparison;

import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.ComparatorFactory;
import IntanceDrivenComparison.EvolutionaryActions.Factories.EvolutionaryActionFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import Utils.Utilities;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Priority;

/**
 *
 * @author shizamura
 */
public class Comparator 
{
    OntModel ontologyModel;
    OntModel instanceModel;
    OntModel evolvedModel;
    
    public Comparator(OntModel ontologyModel, OntModel instanceModel) 
    {
        this.ontologyModel = ontologyModel;
        this.instanceModel = instanceModel;
        this.evolvedModel  = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    }

    
    private boolean validateModels()
    {
        if(ontologyModel.isEmpty() || instanceModel.isEmpty() || !instanceModel.listIndividuals().hasNext())
        {
            Utilities.log(Priority.INFO, "Models have not been instantiated properly." );
            return false;
        }
   
        Utilities.log(Priority.INFO, "Models have been instantiated properly." );
        return true;
    }
    
    public void run() 
    {
        // end app if no models are valid
        if(!validateModels())
            return;
        
        // guidado pelas instancias
        ExtendedIterator<Individual> listIndividuals = instanceModel.listIndividuals();
        
        while(listIndividuals.hasNext())
        {
            Individual instance = listIndividuals.next();
            
            this.compareClasses(instance);
           
            StmtIterator listProperties = instance.listProperties();
            while(listProperties.hasNext())
            {
                Statement stmt  = listProperties.next();
                Triple t        = stmt.asTriple();
                this.compareProperty(t);
            }
            
        }
        
    }

    
    private void compareProperty(Triple t)
    {
        IPropertyCompare comparator = ComparatorFactory.getInstance().getPropertyComparator();
        EvolutionaryAction compare = comparator.compare(this.ontologyModel, t);
        compare.execute(ontologyModel, evolvedModel, t);
        
    }
    
    
    
    /*
    * Verifica se as classes da instânica existem. Se não, adiciona-as.
    */
    private void compareClasses(Individual instance) 
    {
        // pode haver mais que uma, como encontrar?
        // TODO
         
        OntClass ontClass = instance.getOntClass(true);
        
        // comparaçao basica com o URI
        ontClass.getURI();
        
        IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(); 
        IAddClass createAddClassAction = (IAddClass) classComparator.compare(this.ontologyModel, ontClass);
    
        createAddClassAction.execute(this.ontologyModel, this.evolvedModel, ontClass);
        
    }
}
