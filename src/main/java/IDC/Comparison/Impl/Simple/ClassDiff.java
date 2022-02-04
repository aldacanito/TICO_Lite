/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Simple;

import IDC.Comparison.Interfaces.IClassDiff;
import Utils.Utilities;
import java.util.List;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Restriction;
import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * checks the difference between two versions of the same class
 * 
 * vamos assumir que o Resource one é a versão mais antiga e o
 * Resource two é a versão mais recente
 * 
 * 
 * 
 * @author Alda
 */

class ClassDiffs
{
    OntClass cls1;
    OntClass cls2;
    boolean diff;
    
    public ClassDiffs(OntClass cls1, OntClass cls2, boolean diff)
    {
        this.cls1 = cls1;
        this.cls2 = cls2;
        this.diff = diff;
    }
}

/*
    TODO
    falta garantir que só compara com a versão IMEDIATAMENTE ANTERIOR

*/
public class ClassDiff implements IClassDiff
{

    @Override
    public boolean isNewVersion(OntClass one, OntClass two) 
    {
       
        OntClass op1 = (OntClass) one;
        OntClass op2 = (OntClass) two;
        
        System.out.println("Comparing:\n"
                + "\t Class1 :" + op1 + " with Class2: " + op2);
        
        //check if op2 has the same subclasses and equivalent classes as op2
         
        List<OntClass> list1 = op1.listSuperClasses().toList();
        List<OntClass> list2 = op2.listSuperClasses().toList();
        
        System.out.println("SuperClasses:\n"
                + "\t Class 1: " + list1
                + "\t Class 2: " + list2);
        
        if(list1.isEmpty() && list2.isEmpty()) return false;
        if(list1.size()    != list2.size())    return true;
        
        boolean diffSuperClasses = !compareSuperClasses(op1, op2);
        if(diffSuperClasses) return true;
        
        list1 = op1.listEquivalentClasses().toList();
        list2 = op2.listEquivalentClasses().toList();
        
        if(list1.isEmpty() && list2.isEmpty()) return false;
        if(list1.size()    != list2.size())    return true;
        
        boolean diffEquivalentClasses = !compareEquivalentClasses(op1, op2);
        return diffEquivalentClasses;
    }

    private boolean compareRestrictionTypes(Restriction r1, Restriction r2) 
    {
        
        if(!r1.getClass().getName().equalsIgnoreCase(r2.getClass().getName()))
            return false;
        
        if(r1.isAllValuesFromRestriction() && r2.isAllValuesFromRestriction())
           return compareAllValuesFrom(r1, r2); 
        else if(r1.isCardinalityRestriction() && r2.isCardinalityRestriction())
            return compareCardinality(r1, r2);
        else if(r1.isHasValueRestriction() && r2.isHasValueRestriction())
            return compareHasValue(r1, r2);
        else if(r1.isMaxCardinalityRestriction() && r2.isMaxCardinalityRestriction())
            return compareMaxCardinality(r1, r2);
        else if(r1.isMinCardinalityRestriction() && r2.isMinCardinalityRestriction())
            return compareMinCardinality(r1, r2);
        else if(r1.isSomeValuesFromRestriction() && r2.isSomeValuesFromRestriction())
            return compareSomeValuesFrom(r1, r2);
        
        return false;
    }

    /*
    Um dia a comparaçao por URI nao vai funcionar porque vamos ter de pesquisar
    TODOS os sameAs e ver se algum deles faz match porque vamos poder ter
    versoes desactualizadas das classes
    */
    private boolean compareAllValuesFrom(Restriction r1, Restriction r2) 
    {
        
        AllValuesFromRestriction a1 = r1.asAllValuesFromRestriction();
        AllValuesFromRestriction a2 = r2.asAllValuesFromRestriction();

        String uri1 = a1.getOnProperty().getURI();
        String uri2 = a2.getOnProperty().getURI();
                
        if(!uri1.equalsIgnoreCase(uri2))
            return false;
        
        uri1 = a1.getAllValuesFrom().getURI();
        uri2 = a2.getAllValuesFrom().getURI();
        
        if(!uri1.equalsIgnoreCase(uri2))
            return false;
        
        return true;
    }

    private boolean compareCardinality(Restriction r1, Restriction r2) 
    {
        CardinalityRestriction cr1 = r1.asCardinalityRestriction();
        CardinalityRestriction cr2 = r2.asCardinalityRestriction();
        
        int c1 = cr1.getCardinality();
        int c2 = cr2.getCardinality();

        if(c1!=c2) return false;
        
        String uri1 = cr1.getOnProperty().getURI();
        String uri2 = cr2.getOnProperty().getURI();
                
        if(!uri1.equalsIgnoreCase(uri2))
            return false;
        
    
        return true;
    }

