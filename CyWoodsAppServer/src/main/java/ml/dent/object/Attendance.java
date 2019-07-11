package ml.dent.object;

import java.util.ArrayList;

public class Attendance {
	private ArrayList<AttendanceBlock> days;

	public Attendance() {
		days = new ArrayList<>();
	}

	public void addBlock(AttendanceBlock aB) {
		days.add(aB);
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
