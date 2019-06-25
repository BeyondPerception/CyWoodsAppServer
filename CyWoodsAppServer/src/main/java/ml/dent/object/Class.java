package ml.dent.object;

import java.util.ArrayList;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

public class Class {
	private String name;
	private Teacher teacher;
	private ArrayList<Assignment> assigns;
	private double grade;
	private int HAC_id; // For use in navigating HAC; should not be sent to client

	public Class() {
		setName("");
		setTeacher(new Teacher());
		setGrade(Double.NaN);
		setAssigns(new ArrayList<Assignment>());
	}

	public Class(String n) {
		setName(n);
		setTeacher(new Teacher());
		setGrade(Double.NaN);
		setAssigns(new ArrayList<Assignment>());
	}

	public Class(String n, String t, ArrayList<Assignment> a) {
		setName(n);
		setTeacher(new Teacher());
		setGrade(Double.NaN);
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
		JsonObject res = new JsonObject().add("name", name).add("teacher", teacher.getJsonData()).add("grade",
				getGrade());
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

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public double getGrade() {
		return grade;
	}

	public void setGrade(double grade) {
		this.grade = grade;
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

	public int getHAC_id() {
		return HAC_id;
	}

	public void setHAC_id(int hAC_id) {
		HAC_id = hAC_id;
	}
}
