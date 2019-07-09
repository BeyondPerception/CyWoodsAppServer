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
	private double rank; // This is really an int, but it may not exist.
	private double total; // This is really an int, but it may not exist.
	private ArrayList<Block> blocks;

	public Transcript() {
		gpa = Double.NaN;
		rank = Double.NaN;
		total = Double.NaN;
		blocks = new ArrayList<>();
	}

	public JsonObject getJsonData() {
		JsonArray list = new JsonArray();
		for (Block b : blocks) {
			list.add(b.getJsonData());
		}

		return new JsonObject().add("gpa", gpa).add("rank", rank).add("totalPeople", total).add("years", list);
	}

	/**
	 * Each block is representative of a single year of schooling on the transcript,
	 * the "blocks" you see on the transcript page on HAC is the same block here.
	 */
	public class Block {
		private String year;
		private String building;
		private int grade;
		private double totalCredit;
		private ArrayList<Course> courses;

		public Block() {
			year = null;
			building = null;
			grade = -1;
			totalCredit = Double.NaN;
			courses = new ArrayList<>();
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
		public class Course {
			private String name;
			private String courseNum;
			private double sem1; // This is really an int, but it may not exist.
			private double sem2; // This is really an int, but it may not exist.
			private double credit;

			public Course() {
				setName(null);
				setCourseNum(null);
				setSem1(Double.NaN);
				setSem2(Double.NaN);
				setCredit(Double.NaN);
			}

			public JsonObject getJsonData() {
				return new JsonObject().add("course", courseNum).add("description", name).add("sem1", sem1)
						.add("sem2", sem2).add("credit", credit);
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getCourseNum() {
				return courseNum;
			}

			public void setCourseNum(String courseNum) {
				this.courseNum = courseNum;
			}

			public double getSem1() {
				return sem1;
			}

			public void setSem1(double sem1) {
				this.sem1 = sem1;
			}

			public double getSem2() {
				return sem2;
			}

			public void setSem2(double sem2) {
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
