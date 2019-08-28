package ml.dent.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;
import ml.dent.object.news.NewsItem;
import ml.dent.util.Default;
import ml.dent.web.NewsFetcher;

/**
 * Servlet implementation class NewsServlet
 */
@WebServlet("/News")
public class NewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter pw = response.getWriter();

		NewsFetcher newsFetcher = new NewsFetcher();
		try {
			newsFetcher.populateNews();
		} catch (Exception e) {
			pw.println(Default.InternalServerError("failed to fetch news"));
			e.printStackTrace();
			return;
		}

		ArrayList<NewsItem> news = newsFetcher.getNews();

		JsonObject res = new JsonObject();
		JsonArray newsArray = new JsonArray();

		for (NewsItem val : news) {
			newsArray.add(val.getJsonData());
		}

		res.add("news", newsArray);

		String eventFilePath = "/efs/UpdateFiles/Events.txt";
		Scanner file = new Scanner(new File(eventFilePath));

		JsonArray events = new JsonArray();
		while (file.hasNext()) {
			try {
				events.add(new JsonObject().add("title", file.nextLine()).add("dates", file.nextLine()));
				file.nextLine();
			} catch (NoSuchElementException e) {
				// shhh...
			}
		}
		res.add("events", events);

		pw.println(res.format());
	}

	/**
	 *
	 * We don't want to allow them to send POST reqs, but we'll handle them anyways
	 * otherwise it will give them a huge, very unreadable error.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		pw.println(Default.BadGateway("POST reqs not allowed"));
	}

}
