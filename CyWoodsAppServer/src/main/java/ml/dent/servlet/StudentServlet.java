package ml.dent.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.util.Default;
import ml.dent.web.StudentFetcher;

/**
 * The frontend will send an HTTP request to this servlet at the above endpoint
 * (e.g. www.dent.ml/CyWoodsAppServer/Grades), and we'll write our response to
 * the HTTP response.
 * 
 * @author Ronak Malik
 */
@WebServlet("/Student")
public class StudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public StudentServlet() {
		super();
	}

	/**
	 * GET Requests are the default HTTP request, so we want to give a formatted
	 * response if someone tries to perform a GET, but we don't wan't to accept any
	 * GET requests as they are insecure for this servlet.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();

		pw.println(Default.BadRequest("GET Requests are not allowed!"));
	}

	/**
	 * If we use a GET request, the username and password will stored in the url and
	 * can be easily intercepted. Even if both are encrypted in some way, this is
	 * usually bad practice. In this case, since we are sending and receiving
	 * usernames and passwords, POST requests can send parameters through a hidden
	 * form.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();

		String user_encoded = req.getParameter("username");
		String pass_encoded = req.getParameter("password");

		if (user_encoded == null) {
			pw.println(Default.BadRequest("No Username Provided"));
			return;
		}
		if (pass_encoded == null) {
			pw.println(Default.BadRequest("No Password Provided"));
			return;
		}

		String username = user_encoded;
		String password = pass_encoded;

		StudentFetcher grades = new StudentFetcher(username, password);
		String ret = grades.populateStudent();
		if (ret.contains("false")) {
			pw.println(ret);
		} else {
			pw.println(grades.returnStudent().getJsonData().format());
		}
	}
}
