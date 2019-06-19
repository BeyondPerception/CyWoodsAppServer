package ml.dent.json;

import java.util.ArrayList;
import java.util.Collections;

public class JsonObject implements JsonValue {
	private static final char CR = '\n';
	private ArrayList<Pair> list;

	public JsonObject() {
		list = new ArrayList<Pair>();
	}

	public JsonObject add(Pair p) {
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
		int level = 0;
		sb.append("{").append(CR);
		for (int i = 0; i < list.size(); i++) {
			Pair p = list.get(i);
			String tak = p.toString();
			if (tak.contains("{") || tak.contains("[")) {
				level++;
			}
			if (tak.contains("}") || tak.contains("]")) {
				level--;
			}
			tak = String.join("", Collections.nCopies(level, "\t")) + tak;
			sb.append(tak);
			if (i != list.size() - 1)
				sb.append(",").append(CR);
			else
				sb.append(CR);
		}
		sb.append("}");
		return sb.toString();
	}
}
