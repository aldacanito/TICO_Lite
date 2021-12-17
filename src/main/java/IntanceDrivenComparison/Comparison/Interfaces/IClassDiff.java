/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IntanceDrivenComparison.Comparison.Interfaces;

import java.util.List;
import org.apache.jena.rdf.model.Resource;

/**
 *
 * @author Alda
 */
public interface IClassDiff 
{
    // como é que descrevo as diferenças?? Neste momento pode ser só
    // restriçao existe ou nao existe, adiciona
    // depois mais tarde procurar saber se é preciso ou nao ver se a mesma restriçao evolui
    public List<Resource> getDiffs(Resource one, Resource two);

}
