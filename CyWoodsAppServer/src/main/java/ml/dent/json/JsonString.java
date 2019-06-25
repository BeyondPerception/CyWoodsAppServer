package ml.dent.json;

/**
 * JSON wrapper for the already existing main types.
 * 
 * @author Ronak Malik
 */
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

	private String escape(String raw) {
		String escaped = raw;
		escaped = escaped.replace("\\", "\\\\");
		escaped = escaped.replace("\"", "\\\"");
		escaped = escaped.replace("\b", "\\b");
		escaped = escaped.replace("\f", "\\f");
		escaped = escaped.replace("\n", "\\n");
		escaped = escaped.replace("\r", "\\r");
		escaped = escaped.replace("\t", "\\t");
		return escaped;
	}

	@Override
	public String toString() {
		if (value == null) {
			return new JsonNull().toString();
		}
		StringBuilder sb = new StringBuilder();
		sb.append('\"').append(escape(value)).append('\"');
		return sb.toString();
	}
}
