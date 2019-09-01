package ml.dent.object.athletics;

import ml.dent.json.JsonObject;

public class AthleticItem {
	private String sport;
	private String opponent;
	private String ourScore;
	private String oppScore;
	private String dateTime;
	private String location;

	public AthleticItem(String sp, String opp, String ourS, String oppS, String dT, String loc) {
		sport = sp;
		opponent = opp;
		ourScore = ourS;
		oppScore = oppS;
		dateTime = dT;
		location = loc;
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("sport", sport).add("opponent", opponent).add("ourScore", ourScore)
				.add("oppScore", oppScore).add("dateTime", dateTime).add("location", location);
	}

}
