package ml.dent.web;

import java.util.ArrayList;

import ml.dent.object.student.Teacher;

public class FacultyFetcher extends AbstractFetcher {
	private static final String FACULTY_URL = "https://app.cfisd.net/urlcap/campus_list_011.html";

	private ArrayList<Teacher> faculty;

	private boolean fetched;

	public FacultyFetcher() {
		fetched = false;
		faculty = new ArrayList<Teacher>();
	}

	public ArrayList<Teacher> getFaculty() throws IllegalStateException {
		if (!fetched) {
			throw new IllegalStateException("fetchFaculty must be called first");
		}
		return faculty;
	}

	public void fetchFaculty() {

	}

}
