package ml.dent.json;

public class Pair {
	private String name;
	private JsonValue value;

	public Pair(String name, JsonValue value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(name).append("\"").append(": ").append(value.toString());
		return sb.toString();
	}
}
