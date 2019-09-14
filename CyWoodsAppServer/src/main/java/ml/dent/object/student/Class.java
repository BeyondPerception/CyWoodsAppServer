package ml.dent.object.student;

import java.util.ArrayList;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

public class Class {
	private String name;
	private String id; // Same class, different casing, ik its dumb
	private Teacher teacher;
	private ArrayList<Assignment> assigns;
	private double grade;
	// For use in navigating HAC; should not be sent to client
	private int HAC_id;
	private int quarter; // just in case quarters vary between classes.

	// Weights Stuff
	private String cfuName; // Daily Grades
	private String raName; // Assessment Grades
	private String saName; // Test Grades
	private String CFUPoints;
	private String RAPoints;
	private String SAPoints;

	private double cfuWeight;
	private double raWeight;
	private double saWeight;

	public Class() {
		setName("");
		setTeacher(new Teacher());
		setGrade(Double.NaN);
		setAssigns(new ArrayList<Assignment>());
		setQuarter(1);
		setCfuName("DG");
		setRaName("AS");
		setSaName("MG");
		setCfuWeight(Double.NaN);
		setRaWeight(Double.NaN);
		setSaWeight(Double.NaN);
	}

	public Class(String n, String id) {
		this();
		setName(n);
		setId(id);
	}

	public Class(String n, String id, String t, ArrayList<Assignment> a) {
		this(n, id);
		setAssigns(a);
	}

	/**
	 * Some people don't have 7 classes, but we still need some kind of filler to
	 * buffer later classes. (e.g. If someone only has 6 classes, their 2nd period
	 * would show up as their 1st period and we don't want that). We'll fill that
	 * empty period with this class.
	 */
	public static Class getFillerClass() {
		return new Class("", "", "", new ArrayList<Assignment>());
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject().add("name", name).add("grade", getGrade())
				.add("categoryPoints",
						new JsonObject().add(cfuName, CFUPoints).add(raName, RAPoints).add(saName, SAPoints))
				.add("weights", new JsonObject().add(cfuName, cfuWeight).add(raName, raWeight).add(saName, saWeight))
				.add("teacher", teacher.getJsonData());
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

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public String getCFUPoints() {
		return CFUPoints;
	}

	public void setCFUPoints(String cFUWeight) {
		CFUPoints = cFUWeight;
	}

	public String getRAPoints() {
		return RAPoints;
	}

	public void setRAPoints(String rAWeight) {
		RAPoints = rAWeight;
	}

	public String getSAPoints() {
		return SAPoints;
	}

	public void setSAPoints(String sAWeight) {
		SAPoints = sAWeight;
	}

	public String getCfuName() {
		return cfuName;
	}

	public void setCfuName(String cfuName) {
		this.cfuName = cfuName;
	}

	public String getRaName() {
		return raName;
	}

	public void setRaName(String raName) {
		this.raName = raName;
	}

	public String getSaName() {
		return saName;
	}

	public void setSaName(String saName) {
		this.saName = saName;
	}

	public double getCfuWeight() {
		return cfuWeight;
	}

	public void setCfuWeight(double cfuWeight) {
		this.cfuWeight = cfuWeight;
	}

	public double getRaWeight() {
		return raWeight;
	}

	public void setRaWeight(double raWeight) {
		this.raWeight = raWeight;
	}

	public double getSaWeight() {
		return saWeight;
	}

	public void setSaWeight(double saWeight) {
		this.saWeight = saWeight;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
