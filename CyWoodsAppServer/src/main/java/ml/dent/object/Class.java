package ml.dent.object;

import java.util.ArrayList;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

public class Class {
	private String name;
	private String teacher;
	private ArrayList<Assignment> assigns;

	public Class() {
		setName("");
		setTeacher("");
		setAssigns(new ArrayList<Assignment>());
	}

	public Class(String n, String t) {
		setName(n);
		setTeacher(t);
		setAssigns(new ArrayList<Assignment>());
	}

	public Class(String n, String t, ArrayList<Assignment> a) {
		setName(n);
		setTeacher(t);
		setAssigns(a);
	}

	/**
	 * Some people don't have 7 classes, but we still need some kind of filler to
	 * buffer later classes. (e.g. If someone only has 6 classes, their 2nd period
	 * would show up as their 1st period and we don't want that). We'll fill that
	 * empty period with this class.
	 */
	public static Class getFillerClass() {
		return new Class("", "", new ArrayList<Assignment>());
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject().add("name", name).add("teacher", teacher);
		JsonArray assignments = new JsonArray();
		for (Assignment a : assigns) {
			assignments.add(a.getJsonData());
		}
		res.add("assignments", assignments);
		return res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public ArrayList<Assignment> getAssigns() {
		return assigns;
	}

	public void setAssigns(ArrayList<Assignment> assigns) {
		this.assigns = assigns;
	}

	public void addAssign(Assignment a) {
		assigns.add(a);
	}
}
