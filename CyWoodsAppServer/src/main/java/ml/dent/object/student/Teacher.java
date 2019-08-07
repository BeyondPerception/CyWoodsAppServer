package ml.dent.object.student;

import ml.dent.json.JsonObject;

public class Teacher {
	private String name;
	private String email;

	public Teacher(String n, String e) {
		setName(n);
		setEmail(e);
	}

	public Teacher() {
		name = "";
		email = "";
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("name", name).add("email", email);
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

}
