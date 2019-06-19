package ml.dent.util;

import ml.dent.json.JsonObject;

/**
 * This class contains static methods for returning basic useful messages to the
 * frontend in the form of JSON data.
 * 
 * @author Ronak Malik
 */
public class Error {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private Error() {
	}

	public static String NotImplemented(String message) {
		return new JsonObject().add("success", false)
				.add("code", 501)
				.add("description", "Not Implemented")
				.add("message", message)
				.toString();
	}

	public static String BadRequest(String message) {
		return new JsonObject().add("success", false)
				.add("code", 400)
				.add("description", "Bad Request")
				.add("message", message)
				.toString();
	}
	
	public static String OK(String message) {
		return new JsonObject().add("success", true)
				.add("code", 200)
				.add("description", "OK")
				.add("message", message)
				.toString();
	}
	
}
