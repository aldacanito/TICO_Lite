

package IntanceDrivenComparison;

import IntanceDrivenComparison.Comparison.Implementations.Simple.ClassDiff;
import IntanceDrivenComparison.EvolutionaryActions.Implementations.EvolutionaryActionComposite;
import IntanceDrivenComparison.Metrics.ClassPropertyMetrics;
import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import IntanceDrivenComparison.EvolutionaryActions.Factories.ComparatorFactory;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.EvolutionaryAction;
import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import Utils.Utilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
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
    DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");  
        
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
     
            //this.compareClasses(instance);
            //this.compareProperties(instance);     
            this.compareShapes(instance);
        }
        
        executer.execute(ontologyModel, evolvedModel);
        
        // verificar se é preciso acrescentar validaçoes temporais em classes
        updateTemporalRestrictions(ontologyModel, evolvedModel);
    }
    
   
    private void compareShapes(Individual instance)
    {
        boolean ignore = Utilities.isInIgnoreList(instance.getURI());
        if(ignore) return;
        
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

    private void updateTemporalRestrictions(OntModel ontologyModel, OntModel evolvedModel) 
    {
        // correr todas as novas classes e ver se já existiam no ontologyModel
        // se já existia, subtituir o hasBeginning
        // acrescentar o hasEnding na versao anterior
        // renomear a classe
        // fazer sameAS
        
        List<OntClass> e_ontClasses = evolvedModel.listClasses().toList();
        LocalDateTime now      = LocalDateTime.now();  
        
        for(OntClass newCls : e_ontClasses)
        {
            String uri      = newCls.getURI();
            System.out.println("NEW CLASS URI: " + uri);
            if(uri == null) continue;
            OntClass oldCls = ontologyModel.getOntClass(uri);
            
            // classe é totalmente nova, já tem o hasBeginning do construtor
            // ou não há diferença entre as duas classes
            if(oldCls == null || ! new ClassDiff().isNewVersion(oldCls, newCls)) continue;
            
            replaceHasBeginning(newCls, now);
            addHasEnding(oldCls, now);
            
            String oldURI = oldCls.getURI();
            
            if(oldURI.contains("__"))
            {
                String[] split = oldURI.split("__"); // ignorar data que ja tivesse
                oldURI = split[0];
            }
            
            String newURI = oldURI + "__" + dtf2.format(now);
            
            ResourceUtils.renameResource(oldCls, newURI);
            OntologyUtils.copyClass(ontologyModel.getOntClass(newURI), evolvedModel);
            
            //newCls.addSameAs(evolvedModel.getOntClass(newURI));
            newCls.addEquivalentClass(evolvedModel.getOntClass(newURI));
        
        }
     }
    
    private void addHasEnding(OntClass cls, LocalDateTime enddate)
    {
        
        OntProperty ontProperty = this.evolvedModel.getOntProperty("http://www.w3.org/2006/time#hasEnding");

        if(ontProperty == null)
            ontProperty = this.evolvedModel.createObjectProperty("http://www.w3.org/2006/time#hasEnding", false);
            
        OntClass instantClass = this.evolvedModel.getOntClass("http://www.w3.org/2006/time#instant");
        if(instantClass == null)
            instantClass = this.evolvedModel.createClass("http://www.w3.org/2006/time#instant");
        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(enddate), instantClass);
    
        HasValueRestriction createHasValueRestriction = cls.getOntModel().createHasValueRestriction(null, ontProperty, date1);
           
        cls.addSuperClass(createHasValueRestriction);
    }
    
    private void replaceHasBeginning(OntClass cls, LocalDateTime startdate)
    {
        List<OntClass> superClasses = cls.listSuperClasses(true).toList();
        
        for(OntClass superClass : superClasses)
        {
            if(superClass.isRestriction())
            {
                if(superClass.asRestriction().isHasValueRestriction())
                {
                    HasValueRestriction hvr = superClass.asRestriction().asHasValueRestriction();
                    
                    if(hvr.getOnProperty().getURI().equalsIgnoreCase("http://www.w3.org/2006/time#hasBeginning"))
                    {
                        OntClass instantClass = this.evolvedModel.getOntClass("http://www.w3.org/2006/time#instant");
                        if(instantClass == null)
                            instantClass = this.evolvedModel.createClass("http://www.w3.org/2006/time#instant");
                        Individual date1 = this.evolvedModel.createIndividual(dtf2.format(startdate), instantClass);
            
                        hvr.setHasValue(date1);   
                    }
                }
            }
        }

    }
}
