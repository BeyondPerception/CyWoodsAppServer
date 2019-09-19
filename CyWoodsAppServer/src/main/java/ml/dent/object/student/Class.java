package ml.dent.object.student;

import java.util.HashSet;
import java.util.Stack;
import java.util.TreeSet;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

public class Class {
	private String name;
	private String id; // Same class, different casing, ik its dumb
	private Teacher teacher;
	private Stack<Assignment> assigns; // So new assignments show up first
	private double grade;
	// For use in navigating HAC; should not be sent to client
	private int HAC_id;
	private int quarter; // just in case quarters vary between classes.

	// Weights Stuff
	private TreeSet<Category> categoryPoints;

	public Class() {
		setName("");
		setTeacher(new Teacher());
		setGrade(Double.NaN);
		setAssigns(new Stack<Assignment>());
		setQuarter(1);
		categoryPoints = new TreeSet<>();
	}

	public Class(String n, String id) {
		this();
		setName(n);
		setId(id);
	}

	public Class(String n, String id, String t, Stack<Assignment> a) {
		this(n, id);
		setAssigns(a);
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject().add("name", name).add("grade", getGrade()).add("teacher",
				teacher.getJsonData());

		if (categoryPoints.size() < 3) {
			Category dg = new Category("DG", null, Double.NaN);
			Category as = new Category("AS", null, Double.NaN);
			Category mg = new Category("MG", null, Double.NaN);

			if (!categoryPoints.contains(dg)) {
				categoryPoints.add(dg);
			}
			if (!categoryPoints.contains(as)) {
				categoryPoints.add(as);
			}
			if (!categoryPoints.contains(mg)) {
				categoryPoints.add(mg);
			}
		}

		JsonObject categoryContainer = new JsonObject();
		JsonObject weightsContainer = new JsonObject();
		for (Category val : categoryPoints) {
			categoryContainer.add(val.getName(), val.getPoints());
			weightsContainer.add(val.getName(), val.getWeight());
		}
		res.add("categoryPoints", categoryContainer);
		res.add("weights", weightsContainer);

		JsonArray assignments = new JsonArray();
		while (!assigns.isEmpty()) {
			Assignment a = assigns.pop();
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

	public Stack<Assignment> getAssigns() {
		return assigns;
	}

	public void setAssigns(Stack<Assignment> assigns) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addCategory(String name, String points, double weight) {
		categoryPoints.add(new Category(name, points, weight));
	}

	// Below this line may seem like an overly complex way to store categories,
	// because it is.
	// The reason we are storing them like this is because different classes may
	// have different names for each category, which is very annoying because the
	// frontend expects to receive 3 categories with no duplicates. This is the only
	// way I could think of doing that.
	public enum Type {
		DAILY_GRADE, ASSESMENT_GRADE, MAJOR_GRADE, UNKNOWN
	}

	private static HashSet<String> dailyNames;
	private static HashSet<String> assesmentNames;
	private static HashSet<String> majorNames;

	static {
		dailyNames = new HashSet<>();
		assesmentNames = new HashSet<>();
		majorNames = new HashSet<>();

		dailyNames.add("CFU");
		dailyNames.add("DG");

		assesmentNames.add("RA");
		assesmentNames.add("AS");

		majorNames.add("SA");
		majorNames.add("SU");
		majorNames.add("MG");
		majorNames.add("TE");
	}

	private class Category implements Comparable<Category> {
		private String name;
		private String points;
		private double weight;
		private int order; // 1, 2, or 3, tells the json assembler where to put this class
		private Type type;

		public Category(String name, String points, double weight) {
			name = name.toUpperCase();
			this.name = name;
			this.points = points;
			this.weight = weight;

			if (dailyNames.contains(name)) {
				type = Type.DAILY_GRADE;
			} else if (assesmentNames.contains(name)) {
				type = Type.ASSESMENT_GRADE;
			} else if (majorNames.contains(name)) {
				type = Type.MAJOR_GRADE;
			} else {
				type = Type.UNKNOWN;
			}

			if (type == Type.DAILY_GRADE) {
				order = 1;
			} else if (type == Type.ASSESMENT_GRADE) {
				order = 2;
			} else if (type == Type.MAJOR_GRADE) {
				order = 3;
			} else {
				order = 4;
			}
		}

		public String getName() {
			return name;
		}

		public String getPoints() {
			return points;
		}

		public double getWeight() {
			return weight;
		}

		@Override
		public int compareTo(Category o) {
			if (type == o.type) {
				return 0;
			}
			return Integer.compare(order, o.order);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Category) {
				Category o = (Category) obj;
				return compareTo(o) == 0;
			}
			return false;
		}
	}
}
