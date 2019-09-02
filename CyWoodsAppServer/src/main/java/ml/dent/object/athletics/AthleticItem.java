package ml.dent.object.athletics;

import ml.dent.json.JsonObject;

public class AthleticItem {
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

}
