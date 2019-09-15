package ml.dent.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ml.dent.object.student.Assignment;
import ml.dent.object.student.Attendance.AttendanceBlock;
import ml.dent.object.student.Class;
import ml.dent.object.student.Student;
import ml.dent.object.student.Teacher;
import ml.dent.object.student.Transcript.Block;
import ml.dent.object.student.Transcript.Block.Course;
import ml.dent.util.Default;
import ml.dent.util.Logger;

/**
 * This is the meat of the actual program, what fetches the documents from Home
 * Access, and parses it into usable JSON data that can be sent to the client.
 * The way I chose to navigate and scrape the website's DOM is with a library
 * called Jsoup. I would highly recommend looking up the docs, but it is pretty
 * straight forward, and I'll try to leave helpful comments along the way.
 * 
 * @author Ronak Malik
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

	private String userId;

	private Logger logger;

	private AtomicInteger[] fin;

	/**
	 * The username and password must be passed to the grade fetcher, otherwise it
	 * can't do anything. These parameters are already decrypted, as all the
	 * security in transferring information should be done within the servlet.
	 */
	public StudentFetcher(String username, String password, String id) {
		currentUser = new Student(username, password);
		testUser = false;
		userId = id;
		fin = new AtomicInteger[4];
		for (int i = 0; i < fin.length; i++) {
			fin[i] = new AtomicInteger(-1);
		}
		logger = new Logger("Student");
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

	private String ret;

	private boolean ready() {
		if (fin[0].get() == -1 || fin[1].get() == -1 || fin[2].get() == -1 || fin[3].get() == -1) {
			return false;
		}
		return true;
	}

	/**
	 * This method calls each populating method
	 */
	public String populateStudent() {
		ret = "";
		String loginRet = login();

		if (loginRet != null) {
			return loginRet;
		}

		if (testUser) {
			return loginRet;
		}

		Runnable task2 = new Runnable() {
			@Override
			public void run() {
				try {
					fetchTranscript();
					if (fin[2].get() == -1) {
						fin[2].set(0);
					}
				} catch (Exception e) {
					fin[2].set(1);
					logger.log("ID: " + userId);
					logger.log("Failed to fetch transcript");
					logger.logError(e);
					logger.log("START RESPONSE");
					logger.log(currentUser.getJsonData().format());
					logger.log("END RESPONSE");
					ret += "Failed to fetch transcript ";
				}
			}
		};
		new Thread(task2).start();

		Runnable task3 = new Runnable() {
			@Override
			public void run() {
				try {
					fetchAttendance();
					fin[3].set(0);
				} catch (Exception e) {
					fin[3].set(1);
					logger.log("ID: " + userId);
					logger.log("Failed to fetch attendance");
					logger.logError(e);
					logger.log("START RESPONSE");
					logger.log(currentUser.getJsonData().format());
					logger.log("END RESPONSE");
					ret += "Failed to fetch attendance ";
				}
			}
		};
		new Thread(task3).start();

		try {
			fetchWeekView();
			fin[0].set(0);
		} catch (Exception e) {
			fin[0].set(1);
			logger.log("ID: " + userId);
			logger.log("Failed to fetch grades");
			logger.logError(e);
			logger.log("START RESPONSE");
			logger.log(currentUser.getJsonData().format());
			logger.log("END RESPONSE");
			ret += "Failed to fetch grades ";
		}

		try {
			fetchAssignments();
			fin[1].set(0);
		} catch (Exception e) {
			fin[1].set(1);
			logger.log("ID: " + userId);
			logger.log("Failed to fetch assignments");
			logger.logError(e);
			logger.log("START RESPONSE");
			logger.log(currentUser.getJsonData().format());
			logger.log("END RESPONSE");
			ret += "Failed to fetch assignments ";
		}

		while (!ready()) {
			// block
		}

		if (fin[0].get() == 0) {
			return Default.OK(ret.trim());
		}

		return Default.BadGateway(ret.trim());
	}

	/**
	 * This method just logs into Home Access so we can get the session id and
	 * cookies for smooth sailing later.
	 * 
	 * @return true if the login was successful, false otherwise.
	 */
	private String login() {
		if (currentUser.getUsername().equals("s0") && currentUser.getPassword().equals("test")) {
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
			Element courseInfo = row.selectFirst("td:nth-child(1) > div:nth-child(1) > a:nth-child(1)");
			String courseName = courseInfo.text();

			if (currentUser.getClass(courseName.toLowerCase()) != null) {
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

			String teacherName = row.select("td:nth-child(1) > div:nth-child(1) > a:nth-child(3)").text();
			String teacherEmail;
			try {
				teacherEmail = row.select("td:nth-child(1) > div:nth-child(1) > a:nth-child(3)").toString();

				teacherEmail = teacherEmail.substring(teacherEmail.indexOf(":") + 1);
				teacherEmail = teacherEmail.substring(0, teacherEmail.indexOf("\""));
			} catch (Exception e) {
				teacherEmail = null;
			}

			// Adding course and staff info to user
			currentUser.addClass(courseName, courseName.toLowerCase());
			currentUser.getClass(courseName.toLowerCase()).setTeacher(new Teacher(teacherName, teacherEmail, null));
			currentUser.getClass(courseName.toLowerCase()).setHAC_id(Integer.parseInt(courseId));
			currentUser.getClass(courseName.toLowerCase()).setQuarter(Integer.parseInt(quarter));

			String average = row.select("td:nth-child(2) > a:nth-child(1)").text();
			if (!average.isEmpty()) {
				// For lunch and stuff
				currentUser.getClass(courseName.toLowerCase()).setGrade(Double.parseDouble(average));
			}
		}
	}

	/**
	 * Precondition for running this method is that fetchWeekView has been ran and
	 * that classes for the student have been initialized.
	 */
	private void fetchAssignments() throws IOException {
		for (String className : currentUser.getClassList()) {
//			Runnable task = new Runnable() {
//				@Override
//				public void run() {
			Class curClass = currentUser.getClass(className.toLowerCase());
			Document assignmentList;
			try {
				assignmentList = getDocument(assignmentURL(curClass.getHAC_id(), curClass.getQuarter()));
			} catch (IOException e2) {
				fin[1].set(1);
				e2.printStackTrace();
				return;
			}
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
				String score = assign.selectFirst("td:nth-child(5)").ownText(); // So it doesn't pick up the
																				// note
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
				cur.setName(name.substring(0, name.length() - 2)); // Getting rid of weird " *" at the end of all
																	// classes
				cur.setCategory(category);
				cur.setScore(score);
				cur.setNote(note);
				cur.setWeight(Double.parseDouble(weight));
				cur.setMaxScore(maxScore);
				cur.setExtraCredit(Boolean.parseBoolean(isExtraCredit));

				// Adding Assignment to Student;
				curClass.addAssign(cur);
			}

			// Getting the weights for each category
			Elements categoryTable = assignmentList
					.select("#plnMain_rptAssigmnetsByCourse_dgCourseCategories_0 > tbody:nth-child(1)");
			// Make sure that there are weights
			if (!categoryTable.isEmpty()) {
				categoryTable = categoryTable.first().select("tr");
				// First row is titles, last row is totales
				Elements cfuRow;
				try {
					cfuRow = categoryTable.get(1).select("td");
				} catch (IndexOutOfBoundsException e) {
					// May be it doesn't exist, that's ok
					cfuRow = null;
				}
				Elements raRow;
				try {
					raRow = categoryTable.get(2).select("td");
				} catch (IndexOutOfBoundsException e) {
					// May be it doesn't exist, that's ok
					raRow = null;
				}
				Elements saRow;
				try {
					saRow = categoryTable.get(3).select("td");
				} catch (IndexOutOfBoundsException e) {
					// May be it doesn't exist, that's ok
					saRow = null;
				}

				if (cfuRow != null && !cfuRow.get(0).text().contains("Total")) {
					String name = cfuRow.get(0).text();
					String points = cfuRow.get(1).text() + "/" + cfuRow.get(2).text();
					try {
						double cfuWeight = Double.parseDouble(cfuRow.get(4).text()) / 100.0;
						if (cfuWeight == 0) {
							cfuWeight = Double.NaN;
						}
						curClass.addCategory(name, points, cfuWeight);
					} catch (NumberFormatException e) {
						System.err.println("Failed to parse CFU weight");
					}
				}

				if (raRow != null && !raRow.get(0).text().contains("Total")) {
					String name = raRow.get(0).text();
					String points = raRow.get(1).text() + "/" + raRow.get(2).text();
					try {
						double raWeight = Double.parseDouble(raRow.get(4).text()) / 100.0;
						if (raWeight == 0) {
							raWeight = Double.NaN;
						}
						curClass.addCategory(name, points, raWeight);
					} catch (NumberFormatException e) {
						System.err.println("Failed to parse RA weight");
					}
				}

				if (saRow != null && !saRow.get(0).text().contains("Total")) {
					String name = saRow.get(0).text();
					String points = saRow.get(1).text() + "/" + saRow.get(2).text();
					try {
						double saWeight = Double.parseDouble(saRow.get(4).text()) / 100.0;
						if (saWeight == 0) {
							saWeight = Double.NaN;
						}
						curClass.addCategory(name, points, saWeight);
					} catch (NumberFormatException e) {
						System.err.println("Failed to parse SA weight");
					}
				}
			}
//				}
//			};
//			new Thread(task).start();
		}
	}

	private void fetchTranscript() throws IOException {
		Document transView = getDocument(HAC_TRANSCIRPT_URL);
		Elements yearTable = transView.select(".sg-content-grid > table:nth-child(2) > tbody:nth-child(1)")
				.select("tr");
		try {
			if (yearTable.isEmpty()) {
				// No transcript, no highschool credits
				return;
			}

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

					String totalCredit = totalCreditTable.selectFirst("tbody").selectFirst("tr").select("td").get(3)
							.text();

					try {
						currentUser.getTranscript()
								.addBlock(new Block(year, building, grade, Double.parseDouble(totalCredit), courses));
					} catch (NumberFormatException e) {
						currentUser.getTranscript().addBlock(new Block(year, building, grade, Double.NaN, courses));
					}
				}
			}
			try {
				Elements gpaTable = transView
						.selectFirst("#plnMain_rpTranscriptGroup_tblCumGPAInfo > tbody:nth-child(1) > tr:nth-child(2)")
						.select("td");

				String gpa = gpaTable.get(1).text();
				String rank = gpaTable.get(2).text();

				currentUser.getTranscript().setRank(rank);
				try {
					currentUser.getTranscript().setGpa(Double.parseDouble(gpa));
				} catch (NumberFormatException e) {
					currentUser.getTranscript().setGpa(Double.NaN);
				}
			} catch (NullPointerException e) {
			}
		} catch (NullPointerException e) {
			// Prob a freshman, prob got through my other check. I dont even know if the
			// other check works
			return;
		}
	}

	/**
	 * Fetches monthly attendance from HAC
	 */
	private void fetchAttendance() throws IOException, ParseException {
		Document monthlyView = getDocument(HAC_ATTENDANCE_URL);
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

				String year = LocalDate.now().toString().substring(0, 4);

				if (!markers.isEmpty()) {
					currentUser.getAttendance().addBlock(new AttendanceBlock(dayNum, markers));
				} else if (isSchoolClosed) {
					Date date = new SimpleDateFormat("MMMM dd yyyy").parse(dayNum + " " + year);
					String dayOfWeek = date.toString().substring(0, 3);
					if (!dayOfWeek.equals("Sun") && !dayOfWeek.equals("Sat")) {
						currentUser.getAttendance().addBlock(new AttendanceBlock(dayNum, "School Closed"));
					}
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
		currentUser.setName("Wildcat");
		Class testClass = new Class("Computer Science", "computer science");
		testClass.setTeacher(new Teacher("Mr.Knapsack", "", ""));
		testClass.setGrade(100.0);
		Assignment testAssign = new Assignment("Labs", "Tests", "08/027/2019", "08/29/2019", "note", "100", 1.0, "99",
				false);
		testClass.addAssign(testAssign);
		currentUser.addClass(testClass);
	}
}
