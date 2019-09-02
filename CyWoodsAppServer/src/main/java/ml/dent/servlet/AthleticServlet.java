package ml.dent.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;
import ml.dent.object.athletics.AthleticItem;
import ml.dent.util.Default;
import ml.dent.util.Logger;
import ml.dent.web.AthleticsFetcher;

/**
 * Servlet implementation class AthleticServlet
 */
@WebServlet("/Athletics")
public class AthleticServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger logger;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AthleticServlet() {
		super();
		logger = new Logger("Athletics");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			response.setCharacterEncoding("UTF-8");
			PrintWriter pw = response.getWriter();

			AthleticsFetcher af = new AthleticsFetcher();
			af.populateGames();

			Vector<AthleticItem> items = af.getGames();

			JsonObject jO = new JsonObject();

			JsonArray ja = new JsonArray();

			for (AthleticItem ai : items) {
				ja.add(ai.getJsonData());
			}

			jO.add("games", ja);

			pw.println(jO.format());
		} catch (Exception e) {
			logger.log("Athletics fetch failed");
			logger.logError(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();

		pw.println(Default.BadRequest("POST reqs not allowed!"));
	}

}
