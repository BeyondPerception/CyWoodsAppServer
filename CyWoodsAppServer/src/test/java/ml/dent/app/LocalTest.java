package ml.dent.app;

import ml.dent.web.StudentFetcher;

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

		StudentFetcher grades = new StudentFetcher("s692278", "Password12345", "10");
		long start = System.currentTimeMillis();
		String ret = grades.populateStudent();
		System.out.println(ret);
		String test = grades.returnStudent().getJsonData().format();
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(test);

//		System.out.println(System.currentTimeMillis() - start);

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

//		LocalDateTime start = LocalDateTime.now();
//		Thread.sleep(10000);
//		LocalDateTime now = LocalDateTime.now();
//		System.out.println(start.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC));

//		NewsFetcher nf = new NewsFetcher();
//		nf.populateNews();
//
//		TreeSet<NewsItem> news = nf.getNews();
//
//		JsonArray newsArray = new JsonArray();
//
//		for (NewsItem val : news) {
//			newsArray.add(val.getJsonData());
//		}
//
//		System.out.println(new JsonObject().add("news", newsArray).format());

//		String regex = "[^\\s\"']+|\"([^\"]*)\"";
//
//		String match = "\"1st period\" 10:20 14:40";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(match);
//		String[] line = new String[3];
//		for (int i = 0; i < 3 && matcher.find(); i++) {
//			line[i] = matcher.group();
//		}

//		AthleticsFetcher af = new AthleticsFetcher();
//		af.populateGames();
//
//		Vector<AthleticItem> items = af.getGames();
//
//		JsonObject jO = new JsonObject();
//
//		JsonArray ja = new JsonArray();
//
//		for (AthleticItem ai : items) {
//			ja.add(ai.getJsonData());
//		}
//
//		jO.add("games", ja);
//
//		System.out.println(jO.format());

//		Date d = new SimpleDateFormat("MMMM dd yyyy").parse("September 1 2019");
//		
//		System.out.println(LocalDate.now());
	}
}
