package ml.dent.web;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ml.dent.object.student.Assignment;
import ml.dent.object.student.Class;
import ml.dent.object.student.Student;
import ml.dent.object.student.Teacher;
import ml.dent.object.student.Attendance.AttendanceBlock;
import ml.dent.object.student.Transcript.Block;
import ml.dent.object.student.Transcript.Block.Course;
import ml.dent.util.Default;

/**
 * This is the meat of the actual program, what fetches the documents from Home
 * Access, and parses it into usable JSON data that can be sent to the client.
 * The way I chose to navigate and scrape the website's DOM is with a library
 * called Jsoup. I would highly recommend looking up the docs, but it is pretty
 * straight forward, and I'll try to leave helpful comments along the way.
 * 
 * @author Ronak Maik
 */
public class StudentFetcher extends AbstractFetcher {

	// Home Access Center URLS that we will be parsing.
	private static final String HAC_LOGIN_URL = "https://home-access.cfisd.net/HomeAccess/Account/LogOn";
	private static final String HAC_SCHEDULE_URL = "https://home-access.cfisd.net/HomeAccess/Home/WeekView";
	private static final String HAC_TRANSCIRPT_URL = "https://home-access.cfisd.net/HomeAccess/Content/Student/Transcript.aspx";
	private static final String HAC_ATTENDANCE_URL = "https://home-access.cfisd.net/HomeAccess/Content/Attendance/MonthlyView.aspx";
	private static final String HAC_REPORTCARD_URL = "https://home-access.cfisd.net/HomeAccess/Content/Student/ReportCards.aspx";

	// Cookies and such
	private String authCookie;
	private String sessionID;
	// private String siteCode;

	/**
	 * Kind of counter-intuitively, the grade fetcher builds up the instance of the
	 * Student that will eventually be sent to the client as JSON data. The simple
	 * reason is because we don't have any information about the student before
	 * this! (other than the username and password of course).
	 */
	private Student currentUser;

	private boolean testUser;

	/**
	 * The username and password must be passed to the grade fetcher, otherwise it
	 * can't do anything. These parameters are already decrypted, as all the
	 * security in transferring information should be done within the servlet.
	 */
	public StudentFetcher(String username, String password) {
		currentUser = new Student(username, password);
		testUser = false;
	}

	/**
	 * This is the final method that should be called from the GradeFetcher, and
	 * should only be called AFTER, the login method and populating methods are
	 * invoked. Otherwise, a student object with just a username and password will
	 * be returned.
	 */
	public Student returnStudent() {
		return currentUser;
	}

	/**
	 * This method calls each populating method
	 */
	public String populateStudent() {
		String loginRet = login();

		if (loginRet != null) {
			return loginRet;
		}

		if (testUser) {
			return loginRet;
		}
		try {
			fetchWeekView();
		} catch (Exception e) {
			System.err.println("Failed to fetch week view");
			e.printStackTrace();
			return Default.BadGateway("Failed to fetch grades");
		}
		try {
			fetchAssignments();
		} catch (Exception e) {
			System.err.println("Failed to fetch assignments");
			e.printStackTrace();
			return Default.BadGateway("Failed to fetch assignments");
		}

		try {
			fetchTranscript();
		} catch (Exception e) {
			System.err.println("Failed to fetch transcript");
			e.printStackTrace();
			return Default.BadGateway("Failed to fetch transcript");
		}

		try {
			fetchAttendance();
		} catch (Exception e) {
			System.out.println("Failed to fetch attendance");
			e.printStackTrace();
			return Default.BadGateway("Failed to fetch attendance");
		}

		return Default.OK("");
	}

	/**
	 * This method just logs into Home Access so we can get the session id and
	 * cookies for smooth sailing later.
	 * 
	 * @return true if the login was successful, false otherwise.
	 */
	private String login() {
		if (currentUser.getUsername().equals("s0") && currentUser.getPassword().equals("testing")) {
			// These are arbitrary logins that don't actually exist, but can be used to test
			// stuff on the client side without actually changing anything on the client to
			// be special.
			testUser = true;
			populateTestUser();
			return Default.OK("Test User");
		}

		try {
			Response loginResponse = Jsoup.connect(HAC_LOGIN_URL).data("Database", "10")
					.data("LogOnDetails.UserName", currentUser.getUsername())
					.data("LogOnDetails.Password", currentUser.getPassword()).method(Method.POST)
					.ignoreContentType(true).execute();
			Document returnPage = loginResponse.parse();

			authCookie = loginResponse.cookie(".AuthCookie");
			sessionID = loginResponse.cookie("ASP.NET_SessionId");
			// siteCode = loginResponse.cookie("SPIHACSiteCode");

			Elements loginSummary = returnPage.getElementsByClass("validation-summary-errors");
			if (!loginSummary.isEmpty()) {
				return Default.NotAcceptable(loginSummary.text());
			}
		} catch (IOException e) {
			// This will really only happen if the website is completely down.
			e.printStackTrace();
			return Default.BadGateway("Home Access might be down");
		}

		return null;
	}

