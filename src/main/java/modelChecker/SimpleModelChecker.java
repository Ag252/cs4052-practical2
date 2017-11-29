package modelChecker;

import formula.stateFormula.*;
import formula.pathFormula.*;
import model.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class SimpleModelChecker implements ModelChecker {

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
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
      else if (formula instanceof ThereExists) {
        PathFormula pf = ((ThereExists)formula).pathFormula;
        if(pf instanceof Next) {
          return satEX(model, ((Next)pf).stateFormula);
        }
      }
      else if (formula instanceof ForAll) {
        PathFormula pf = ((ForAll)formula).pathFormula;
        if(pf instanceof Next) {
          return satAX(model, ((Next)pf).stateFormula);
        }
      }
      return null;
    }

    public ArrayList<State> satEX(Model model, StateFormula formula) {
      ArrayList<State> validStates = new ArrayList<State>();
      for (State s : model.getStateList()) {
        if(!intersection(post(model, s), sat(model, formula)).isEmpty()) {
          validStates.add(s);
        }
      }
      return validStates;
    }

    public ArrayList<State> satAX(Model model, StateFormula formula) {
      ArrayList<State> validStates = new ArrayList<State>();
      for (State s : model.getStateList()) {
        if(sat(model, formula).containsAll(post(model, s))) {
          validStates.add(s);
        }
      }
      return validStates;
    }



    public ArrayList<State> post(Model model, State state) {
      return model.getTargetsofState(state);
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
      for(State s : states){
        ArrayList<String>stateLabels = new ArrayList<String>(Arrays.asList(s.getLabel()));
        if(stateLabels.contains(atomLabel)){
          trueStates.add(s);
        }
      }

      // for(int i = 0; i < states.length; i++) {
      //   if(contains(atomLabel,states[i].getLabel())){
      //     trueStates.add(states[i]);
      //   }
      // }
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

    public void printArrayList(ArrayList<State> arrayList) {
      System.out.println(Arrays.toString(arrayList.toArray()));
    }

}
