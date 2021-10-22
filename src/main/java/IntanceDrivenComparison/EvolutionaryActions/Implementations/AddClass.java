/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import java.util.List;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author Alda
 */
public class AddClass implements IAddClass
{
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    private String URI;
    
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
        OntClass newClass = Utils.OntologyUtils.getClassFromModel(evolvedModel, this.URI);
        
        if(newClass==null)
        {
            newClass = evolvedModel.createClass(URI);
        }
        else
        {
            //class existe. fazer o que?
        }
    
    
    }
    
}
