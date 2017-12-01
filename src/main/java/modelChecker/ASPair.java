package modelChecker;

import model.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

public class ASPair {
  public String[] actions;
  public State state;

  public ASPair(String[] actions, State state) {
    this.actions = actions;
    this.state = state;
  }

  public static ArrayList<State> thereExistsStatesWithValidActions(ArrayList<ASPair> asPairs, Set<String> actions) {
    ArrayList<State> validStates = new ArrayList<State>();
    for(ASPair a : asPairs) {
      ArrayList<String> stateActions = new ArrayList<String>(Arrays.asList(a.actions));
      ArrayList<String> constraintActions = new ArrayList<String>(actions);


      stateActions.retainAll(constraintActions);

      if(!stateActions.isEmpty() || constraintActions.isEmpty()) {
        validStates.add(a.state);
      }
    }
    return validStates;
  }

  public static ArrayList<State> forAllStatesWithValidActions(ArrayList<ASPair> asPairs, Set<String> actions) {
    ArrayList<State> validStates = new ArrayList<State>();
    for(ASPair a : asPairs) {
      ArrayList<String> stateActions = new ArrayList<String>(Arrays.asList(a.actions));
      ArrayList<String> constraintActions = new ArrayList<String>(actions);

      if(constraintActions.containsAll(stateActions) || constraintActions.isEmpty()) {
        validStates.add(a.state);
      }

    }
    return validStates;
  }
}
