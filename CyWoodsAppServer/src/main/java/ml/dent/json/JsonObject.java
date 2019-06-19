package ml.dent.json;

import java.util.ArrayList;

/**
 * This is probably where you should start before sending information from the
 * servlets. This package and series of classes allows for a simple and
 * structured way to create JSON data and send it as strings to the client. <br>
 * Usage: <br>
 * Using these classes is fairly simple, all you need to do is create an
 * instance of the JsonObject class and add Pairs to it. A Pair is a
 * String-Value pair that should contain useful information. A value can range
 * from another JsonObject, a JsonArray, or any other class that implements the
 * JsonValue interface. <br>
 * <br>
 * Calling the toString method in any of these classes makes JSON compatible
 * strings that can be inserted into the other JSON suite of classes. ALWAYS
 * call the toString on the original JsonObject before sending it to the client.
 * If you wish to extend these classes, make sure they have a working toString
 * method.<br>
 * <br>
 * Why did I write these classes versus just using a library? idk, cause I felt
 * like it.
 * 
 * @author Ronak Malik
 */
public class JsonObject implements JsonValue {
	private static final char LF = '\n';
	private static final String TAB = "  "; // 2 space tabs looks nice in JSON

	private ArrayList<Pair> list;

	public JsonObject() {
		list = new ArrayList<Pair>();
	}

	private JsonObject add(Pair p) {
		list.add(p);
		return this;
	}

	public JsonObject add(String name, boolean value) {
		add(new Pair(name, new JsonBoolean(value)));
		return this;
	}

	public JsonObject add(String name, int value) {
		add(new Pair(name, new JsonNumber(value)));
		return this;
	}

	public JsonObject add(String name, long value) {
		add(new Pair(name, new JsonNumber(value)));
		return this;
	}

	public JsonObject add(String name, double value) {
		add(new Pair(name, new JsonNumber(value)));
		return this;
	}

	public JsonObject add(String name, String value) {
		add(new Pair(name, new JsonString(value)));
		return this;
	}

	public JsonObject add(String name, JsonObject value) {
		add(new Pair(name, value));
		return this;
	}

	public JsonObject add(String name, JsonArray value) {
		add(new Pair(name, value));
		return this;
	}

	public JsonObject addNull(String name) {
		add(new Pair(name, new JsonNull()));
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(LF);
		for (int i = 0; i < list.size(); i++) {
			Pair p = list.get(i);
			sb.append(p.toString());
			if (i != list.size() - 1)
				sb.append(",").append(LF);
			else
				sb.append(LF);
		}
		sb.append("}");
		return sb.toString();
	}
}
