package ml.dent.json;

/**
 * JSON wrapper for the already existing main types.
 * 
 * @author Ronak Malik
 */
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
