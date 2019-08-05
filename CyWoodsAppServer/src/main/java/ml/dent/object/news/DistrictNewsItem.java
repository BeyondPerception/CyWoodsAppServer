package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class DistrictNewsItem extends NewsItem {
	public DistrictNewsItem(String title, String date, String url) throws Exception {
		super("District News", title, new SimpleDateFormat("E dd MMM y HH").parse(date.substring(0, date.indexOf(":")).replaceAll("[\\.,]", "").trim()), url);
	}
}
