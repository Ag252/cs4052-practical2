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
          return satEX(model, ((Next)pf).stateFormula);
        }
        else if (pf instanceof Until) {
          Until until = (Until) pf;
          return satEU(model, until.left, until.right);
        }
        else if (pf instanceof Always) {
          return satEG(model, pf);
        }
        else if (pf instanceof Eventually) {
          return satEU(model, new BoolProp(true), ((Eventually)pf).stateFormula);
        }
      }
      else if (formula instanceof ForAll) {
        PathFormula pf = ((ForAll)formula).pathFormula;
        if(pf instanceof Next) {
          return satAX(model, ((Next)pf).stateFormula);
        }
        else if(pf instanceof Until) {
          return satAU(model, pf);
          //Until until = (Until) pf;

          // StateFormula notPhi1 = new Not(until.left);
          // StateFormula notPhi2 = new Not(until.right);
          // StateFormula and = new And(notPhi1, notPhi2);
          //
          // PathFormula untilAnd = new Until(notPhi2, and);
          // PathFormula alwaysNot = new Always(notPhi2);
          //
          // StateFormula existsUntil = new ThereExists(untilAnd);
          // StateFormula existsAlwaysNot = new ThereExists(alwaysNot);
          // StateFormula or = new Or(existsUntil, existsAlwaysNot);
          // StateFormula notOr = new Not(or);

          // return sat(model, notOr);
        }
        else if (pf instanceof Always) {
          StateFormula notPhi = new Not(((Always)pf).stateFormula);
          PathFormula eventually = new Eventually(notPhi);
          StateFormula thereExists = new ThereExists(eventually);
          StateFormula notThereExists = new Not(thereExists);
          return sat(model, notThereExists);
        }
        else if (pf instanceof Eventually) {
          return satAU(model, new Until(new BoolProp(true), ((Eventually)pf).stateFormula));
        }
      }
      return null;
    }

    public ArrayList<State> satActions(Model model, StateFormula formula) {
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
        return setDiff(model.getStateList(),satActions(model, ((Not)formula).stateFormula));
      }
      else if(formula instanceof And) {
        return intersection(satActions(model, ((And)formula).left), satActions(model, ((And)formula).right));
      }
      else if (formula instanceof Or) {
        return union(satActions(model, ((Or)formula).left), satActions(model, ((Or)formula).right));
      }
      else if (formula instanceof ThereExists) {
        PathFormula pf = ((ThereExists)formula).pathFormula;
        if(pf instanceof Next) {
          return ACTsatEX(model, pf);
        }
        else if (pf instanceof Until) {
          return ACTsatEU(model, pf);
        }
        // else if (pf instanceof Always) {
        //   return satEG(model, pf);
        // }
        // else if (pf instanceof Eventually) {
        //   return satEU(model, new BoolProp(true), ((Eventually)pf).stateFormula);
        // }
      }
      else if (formula instanceof ForAll) {
        PathFormula pf = ((ForAll)formula).pathFormula;
        if(pf instanceof Next) {
          return ACTsatAX(model, pf);
        }
      }
      //   else if(pf instanceof Until) {
      //     return satAU(model, pf);
      //     //Until until = (Until) pf;
      //
      //     // StateFormula notPhi1 = new Not(until.left);
      //     // StateFormula notPhi2 = new Not(until.right);
      //     // StateFormula and = new And(notPhi1, notPhi2);
      //     //
      //     // PathFormula untilAnd = new Until(notPhi2, and);
      //     // PathFormula alwaysNot = new Always(notPhi2);
      //     //
      //     // StateFormula existsUntil = new ThereExists(untilAnd);
      //     // StateFormula existsAlwaysNot = new ThereExists(alwaysNot);
      //     // StateFormula or = new Or(existsUntil, existsAlwaysNot);
      //     // StateFormula notOr = new Not(or);
      //
      //     // return sat(model, notOr);
      //   }
      //   else if (pf instanceof Always) {
      //     StateFormula notPhi = new Not(((Always)pf).stateFormula);
      //     PathFormula eventually = new Eventually(notPhi);
      //     StateFormula thereExists = new ThereExists(eventually);
      //     StateFormula notThereExists = new Not(thereExists);
      //     return satActions(model, notThereExists);
      //   }
      //   else if (pf instanceof Eventually) {
      //     return satAU(model, new Until(new BoolProp(true), ((Eventually)pf).stateFormula));
      //   }
      // }
      return null;
    }

    public ArrayList<State> satAU(Model model, PathFormula formula) {
      Until until = (Until) formula;
      ArrayList<State> satSetUntil = new ArrayList<State>();
      ArrayList<State> satSetPhi1 = sat(model, until.left);
      ArrayList<State> satSetPhi2 = sat(model, until.right);
      for(State s : satSetPhi1) {
        BooleanHolder allPathsHold = new BooleanHolder(true);
        recurseOverAUPostStates(allPathsHold, model, s, s, satSetPhi1, satSetPhi2, new ArrayList<State>(), satSetUntil);
        if(allPathsHold.value) {
          satSetUntil.add(s);
        }
      }
      return satSetUntil;
    }

    public void recurseOverAUPostStates(BooleanHolder allPathsHold, Model model, State initState, State currentState, ArrayList<State> satSetPhi1, ArrayList<State> satSetPhi2, ArrayList<State> tempSet, ArrayList<State> satSetUntil) {
      ArrayList<State> postStates = post(model, currentState);
      for(State p : postStates) {
        if(tempSet.contains(p)) {
          if(satSetPhi2.contains(p)) {
            continue;
          }
          else {
            allPathsHold.value = false;
            return;
          }
        }
        else if (satSetPhi2.contains(p)) {
          continue;
        }
        else if (satSetPhi1.contains(p)) {
          tempSet.add(p);
          recurseOverAUPostStates(allPathsHold, model, initState, p, satSetPhi1, satSetPhi2, tempSet, satSetUntil);
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
      for(State s : satSetPhi) {
        recurseOverEGPostStates(model, s, s, satSetPhi, new ArrayList<State>(), satSetAlways);
      }
      return satSetAlways;
    }

    public void recurseOverEGPostStates(Model model, State initState, State currentState, ArrayList<State> satSetPhi, ArrayList<State> tempSet, ArrayList<State> satSetAlways) {
      ArrayList<State> postStates = post(model, currentState);
      for(State p : postStates) {
        if(tempSet.contains(p)) {
          satSetAlways.add(initState);
        }
        else if (satSetPhi.contains(p)) {
          tempSet.add(p);
          recurseOverEGPostStates(model, initState, p, satSetPhi, tempSet, satSetAlways);
        }
      }
    }

    public ArrayList<State> satEU(Model model, StateFormula left, StateFormula right) {
      ArrayList<State> satSetUntil = new ArrayList<State>();
      ArrayList<State> satSetPhi1 = sat(model, left);
      ArrayList<State> satSetPhi2 = sat(model, right);
      for(State s : satSetPhi2) {
        recurseOverPreStates(model, s, satSetPhi1, satSetUntil);
      }
      return satSetUntil;
    }

    public ArrayList<State> ACTsatEU(Model model, PathFormula formula) {
      Until until = (Until) formula;
      ArrayList<State> satSetUntil = new ArrayList<State>();
      ArrayList<State> satSetPhi1 = satActions(model, until.left);
      ArrayList<State> satSetPhi2 = satActions(model, until.right);
      Set<String> left = until.getLeftActions();
      Set<String> right = until.getRightActions();

      for(State s : satSetPhi1) {
        recurseOverEUPostStates(left, right, model, s, s, satSetPhi1, satSetPhi2, new ArrayList<State>(), satSetUntil);
      }
      return satSetUntil;
    }

    public void recurseOverEUPostStates(Set<String> left, Set<String> right, Model model, State initState, State currentState, ArrayList<State> satSetPhi1, ArrayList<State> satSetPhi2, ArrayList<State> tempSet, ArrayList<State> satSetUntil) {
      ArrayList<ASPair> postASPairs = ACTpost(model, currentState);
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

    public void recurseOverPreStates(Model model, State s, ArrayList<State> satSetPhi1, ArrayList<State> satSetUntil){
	     ArrayList<State> preStates = pre(model, s);
      	for(State p : preStates) {
      		if(satSetUntil.contains(p)) {
      			continue;
      		}
      		else if(satSetPhi1.contains(p)) {
      			satSetUntil.add(p);
      			recurseOverPreStates(model, p, satSetPhi1, satSetUntil);
          }
        }
    }

    public ArrayList<State> pre(Model model, State s) {
      return model.getSourcesOfState(s);
    }

    public ArrayList<State> ACTsatEX(Model model, PathFormula formula) {
      Next next = (Next) formula;
      Set<String> actions = next.getActions();
      ArrayList<State> validStates = new ArrayList<State>();

      for (State s : model.getStateList()) {
        ArrayList<ASPair> postASPairs = ACTpost(model, s);
        ArrayList<State> validActionPostStates = ASPair.thereExistsStatesWithValidActions(postASPairs, actions);
        if(!intersection(validActionPostStates, satActions(model, next.stateFormula)).isEmpty()) {
          validStates.add(s);
        }
      }
      return validStates;
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

    public ArrayList<State> ACTsatAX(Model model, PathFormula formula) {
      Next next = (Next) formula;
      Set<String> actions = next.getActions();
      ArrayList<State> validStates = new ArrayList<State>();

      for (State s : model.getStateList()) {
        ArrayList<ASPair> postASPairs = ACTpost(model, s);
        ArrayList<State> validActionPostStates = ASPair.forAllStatesWithValidActions(postASPairs, actions);
        if(!validActionPostStates.isEmpty() && satActions(model, next.stateFormula).containsAll(validActionPostStates)) {
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

    public ArrayList<ASPair> ACTpost(Model model, State state) {
      return model.ACTgetTargetsOfState(state);
    }

    public ArrayList<State> post(Model model, State state) {
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
