package model;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import modelChecker.ASPair;

import com.google.gson.Gson;

/**
 * A model is consist of states and transitions
 */
public class Model {
    State[] states;
    Transition[] transitions;

    public static Model parseModel(String filePath) throws IOException {
        Gson gson = new Gson();
        Model model = gson.fromJson(new FileReader(filePath), Model.class);
        for (Transition t : model.transitions) {
            System.out.println(t);
            ;
        }
        return model;
    }

    /**
     * Returns the list of the states
     *
     * @return list of state for the given model
     */
    public State[] getStates() {
        return states;
    }

    public ArrayList<State> getStateList() {
      return(new ArrayList<State>(Arrays.asList(states)));
    }

    public ArrayList<ASPair> getTargetsOfState(State state) {
      ArrayList<ASPair> targets = new ArrayList<ASPair>();
      for(int i = 0; i < transitions.length; i++) {
        if(transitions[i].getSource().equals(state.getName())) {
          String[] actions = transitions[i].getActions();
          State targetState = getStateFromName(transitions[i].getTarget());
          targets.add(new ASPair(actions, targetState));
        }
      }
      return targets;
    }

    /**
     * Returns the list of transitions
     *
     * @return list of transition for the given model
     */
    public Transition[] getTransitions() {
        return transitions;
    }

    public State getStateFromName(String name) {
      for (State s : this.getStates()) {
          if (s.getName().equals(name)) {
            return s;
          }
      }
      return null;
    }

}
