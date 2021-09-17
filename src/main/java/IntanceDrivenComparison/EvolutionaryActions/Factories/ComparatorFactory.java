/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.EvolutionaryActions.Factories;

import IntanceDrivenComparison.Comparison.Implementations.Simple.ClassCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.DatatypePropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.ObjectPropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Implementations.Simple.PropertyCompareSimple;
import IntanceDrivenComparison.Comparison.Interfaces.IClassCompare;
import IntanceDrivenComparison.Comparison.Interfaces.IPropertyCompare;
import Utils.OntologyUtils;
import Utils.Utilities;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author shizamura
 */
public class ComparatorFactory 
{
    private static ComparatorFactory theFactory = new ComparatorFactory();
   
    private ComparatorFactory()
    {
        //TODO CHECK CONFIGS
    }
    
    public static ComparatorFactory getInstance()
    {
        return theFactory;
    }
    
    //TODO alterar para ser dinamico
    public IClassCompare getClassComparator(OntClass cls, OntModel mdl)
    {
        boolean ignore = Utilities.isInIgnoreList(cls.getURI());
        
        if(ignore)
            return null;
        
        IClassCompare compare = new ClassCompareSimple(cls, mdl);
        return compare;
    }

    
    
    //TODO vERIFICAR SE ESTE METODO TÁ A COMPARAR EM CONDIÇOES
    //NESTE MOMENTO ACHO QUE ESTA A COMPARAR AS NOVAS PROPRIEDADES COM O MODELO NOVO
     public IPropertyCompare getPropertyComparator(Statement statement, OntModel ontModel) 
    {
        Utilities.logInfo("getPropertyComparator URI: " + statement.getPredicate().getURI());
        
        boolean ignore = Utilities.isInIgnoreList(statement.getPredicate().getURI());
        
        if(ignore)
            return null;
        
        
        // VERIFICAR SE JA EXISTE
        
        // SE NAO EXISTIR, VERIFICAR SE É OP OU DTP E CRIAR O COMPARADOR DE ACORDO
        
        if(statement.getObject().isResource())
            return new ObjectPropertyCompareSimple(statement, ontModel);
        
        if(statement.getObject().isLiteral())
            return new DatatypePropertyCompareSimple(statement, ontModel);
            
        Utilities.logInfo("URI does not match any ObjectProperty or DatatypeProperty definitions in the model.");
        return new PropertyCompareSimple(statement, ontModel);
    }
     
    /*
    public IPropertyCompare getPropertyComparator(Node predicate, OntModel ontModel) 
    {
        Utilities.logInfo("getPropertyComparator URI: " + predicate.getURI());
        
        boolean objectProperty = OntologyUtils.isObjectProperty(predicate, ontModel);
        boolean datatypeProperty = OntologyUtils.isDatatypeProperty(predicate, ontModel);
        
        if(objectProperty)
            return new ObjectPropertyCompareSimple();
        
        if(datatypeProperty)
            return new DatatypePropertyCompareSimple();
            
        Utilities.logInfo("URI does not match any ObjectProperty or DatatypeProperty definitions in the model.");
        return new PropertyCompareSimple();
    }*/
    
}
