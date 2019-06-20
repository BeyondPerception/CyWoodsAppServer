package ml.dent.app;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;

public class LocalTest {
	public static void main(String[] args) {
		JsonObject test = new JsonObject();
		test.add("firstName", "John")
	     .add("lastName", "Smith")
	     .add("age", 25)
	     .add("address", new JsonObject()
	         .add("streetAddress", "21 2nd Street")
	         .add("city", "New York")
	         .add("state", "NY")
	         .add("postalCode", "10021"))
	     .add("phoneNumber", new JsonArray()
	         .add(new JsonObject()
	             .add("type", "home")
	             .add("number", "212 555-1234"))
	         .add(new JsonObject()
	             .add("type", "fax")
	             .add("number", "646 555-4567")));
		
		System.out.println(test.format());
		
	}
}
