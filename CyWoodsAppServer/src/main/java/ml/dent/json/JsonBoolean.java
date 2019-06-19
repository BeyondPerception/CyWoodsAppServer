package ml.dent.json;

public class JsonBoolean implements JsonValue {
	private Boolean value;

	public JsonBoolean(boolean b) {
		value = b;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
