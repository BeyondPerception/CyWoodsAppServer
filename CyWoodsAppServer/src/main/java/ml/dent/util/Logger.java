package ml.dent.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * 
 * This is a system wide student logger that logs errrors
 * 
 * @author Ronak Malik
 */
public class Logger {

	private static final String LOG_DIR = "/efs/logs/";
	private String logDir;

	private PrintWriter pw;

	private static String currentDate;

	private File logFile;

	{
		File logDir = new File(LOG_DIR);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
	}

	public Logger(String internalDir) {
		currentDate = LocalDate.now().toString();
		logDir = LOG_DIR + internalDir + "/";
		logFile = new File(logDir + currentDate);
	}

	private void check() {
		File logDirTest = new File(logDir);
		if (!logDirTest.exists()) {
			logDirTest.mkdirs();
		}
		if (!currentDate.equals(LocalDate.now().toString())) {
			currentDate = LocalDate.now().toString();
		}
		logFile = new File(logDir + currentDate);
		if (pw == null) {
			try {
				pw = new PrintWriter(logFile);
			} catch (FileNotFoundException e) {
				// be sad, nowhere to log :(
				e.printStackTrace();
			}
		}
	}

	public void logError(Exception e) {
		check();
		e.printStackTrace(pw);
		e.printStackTrace();
		pw.println();
		pw.flush();
	}

	public void log(String s) {
		check();
		pw.println(s);
		pw.flush();
	}
}
