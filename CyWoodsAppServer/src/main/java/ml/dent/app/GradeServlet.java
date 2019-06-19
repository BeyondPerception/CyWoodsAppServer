package ml.dent.app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Grades")
public class GradeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GradeServlet() {
		super();
	}

	/**
	 * Other methods exist such as doPost, doPut, and doDelete, but in this case
	 * only GET requests are necessary.
	 * 
	 * The frontend will send an HTTP request to this servlet at the above endpoint
	 * (e.g. www.dent.ml/Grades), and we'll write our response to the HTTP response.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String user_encoded = request.getParameter("username");
		String pass_encoded = request.getParameter("password");
		
		if (user_encoded == null) {
			
		}
		
	}
}
