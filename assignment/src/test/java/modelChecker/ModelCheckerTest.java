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
        StateFormula query = new FormulaParser("src/test/resources/ctl3.json").parse();

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
        StateFormula query = new FormulaParser("src/test/resources/ctl3.json").parse();

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
        StateFormula query = new FormulaParser("src/test/resources/ctl4.json").parse();

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
        StateFormula query = new FormulaParser("src/test/resources/ctl5.json").parse();

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
        StateFormula query = new FormulaParser("src/test/resources/ctl6.json").parse();

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
        StateFormula query = new FormulaParser("src/test/resources/ctl3.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        System.out.println(Arrays.toString(mc.postEX(model, model.getStates()[2]).toArray()));

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
        StateFormula query = new FormulaParser("src/test/resources/ctl7.json").parse();

        SimpleModelChecker mc = new SimpleModelChecker();
        printArrayList(mc.sat(model, query));
        assertEquals(mc.sat(model, query).size(), 3);
      } catch (IOException e) {
          e.printStackTrace();
          fail(e.toString());
      }
    }

    public void printArrayList(ArrayList<State> arrayList) {
      System.out.println(Arrays.toString(arrayList.toArray()));
    }

}
