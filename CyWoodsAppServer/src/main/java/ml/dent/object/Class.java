package ml.dent.object;

import java.util.ArrayList;

public class Class {
	private String name;
	private Teacher teacher;
	private ArrayList<Assignment> assigns;

	public Class() {
		name = "";
		assigns = new ArrayList<Assignment>();
	}

	public Class(String n) {
		name = n;
		assigns = new ArrayList<Assignment>();
	}
	
}
