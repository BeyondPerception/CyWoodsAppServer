package ml.dent.object.athletics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import ml.dent.json.JsonObject;

public class AthleticItem implements Comparable<AthleticItem> {
	private String sport;
	private String opponent;
	private String score;
	private String date;
	private String time;
	private String location;
	private String mapLink;

	public AthleticItem(String sp, String opp, String s, String d, String t, String loc, String link) {
		sport = sp;
		opponent = opp;
		score = s;
		date = d;
		time = t;
		location = loc;
		mapLink = link;
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("sport", sport).add("opponent", opponent).add("score", score).add("date", date)
				.add("time", time).add("location", location).add("mapLink", mapLink);
	}

	@Override
	public int compareTo(AthleticItem o) {
		Date first;
		Date second;
		try {
			first = new SimpleDateFormat("MM/dd/yy").parse(date);
			second = new SimpleDateFormat("MM/dd/yy").parse(o.date);
		} catch (ParseException e) {
			first = new Date();
			second = new Date();
		}

		return first.compareTo(second);
	}

}
