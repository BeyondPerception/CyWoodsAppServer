package ml.dent.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.util.Default;

/**
 * Servlet implementation class BugServlet
 */
@WebServlet("/Bugs")
public class BugServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BugServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("UTF-8");
		PrintWriter pw = response.getWriter();
		pw.println(Default.BadRequest("GET reqs not allowed!"));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("UTF-8");
		PrintWriter pw = response.getWriter();

		String message = request.getParameter("message");

		if (message == null) {
			pw.println(Default.BadRequest("No message provided"));
			return;
		}

		message.replace("\n", "");
		message += "\n"; // Needed to handle last line correctly
		message = message.replaceAll("(.{1,75})\\s+", "$1\n"); // Line wrap

		// This is a unique id for the device that filed the bug report so you
		// can look up all the http reqs and responses that they have
		// sent/received
		String id = request.getParameter("id");

		if (id == null) {
			pw.println(Default.BadRequest("No id provided"));
			return;
		}

		String date = LocalDate.now().toString();

		String bugDirPath = "/efs/BugReports";
		File bugDir = new File(bugDirPath);

		if (!bugDir.exists() || !bugDir.isDirectory()) {
			bugDir.mkdir();
		}

		File bugReport = new File("/efs/BugReports/bugs-" + date);

		if (!bugReport.exists()) {
			bugReport.createNewFile();
		}

		PrintWriter fw = new PrintWriter(bugReport);

		fw.println("Date Filed: " + date);
		fw.println("Device ID: " + id);
		fw.println("Message:");
		fw.println(message);
		fw.println();
		fw.println("===================");
		fw.println();

		fw.close();

		pw.println(Default.OK("Bug report recieved and filed successfully"));
	}
}
