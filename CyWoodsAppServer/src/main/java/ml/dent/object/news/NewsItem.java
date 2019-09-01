package ml.dent.object.news;

import java.text.SimpleDateFormat;
import java.util.Date;

import ml.dent.json.JsonObject;

public class NewsItem implements Comparable<NewsItem> {
	private String type;

	private String title;
	private Date date;

	private Integer priority;

	private String url;

	public NewsItem(String source, String title, Date date, int priority) {
		this.type = source;
		this.title = title;
		this.date = date;
		this.priority = priority;
	}

	public NewsItem(String source, String title, Date date, String url, int priority) {
		this(source, title, date, priority);
		this.url = url;
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("type", type).add("title", title)
				.add("date", new SimpleDateFormat("MM/dd/yy").format(date)).add("url", url);
	}

	public String getURL() {
		return url;
	}

	public void setURL(String u) {
		url = u;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
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
		if (other.priority > priority)
			return 1;
		if (other.priority < priority)
			return -1;

		int dateCompare = date.compareTo(other.date);
		if (dateCompare != 0) {
			return -dateCompare;
		}

		return title.compareTo(other.title);
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
