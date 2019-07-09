package ml.dent.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
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
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// This grounds the path in the real file
		// system so we can use relative paths
		// without the virtual context messing us up
		String filePath = this.getServletContext().getRealPath("WEB-INF");
		// The "../"s mean the previous
		// directory; 3 of them mean
		// to escape the server dir
		// to find the schedule
		// file.
		filePath = "../../../" + filePath;

		// Using a scanner rather than something faster like a buffered reader because
		// the performance difference is not important, and we can choose to use regex
		// if necessary
		Scanner file = new Scanner(new File(filePath));
		// Change formatting from yyyy-mm-dd to mm/dd
		String currentDay = LocalDate.now().toString().replaceAll("\\d{4}-(\\d{2})-(\\d{2})", "$1/$2");
		// Move to the current date within the file; consumes characters
		file.findWithinHorizon(currentDay, Integer.MAX_VALUE);

		// Prepare writer for response
		PrintWriter pw = response.getWriter();

		JsonObject res = new JsonObject().add("date", currentDay);

		String type = file.nextLine();
		switch (type) {
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
				res.add("type", "custom");
				JsonObject shed = new JsonObject();
				String nextLine;
				while ((nextLine = file.nextLine()).equals("LUNCH")) {
					String[] line = nextLine.split(" ");
					shed.add(line[0], new JsonArray().add(line[1], line[2]));
				}
				while ((nextLine = file.nextLine()).equals("END")) {
					String[] line = nextLine.split(" ");
				}
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
