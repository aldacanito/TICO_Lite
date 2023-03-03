/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Copy;

import IDC.EvolActions.Interfaces.IAddClass;
import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.ontology.OntClass;

/**
 *
 * @author shizamura
 */
public class CopyClass implements IAddClass
{
    private OntClass ontClass;
    
    public CopyClass(OntClass toAdd)
    {
        this.ontClass = toAdd;
    }
        
 

    @Override
    public void execute() 
    {
        
    
        // BÁSICO, TENTA COPIAR PROPRIEDADES E DEFINICOES DA CLASSE
        //verificar
        //Resource inModel = ontClass.inModel(evolvedModel);

        
        if(!Utilities.isInIgnoreList(ontClass.getURI()))
            OntologyUtils.copyClass(ontClass, ModelManager.getManager().getEvolvingModel());
    }

    public String toString()
    {
        String toPrint="ADDCLASS EVOLUTIONARY ACTION: Class URI: " + this.ontClass.getURI(); 
        toPrint += OntologyUtils.classStats(ontClass);
        return toPrint;
    }
    


    @Override
    public String getURI() 
    {
        return this.ontClass.getURI();
    }

   
    
}
