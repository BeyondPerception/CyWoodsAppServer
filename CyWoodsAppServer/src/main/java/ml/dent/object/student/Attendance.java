package ml.dent.object.student;

import java.util.ArrayList;

import ml.dent.json.JsonObject;

public class Attendance {
	private ArrayList<AttendanceBlock> days;

	public Attendance() {
		days = new ArrayList<>();
	}

	public void addBlock(AttendanceBlock aB) {
		days.add(aB);
	}

	public JsonObject getJsonData() {
		JsonObject res = new JsonObject();
		for (AttendanceBlock aB : days) {
			res.add(aB.day, aB.marker);
		}
		return res;
	}

	public static class AttendanceBlock {
		private String day;
		private String marker;

		public AttendanceBlock() {
			setDay(null);
		}

		public AttendanceBlock(String day, String marker) {
			this.day = day;
			this.marker = marker;
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getMarker() {
			return marker;
		}

		public void setMarker(String marker) {
			this.marker = marker;
		}
	}
}
