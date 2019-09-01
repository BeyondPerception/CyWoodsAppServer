package ml.dent.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeSet;

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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();

		// We cache news and only update every hour to save time on the frontend
		// Check if the news cache exists
		File newsCache = new File("/efs/UpdateFiles/NewsCache.txt");
		if (!newsCache.exists()) {
			newsCache.createNewFile();
			PrintWriter tmp = new PrintWriter(newsCache);
			tmp.println(LocalDateTime.now().minus(61, ChronoUnit.MINUTES).toEpochSecond(ZoneOffset.UTC));
			tmp.close();
		}

		// Prepare to read the cache
		BufferedReader br = new BufferedReader(new FileReader(newsCache)); // Buffered Reader because very little
																			// parsing
		long lastFetch;
		try {
			lastFetch = Long.parseLong(br.readLine());
		} catch (IOException | NumberFormatException e) {
			// If the file is empty;
			lastFetch = LocalDateTime.now().minus(61, ChronoUnit.MINUTES).toEpochSecond(ZoneOffset.UTC);
		}
		long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

		final long ONEHOURINSECONDS = 3600;
		if ((now - ONEHOURINSECONDS) - lastFetch >= 0) {
			// It has been at least one hour since the last fetch
			NewsFetcher newsFetcher = new NewsFetcher();
			try {
				newsFetcher.populateNews();
			} catch (Exception e) {
				pw.println(Default.InternalServerError("failed to fetch news"));
				e.printStackTrace();
				br.close();
				return;
			}

			TreeSet<NewsItem> news = newsFetcher.getNews();

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
			file.close();
			res.add("events", events);

			// Print the newly fetched data to a caching file
			newsCache.delete();
			newsCache.createNewFile();
			PrintWriter cacheWriter = new PrintWriter(newsCache);
			cacheWriter.println(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
			cacheWriter.println(res.format());
			cacheWriter.close();

			pw.println(res.format());
		} else {
			// Just read the cached file
			StringBuilder sb = new StringBuilder();
			String read;
			while ((read = br.readLine()) != null) {
				sb.append(read).append("\n");
			}

			pw.println(sb.toString().trim());
		}
		br.close();
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
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		pw.println(Default.BadGateway("POST reqs not allowed"));
	}
}
