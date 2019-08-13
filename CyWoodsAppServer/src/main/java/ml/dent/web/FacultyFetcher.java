package ml.dent.web;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

	public void fetchFaculty() throws IOException {
		// Get the faculty page
		Document facultyPage = getDocument(FACULTY_URL);

		// Select the table that has all the rows, then get each row into a list to loop
		// through
		Elements table = facultyPage.selectFirst(
				"body > center:nth-child(2) > center:nth-child(3) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1)")
				.select("td");

		for (Element td : table) {
			// For each row, each faculty member is contained within a span
			Elements names = td.select("span");

			for (Element name : names) {
				String test = name.text();
				if (test.length() == 1) {
					// If the text length is one, then it is indexing the alphabetical identifier
					continue;
				}

				// The faculty member's info is stored in a series of hyperlinks
				Elements info = name.select("a");

				if (info.size() == 1) {
					// We know for sure that all faculty members will have an email, becuase cfisd
					// gives them one, but if they don't have a website, then the email will be the
					// first thing on the list.

					String facultyName = info.text(); // The only actual text is the faculty's name;
					String email = info.get(0).toString();
					email = email.substring(email.indexOf("mailto:") + 7);
					email = email.substring(0, email.indexOf("\""));
					faculty.add(new Teacher(facultyName, email, ""));
				} else {
					// Faculty member has all the information in their span

					String facultyName = info.get(0).text(); // The only actual text is the faculty's name;
					String website = info.get(0).attr("href");
					String email = info.get(2).toString();
					email = email.substring(email.indexOf("mailto:") + 7);
					email = email.substring(0, email.indexOf("\""));
					faculty.add(new Teacher(facultyName, email, website));
				}
			}
		}
		fetched = true;
	}

}
