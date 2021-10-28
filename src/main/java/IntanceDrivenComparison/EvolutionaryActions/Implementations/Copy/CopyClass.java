/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Implementations.Copy;

import IntanceDrivenComparison.EvolutionaryActions.Interfaces.IAddClass;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

/**
 *
 * @author shizamura
 */
public class CopyClass implements IAddClass
{
    private OntClass ontClass;
    private OntModel originalModel;
    private OntModel evolvedModel;
    
    public CopyClass(OntClass toAdd)
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
    
        // BÁSICO, TENTA COPIAR PROPRIEDADES E DEFINICOES DA CLASSE
        //verificar
        //Resource inModel = ontClass.inModel(evolvedModel);

        
        if(!Utilities.isInIgnoreList(ontClass.getURI()))
            OntologyUtils.copyClass(ontClass, evolvedModel);
    }

    public String toString()
    {
        String toPrint="ADDCLASS EVOLUTIONARY ACTION: Class URI: " + this.ontClass.getURI(); 
        toPrint += OntologyUtils.classStats(ontClass);
        return toPrint;
    }
    
    
    @Override
    public OntModel getEvolvedModel() 
    {
        return this.evolvedModel;
    }

    @Override
    public String getURI() 
    {
        return this.ontClass.getURI();
    }

   
    
}