    private boolean compareHasValue(Restriction r1, Restriction r2) 
    {
        HasValueRestriction cr1 = r1.asHasValueRestriction();
        HasValueRestriction cr2 = r2.asHasValueRestriction();
        
        String uri1 = cr1.getOnProperty().getURI();
        String uri2 = cr2.getOnProperty().getURI();
                
        if(!uri1.equalsIgnoreCase(uri2))  
            return false;
        
      
        RDFNode hv1 = cr1.getHasValue();
        RDFNode hv2 = cr1.getHasValue();
        
        if(hv2.isLiteral() && hv1.isLiteral())
        {
            Literal l1 = hv1.asLiteral();
            Literal l2 = hv2.asLiteral();

            if(l1.getValue() != l1.getValue()) return false; // TEST
        }
        if(hv2.isResource() && hv1.isResource())
        {
            Resource rr1 = hv1.asResource();
            Resource rr2 = hv2.asResource();
            
            if(!rr1.equals(rr2)) return false; // test 
        }

        return true;
    }

    private boolean compareMaxCardinality(Restriction r1, Restriction r2) 
    {
        MaxCardinalityRestriction mc1 = r1.asMaxCardinalityRestriction();
        MaxCardinalityRestriction mc2 = r2.asMaxCardinalityRestriction();
    
        if(mc1.getMaxCardinality() != mc2.getMaxCardinality())
            return false;
    
        String uri1 = mc1.getOnProperty().getURI();
        String uri2 = mc2.getOnProperty().getURI();
                
        return uri1.equalsIgnoreCase(uri2);
    }
    
    private boolean compareMinCardinality(Restriction r1, Restriction r2)
    {
        MinCardinalityRestriction mc1 = r1.asMinCardinalityRestriction();
        MinCardinalityRestriction mc2 = r2.asMinCardinalityRestriction();
    
        if(mc1.getMinCardinality() != mc2.getMinCardinality())
            return false;
    
        String uri1 = mc1.getOnProperty().getURI();
        String uri2 = mc2.getOnProperty().getURI();
                
        return uri1.equalsIgnoreCase(uri2);
       
    }

    private boolean compareSomeValuesFrom(Restriction r1, Restriction r2) 
    {
        SomeValuesFromRestriction svf1 = r1.asSomeValuesFromRestriction();
        SomeValuesFromRestriction svf2 = r2.asSomeValuesFromRestriction();

        Resource sv1 = svf1.getSomeValuesFrom();
        Resource sv2 = svf2.getSomeValuesFrom();

        if(sv1.isResource() && sv1.isResource())
        {
            Resource rr1 = sv1.asResource();
            Resource rr2 = sv1.asResource();
            
            if(!rr1.equals(rr2)) return false; // test 
        }
        
        String uri1 = svf1.getOnProperty().getURI();
        String uri2 = svf2.getOnProperty().getURI();
        
        return uri1.equalsIgnoreCase(uri2);
    }

    private boolean compareEquivalentClasses(OntClass op1, OntClass op2) 
    {
        List<OntClass> op1EQ = op1.listEquivalentClasses().toList();
        List<OntClass> op2EQ = op2.listEquivalentClasses().toList();
        
        // nao pode ser pelo URI porque restricoes podem ser anonimas

       return compareLists(op1EQ, op2EQ);
    }

    /**
        Verifica se as super Classes são IGUAIS.
        * Se ambas forem vazias são iguais;
        * Se tiverem números diferentes são diferentes.
     * 
    **/
     private boolean compareSuperClasses(OntClass op1, OntClass op2) 
    {
        List<OntClass> list1 = op1.listSuperClasses().toList();
        List<OntClass> list2 = op2.listSuperClasses().toList();
               
        return compareLists(list1, list2);
    }

    private boolean compareLists(List<OntClass> op1EQ, List<OntClass> op2EQ) 
    {
        boolean no_match = false;
        
        System.out.println("Comparing super/eq classes.");
            
        for(OntClass cls1 : op1EQ)
        {
            for(OntClass cls2 : op2EQ)
            {
                // comparar para ja pelo menos as restricoes
                System.out.println("Comparing: " + cls1 + " and " + cls2);
                
                if(cls1.isRestriction() && cls2.isRestriction())
                {
                    Restriction r1 = cls1.asRestriction();
                    Restriction r2 = cls2.asRestriction();
                    
                    String p_uri1 = r1.getOnProperty().getURI();
                    String p_uri2 = r2.getOnProperty().getURI();
                    
                    if(!Utilities.isInIgnoreList(p_uri1) && !Utilities.isInIgnoreList(p_uri2)
                       && !p_uri1.equalsIgnoreCase(p_uri2))
                    {
                        if(compareRestrictionTypes(r1, r2))
                           break; //encontrou o match, nao precisa procurar mais
                        else
                            no_match = true;
                    }            
                }
                else if(!cls1.isRestriction() && !cls2.isRestriction())
                {
                    // se nao é restriçao é superclasse normal?
                    if(cls1.getURI()!=null && cls2.getURI()!=null)
                    
                    if(cls1.getURI().equalsIgnoreCase(cls2.getURI()))
                        break;
                }
            }           
        }

        return !no_match;
    }
     
    

    
}
