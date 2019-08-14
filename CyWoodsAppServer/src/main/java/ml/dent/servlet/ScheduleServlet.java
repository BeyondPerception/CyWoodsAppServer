package ml.dent.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;
import ml.dent.util.Default;

/**
 * This servlet gets queried when the client wants to know the schedule for the
 * day; that's all :)
 * 
 * @author Ronak Malik
 */
@WebServlet("/Schedule")
public class ScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScheduleServlet() {
		super();
	}

	/**
	 * All we need for this class is to handle a simple get request and return
	 * today's schedule read from a file (there are much better ways to do this, but
	 * because of the way the school gives us the info about each day's schedule,
	 * this is what we have to do)
	 */
	@SuppressWarnings("resource")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Prepare writer for response
		PrintWriter pw = response.getWriter();

		// We want them to send us the day so it is more customizable for the client
		String day = request.getParameter("day");
		if (day == null) {
			pw.println(Default.BadRequest("Missing paramater \'day\'"));
			return;
		}
		if (!day.matches("\\d{2}-\\d{2}")) {
			pw.println(Default.BadRequest("Paramater \'day\' should be formatted as mm-dd"));
			return;
		}
		// We can only handle the current month, so we verify using our own clock
		String month = LocalDate.now().toString().replaceAll("\\d{4}-(\\d{2})-(\\d{2})", "$1");
		if (!day.matches(month + "-\\d{2}")) {
			pw.println(Default.BadRequest("Incorrect month"));
			return;
		}

		String currentDay = day.replace("-", "/");

		// This grounds the path in the real files
		// system so we can use relative paths
		// without the virtual context messing us up
		// The schedule file must be in the user's home dir
		String filePath = System.getProperty("user.home") + "/UpdateFiles/MonthlySchedules.txt";

		try {
			// Using a scanner rather than something faster like a buffered reader because
			// the performance difference is not important, and we can choose to use regex
			// if necessary
			Scanner file = null;
			try {
				file = new Scanner(new File(filePath));
			} catch (FileNotFoundException e) {
				pw.println(Default.InternalServerError("SEVERE: Master schedule file missing"));
				return;
			}

			// Move to the current date within the file; consumes characters
			file.findWithinHorizon(currentDay, Integer.MAX_VALUE);
			file.nextLine(); // pick up new line char

			JsonObject res = new JsonObject().add("date", currentDay);

			String regex = "[^\\s\"']+|\"([^\"]*)\"|'([^']*)'";

			// Parsing of the master schedule file
			String type = file.nextLine();
			switch (type) {
				// If the types are one of the regulars, just send that, the client will know
				// how to deal with it. These are most common, custom will very rarely happen,
				// but it is necessary.
				case "standard":
					res.add("type", "standard");
					break;
				case "second":
					res.add("type", "second");
					break;
				case "seventh":
					res.add("type", "seventh");
					break;
				// If you take a look at the master schedule file, this lump of code may kinda
				// make sense. I tried to make it as intuitive as possible.
				case "custom":
					res.add("type", "custom");
					res.add("name", file.nextLine());
					JsonObject shed = new JsonObject();
					String nextLine;
					// Reads all periods until lunch begins (usually p.1-p.3)
					while (!(nextLine = file.nextLine()).equals("LUNCH")) {
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(nextLine);
						String[] line = new String[3];
						for (int i = 0; i < 3 && matcher.find(); i++) {
							line[i] = matcher.group();
						}
						line[0] = line[0].substring(1, line[0].length() - 1);
						shed.add(line[0], new JsonArray().add(line[1], line[2]));
					}
					// Parsing the periods that change because of lunch (usually p.4-p.5)
					JsonObject lunch = new JsonObject();
					while (!(nextLine = file.nextLine()).equals("END")) {
						if (nextLine.length() == 1) {
							lunch = new JsonObject();
							shed.add(nextLine, lunch);
						} else {
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(nextLine);
							String[] line = new String[3];
							for (int i = 0; i < 3 && matcher.find(); i++) {
								line[i] = matcher.group();
							}
							line[0] = line[0].substring(1, line[0].length() - 1);
							lunch.add(line[0], new JsonArray().add(line[1], line[2]));
						}
					}
					// Reading the rest of the day (usually p.6-p.7)
					while (!(nextLine = file.nextLine()).equals("END")) {
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher = pattern.matcher(nextLine);
						String[] line = new String[3];
						for (int i = 0; i < 3 && matcher.find(); i++) {
							line[i] = matcher.group();
						}
						line[0] = line[0].substring(1, line[0].length() - 1);
						shed.add(line[0], new JsonArray().add(line[1], line[2]));
					}
					res.add("schedule", shed);
					break;
			}

			pw.println(res.format());

		} catch (Exception e) {
			pw.println(Default.InternalServerError("Failed to parse schedule file!"));
			System.out.println("Failed to parse schedule file!");
			e.printStackTrace();
		}
	}

	/**
	 * We don't want to allow them to send POST reqs, but we'll handle them anyways
	 * otherwise it will give them a huge, very unreadable error.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		pw.println(Default.BadGateway("POST reqs not allowed"));
	}
}
