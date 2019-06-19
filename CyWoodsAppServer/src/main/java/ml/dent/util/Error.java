package ml.dent.util;

/**
 * This class contains static methods for returning basic useful messages to the
 * frontend in the form of JSON data.
 * 
 * @author Ronak Malik
 */
public class Error {

	private static final char CR = '\n';
	private static final char TAB = '\t';

	/**
	 * Don't let anyone instantiate this class.
	 */
	private Error() {
	}

	public static String NotImplemented(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(CR);
		sb.append("\t\"Error\" : \"501 Not Implemented\"").append(CR);
		sb.append("}");
		return sb.toString();
	}

	public static String UnprocessableEntity(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(CR);
		sb.append("\t\"code\" : \"422 422 Unprocessable Entity\"").append(CR);
		sb.append("\t\"\" : ").append(CR);
		sb.append("}");
		return sb.toString();
	}
}
