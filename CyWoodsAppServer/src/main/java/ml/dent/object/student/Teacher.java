package ml.dent.object.student;

import ml.dent.json.JsonObject;

public class Teacher {
	private String name;
	private String email;
	private String website;

	public Teacher(String n, String e, String w) {
		setName(n);
		setEmail(e);
		setWebsite(w);
	}

	public Teacher() {
		name = "";
		email = "";
		website = null;
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject().add("name", name).add("email", email);
		if (website != null) {
			res.add("website", website);
		}
		return res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}
