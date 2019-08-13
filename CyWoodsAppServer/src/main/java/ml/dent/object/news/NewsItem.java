package ml.dent.object.news;

import java.text.SimpleDateFormat;
import java.util.Date;

import ml.dent.json.JsonObject;

public class NewsItem implements Comparable<NewsItem> {
	private String type;

	private String title;
	private String date;

	private Integer priority;

	private String url;

	public NewsItem(String source, String title, Date date) {
		this.type = source;
		this.title = title;
		this.date = new SimpleDateFormat("MMM d, y").format(date);
	}

	public NewsItem(String source, String title, Date date, String url) {
		this(source, title, date);
		this.url = url;
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("type", type).add("title", title).add("date", date).add("priority", priority)
				.add("url", url);
	}

	public String getURL() {
		return url;
	}

	public void setURL(String u) {
		url = u;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String d) {
		date = d;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		title = t;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer p) {
		priority = p;
	}

	public String getType() {
		return type;
	}

	public void setType(String source) {
		type = source;
	}

	@Override
	public int compareTo(NewsItem other) {
		SimpleDateFormat format = new SimpleDateFormat("MMM d, y");
		try {
			int compare = format.parse(other.date).compareTo(format.parse(date));
			if (compare == 0)
				compare = title.compareTo(other.title);
			return compare;
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof NewsItem) {
			NewsItem o = (NewsItem) other;
			return compareTo(o) == 0;
		}
		return false;
	}
}
