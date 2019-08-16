package ml.dent.object.athletics;

import ml.dent.json.JsonObject;

public class AthleticItem {
	private String type;
	private String opponent;
	private int ourScore;
	private int oppScore;

	public AthleticItem(String t, String o, int ourS, int oppS) {
		type = t;
		opponent = o;
		ourScore = ourS;
		oppScore = oppS;
	}

	public JsonObject getJsonData() {
		return new JsonObject().add("type", type).add("opponent", opponent).add("ourScore", ourScore).add("oppScore",
				oppScore);
	}

}
