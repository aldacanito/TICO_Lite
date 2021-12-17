/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations.Addition;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author Alda
 */
public class AddClass implements IAddClass
{
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    private String URI;
    private OntClass newClass;
    private OntClass oldClass;
    
    private HashMap<OntClass, String> restrictions;
    
    //private String [] superClasses;
    //private String [] subClasses;
    private List<OntClass> disjoinWith;
    
    public AddClass(OntClass oldClass)
    {
        this.oldClass = oldClass;
        this.URI = oldClass.getURI();
    }
    
    public AddClass(String URI)
    {  
        this.URI = URI;
    }
    
    public void addRestriction(String restrictionType, OntClass restriction)
    {
        restrictions.put(restriction, restrictionType);
    }
    
    
    @Override
    public String getURI() 
    {
        return this.URI;
    }

    @Override
    public OntModel getEvolvedModel() {
        return this.evolvedModel;
    }

    @Override
    public void setUp(OntModel originalModel, OntModel evolvedModel) 
    {
        this.originalModel = originalModel;
        this.evolvedModel  = evolvedModel;
    }

    @Override
    public void execute() 
    {        
        if(this.originalModel==null)
            Utils.Utilities.logError("Original Model is not instantiated", "ADDCLASS : EXECUTE");
        
        if(this.oldClass==null)
            this.oldClass = this.originalModel.getOntClass(URI);
        
        boolean create = false;
        if(this.evolvedModel.getOntClass(URI) !=null) // class does not exist in the evolved model
            create = true;
        
        if(create) // preciso instanciar nova classe
        {    
            this.newClass = this.evolvedModel.createClass(URI);
            
            OntProperty ontProperty = this.evolvedModel.getOntProperty("http://www.w3.org/2006/time#hasBeginning");
            
            if(ontProperty == null)
                ontProperty = this.evolvedModel.createObjectProperty("http://www.w3.org/2006/time#hasBeginning", false);
            
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");  
            LocalDateTime now = LocalDateTime.now();  
   
            OntClass instantClass = this.evolvedModel.getOntClass("http://www.w3.org/2006/time#instant");
            if(instantClass == null)
                instantClass = this.evolvedModel.createClass("http://www.w3.org/2006/time#instant");
            
            Individual date1 = this.evolvedModel.createIndividual(dtf2.format(now), instantClass);
            
            //date1.addLabel(dtf.format(now), null);
            
            System.out.println("PRITNING AGORA O BOENCO");
          
            
            HasValueRestriction createHasValueRestriction = this.evolvedModel.createHasValueRestriction(null, ontProperty, date1);
            
            
            this.newClass.addSuperClass(createHasValueRestriction);
        }
        else // copia o que já existe
        {
            //TODO REVER QUESTAO TEMPORAL AQUI!!!!!!!!!!!!!!!!!!!!!!
            
            //SubClassOf time:hasBegining VALUE DataInicio
            //SubClassOf time:hasEnd VALUE DataInicio
            
            
            //primeiro é preciso ver se vai ser precisa uma definiçao nova da classe
            //tenho de saber quais as restriçoes!!!!!!!!!!! que estao no keyset
            this.newClass = Utils.OntologyUtils.copyClass(oldClass, evolvedModel);
        }
        
        //adicionar restrições
        // fazer esta parte sem ser copia!!!

        if(this.restrictions!=null)
        {
            for(OntClass restriction : restrictions.keySet())
            {
                String restrictionType = restrictions.get(restriction);   
                //Utils.OntologyUtils.copyRestriction(restriction, newClass, restrictionType);
            }
        }
        
        this.addDisjoints();
        
    }
    
    public void addDisjoints()
    {
        if(this.disjoinWith!=null)
        {
            for(OntClass disjoint : this.disjoinWith)
            {
                this.newClass.addDisjointWith(disjoint);
            }
        }
    }
    
}
