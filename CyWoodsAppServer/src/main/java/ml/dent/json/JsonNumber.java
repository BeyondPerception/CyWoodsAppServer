package ml.dent.json;

/**
 * JSON wrapper for the already existing main types.
 * 
 * @author Ronak Malik
 */
public class JsonNumber implements JsonValue {
	private Number value;

	public JsonNumber(Integer i) {
		value = i;
	}

	public JsonNumber(Double d) {
		value = d;
	}

	public JsonNumber(Long l) {
		value = l;
	}

	@Override
	public String toString() {
		if (Double.isNaN(value.doubleValue())) {
			return new JsonNull().toString();
		}
		return value.toString();
	}
}
