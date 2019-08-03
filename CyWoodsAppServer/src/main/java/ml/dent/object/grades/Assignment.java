package ml.dent.object.grades;

import ml.dent.json.JsonObject;

public class Assignment {
	private String name;
	private String category;
	private String dateAssigned;
	private String dateDue;
	private String note;
	private String score;
	private double weight;
	private double maxScore;
	private boolean isExtraCredit;

	/**
	 * Although a little overwhelming, the reason there is only one (other than
	 * default) constructor with EVERY value is because the assignment will be
	 * re-instantiated every time the user refreshes.
	 */
	public Assignment(String name, String category, String dateAssigned, String dateDue, String note, String maxScore,
			double weight, String score, boolean isExtraCredit) {
		setName(name);
		setCategory(category);
		setDateAssigned(dateAssigned);
		setDateDue(dateDue);
		setNote(note);
		setWeight(weight);
		setScore(score);
		setMaxScore(maxScore);
		setExtraCredit(isExtraCredit);
	}

	public Assignment() {
		setName("");
		setCategory("");
		setDateAssigned("");
		setDateDue("");
		setNote("");
		setScore("");
		setWeight(Double.NaN);
		setMaxScore("");
		setExtraCredit(false);
	}

	/**
	 * @return A JsonObject with the consolidated data in this assignment.
	 */
	public JsonObject getJsonData() {
		return new JsonObject().add("name", name).add("category", category).add("dateAssigned", dateAssigned)
				.add("dateDue", dateDue).add("score", score).add("weight", weight).add("maxScore", maxScore)
				.add("extraCredit", isExtraCredit).add("note", getNote());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDateAssigned() {
		return dateAssigned;
	}

	public void setDateAssigned(String dateAssigned) {
		this.dateAssigned = dateAssigned;
	}

	public String getDateDue() {
		return dateDue;
	}

	public void setDateDue(String dateDue) {
		this.dateDue = dateDue;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public boolean isExtraCredit() {
		return isExtraCredit;
	}

	public void setExtraCredit(boolean isExtraCredit) {
		this.isExtraCredit = isExtraCredit;
	}

	public double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(String maxScore) {
		try {
			this.maxScore = Double.parseDouble(maxScore);
		} catch (NumberFormatException e) {
			this.maxScore = Double.NaN;
		}
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
