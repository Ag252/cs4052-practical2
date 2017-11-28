package model;

import java.util.Arrays;

/**
 *
 * */
public class State {
    private boolean init;
    private String name;
    private String [] label;

    /**
     * Is state an initial state
     * @return boolean init
     * */
    public boolean isInit() {
	return init;
    }

    /**
     * Returns the name of the state
     * @return String name
     * */
    public String getName() {
	return name;
    }

    /**
     * Returns the labels of the state
     * @return Array of string labels
     * */
    public String[] getLabel() {
	return label;
    }

    public String toString() {
      String labels = Arrays.toString(label);
      return "init: " + init + " name: " + name + " labels: " + labels;
    }

}
