package ml.dent.object.news;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsItem implements Comparable<NewsItem> {
	private String Source;

	private String Title;
	private String Date;

	private Integer Priority;

	private String URL;

	public NewsItem(String source, String title, Date date) {
		this.Source = source;
		this.Title = title;
		this.Date = new SimpleDateFormat("MMM d, y").format(date);
	}

	public NewsItem(String source, String title, Date date, String url) {
		this(source, title, date);
		this.URL = url;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public Integer getPriority() {
		return Priority;
	}

	public void setPriority(Integer priority) {
		Priority = priority;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	@Override
	public int compareTo(NewsItem other) {
		SimpleDateFormat format = new SimpleDateFormat("MMM d, y");
		try {
			int compare = format.parse(other.Date).compareTo(format.parse(Date));
			if (compare == 0)
				compare = Title.compareTo(other.Title);
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
