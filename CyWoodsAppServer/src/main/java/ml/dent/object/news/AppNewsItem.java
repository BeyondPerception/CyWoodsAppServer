package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class AppNewsItem extends NewsItem {
	public AppNewsItem(String title, String date, int priority) throws Exception {
		super("App News", title, new SimpleDateFormat("MM/dd/YYYY").parse(date), priority);
	}
}
