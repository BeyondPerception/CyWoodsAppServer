package ml.dent.object.student;

import java.util.LinkedHashMap;
import java.util.Set;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

/**
 * Learning from past experiences, we want to make this app as robust as
 * possible. To do this, we can't assume anything. Not everyone has 7 classes,
 * not everyone has teachers, not everyone has a first and last name, etc.
 * 
 * Make everything as flexible as possible.
 * 
 * @author Ronak Malik
 */
public class Student {
	private String name;
	private String username; // This is kept private and should NOT be put in the JSON data.
	private String password; // This is kept private and should NOT be put in the JSON data.

	// This is an ordered list, the first element corresponds to first
	// period. Making it a map with the names of the classes and the actual class
	// object makes it easier on us in the future.
	private LinkedHashMap<String, Class> classes;
	private Transcript transcript;
	private Attendance attendance;

	public Student(String username, String password) {
		this.username = username;
		this.password = password;
		classes = new LinkedHashMap<>();
		transcript = new Transcript();
		attendance = new Attendance();
	}

	public Student(String name, String username, String password) {
		this.setName(name);
		this.setUsername(username);
		this.setPassword(password);
		classes = new LinkedHashMap<>();
		transcript = new Transcript();
		attendance = new Attendance();
	}

	public Student(String name, String username, String password, LinkedHashMap<String, Class> classes) {
		this.setName(name);
		this.setUsername(username);
		this.setPassword(password);
		this.classes = classes;
		transcript = new Transcript();
		attendance = new Attendance();
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject().add("name", name);
		JsonArray classArray = new JsonArray();
		for (String s : classes.keySet()) {
			Class c = classes.get(s);
			classArray.add(c.getJsonData());
		}
		res.add("classes", classArray).add("transcript", transcript.getJsonData()).add("attendance",
				attendance.getJsonData());
		return res;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void addClass(Class c) {
		classes.put(c.getName(), c);
	}

	public void addClass(String s, String id) {
		classes.put(id, new Class(s, id));
	}

	public Class getClass(String s) {
		return classes.get(s);
	}

	public Set<String> getClassList() {
		return classes.keySet();
	}

	public Transcript getTranscript() {
		return transcript;
	}

	public Attendance getAttendance() {
		return attendance;
	}
}
