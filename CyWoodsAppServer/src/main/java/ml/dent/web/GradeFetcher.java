package ml.dent.web;

import java.io.IOException;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ml.dent.object.Student;
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
public class GradeFetcher {

	// Home Access Center URLS that we will be parsing.
	private static final String HAC_LOGIN_URL = "https://home-access.cfisd.net/HomeAccess/Account/LogOn";
	private static final String HAC_SCHEDULE_URL = "https://home-access.cfisd.net/HomeAccess/Home/WeekView";
	private static final String HAC_TRANSCIRPT_URL = "https://home-access.cfisd.net/HomeAccess/Grades/Transcript";
	private static final String HAC_ATTENDANCE_URL = "https://home-access.cfisd.net/HomeAccess/Content/Attendance/MonthlyView.aspx";
	private static final String HAC_ASSIGNMENTS_URL = "https://home-access.cfisd.net/HomeAccess/Content/Student/Assignments.aspx";
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
	 * A 1 indexed number from 1-4 so we can get the assignments for a specific
	 * class later.
	 */
	private int quarter;

	/**
	 * The username and password must be passed to the grade fetcher, otherwise it
	 * can't do anything. These parameters are already decrypted, as all the
	 * security in transferring information should be done within the servlet.
	 */
	public GradeFetcher(String username, String password) {
		currentUser = new Student(username, password);
		testUser = false;
		quarter = 1;
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

	public String populateStudent() {
		String loginRet = login();
		if (testUser) {
			return loginRet;
		}
		String weekViewRet = fetchWeekView();

		return loginRet;
	}

	/**
	 * This method just logs into Home Access so we can get the session id and
	 * cookies for smooth sailing later.
	 * 
	 * @return true if the login was successful, false otherwise.
	 */
	public String login() {
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

		return Default.OK("");
	}

	/**
	 * Grabs everything it can from the WeekView on Home Access. Not assignments
	 * though. Assignments are easier to get from another page.
	 */
	public String fetchWeekView() {
		try {
			// We use URLS to navigate HAC because faking clicks is sketchy
			Document weekView = getDocument(HAC_SCHEDULE_URL);

			// This may seem very specific, but getting exact CSS selectors like this is
			// really easy. Firefox(which you should be using) has a handy tool that allows
			// you to right click an element in inspect element and copy the CSS Selector
			String studentName = weekView.select("li.sg-banner-menu-element:nth-child(1) > span:nth-child(1)").text();
			currentUser.setName(studentName);

			Element classTable = weekView.select(".sg-asp-table > tbody:nth-child(2)").first();
			Elements rows = classTable.select("tr");

			for (Element row : rows) {
				System.out.println(row.getElementById("courseName").text());
			}

		} catch (IOException e) {
			e.printStackTrace();
			return Default.BadGateway("Failed to get week view after login");
		}

		return Default.OK("");
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).cookie(".AuthCookie", authCookie).cookie("ASP.NET_SessionId", sessionID).get();
	}

	private String assignmentURL(String id) {
		return new StringBuilder().append(
				"https://home-access.cfisd.net/HomeAccess/Content/Student/AssignmentsFromRCPopUp.aspx?section_key=")
				.append(id)
				.append("&course_session=1&RC_RUN=4&MARK_TITLE=9WK%20%20.Trim()&MARK_TYPE=9WK%20%20.Trim()&SLOT_INDEX=1")
				.toString();
	}

	public void populateTestUser() {

	}
}
