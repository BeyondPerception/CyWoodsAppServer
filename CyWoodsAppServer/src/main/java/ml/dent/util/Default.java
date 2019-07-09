package ml.dent.util;

import ml.dent.json.JsonObject;

/**
 * This class contains static methods for returning basic useful messages to the
 * frontend in the form of JSON data.
 * 
 * @author Ronak Malik
 */
public class Default {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private Default() {
	}

	public static String NotImplemented(String message) {
		return new JsonObject().add("success", false)
				.add("code", 501)
				.add("description", "Not Implemented")
				.add("message", message)
				.format();
	}

	public static String BadRequest(String message) {
		return new JsonObject().add("success", false)
				.add("code", 400)
				.add("description", "Bad Request")
				.add("message", message)
				.format();
	}
	
	public static String BadGateway(String message) {
		return new JsonObject().add("success", false)
				.add("code", 502)
				.add("description", "Bad Gateway")
				.add("message", message)
				.format();
	}
	
	public static String NotAcceptable(String message) {
		return new JsonObject().add("success", false)
				.add("code", 406)
				.add("description", "Not Acceptable")
				.add("message", message)
				.format();
	}
	
	public static String OK(String message) {
		return new JsonObject().add("success", true)
				.add("code", 200)
				.add("description", "OK")
				.add("message", message)
				.format();
	}

	public static String InternalServerError(String message) {
		return new JsonObject().add("success", false)
				.add("code", 500)
				.add("description", "Internal Server Error")
				.add("message", message)
				.format();
	}
	
}
