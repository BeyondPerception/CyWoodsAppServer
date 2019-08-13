package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class CrimsonConnectionNewsItem extends NewsItem {
	public CrimsonConnectionNewsItem(String title, String date, String url) throws Exception {
		super("Crimson Connection", title, new SimpleDateFormat("MMMM dd, yyyy").parse(date), url, 1);
	}
}
