package ml.dent.object.news;

import java.text.SimpleDateFormat;

public class DistrictNewsItem extends NewsItem {
	public DistrictNewsItem(String title, String date, String url) throws Exception {
		super("District News", title, new SimpleDateFormat("MMM. dd, yyyy").parse(date), url, 1);
	}
}
