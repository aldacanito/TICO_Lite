package IDC;

import IDC.Metrics.*;
import Utils.*;

import java.util.List;
import org.apache.jena.ontology.*;



public class PropertyAxiomScoring
{
    public PropertyAxiomScoring()
    {
    }


    /***
     * Guided by the instances! Only works for individuals with OntClasses associated to them.
     * Doesn't use reasoner.
     */
    public void run(List<String> inds)
    {
        getIndividualsMetrics(inds);
    }

    /***
     * Guided by the instances! Only works for individuals with OntClasses associated to them.
     * Doesn't use reasoner.
     */
    public void run()
    {
        List<String> individuals_uris = Utilities.extractInstancesFromFile();

        //List <String> individuals_uris = SPARQLUtils.getIndividualsSPARQL(ModelManager.getManager().getInstanceModel());

        int partitionSize  = 3000;

        List<List<String>> partitions = Utilities.chopped(individuals_uris, partitionSize);
        List<String> inds = partitions.get(0);

        AnalyticUtils.deleteAnalytics();
        getIndividualsMetrics(inds);



    }

    public void getIndividualsMetrics(List<String> individuals_uris)
    {
        int total_inds = individuals_uris.size();
        int count_inds = 0;

        for(String uri : individuals_uris)
        {
            count_inds++;

            float percent_inds = (float) count_inds / (float) total_inds ;

            System.out.println("Analysing individual " + uri +
                    ".\n\t> Total individuals to analyse: " + total_inds
                    + ".\n\t Currently on individual number: " + count_inds
                    + ".\n\t Analaysed percentage: " + percent_inds);

            Individual individual = ModelManager.getManager().getInstanceModel().getIndividual(uri);

            //if(individual.getURI().contains("zygarde")) continue;

            IndividualMetrics im = new IndividualMetrics(individual);

            if(im.getProperties().size() == 0)
                continue;

            EntityMetricsStore.getStore().addIndividualMetrics(im);

            // use sliding window
            ModelManager.getManager().addToWindow(individual);

            ClassPropertyMetrics.computeAllMetricsForIndividual(im, count_inds);
            ClassPropertyMetrics.static_printComputations2File(count_inds);

        }
    }




}