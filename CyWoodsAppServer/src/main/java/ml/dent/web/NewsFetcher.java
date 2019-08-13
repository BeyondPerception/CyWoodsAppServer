package ml.dent.web;

import java.util.ArrayList;

import ml.dent.object.news.NewsItem;

public class NewsFetcher extends AbstractFetcher {
	private static final String SCHOOL_NEWS_URL = "https://cywoods.cfisd.net/en/news/school-news/";
	private static final String CRIMSON_CONNECTION_URL = "http://www.thecrimsonconnection.com/category/news/cy-woods/";
	private static final String APP_NEWS = System.getProperty("user.home") + "AppNews.txt";

	ArrayList<NewsItem> news;

	public NewsFetcher() {
		news = new ArrayList<>();
	}

}
