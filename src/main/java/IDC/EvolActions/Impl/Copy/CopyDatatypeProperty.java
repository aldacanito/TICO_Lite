/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.EvolActions.Impl.Copy;

import IDC.EvolActions.Interfaces.IAddDatatypeProperty;
import IDC.ModelManager;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;

/**
 *
 * @author shizamura
 */
public class CopyDatatypeProperty extends CopyProperty
{
    public CopyDatatypeProperty(OntProperty t) 
    {
        super(t);
    }

    public CopyDatatypeProperty(Triple t) 
    {
        super(t);
    }
    
    @Override
    public void execute() 
    {
        ModelManager.getManager().getEvolvingModel().createDatatypeProperty(theProperty.getURI());
        
        // TODO VERIFICAR SE ISTO SE AGUENTA     
        
        if(!Utilities.isInIgnoreList(theProperty.getURI()))
            OntologyUtils.copyProperty(ModelManager.getManager().getEvolvingModel(), theProperty);
    }
    
    public String toString()
    {
        String toPrint="ADD DATATYPE PROPERTY EVOLUTIONARY ACTION: " ;
        toPrint += OntologyUtils.propertyStats(theProperty);
        return toPrint;
    }
    
    
}
