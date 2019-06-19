package ml.dent.json;

public class JsonString implements JsonValue {
	private String value;

	public JsonString() {
		setString("");
	}

	public JsonString(String s) {
		setString(s);
	}

	public String getString() {
		return value;
	}

	public void setString(String string) {
		this.value = string;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\"').append(value).append('\"');
		return sb.toString();
	}
}
