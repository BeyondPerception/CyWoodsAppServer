package ml.dent.object;

import java.util.ArrayList;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

/**
 * Out of all the object classes, this one is the most convoluted(sorry). But
 * this was the best way I could think of formatting the transcript without it
 * getting too messy.
 * 
 * @author Ronak Malik
 *
 */
public class Transcript {
	private double gpa; // This is really an int, but it may not exist.
	private String rank; // This is a string in the format of \d+/\d+
	private ArrayList<Block> blocks;

	public Transcript() {
		setGpa(Double.NaN);
		setRank(null);
		blocks = new ArrayList<>();
	}

	public JsonObject getJsonData() {
		JsonArray list = new JsonArray();
		for (Block b : blocks) {
			list.add(b.getJsonData());
		}

		return new JsonObject().add("gpa", getGpa()).add("rank", getRank()).add("years", list);
	}

	public void addBlock(Block b) {
		blocks.add(b);
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public double getGpa() {
		return gpa;
	}

	public void setGpa(double gpa) {
		this.gpa = gpa;
	}

	/**
	 * Each block is representative of a single year of schooling on the transcript,
	 * the "blocks" you see on the transcript page on HAC is the same block here.
	 */
	public static class Block {
		private String year;
		private String building;
		private String grade;
		private double totalCredit;
		private ArrayList<Course> courses;

		public Block() {
			year = null;
			building = null;
			grade = null;
			totalCredit = Double.NaN;
			courses = new ArrayList<>();
		}

		public Block(String y, String b, String g, double tC, ArrayList<Course> c) {
			year = y;
			building = b;
			grade = g;
			totalCredit = tC;
			courses = c;
		}

		public JsonObject getJsonData() {
			JsonArray list = new JsonArray();
			for (Course c : courses) {
				list.add(c.getJsonData());
			}

			return new JsonObject().add("year", year).add("building", building).add("grade", grade)
					.add("totalCredit", totalCredit).add("courses", list);
		}

		/**
		 * A bit more straightforward, a Course is just a row in one of the blocks.
		 * Again on the Transcript page on HAC, a row in one of the blocks is
		 * represented by this class.
		 */
		public static class Course {
			private String description;
			private String courseNum;
			private String sem1; // This is really an int, but it may not exist.
			private String sem2; // This is really an int, but it may not exist.
			private double credit;

			public Course() {
				setName(null);
				setCourseNum(null);
				setSem1(null);
				setSem2(null);
				setCredit(Double.NaN);
			}

			public Course(String name, String courseNum, String sem1, String sem2, double credit) {
				setName(name);
				setCourseNum(courseNum);
				setSem1(sem1);
				setSem2(sem2);
				setCredit(credit);
			}

			public JsonObject getJsonData() {
				return new JsonObject().add("course", courseNum).add("description", description).add("sem1", sem1)
						.add("sem2", sem2).add("credit", credit);
			}

			public String getName() {
				return description;
			}

			public void setName(String name) {
				this.description = name;
			}

			public String getCourseNum() {
				return courseNum;
			}

			public void setCourseNum(String courseNum) {
				this.courseNum = courseNum;
			}

			public String getSem1() {
				return sem1;
			}

			public void setSem1(String sem1) {
				this.sem1 = sem1;
			}

			public String getSem2() {
				return sem2;
			}

			public void setSem2(String sem2) {
				this.sem2 = sem2;
			}

			public double getCredit() {
				return credit;
			}

			public void setCredit(double credit) {
				this.credit = credit;
			}
		}
	}
}