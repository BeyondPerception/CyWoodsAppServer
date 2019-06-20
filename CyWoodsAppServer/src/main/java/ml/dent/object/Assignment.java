package ml.dent.object;

import ml.dent.json.JsonObject;

public class Assignment {
	private String name;
	private String category;
	private String dateAssigned;
	private String dateDue;
	private double score;
	private double weight;
	private boolean isExtraCredit;
	private double maxScore;

	/**
	 * Although a little overwhelming, the reason there is only one constructor with
	 * EVERY value is because the assignment will be re-instantiated every time the
	 * user refreshes.
	 */
	public Assignment(String name, String category, String dateAssigned, String dateDue, double maxScore, double weight,
			double score, boolean isExtraCredit) {
		this.setName(name);
		this.setCategory(category);
		this.setDateAssigned(dateAssigned);
		this.setDateDue(dateDue);
		this.setWeight(weight);
		this.setScore(score);
		this.setMaxScore(maxScore);
		this.setExtraCredit(isExtraCredit);
	}

	/**
	 * @return A JsonObject with the consolidated data in this assignment.
	 */
	public JsonObject getJsonData() {
		return new JsonObject().add("name", name)
				.add("category", category)
				.add("Date Assigned", dateAssigned)
				.add("Date Due", dateDue)
				.add("score", score)
				.add("weight", weight)
				.add("maxScore", maxScore)
				.add("Extra Credit", isExtraCredit);
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

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
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

	public void setMaxScore(double maxScore) {
		this.maxScore = maxScore;
	}
}
