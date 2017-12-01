package modelChecker;

import formula.stateFormula.*;
import formula.pathFormula.*;
import model.*;
import modelChecker.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class SimpleModelChecker implements ModelChecker {

    @Override
    public boolean check(Model model, StateFormula constraint, StateFormula query) {
        return false;
    }

    public ArrayList<State> sat(Model model, StateFormula formula) {
      if(formula instanceof BoolProp) {
        if(((BoolProp)formula).value) {
          return model.getStateList();
        }
        else {
          return (new ArrayList<State>());
        }
      }
      else if(formula instanceof AtomicProp) {
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
          return satEX(model, pf);
        }
        else if (pf instanceof Until) {
          return satEU(model, pf);
        }
        else if (pf instanceof Always) {
          return satEG(model, pf);
        }
        else if (pf instanceof Eventually) {
          //make a new Until pathformula and pass action sets  and b in contstructor
          //then return ACTsatEU on the created Until pathformula
          StateFormula f = ((Eventually)pf).stateFormula;
          Set<String> actionsLeft = ((Eventually)pf).getLeftActions();
          Set<String> actionsRight = ((Eventually)pf).getRightActions();
          Until until = new Until(new BoolProp(true), f, actionsLeft, actionsRight);
           return satEU(model, until);
        }
      }
      else if (formula instanceof ForAll) {
        PathFormula pf = ((ForAll)formula).pathFormula;
        if(pf instanceof Next) {
          return satAX(model, pf);
        }
        else if(pf instanceof Until) {
          return satAU(model, pf);
        }
        else if (pf instanceof Always) {
          StateFormula notPhi = new Not(((Always)pf).stateFormula);
          Set<String> actions = ((Always)pf).getActions();
          PathFormula eventually = new Eventually(notPhi, actions, new HashSet<String>());
          StateFormula thereExists = new ThereExists(eventually);
          StateFormula notThereExists = new Not(thereExists);
          return sat(model, notThereExists);
        }
        else if (pf instanceof Eventually) {
          StateFormula f = ((Eventually)pf).stateFormula;
          Set<String> actionsLeft = ((Eventually)pf).getLeftActions();
          Set<String> actionsRight = ((Eventually)pf).getRightActions();
          Until until = new Until(new BoolProp(true), f, actionsLeft, actionsRight);
          return satAU(model, until);
        }
      }
      return null;
    }

    public ArrayList<State> satAU(Model model, PathFormula formula) {
      Until until = (Until) formula;
      ArrayList<State> satSetUntil = new ArrayList<State>();
      ArrayList<State> satSetPhi1 = sat(model, until.left);
      ArrayList<State> satSetPhi2 = sat(model, until.right);
      Set<String> left = until.getLeftActions();
      Set<String> right = until.getRightActions();

      for(State s : satSetPhi1) {
        BooleanHolder allPathsHold = new BooleanHolder(true);
        recurseOverAUPostStates(left, right, allPathsHold, model, s, s, satSetPhi1, satSetPhi2, new ArrayList<State>(), satSetUntil);
        if(allPathsHold.value) {
          satSetUntil.add(s);
        }
      }
      return satSetUntil;
    }

    public void recurseOverAUPostStates(Set<String> left, Set<String> right, BooleanHolder allPathsHold, Model model, State initState, State currentState, ArrayList<State> satSetPhi1, ArrayList<State> satSetPhi2, ArrayList<State> tempSet, ArrayList<State> satSetUntil) {
      ArrayList<ASPair> postASPairs = post(model, currentState);
      ArrayList<State> validActionPostStatesForActionSetPhi1 = ASPair.forAllStatesWithValidActions(postASPairs, left);
      ArrayList<State> validActionPostStatesForActionSetPhi2 = ASPair.forAllStatesWithValidActions(postASPairs, right);

      ArrayList<State> validActionPostStates = union(validActionPostStatesForActionSetPhi1, validActionPostStatesForActionSetPhi2);

      if(validActionPostStates.isEmpty()) {
        allPathsHold.value = false;
        return;
      }
      for(State p : validActionPostStates) {
        if(tempSet.contains(p)) {
          if(satSetPhi2.contains(p) && validActionPostStatesForActionSetPhi2.contains(p)) {
            continue;
          }
          else {
            allPathsHold.value = false;
            return;
          }
        }
        else if (satSetPhi2.contains(p) && validActionPostStatesForActionSetPhi2.contains(p)) {
          continue;
        }
        else if (satSetPhi1.contains(p) && validActionPostStatesForActionSetPhi1.contains(p)) {
          tempSet.add(p);
          recurseOverAUPostStates(left, right, allPathsHold, model, initState, p, satSetPhi1, satSetPhi2, tempSet, satSetUntil);
        }
        else {
          allPathsHold.value = false;
          return;
        }
      }
    }

    public ArrayList<State> satEG(Model model, PathFormula formula) {
      Always always = (Always) formula;
      ArrayList<State> satSetAlways = new ArrayList<State>();
      ArrayList<State> satSetPhi = sat(model, always.stateFormula);
      Set<String> actions = always.getActions();
      for(State s : satSetPhi) {
        recurseOverEGPostStates(actions, model, s, s, satSetPhi, new ArrayList<State>(), satSetAlways);
      }
      return satSetAlways;
    }

    public void recurseOverEGPostStates(Set<String> actions, Model model, State initState, State currentState, ArrayList<State> satSetPhi, ArrayList<State> tempSet, ArrayList<State> satSetAlways) {
      ArrayList<ASPair> postASPairs = post(model, currentState);
      ArrayList<State> validActionPostStates = ASPair.thereExistsStatesWithValidActions(postASPairs, actions);

      for(State p : validActionPostStates) {
        if(tempSet.contains(p)) {
          satSetAlways.add(initState);
        }
        else if (satSetPhi.contains(p)) {
          tempSet.add(p);
          recurseOverEGPostStates(actions, model, initState, p, satSetPhi, tempSet, satSetAlways);
        }
      }
    }

    public ArrayList<State> satEU(Model model, PathFormula formula) {
      Until until = (Until) formula;
      ArrayList<State> satSetUntil = new ArrayList<State>();
      ArrayList<State> satSetPhi1 = sat(model, until.left);
      ArrayList<State> satSetPhi2 = sat(model, until.right);
      Set<String> left = until.getLeftActions();
      Set<String> right = until.getRightActions();

      for(State s : satSetPhi1) {
        recurseOverEUPostStates(left, right, model, s, s, satSetPhi1, satSetPhi2, new ArrayList<State>(), satSetUntil);
      }
      return satSetUntil;
    }

    public void recurseOverEUPostStates(Set<String> left, Set<String> right, Model model, State initState, State currentState, ArrayList<State> satSetPhi1, ArrayList<State> satSetPhi2, ArrayList<State> tempSet, ArrayList<State> satSetUntil) {
      ArrayList<ASPair> postASPairs = post(model, currentState);
      ArrayList<State> validActionPostStatesForActionSetPhi1 = ASPair.thereExistsStatesWithValidActions(postASPairs, left);
      ArrayList<State> validActionPostStatesForActionSetPhi2 = ASPair.thereExistsStatesWithValidActions(postASPairs, right);

      ArrayList<State> validActionPostStates = union(validActionPostStatesForActionSetPhi1, validActionPostStatesForActionSetPhi2);

      for(State p : validActionPostStates) {
        if(satSetUntil.contains(initState)) {
          continue;
        }
        else if(tempSet.contains(p)) {
          if(satSetPhi2.contains(p) && validActionPostStatesForActionSetPhi2.contains(p)) {
            satSetUntil.add(initState);
            return;
          }
        }
        else if (satSetPhi2.contains(p) && validActionPostStatesForActionSetPhi2.contains(p)) {
          satSetUntil.add(initState);
          return;
        }
        else if (satSetPhi1.contains(p) && validActionPostStatesForActionSetPhi1.contains(p)) {
          tempSet.add(p);
          recurseOverEUPostStates(left, right, model, initState, p, satSetPhi1, satSetPhi2, tempSet, satSetUntil);
        }
      }
    }

    public ArrayList<State> satEX(Model model, PathFormula formula) {
      Next next = (Next) formula;
      Set<String> actions = next.getActions();
      ArrayList<State> validStates = new ArrayList<State>();

      for (State s : model.getStateList()) {
        ArrayList<ASPair> postASPairs = post(model, s);
        ArrayList<State> validActionPostStates = ASPair.thereExistsStatesWithValidActions(postASPairs, actions);
        if(!intersection(validActionPostStates, sat(model, next.stateFormula)).isEmpty()) {
          validStates.add(s);
        }
      }
      return validStates;
    }

    public ArrayList<State> satAX(Model model, PathFormula formula) {
      Next next = (Next) formula;
      Set<String> actions = next.getActions();
      ArrayList<State> validStates = new ArrayList<State>();

      for (State s : model.getStateList()) {
        ArrayList<ASPair> postASPairs = post(model, s);
        ArrayList<State> validActionPostStates = ASPair.forAllStatesWithValidActions(postASPairs, actions);
        if(!validActionPostStates.isEmpty() && sat(model, next.stateFormula).containsAll(validActionPostStates)) {
          validStates.add(s);
        }
      }
      return validStates;
    }

    public ArrayList<ASPair> post(Model model, State state) {
      return model.getTargetsOfState(state);
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
