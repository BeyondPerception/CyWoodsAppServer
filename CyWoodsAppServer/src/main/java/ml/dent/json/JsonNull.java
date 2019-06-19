package ml.dent.json;

/**
 * JSON wrapper for the already existing main types.
 * 
 * @author Ronak Malik
 */
public class JsonNull implements JsonValue {
	@Override
	public String toString() {
		return "null";
	}
}
