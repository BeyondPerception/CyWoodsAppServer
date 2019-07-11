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
		private ArrayList<String> markers;

		public AttendanceBlock() {
			setDay(null);
			markers = new ArrayList<>();
		}

		public AttendanceBlock(ArrayList<String> s) {
			markers = s;
		}

		public void addMarker(String s) {
			markers.add(s);
		}

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}
	}
}
