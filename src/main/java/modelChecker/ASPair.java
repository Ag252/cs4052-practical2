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
      ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(a.actions));
      ArrayList<String> list2 = new ArrayList<String>(actions);

      list1.retainAll(list2);

      if(!list1.isEmpty()) {
        validStates.add(a.state);
      }
    }
    return validStates;
  }
}
