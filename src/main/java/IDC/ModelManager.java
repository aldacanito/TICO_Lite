/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IDC;

import Utils.Configs;
import Utils.OntologyUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


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
    private OntModel slidingWindowModel;

    Queue<String> slidingUris;


    private ModelManager()
    {
    }
    
    public static ModelManager getManager()
    {
        if(theManager == null)
            theManager = new ModelManager();
        
        return theManager;
       
    }

    public void setup(OntModel om, OntModel ev, OntModel im)
    {
        originalModel = om;

        AnnotationProperty o_version = originalModel.createAnnotationProperty("version");
        OntResource o_versionInfo    = originalModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "original");

        evolvingModel = ev;
        o_version     = evolvingModel.createAnnotationProperty("version");
        o_versionInfo = evolvingModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "evolving");

        instanceModel = im;
        o_version     = instanceModel.createAnnotationProperty("version");
        o_versionInfo = instanceModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "instance");

        slidingWindowModel  = ModelFactory.createOntologyModel();
        o_version           = slidingWindowModel.createAnnotationProperty("version");
        o_versionInfo       = slidingWindowModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "sliding");

        temporal_instancesModel = evolvingModel;
        slidingUris = new PriorityQueue<String>(Configs.windowSize);

    }
    public void setup(String onto_path, String instance_path)
    {
        originalModel = OntologyUtils.readModel(onto_path);

        AnnotationProperty o_version = originalModel.createAnnotationProperty("version");
        OntResource o_versionInfo    = originalModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "original");

        evolvingModel = OntologyUtils.readModel(onto_path);
        o_version     = evolvingModel.createAnnotationProperty("version");
        o_versionInfo = evolvingModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "evolving");


        instanceModel = OntologyUtils.readModel(instance_path);
        o_version     = instanceModel.createAnnotationProperty("version");
        o_versionInfo = instanceModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "instance");

        slidingWindowModel  = ModelFactory.createOntologyModel();
        o_version           = slidingWindowModel.createAnnotationProperty("version");
        o_versionInfo       = slidingWindowModel.createOntResource("VersionInfo");
        o_versionInfo.addLiteral(o_version, "sliding");

        temporal_instancesModel = evolvingModel;
        slidingUris = new PriorityQueue<String>();

    }

    public Individual addToWindow(Individual i)
    {
        if(slidingUris.size() >= Configs.windowSize)
        {
            String toRemove = slidingUris.remove();
            OntologyUtils.removeIndividual(toRemove, slidingWindowModel);
        }

        i = OntologyUtils.copyIndividual(i, slidingWindowModel);
        slidingUris.add(i.getURI());

        return i;
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