	/**
	 * Grabs everything it can from the WeekView on Home Access. Not assignments
	 * though. Assignments are easier to get from another page.
	 */
	private void fetchWeekView() throws IOException {
		// We use URLS to navigate HAC because faking clicks is sketchy
		Document weekView = getDocument(HAC_SCHEDULE_URL);

		// This may seem very specific, but getting exact CSS selectors like this is
		// really easy. Firefox(which you should be using) has a handy tool that allows
		// you to right click an element in inspect element and copy the CSS Selector
		String studentName = weekView.select("li.sg-banner-menu-element:nth-child(1) > span:nth-child(1)").text();
		currentUser.setName(studentName);

		Element classTable = weekView.selectFirst(".sg-asp-table > tbody:nth-child(2)");
		Elements rows = classTable.select("tr");

		for (Element row : rows) {
			// Course and staff information
			Element courseInfo = row.getElementById("courseName");
			String courseName = courseInfo.text();

			if (currentUser.getClass(courseName) != null) {
				// You've probable seen it before, but HAC sometimes does this really dumb thing
				// where it has duplicate classes around certain periods like lunch. So if the
				// class already exists, skip this row.
				continue;
			}

			int jsIndex = courseInfo.toString().indexOf("ViewClassPopUp(") + ("ViewClassPopUp(".length());
			String courseId = courseInfo.toString().substring(jsIndex, courseInfo.toString().indexOf(",", jsIndex));
			int courseIndex = courseInfo.toString().indexOf(courseId);
			String quarter = courseInfo.toString().substring(courseIndex + courseId.length() + 2,
					courseIndex + courseId.length() + 3); // One character that is the quarter number;

			String teacherName = row.getElementById("staffName").text();
			String teacherEmail = row.getElementById("staffName").toString();
			teacherEmail = teacherEmail.substring(teacherEmail.indexOf(":") + 1);
			teacherEmail = teacherEmail.substring(0, teacherEmail.indexOf("\""));

			// Adding course and staff info to user
			currentUser.addClass(courseName);
			currentUser.getClass(courseName).setTeacher(new Teacher(teacherName, teacherEmail, null));
			currentUser.getClass(courseName).setHAC_id(Integer.parseInt(courseId));
			currentUser.getClass(courseName).setQuarter(Integer.parseInt(quarter));

			String average = row.getElementById("average").text();
			if (!average.isEmpty()) {
				// For lunch and stuff
				currentUser.getClass(courseName).setGrade(Double.parseDouble(average));
			}
		}
	}

	/**
	 * Precondition for running this method is that fetchWeekView has been ran and
	 * that classes for the student have been initialized.
	 */
	private void fetchAssignments() throws IOException {
		for (String className : currentUser.getClassList()) {
			Class curClass = currentUser.getClass(className);
			Document assignmentList = getDocument(assignmentURL(curClass.getHAC_id(), curClass.getQuarter()));
			// Select all elements within assignment table
			Elements table = assignmentList
					.select("#plnMain_rptAssigmnetsByCourse_dgCourseAssignments_0 > tbody:nth-child(1)").select("tr");

			for (Element assign : table) {
				if (assign.selectFirst("td:nth-child(1)").text().equals("Date Due")) {
					// First element in table which is header
					continue;
				}
				String dateDue = assign.selectFirst("td:nth-child(1)").text();
				String dateAssigned = assign.selectFirst("td:nth-child(2)").text();
				String name = assign.selectFirst("td:nth-child(3)").text();
				String category = assign.selectFirst("td:nth-child(4)").text();
				String score = assign.selectFirst("td:nth-child(5)").ownText(); // So it doesn't pick up the note
				String note;
				try {
					note = assign.selectFirst("td:nth-child(5) > span:nth-child(1) > img:nth-child(1)").attr("title");
				} catch (NullPointerException e1) {
					// Not all assignments have notes
					note = null;
				}
				String weight = assign.selectFirst("td:nth-child(6)").text();
				String maxScore = assign.selectFirst("td:nth-child(8)").text();
				String isExtraCredit = assign.selectFirst("td:nth-child(10)").text();

				if (isExtraCredit.toLowerCase().replaceAll(" ", "").contains("extracredit")) {
					isExtraCredit = "true";
				} else {
					isExtraCredit = "false";
				}

				// Adding the fetched elements to an Assignment
				Assignment cur = new Assignment();
				cur.setDateDue(dateDue);
				cur.setDateAssigned(dateAssigned);
				cur.setName(name.substring(0, name.length() - 2)); // Getting rid of weird "*" at the end of all classes
				cur.setCategory(category);
				cur.setScore(score);
				cur.setNote(note);
				cur.setWeight(Double.parseDouble(weight));
				cur.setMaxScore(maxScore);
				cur.setExtraCredit(Boolean.parseBoolean(isExtraCredit));

				// Adding Assignment to Student;
				curClass.addAssign(cur);
			}
		}
	}

