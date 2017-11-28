package modelChecker;

import formula.stateFormula.*;
import model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class SimpleModelChecker implements ModelChecker {

    public static void main(String[] args) {
      System.out.println("Test");
    }

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
        // TODO Auto-generated method stub
        return false;
    }

    public ArrayList<State> sat(Model model, StateFormula formula) {
      if(formula instanceof AtomicProp) {
        return atomicEval(model, ((AtomicProp)formula).label);
      }
      else if(formula instanceof Not) {
        return setDiff(model.getStateList(),sat(model, ((Not)formula).stateFormula));
      }
      else if(formula instanceof And) {
        return intersection(sat(model, ((And)formula).left), sat(model, ((And)formula).right));
      }
      else if (formula instanceof Or) {
        return union(sat(model, ((Or)formula).left), sat(model, ((Or)formula).right));
      }
      return null;
    }

    public ArrayList<State> setDiff(ArrayList<State> allStates, ArrayList<State> removeStates) {
        allStates.removeAll(removeStates);

        return allStates;
    }
    /* Adapted from https://stackoverflow.com/questions/5283047/intersection-and-union-of-arraylists-in-java */
    public ArrayList<State> union(ArrayList<State> list1, ArrayList<State> list2) {
        HashSet<State> set = new HashSet<State>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<State>(set);
    }
    /* Adapted from https://stackoverflow.com/questions/5283047/intersection-and-union-of-arraylists-in-java */
    public ArrayList<State> intersection(ArrayList<State> list1, ArrayList<State> list2) {
        ArrayList<State> list = new ArrayList<State>();

        for (State t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public ArrayList<State> atomicEval(Model model, String atomLabel) {
      ArrayList<State>trueStates = new ArrayList<State>();
      State[] states = model.getStates();
      for(int i = 0; i < states.length; i++) {
        if(contains(atomLabel,states[i].getLabel())){
          trueStates.add(states[i]);
        }
      }
      return trueStates;
    }

    public boolean contains(String label, String[] labels) {
      for(int i = 0; i < labels.length; i++) {
        if(labels[i].equals(label)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public String[] getTrace() {
        // TODO Auto-generated method stub
        return null;
    }

}
