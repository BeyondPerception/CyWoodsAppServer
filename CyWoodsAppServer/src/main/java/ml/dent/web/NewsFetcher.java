package ml.dent.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import ml.dent.object.news.AppNewsItem;
import ml.dent.object.news.NewsItem;

public class NewsFetcher extends AbstractFetcher {
	private static final String SCHOOL_NEWS_URL = "https://cywoods.cfisd.net/en/news/school-news/";
	private static final String CRIMSON_CONNECTION_URL = "http://www.thecrimsonconnection.com/category/news/cy-woods/";
	private static final String DISTRICT_NEWS_URL = "https://www.cfisd.net/en/news-media/district/";
	private static final String APP_NEWS = System.getProperty("user.home") + "/AppNews.txt";

	private ArrayList<NewsItem> news;

	private boolean fetched;

	public NewsFetcher() {
		news = new ArrayList<>();
		fetched = false;
	}

	public ArrayList<NewsItem> getNews() throws IllegalStateException {
		if (!fetched) {
			throw new IllegalStateException("populateNews must be called first");
		}
		Collections.sort(news);
		return news;
	}

	public void populateNews() throws Exception {
		fetchSchoolNews();
		fetchDistrictNews();
		fetchCrimsonConnection();
		fetchAppNews();
	}

	private void fetchSchoolNews() {

	}

	private void fetchDistrictNews() {

	}

	private void fetchCrimsonConnection() {

	}

	private void fetchAppNews() throws Exception {
		Scanner file = new Scanner(new File(APP_NEWS));

		while (file.hasNext()) {
			String date = file.nextLine();
			String title = file.nextLine();
			String url = file.nextLine();
			int priority = file.nextInt();
			file.nextLine();

			news.add(new AppNewsItem(title, date, priority));
		}
	}
}
