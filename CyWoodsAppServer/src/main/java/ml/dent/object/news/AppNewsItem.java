package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class AppNewsItem extends NewsItem {
	public AppNewsItem(String title, String date) throws Exception {
		super("App News", title, new SimpleDateFormat("MMMM d, y").parse(date));
	}
}