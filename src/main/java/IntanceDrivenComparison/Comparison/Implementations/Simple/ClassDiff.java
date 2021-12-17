/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Implementations.Simple;

import IntanceDrivenComparison.Comparison.Interfaces.IClassDiff;
import Utils.Utilities;
import java.util.List;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * checks the difference between two versions of the same class
 * @author Alda
 */
public class ClassDiff implements IClassDiff
{

    @Override
    public List<Resource> getDiffs(Resource one, Resource two) 
    {
        
        if(one instanceof OntClass && two instanceof OntClass)
        {
            OntClass op1 = (OntClass) one;
            OntClass op2 = (OntClass) two;
            
            //check if op2 has the same subclasses and equivalent classes as op2
            List<OntClass> op1EQ = op1.listEquivalentClasses().toList();
            List<OntClass> op2EQ = op2.listEquivalentClasses().toList();
            
            for(OntClass cls1 : op1EQ)
            {
                for(OntClass cls2 : op1EQ)
                {

                    boolean equals = false;
                    // comparar para ja pelo menos as restricoes
                    if(cls1.isRestriction() && cls2.isRestriction())
                    {
                        Restriction r1 = cls1.asRestriction();
                        Restriction r2 = cls2.asRestriction();

                        equals = compareRestrictionTypes(r1, r2);
                    }
                    else
                    {
                        continue;
                    }
                    
                   
                    if(equals)
                        break;
                }
            
            }
            
        }
        else
        {
            
            Utilities.logError("CLASS DIFF : Resources were not OntClass. No comparison occurred.");
            return null;
        }
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean compareRestrictionTypes(Restriction r1, Restriction r2) 
    {
        
        if(!r1.getClass().getName().equalsIgnoreCase(r2.getClass().getName()))
            return false;
        
        if(r1.isAllValuesFromRestriction() && r2.isAllValuesFromRestriction())
           return compareAllValuesFrom(r1, r2); 
        else if(r1.isCardinalityRestriction() && r2.isCardinalityRestriction())
            return compareCardinality(r1, r2);
        
    
        return false;
    }
    
}
