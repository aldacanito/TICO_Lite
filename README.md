
To repeat the experiments:

- Open the TICO_Lite project and select the "run" directory. There are two subdirectories, DependentPros and IndepedentPros. The only difference is between running the script for each property independently from all others, or all concurrently. The files are the same on the two subdirectories:
    - TestConferenceAxioms: run to test for the ontologies in Experiment I
    - TestPokemonInstances: run to test for the ontologies in Experiment II.

The directory structure for the input models should not need to be modified. Each file has a list of the properties that are analysed; the reasoning behind this, in Experiment I, was to analyse only for properties that had at least one proprty axiom associated.

In the Utils directory you can find the script that creates the Pokémon RDF ontology from wikidata query results (JSON2RDF_PokeLabels). The resulting ontologies are already included, so recreating them should not be necessary. But it might be interesting. It creates a different ontology for each generation. The source files are in the Populated Datasets/pokemon outputs/ directory.


Executing any of the files in the run directory will generate a new directory with the results, in a csv file. Directory should be Analytics/YYYY/ConstructorsXX/, in which YYYY is the ontology name and XX the window size. Both can be edited in the code. If the script does not generate these directories automatically, creating an empty YYYY should fix it.

The generated files will be named as:

Analytics/YYYY/ConstructorsXX/Constructors_totals_ZZZ.csv

With YYYY and XX as before and ZZZ the property name.

NOTE: both the scripts and the spreadsheets have mentions to the Reflexive property axiom, which is not discussed in the paper. This belongs to a previous iteration of the work with a very restricted interpretation of the reflexive axiom.

To generate the graphs and visuals, and calculate ARI, you can copy the contents of this file into the empty slots in the PROPERTY RATIOS EMPTY file. If you want to run the script with different ontologies than the ones provided, you can calculate Precision/Recall and F-Measure by using the sheet for the axiom you know to be present. Existing results are provided.


For Experiment II (Evolving ARI):

If you do not wish to calculate any of the information retrieval  metrics mentioned (e.g. for the pokémon dataset results), you can copy the Constructor totals' file contents to the sheets in POKEMON GENERATION EMPTY file to visualize the results. Pre-executed results for each generation can be found in a subdirectory "Results per Generation".

Copy the last entry in both confirmations and ARI columns for each property in each generation and use them to populate the sheet Results in the file Evolving ARI Experiments.xlsx. This file has differen sheets for each property and a blank one if you wish to use for different purposes. You can copy each complete line in the sheet Results and paste it in the other sheets to generate the graphs with the visuals for each property evolution over time.




