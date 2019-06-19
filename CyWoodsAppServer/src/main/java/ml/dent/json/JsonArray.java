package ml.dent.json;

import java.util.*;

public class JsonArray implements JsonValue {
	private static final char CR = '\n';
	private ArrayList<JsonValue> list;

	public JsonArray() {
		list = new ArrayList<JsonValue>();
	}

	public JsonArray add(JsonValue v) {
		list.add(v);
		return this;
	}

	public JsonArray add(JsonValue... jsonValues) {
		for (JsonValue jv : jsonValues) {
			add(jv);
		}
		return this;
	}

	public JsonArray add(boolean value) {
		add(new JsonBoolean(value));
		return this;
	}

	public JsonArray add(int value) {
		add(new JsonNumber(value));
		return this;
	}

	public JsonArray add(long value) {
		add(new JsonNumber(value));
		return this;
	}

	public JsonArray add(double value) {
		add(new JsonNumber(value));
		return this;
	}

	public JsonArray add(String value) {
		add(new JsonString(value));
		return this;
	}

	public JsonArray add(JsonArray value) {
		list.add(value);
		return this;
	}

	public JsonArray add(JsonObject value) {
		list.add(value);
		return this;
	}

	public JsonArray addNull(String name) {
		add(new JsonNull());
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(CR);
		for (int i = 0; i < list.size(); i++) {
			JsonValue jv = list.get(i);
			sb.append(jv.toString());
			if (i != list.size() - 1)
				sb.append(",").append(CR);
			else
				sb.append(CR);
		}
		sb.append("]");
		return sb.toString();
	}
}
