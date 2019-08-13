package ml.dent.app;

import java.util.ArrayList;

import ml.dent.json.JsonArray;
import ml.dent.json.JsonObject;
import ml.dent.object.news.NewsItem;
import ml.dent.web.NewsFetcher;

public class LocalTest {
	public static void main(String[] args) throws Exception {
//		JsonObject test = new JsonObject();
//		test.add("firstName", "John")
//	     .add("lastName", "Smith")
//	     .add("age", 25)
//	     .add("address", new JsonObject()
//	         .add("streetAddress", "21 2nd Street")
//	         .add("city", "New York")
//	         .add("state", "NY")
//	         .add("postalCode", "10021"))
//	     .add("phoneNumber", new JsonArray()
//	         .add(new JsonObject()
//	             .add("type", "home")
//	             .add("number", "212 555-1234"))
//	         .add(new JsonObject()
//	             .add("type", "fax")
//	             .add("number", "646 555-4567")));
//		
//		System.out.println(test.format());

//		String username = "s692278";
//		String password = "Smart10334";
//
//		StudentFetcher grades = new StudentFetcher(username, password);
//		long start = System.currentTimeMillis();
//		String ret = grades.populateStudent();
//		System.out.println(ret);
//		System.out.println(grades.returnStudent().getJsonData().format());
//		System.out.println(System.currentTimeMillis() - start);
//
//		System.out.println(new JsonObject().add("08/10", "School Closed").add("08/11", "1 No Contact\n2 No Contact")
//				.add("08/12", "1 No Contact").format());

//		FacultyFetcher ff = new FacultyFetcher();
//		ff.fetchFaculty();
//
//		ArrayList<Teacher> fac = ff.getFaculty();
//
//		JsonObject jo = new JsonObject();
//		JsonArray teachers = new JsonArray();
//
//		for (Teacher val : fac) {
//			teachers.add(val.getJsonData());
//		}
//
//		System.out.println(jo.add("faculty", teachers).format());

//		Date d = new SimpleDateFormat("MM/dd/yy").parse("08/13/19");
//		System.out.println(new SimpleDateFormat("MM/dd/yyyy").format(d));

		NewsFetcher nf = new NewsFetcher();
		nf.populateNews();

		ArrayList<NewsItem> news = nf.getNews();

		JsonArray newsArray = new JsonArray();

		for (NewsItem val : news) {
			newsArray.add(val.getJsonData());
		}

		System.out.println(new JsonObject().add("news", newsArray).format());

//		String regex = "[^\\s\"']+|\"([^\"]*)\"";
//
//		String match = "\"1st period\" 10:20 14:40";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(match);
//		String[] line = new String[3];
//		for (int i = 0; i < 3 && matcher.find(); i++) {
//			line[i] = matcher.group();
//		}
	}
}
