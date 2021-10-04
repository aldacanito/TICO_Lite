

package IntanceDrivenComparison;

import IntanceDrivenComparison.EvolutionaryActions.Implementations.EvolutionaryActionComposite;
import IntanceDrivenComparison.Metrics.ClassPropertyMetrics;
import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.ComparatorFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.util.ArrayList;
import java.util.List;
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
    EvolutionaryActionComposite executer;
    List<ClassPropertyMetrics> clsPropMetrics ;
    
    public Comparator(OntModel ontologyModel, OntModel instanceModel) 
    {
        this.ontologyModel = ontologyModel;
        this.instanceModel = instanceModel;
        this.evolvedModel  = ontologyModel; //ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        this.executer      = new EvolutionaryActionComposite();
    
        clsPropMetrics = new ArrayList<ClassPropertyMetrics>();
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
        // só funciona para as instâncias que tenham CLASSES associadas
        // reasoner desligado
        ExtendedIterator<Individual> listIndividuals = instanceModel.listIndividuals();
        
        Utilities.logInfo("\n\n==========================================\nListing all individuals...\n\n ");
        
        while(listIndividuals.hasNext())
        {
            Individual instance = listIndividuals.next();
           
            Utilities.logInfo("Current Individual: " + instance.getURI());
     
            this.compareClasses(instance);
            this.compareProperties(instance);     
            this.compareShapes(instance);
        }
        
        executer.execute(ontologyModel, evolvedModel);
    }
    
    private void compareShapes(Individual instance)
    {
        Utilities.logInfo("\n\n%%%%%%%%%%%%%%%%%\nAnalysing the shape of Individual " 
                + instance.getURI() + ".");

        IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(instance, ontologyModel);
       
        if(classComparator != null)
        {
            EvolutionaryAction compare = classComparator.compare();
            this.executer.add(compare);
        }    
    }
    
    
    private void compareProperties(Individual instance)
    {
        List<Statement> listProperties = instance.listProperties().toList();
               
        Utilities.logInfo("\n\n%%%%%%%%%%%%%%%%%\nIterating through the properties of Individual " 
                + instance.getURI() + ". Currently with " + listProperties.size() + " properties.");

        for(Statement stmt : listProperties)
        {
            Utilities.logInfo(OntologyUtils.printStatement(stmt));
            this.compareProperty(stmt);
        }
        
        Utilities.logInfo("\n\nFinished iterating through Individual " 
                + instance.getURI() + "'s properties\n%%%%%%%%%%%%%%%%%\n\n");
    }
    
   
    private void compareProperty(Statement t)
    {
        boolean ignore = Utilities.isInIgnoreList(t.getPredicate().getURI());
        
        if(ignore) return;
        IPropertyCompare comparator = ComparatorFactory.getInstance().getPropertyComparator(t, this.ontologyModel);
        
        if(comparator != null)
        {
            EvolutionaryAction compare = comparator.compare();
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
            boolean ignore = Utilities.isInIgnoreList(cls.getURI());
            
            if(ignore) continue;
            
            IClassCompare classComparator  = ComparatorFactory.getInstance().getClassComparator(cls, this.ontologyModel); 
            
            if(classComparator!=null)
            {
                IAddClass createAddClassAction = (IAddClass) classComparator.compare();
                if(createAddClassAction!=null)
                    this.executer.add(createAddClassAction);  
            }
        }
    }

    public String printStats() 
    {
        return this.executer.toString();
    }
}
