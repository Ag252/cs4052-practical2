package modelChecker;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import formula.FormulaParser;
import formula.stateFormula.StateFormula;
import modelChecker.ModelChecker;
import modelChecker.SimpleModelChecker;
import model.*;
import java.util.Arrays;
import java.util.ArrayList;

public class ModelCheckerTest {

    /*
     * An example of how to set up and call the model building methods and make
     * a call to the model checker itself. The contents of model.json,
     * constraint1.json and ctl.json are just examples, you need to add new
     * models and formulas for the mutual exclusion task.
     */
    @Test
    public void buildAndCheckModel() {
        try {
            Model model = Model.parseModel("src/test/resources/model1.json");

            StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
            System.out.println(fairnessConstraint);
            StateFormula query = new FormulaParser("src/test/resources/ctl1.json").parse();
            System.out.println(query);

            ModelChecker mc = new SimpleModelChecker();
            // TO IMPLEMENT
            // assertTrue(mc.check(model, fairnessConstraint, query));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    @Test
    public void testAtomicPropModel1() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");
        printArrayList(model.getStateList());
        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlAtomic.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(mc.sat(model, query).size());
        assertEquals(mc.sat(model, query).size(), 1);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testAtomicPropModel2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");
        printArrayList(model.getStateList());

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlAtomic.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(mc.sat(model, query).size());
        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testNot() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlNot.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(mc.sat(model, query).size());

        assertEquals(mc.sat(model, query).size(), 2);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testAnd() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlAnd.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(mc.sat(model, query).size());

        assertEquals(mc.sat(model, query).size(), 1);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testOr() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlOr.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(mc.sat(model, query).size());

        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testPost() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlAtomic.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(Arrays.toString(mc.post(model, model.getStates()[2]).toArray()));

        //assertEquals(mc.sat(model, query).size(), 2);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsNext() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsNext.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllNext() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllNext.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testThereExistsUntil() {
      try {
        Model model = Model.parseModel("src/test/resources/model1.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsUntil.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 2);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testThereExistsUntil2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsUntil2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testPre() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsUntil2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.print("model state: " + model.getStates()[3]);
        printArrayList(mc.pre(model, model.getStates()[3]));
        //assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testThereExistsAlways() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsAlways.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testThereExistsAlways2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsAlways2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 2);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsUntilExistsAlways() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctl3.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsEventually() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsEventually.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllUntil() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllUntil.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 1);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllUntil2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllUntil2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllUntil3() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllUntil3.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 1);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllEventually() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllEventually.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllEventually2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllEventually2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        //assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllAlways() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllAlways.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsAlwaysNot() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsAlwaysNot.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        //assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testNotAndNot() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlNotAndNot.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        //assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testNotUntilAnd() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlNotUntilAnd.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        //assertEquals(mc.sat(model, query).size(), 4);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testNotExistsUntilOrExistsAlways() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlNotExistsUntilOrExistsAlways.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.sat(model, query));
        //assertEquals(mc.sat(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsNextActions() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsNextActions.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.satActions(model, query));
        assertEquals(mc.satActions(model, query).size(), 3);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllNextActions() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllNextActions.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.satActions(model, query));
        assertEquals(mc.satActions(model, query).size(), 2);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsUntilActions() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlExistsUntilActions.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.satActions(model, query));
        assertEquals(mc.satActions(model, query).size(), 1);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testExistsUntilActions2() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctl2.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.satActions(model, query));
        assertEquals(mc.satActions(model, query).size(), 2);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    @Test
    public void testForAllUntilActions() {
      try {
        Model model = Model.parseModel("src/test/resources/model2.json");

        StateFormula fairnessConstraint = new FormulaParser("src/test/resources/constraint1.json").parse();
        StateFormula query = new FormulaParser("src/test/resources/ctlForAllUntilActions.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();

        printArrayList(mc.satActions(model, query));
        assertEquals(mc.satActions(model, query).size(), 1);

      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }




    public void printArrayList(ArrayList<State> arrayList) {
      System.out.println(Arrays.toString(arrayList.toArray()));
    }

}
