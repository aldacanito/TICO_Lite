/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author shizamura
 */
public class AddClass implements IAddClass
{
    private OntClass ontClass;
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    public AddClass(OntClass toAdd)
    {
        this.ontClass = toAdd;
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
        
        if(evolvedModel.isEmpty())
            evolvedModel = originalModel;
    
        // BÁSICO, SÓ ADICIONA O NOVO URI
        
        //verificar
        ontClass.inModel(evolvedModel);
        evolvedModel.createClass(ontClass.getURI());
        
    }

    @Override
    public OntModel getEvolvedModel() 
    {
        return this.evolvedModel;
    }
    
   
    
}
