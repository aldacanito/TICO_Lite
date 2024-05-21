package helpers.datasetconverter;

import IDC.Comparator;
import IDC.Metrics.EntityMetricsStore;
import IDC.ModelManager;
import Utils.*;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestPokemonInstances
{

    //static String base = "C:\\Users\\Alda\\Documents\\TICO\\";

    static String base = "C:\\Users\\shiza\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub\\TICO\\";
    static String datasetsFolder = base + "PopulatedDatasets\\";

    static String props[] =
            {
                  //  "https://pokemon.com#presentIn",

                   // "http://www.wikidata.org/prop/P2283",
                   // "https://pokemon.com#hasSignatureAbility",
                   // "https://pokemon.com#hatches"

                    //"https://pokemon.com#presentIn",
                    //"https://pokemon.com#presentIn",

                   // "https://pokemon.com#alternateDexEntry",

                    "https://pokemon.com#bordersWith",
                    //"https://pokemon.com#catalogsRegion",
                    //"https://pokemon.com#causedBy",
                    //"https://pokemon.com#followedBy",
                    //"https://pokemon.com#follows",
                  //  "https://pokemon.com#from",
                    "https://pokemon.com#hasAltForm",
                    "https://pokemon.com#hasMegaEvolution",
                    "https://pokemon.com#hasRegionalForm",
                    //"https://pokemon.com#hasMoveType",
                    //"https://pokemon.com#hasNoEffectAgainst",
                    //"https://pokemon.com#hasNormalEffectAgainst",
                  //  "https://pokemon.com#hasPart",
                    //"https://pokemon.com#hasPokedexEntry",
                    "https://pokemon.com#hasSignatureAbility",

                    "https://pokemon.com#hatches",
                    //"https://pokemon.com#evolutionGroup",
                    //"https://pokemon.com#instanceOf",
                    //"https://pokemon.com#hasHalfEffectAgainst",
                    "https://pokemon.com#isSignatureAbilityOf",
                    //"https://pokemon.com#isSuperEffectiveAgainst",
                    //"https://pokemon.com#hasGenderRatio",
                   // "https://pokemon.com#locatedIn",
                   // "https://pokemon.com#partOf",
                    //"https://pokemon.com#presentIn",
                    //"https://pokemon.com#introducedIn",
                    "http://www.wikidata.org/prop/P2283",

                    // datatype properties
                    /*
                    "https://pokemon.com#pokemonType",
                    "https://pokemon.com#hasColor",
                    "https://pokemon.com#hasFacet",
                    "https://pokemon.com#hasHeight",
                    "https://pokemon.com#hasName",
                    "https://pokemon.com#hasPokedexNumber",
                    "https://pokemon.com#hasShape",
                    "https://pokemon.com#hasWeight",
                    "https://pokemon.com#hasValue" */
            };


    public static void main(String[] args)
    {

        for (String propertyURI : props)
        {
            Configs configs       = new Configs();
            List<String> theProps = new ArrayList<>();
            theProps.add(propertyURI);

            Utilities.importantProps = new HashMap<>();
            Utilities.importantProps.put("pokemon", theProps);

            AnalyticUtils.ONTO_NAME      = "pokemon";
            AnalyticUtils.CURRENT_FOLDER = datasetsFolder + "pokemon outputs\\";

            String[] datasets = {AnalyticUtils.ONTO_NAME};

            String gen = "genIX";

            OntModel originalModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + gen + ".ttl");
            OntModel evolvingModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + gen + ".ttl");
            OntModel instanceModel = OntologyUtils.readModel(AnalyticUtils.CURRENT_FOLDER + gen + ".ttl");

            ModelManager.getManager().setup(originalModel, evolvingModel, instanceModel);

            List<String> individuals_uris = SPARQLUtils.getIndividualsSPARQL(originalModel);

            Configs.windowSize = 50;
            for (String dataset : datasets)
                run(dataset, individuals_uris, Configs.windowSize);

            EntityMetricsStore.cleanStore();
        }

    }

    public static void run(String ontologyName, List<String> inds, int windo_size) {
        AnalyticUtils.ONTO_NAME = ontologyName;

        AnalyticUtils.CURRENT_FOLDER = datasetsFolder + "\\pokemon outputs\\";

        AnalyticUtils.CONSTRUCTOR_ANALYTICS_FOLDER = "Analytics/" + ontologyName + "/Constructors" + windo_size + "/";
        AnalyticUtils.ANALYTICS_FOLDER = "Analytics/" + ontologyName;

        Comparator comparator = new Comparator();
        comparator.run(inds);

        String stats = comparator.printStats();
        System.out.println(" stats time: " + stats);




    }





}
