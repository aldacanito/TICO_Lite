package helpers.datasetconverter;

import IDC.EvolActions.Impl.Additions.AddClass;
import IDC.EvolActions.Impl.Additions.AddDatatypeProperty;
import IDC.EvolActions.Impl.Additions.AddObjectProperty;
import IDC.EvolActions.Impl.Additions.Restriction.AddAllValuesFromRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddCardinalityRestriction;
import IDC.EvolActions.Impl.Additions.Restriction.AddSomeValuesFromRestriction;
import IDC.EvolActions.Impl.EvolutionaryActionComposite;
import Utils.Configs;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;

public class TestCompositeAdd
{
    static OntModel model = ModelFactory.createOntologyModel();


    public static void main(String[] args)
    {
        Configs configs = new Configs();
        EvolutionaryActionComposite composite = new EvolutionaryActionComposite();

        EvolutionaryActionComposite composite2 = new EvolutionaryActionComposite();

        OntClass cls1 = model.createClass("CLASS1");
        OntClass cls2 = model.createClass("CLASS2");

        OntProperty prop1 = model.createObjectProperty("prop1");
        OntProperty prop2 = model.createObjectProperty("prop2");

        OntProperty prop3 = model.createDatatypeProperty("prop3");
        OntProperty prop4 = model.createDatatypeProperty("prop4");

        AddCardinalityRestriction rec1 = new AddCardinalityRestriction(cls1, prop3, true, true);
        AddCardinalityRestriction rec2 = new AddCardinalityRestriction(cls1, prop3, true, true);
        AddCardinalityRestriction rec3 = new AddCardinalityRestriction(cls1, prop4, true, true);

        AddAllValuesFromRestriction aavfR1 = new AddAllValuesFromRestriction(cls1, prop2, true, true, cls2);
        AddAllValuesFromRestriction aavfR2 = new AddAllValuesFromRestriction(cls1, prop2, true, true, cls2);
        AddAllValuesFromRestriction aavfR3 = new AddAllValuesFromRestriction(cls1, prop1, true, true, cls2);

        AddSomeValuesFromRestriction asvfR1 = new AddSomeValuesFromRestriction(cls1, prop2, true, true, cls2);
        AddSomeValuesFromRestriction asvfR2 = new AddSomeValuesFromRestriction(cls1, prop2, true, true, cls2);
        AddSomeValuesFromRestriction asvfR3 = new AddSomeValuesFromRestriction(cls2, prop1, true, true, cls2);

        AddDatatypeProperty addDtP1 = new AddDatatypeProperty("prop3", true);
        AddDatatypeProperty addDtP2 = new AddDatatypeProperty("prop3", true);
        AddDatatypeProperty addDtP3 = new AddDatatypeProperty("prop4", false);

        AddObjectProperty addOP1 = new AddObjectProperty("prop1", true);
        AddObjectProperty addOP2 = new AddObjectProperty("prop1", true);
        AddObjectProperty addOP3 = new AddObjectProperty("prop2", false);

        AddClass addCls1 = new AddClass(cls1);
        AddClass addCls2 = new AddClass(cls1);
        AddClass addCls3 = new AddClass(cls2);

        System.out.println("Adding Cardinality Restrictions");

        System.out.println("\tAdded 1? " + composite.add(rec1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(rec2) + ". Should be: false.");
        System.out.println("\tAdded 3? " + composite.add(rec3) + ". Should be: true." );

        System.out.println("Adding All Values From Restrictions");
        System.out.println("\tAdded 1? " + composite.add(aavfR1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(aavfR2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(aavfR3) + ". Should be: true." );

        System.out.println("Adding Some Values From Restrictions");
        System.out.println("\tAdded 1? " + composite.add(asvfR1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(asvfR2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(asvfR3) + ". Should be: true." );

        System.out.println("Adding repeated");
        System.out.println("\tAdded 1? " + composite.add(rec1)   + ". Should be: false." );
        System.out.println("\tAdded 2? " + composite.add(aavfR2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(asvfR3) + ". Should be: false." );

        System.out.println("Adding Datatype Properties");
        System.out.println("\tAdded 1? " + composite.add(addDtP1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(addDtP2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(addDtP3) + ". Should be: true." );

        System.out.println("Adding Object Properties");
        System.out.println("\tAdded 1? " + composite.add(addOP1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(addOP2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(addOP3) + ". Should be: true." );

        System.out.println("Adding Add Class");
        System.out.println("\tAdded 1? " + composite.add(addCls1) + ". Should be: true." );
        System.out.println("\tAdded 2? " + composite.add(addCls2) + ". Should be: false." );
        System.out.println("\tAdded 3? " + composite.add(addCls3) + ". Should be: true." );

        System.out.println(composite.toString());
        System.out.println("Composite stats:\n\t> Should have 12 elements. Has " + composite.getActions().size() + ".");

        composite2.add(composite);
        composite2.add(asvfR1);
        composite2.add(composite);

        System.out.println(composite2.toString());
        System.out.println("Composite stats:\n\t> Should have 12 elements. Has " + composite2.getActions().size() + ".");





    }
}
