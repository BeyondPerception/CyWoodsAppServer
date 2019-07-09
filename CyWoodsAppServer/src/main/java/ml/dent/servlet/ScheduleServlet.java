package ml.dent.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

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

		// We want them to send us the day so it is more customizable for the client and
		// we don't have to worry about setting our clock
		String day = request.getParameter("day");
		if (day == null) {
			
		}
		
		// This grounds the path in the real file
		// system so we can use relative paths
		// without the virtual context messing us up
		String filePath = "/home/ronak/Workspace/CyWoodsAppServer/MonthlySchedules.txt";// this.getServletContext().getRealPath("WEB-INF");
		// The "../"s mean the previous
		// directory; 3 of them mean
		// to escape the server dir
		// to find the schedule
		// file.
//		filePath = "../../.." + filePath;
//		System.out.println(filePath);

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
		// Change formatting from yyyy-mm-dd to mm/dd
		String currentDay = LocalDate.now().toString().replaceAll("\\d{4}-(\\d{2})-(\\d{2})", "$1/$2");
		// Move to the current date within the file; consumes characters
		file.findWithinHorizon(currentDay, Integer.MAX_VALUE);
		file.nextLine(); // pick up new line char

		JsonObject res = new JsonObject().add("date", currentDay);

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
			case "custom":
				// If you take a look at the master schedule file, this lump of code may kinda
				// make sense. I tried to make it as intuitive as possible.
				res.add("type", "custom");
				JsonObject shed = new JsonObject();
				String nextLine;
				// Reads all periods until lunch begins (usually p.1-p.3)
				while (!(nextLine = file.nextLine()).equals("LUNCH")) {
					String[] line = nextLine.split(" ");
					shed.add(line[0], new JsonArray().add(line[1], line[2]));
				}
				// Parsing the periods that change because of lunch (usually p.4-p.5)
				JsonObject lunch = new JsonObject();
				while (!(nextLine = file.nextLine()).equals("END")) {
					String[] line = nextLine.split(" ");
					if (line.length == 1) {
						lunch = new JsonObject();
						shed.add(line[0], lunch);
					} else {
						lunch.add(line[0], new JsonArray().add(line[1], line[2]));
					}
				}
				// Reading the rest of the day (usually p.6-p.7)
				while (!(nextLine = file.nextLine()).equals("END")) {
					String[] line = nextLine.split(" ");
					shed.add(line[0], new JsonArray().add(line[1], line[2]));
				}
				res.add("schedule", shed);
				break;
		}

		pw.println(res.format());
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
