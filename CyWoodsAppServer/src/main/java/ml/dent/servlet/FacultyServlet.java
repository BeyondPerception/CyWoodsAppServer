package ml.dent.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;
import ml.dent.object.student.Teacher;
import ml.dent.util.Default;
import ml.dent.util.Logger;
import ml.dent.web.FacultyFetcher;

/**
 * Servlet implementation class FacultyServlet
 */
@WebServlet("/Faculty")
public class FacultyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger logger;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FacultyServlet() {
		super();
		logger = new Logger("Faculty");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();

		FacultyFetcher facultyFetcher = new FacultyFetcher();
		try {
			facultyFetcher.fetchFaculty();
		} catch (IOException e) {
			pw.println(Default.InternalServerError("Failed to fetch faculty"));
			logger.log("Failed to fetch faculty");
			logger.logError(e);
		}

		ArrayList<Teacher> fac = facultyFetcher.getFaculty();

		JsonArray teachers = new JsonArray();

		for (Teacher val : fac) {
			teachers.add(val.getJsonData());
		}

		pw.println(new JsonObject().add("faculty", teachers).format());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		pw.println(Default.BadGateway("POST reqs not allowed"));
	}

}