	private void fetchTranscript() throws IOException {
		Document transView = getDocument(HAC_TRANSCIRPT_URL);
		Elements yearTable = transView.select(".sg-content-grid > table:nth-child(2) > tbody:nth-child(1)")
				.select("tr");

		for (int i = 0; i < yearTable.size() - 3; i++) {
			Element row = yearTable.get(i);
			Elements blocks = row.getElementsByClass("sg-transcript-group");
			for (Element block : blocks) {
				Elements tables = block.select("table");

				Element info = tables.get(0);
				Element coursesTable = tables.get(1);
				Element totalCreditTable = tables.get(2);

				Elements infoFirstRow = info.selectFirst("tbody").select("tr").first().select("td");
				Elements infoSecondRow = info.selectFirst("tbody").select("tr").last().select("td");

				String year = infoFirstRow.get(1).text();
				String grade = infoFirstRow.get(5).text();
				String building = infoSecondRow.get(1).text();

				Elements coursesList = coursesTable.selectFirst("tbody").select("tr");
				ArrayList<Course> courses = new ArrayList<>();

				for (Element course : coursesList) {
					Elements rowElements = course.select("td");

					String courseNum = rowElements.get(0).text();
					String description = rowElements.get(1).text();
					String sem1 = rowElements.get(2).text();
					String sem2 = rowElements.get(3).text();
					String credit = rowElements.get(4).text();

					if (courseNum.equals("Course")) {
						continue;
					}

					try {
						courses.add(new Course(description, courseNum, sem1, sem2, Double.parseDouble(credit)));
					} catch (NumberFormatException e) {
						courses.add(new Course(description, courseNum, sem1, sem2, Double.NaN));
					}
				}

				String totalCredit = totalCreditTable.selectFirst("tbody").selectFirst("tr").select("td").get(3).text();

				try {
					currentUser.getTranscript()
							.addBlock(new Block(year, building, grade, Double.parseDouble(totalCredit), courses));
				} catch (NumberFormatException e) {
					currentUser.getTranscript().addBlock(new Block(year, building, grade, Double.NaN, courses));
				}
			}
		}

		Elements gpaTable = transView.getElementById("plnMain_rpTranscriptGroup_trGPA1").select("td");

		String gpa = gpaTable.get(1).text();
		String rank = gpaTable.get(2).text();

		currentUser.getTranscript().setRank(rank);
		try {
			currentUser.getTranscript().setGpa(Double.parseDouble(gpa));
		} catch (NumberFormatException e) {
			currentUser.getTranscript().setGpa(Double.NaN);
		}
	}

	/**
	 * Fetches monthly attendance from HAC
	 */
	private void fetchAttendance() throws IOException {
		Document monthlyView = getDocument(HAC_ATTENDANCE_URL);
		System.out.println(monthlyView);
		Elements calendarRows = monthlyView.select("#plnMain_cldAttendance > tbody:nth-child(1)").select("tr");

		String month = monthlyView
				.selectFirst("table.sg-asp-calendar-header > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)")
				.text();
		month = month.substring(0, month.indexOf(" "));

		for (int i = 2/* The first two rows are the month/year and the days */; i < calendarRows.size(); i++) {
			Elements days = calendarRows.get(i).select("td");

			for (Element day : days) {
				String dayNum = month + " " + day.text();
				String markers = day.attr("title");

				boolean isSchoolClosed = day.attr("style").contains("background-color:#CCCCCC");

				if (!markers.isEmpty()) {
					currentUser.getAttendance().addBlock(new AttendanceBlock(dayNum, markers));
				} else if (isSchoolClosed) {
					currentUser.getAttendance().addBlock(new AttendanceBlock(dayNum, "School Closed"));
				}
			}
		}
	}

	@Override
	protected Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).cookie(".AuthCookie", authCookie).cookie("ASP.NET_SessionId", sessionID)
				.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0").get();
	}

	/**
	 * Way easier and more accurate to parse from here than from Week View or
	 * Classes; Classes is sketch, don't use classes
	 */
	private String assignmentURL(int id, int quarter) {
		return new StringBuilder().append(
				"https://home-access.cfisd.net/HomeAccess/Content/Student/AssignmentsFromRCPopUp.aspx?section_key=")
				.append(id).append("&course_session=1&RC_RUN=").append(quarter)
				.append("&MARK_TITLE=9WK%20%20.Trim()&MARK_TYPE=9WK%20%20.Trim()&SLOT_INDEX=1").toString();
	}

	public void populateTestUser() {

	}
}
