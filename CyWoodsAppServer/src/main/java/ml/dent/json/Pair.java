package ml.dent.json;

/**
 * This class represents a name-value pair that can be stored in a JsonObject.
 * The name will always be a string as per JSON specs, but the value can be a
 * range of things that implement the JsonValue interface.
 * 
 * @author Ronak Malik
 */
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
