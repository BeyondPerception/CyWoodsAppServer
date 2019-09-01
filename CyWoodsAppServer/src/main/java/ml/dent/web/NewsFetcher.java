package ml.dent.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ml.dent.object.news.AppNewsItem;
import ml.dent.object.news.CrimsonConnectionNewsItem;
import ml.dent.object.news.DistrictNewsItem;
import ml.dent.object.news.NewsItem;
import ml.dent.object.news.SchoolNewsItem;

public class NewsFetcher extends AbstractFetcher {
	private static final String SCHOOL_NEWS_URL = "https://cywoods.cfisd.net/en/news/school-news/";
	private static final String CRIMSON_CONNECTION_URL = "https://www.thecrimsonconnection.com/category/news/cy-woods/";
	private static final String DISTRICT_NEWS_URL = "https://www.cfisd.net/en/news-media/district/";
	private static final String APP_NEWS = "/efs/UpdateFiles/AppNews.txt";

	private TreeSet<NewsItem> news;

	private boolean fetched;

	public NewsFetcher() {
		news = new TreeSet<>();
		fetched = false;
	}

	public TreeSet<NewsItem> getNews() throws IllegalStateException {
		if (!fetched) {
			throw new IllegalStateException("populateNews must be called first");
		}
		return news;
	}

	public void populateNews() throws Exception {
		boolean[] failed = new boolean[4];
		try {
			fetchSchoolNews();
		} catch (Exception e) {
			// Continue trying to fetch
			failed[0] = true;
		}
		try {
			fetchDistrictNews();
		} catch (Exception e) {
			// Continue trying to fetch
			failed[1] = true;
		}
		try {
			fetchCrimsonConnection();
		} catch (Exception e) {
			// Continue trying to fetch
			failed[2] = true;
		}
		try {
			fetchAppNews();
		} catch (Exception e) {
			// Continue trying to fetch
			failed[3] = true;
		}
		if (!Arrays.equals(failed, new boolean[] { true, true, true, true })) {
			fetched = true;
		}
	}

	private void fetchSchoolNews() throws Exception {
		// Get the base news page
		Document newsPage = getDocument(SCHOOL_NEWS_URL);

		// Gets the list of news nested in the page
		Element newsList = newsPage.selectFirst("ul.news-index");

		// Gets each news item from the news list in an iterable form
		Elements newsItems = newsList.getElementsByClass("index-item");

		for (Element newsItem : newsItems) {
			// All the relevant info except the date is stored in this tag
			Element info = newsItem.getElementsByClass("item-title").first().selectFirst("a");
			String title = info.text();
			String url = "https://www.cfisd.net" + info.attr("href");

			String date = newsItem.getElementsByClass("item-date").first().text();

			news.add(new SchoolNewsItem(title, date, url));
		}
	}

	// I didn't realize this would be the exact same code...
	private void fetchDistrictNews() throws Exception {
		// Get the base news page
		Document newsPage = getDocument(DISTRICT_NEWS_URL);

		// Gets the list of news nested in the page
		Element newsList = newsPage.selectFirst("ul.news-index");

		// Gets each news item from the news list in an iterable form
		Elements newsItems = newsList.getElementsByClass("index-item");

		for (Element newsItem : newsItems) {
			// All the relevant info except the date is stored in this tag
			Element info = newsItem.getElementsByClass("item-title").first().selectFirst("a");
			String title = info.text();
			String url = "https://www.cfisd.net" + info.attr("href");

			String date = newsItem.getElementsByClass("item-date").first().text();

			news.add(new DistrictNewsItem(title, date, url));
		}
	}

	private void fetchCrimsonConnection() throws Exception {
		// Gets the base news page
		Document newsPage = getDocument(CRIMSON_CONNECTION_URL);

		// Gets the news items in an iterable form; sno-animate because they thought it
		// would be a good idea to animate them
		Elements newsItems = newsPage.selectFirst(".postarea").getElementsByClass("sno-animate");

		for (Element newsItem : newsItems) {
			// Again, pretty much all info except date is in here
			Element info = newsItem.selectFirst("h2").selectFirst("a");

			String title = info.text();
			String url = info.attr("href");

			String date = newsItem.getElementsByClass("categorydate").first().text();

			news.add(new CrimsonConnectionNewsItem(title, date, url));
		}
	}

	private void fetchAppNews() throws Exception {
		Scanner file = new Scanner(new File(APP_NEWS));

		while (file.hasNext()) {
			String date = file.nextLine();
			String title = file.nextLine();
			int priority = file.nextInt();
			file.nextLine();

			news.add(new AppNewsItem(title, date, priority));
		}
		file.close();
	}
}
