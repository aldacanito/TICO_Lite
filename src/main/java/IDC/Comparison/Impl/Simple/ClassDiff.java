/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC.Comparison.Impl.Simple;

import IDC.Comparison.Interfaces.IClassDiff;
import Utils.OntologyUtils;
import Utils.SPARQLUtils;
import Utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.*;
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


    private List<OntClass> excludeIgnoreElements(List<OntClass> list1)
    {
        List<OntClass> ret = new ArrayList<>();
        for(OntClass cls : list1)
        {
            String uri = cls.getURI();

            if(uri != null)
            {
                if (!Utils.Utilities.isInIgnoreList(uri))
                    ret.add(cls);
            }
            else
                ret.add(cls);
        }

        return ret;
    }

    @Override
    /**
     * Verifies is two is a new version of one.
     * returns TRUE if the Classes differ in their restrictions.
     * return FALSE if the Classes are the same.
     */
    public boolean isNewVersion(OntClass one, OntClass two) 
    {
       
        OntClass op1 = (OntClass) one;
        OntClass op2 = (OntClass) two;

        System.out.println("Comparing:\n"
                + "\t Class1: " + op1 + " ("+ OntologyUtils.getModelVersion(op1.getOntModel())+") " +
                "with Class2: " + op2 + " ("+ OntologyUtils.getModelVersion(op2.getOntModel())+") ");
        
        //check if op2 has the same subclasses and equivalent classes as op2
         
        List<OntClass> list1 = op1.listSuperClasses().toList();
        List<OntClass> list2 = op2.listSuperClasses().toList();

        list1 = excludeTemporalClasses(list1);
        list2 = excludeTemporalClasses(list2);

        System.out.println("SuperClasses:\n"
                + "\t > "   + op1 + ": " + list1.size()
                + "\n\t > " + op2 + ": " + list2.size());
        
        if(list1.isEmpty() && list2.isEmpty()) return false;
        //if(list1.size()    != list2.size())    return true;

        boolean diffSuperClasses = false;

        if(list1.size() >= list2.size())
            diffSuperClasses = !compareSuperClasses(op1, op2);
        else
            diffSuperClasses = !compareSuperClasses(op2, op1);

        return diffSuperClasses;

//
//        list1 = op1.listEquivalentClasses().toList();
//        list2 = op2.listEquivalentClasses().toList();
//
//        if(list1.isEmpty() && list2.isEmpty()) return false;
//        if(list1.size()    != list2.size())    return true;
//
//        boolean diffEquivalentClasses = !compareEquivalentClasses(op1, op2);
//
//        System.out.println("Is New Version? " + diffEquivalentClasses);
//        return diffEquivalentClasses;
    }

    /**
     * Removes all OntClasses in a List that are either Temporal on the Ignore List
     * @param classes List of OntClasses
     * @return List of OntClasses without temporal or ignore Classes
     */
    private List<OntClass> excludeTemporalClasses(List<OntClass> classes)
    {
        List<OntClass> ret = new ArrayList<>();

        for(OntClass cls1 : classes)
        {
            if (cls1.isRestriction())
            {
                Restriction r1 = cls1.asRestriction();
                String p_uri1 = r1.getOnProperty().getURI();

                if (p_uri1.equalsIgnoreCase(OntologyUtils.DURING_P) || p_uri1.equalsIgnoreCase(OntologyUtils.HAS_SLICE_P)
                        || p_uri1.equalsIgnoreCase(OntologyUtils.BEFORE_P) || Utilities.isInIgnoreList(p_uri1))
                    continue;
            }

            ret.add(cls1);

        }

        return ret;
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
        Returns TRUE if classes op1 and op2 have the same set of Restrictions, FALSE otherwise.
    **/
     private boolean compareSuperClasses(OntClass op1, OntClass op2) 
    {
        List<OntClass> list1 = op1.listSuperClasses().toList();

        System.out.println("FOR CLASS " + op1.getURI() +" ("+ OntologyUtils.getModelVersion(op1.getOntModel())+")" );
        return compareRestrictionsSPARQL(list1, op2);
    }


    /**
     *
     * @param classes
     * @param ontClass2
     * @return TRUE if the restrictions are the same, FALSE otherwise.
     */
    private boolean compareRestrictionsSPARQL(List<OntClass> classes, OntClass ontClass2)
    {
        for(OntClass cls1 : classes)
        {
            if(!cls1.isAnon())
                System.out.println("Comparing Super/Equiv Class with URI: " + cls1.getURI() );

            if (cls1.isRestriction())
            {
                Restriction r1 = cls1.asRestriction();
                String p_uri1  = r1.getOnProperty().getURI();

                System.out.println("RESTRICTION ON PROPERTY: " + p_uri1 + " ("+ OntologyUtils.getModelVersion(cls1.getOntModel())+") ");

                if (p_uri1.equalsIgnoreCase(OntologyUtils.DURING_P) || p_uri1.equalsIgnoreCase(OntologyUtils.HAS_SLICE_P)
                        || p_uri1.equalsIgnoreCase(OntologyUtils.BEFORE_P) || Utilities.isInIgnoreList(p_uri1))
                    continue;

                if(!SPARQLUtils.hasRestrictionSPARQL(ontClass2, r1))
                    return false;
            }
            else
            {
//                System.out.println("\t>Class is not Restriction. ");
//                System.out.println("\t> Is Class? " + cls1.isClass() );
//
//                System.out.println("\t> Is Anon? " + cls1.isAnon() );
//                System.out.println("\t> Is Intersection Class? " + cls1.isIntersectionClass() );
//                System.out.println("\t> Is Complement Class? " + cls1.isComplementClass() );
//                System.out.println("\t> Is Enumerated Class? " + cls1.isEnumeratedClass() );
//                System.out.println("\t> Is Union Class? " + cls1.isUnionClass() );
//                System.out.println("\t> Is Hierarchy Root? " + cls1.isHierarchyRoot() );
            }
        }

        return true;
    }



    private boolean compareLists(List<OntClass> op1EQ, List<OntClass> op2EQ) 
    {
        boolean no_match = false;

        for(OntClass cls1 : op1EQ)
        {
            if(cls1.isRestriction())
            {
                Restriction r1 = cls1.asRestriction();
                String p_uri1 = r1.getOnProperty().getURI();

                if (p_uri1.equalsIgnoreCase(OntologyUtils.DURING_P) || p_uri1.equalsIgnoreCase(OntologyUtils.HAS_SLICE_P)
                        || p_uri1.equalsIgnoreCase(OntologyUtils.BEFORE_P))
                    continue;

                if (Utilities.isInIgnoreList(p_uri1))
                    continue;
            }

            for(OntClass cls2 : op2EQ)
            {
                if(cls1.isRestriction() && cls2.isRestriction())
                {
                    Restriction r2 = cls2.asRestriction();
                    String p_uri2  = r2.getOnProperty().getURI();

                    if(p_uri2.equalsIgnoreCase(OntologyUtils.DURING_P) || p_uri2.equalsIgnoreCase(OntologyUtils.HAS_SLICE_P)
                            || p_uri2.equalsIgnoreCase(OntologyUtils.BEFORE_P) )
                        continue;

                    if(Utilities.isInIgnoreList(p_uri2))
                        continue;

                    Restriction r1 = cls1.asRestriction();
                    String p_uri1  = r1.getOnProperty().getURI();

                    if(p_uri1.equalsIgnoreCase(p_uri2))
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

        System.out.println("\t\t > Is there a match? " + !no_match);
        return !no_match;
    }
     
    

    
}
