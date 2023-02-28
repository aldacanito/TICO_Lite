/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC;

import Utils.OntologyUtils;
import org.apache.jena.ontology.OntModel;

/**
 *
 * @author Alda
 */
public class ModelManager 
{
    private static ModelManager theManager;
    private OntModel originalModel;
    private OntModel evolvingModel;
    private OntModel instanceModel;
    private OntModel temporal_instancesModel;
    
    
    private ModelManager()
    {}
    
    public static ModelManager getManager()
    {
        if(theManager == null)
            theManager = new ModelManager();
        
        return theManager;
       
    }

    public void setup(String onto_path, String instance_path)
    {
        originalModel = OntologyUtils.readModel(onto_path);
        evolvingModel = OntologyUtils.readModel(onto_path);
        instanceModel = OntologyUtils.readModel(instance_path);
        temporal_instancesModel = evolvingModel;
    }
    
    
    /**
     * @return the originalModel
     */
    public OntModel getOriginalModel() {
        return originalModel;
    }

    /**
     * @param originalModel the originalModel to set
     */
    public void setOriginalModel(OntModel originalModel) {
        this.originalModel = originalModel;
    }

    /**
     * @return the evolvingModel
     */
    public OntModel getEvolvingModel() {
        return evolvingModel;
    }

    /**
     * @param evolvingModel the evolvingModel to set
     */
    public void setEvolvingModel(OntModel evolvingModel) {
        this.evolvingModel = evolvingModel;
    }

    /**
     * @return the instanceModel
     */
    public OntModel getInstanceModel() {
        return instanceModel;
    }

    /**
     * @param instanceModel the instanceModel to set
     */
    public void setInstanceModel(OntModel instanceModel) {
        this.instanceModel = instanceModel;
    }

    /**
     * @return the temporal_instancesModel
     */
    public OntModel getTemporal_instancesModel() {
        return temporal_instancesModel;
    }

    /**
     * @param aTemporal_instancesModel the temporal_instancesModel to set
     */
    public void setTemporal_instancesModel(OntModel aTemporal_instancesModel) {
        temporal_instancesModel = aTemporal_instancesModel;
    }
    
    
    
    
    
}
