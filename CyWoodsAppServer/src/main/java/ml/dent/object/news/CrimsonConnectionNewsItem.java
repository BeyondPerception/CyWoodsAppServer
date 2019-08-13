package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class CrimsonConnectionNewsItem extends NewsItem {
	public CrimsonConnectionNewsItem(String title, String date, String url) throws Exception {
		super("Crimson Connection", title, new SimpleDateFormat("MMMM d y").parse(date.replaceAll("[\\.,]", "")), url,
				1);
	}
}
