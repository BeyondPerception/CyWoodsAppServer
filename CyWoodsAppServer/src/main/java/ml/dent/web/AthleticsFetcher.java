package ml.dent.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ml.dent.object.athletics.AthleticItem;

public class AthleticsFetcher extends AbstractFetcher {

	private static final String[] SPORT_LINKS = {
			"https://www.rankonesport.com/Schedules/View_Schedule.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=3&Tm=20709&L=1",
			"https://www.rankonesport.com/Schedules/View_Schedule.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=7&Tm=20703&L=1",
			"https://www.rankonesport.com/Schedules/View_Schedule.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=5&Tm=20755&L=1", };

	// Must use Vector to keep synchronization
	private Vector<AthleticItem> games;

	private Vector<AtomicBoolean> fin; // can really be anything, booleans for stat purposes

	private boolean fetched;

	public AthleticsFetcher() {
		games = new Vector<>();
		fetched = false;
		fin = new Vector<>();
	}

	public Vector<AthleticItem> getGames() throws IllegalStateException {
		if (!fetched) {
			throw new IllegalStateException("populateGames must be called first!");
		}

		Collections.sort(games);
		return games;
	}

	private boolean ready() {
		if (fin.size() == SPORT_LINKS.length) {
			return true;
		}
		return false;
	}

	public void populateGames() {
		fetchSports();
		while (!ready()) {
			// block
		}
		fetched = true;
	}

	// All the schedule pages look the same, so we can treat them all the same
	public void fetchSports() {
		for (String LINK : SPORT_LINKS) {
			try {
				// Get the main page for a particular game
				Document gamePage = getDocument(LINK);

				// The actual list of games
				Elements gameTable = gamePage.selectFirst("#dvSchedules > div:nth-child(4) > table > tbody")
						.select("tr");

				// Using index to easily skip first row which is the header
				for (int i = 1; i < gameTable.size(); i++) {
					try {
						// I try to use selectFirst if it is the only tag, and then either get, first,
						// or last if there are multiple tags
						Element row = gameTable.get(i).selectFirst("td");
						Elements rowElements = row.children();

						Elements dateTimeDiv = rowElements.get(0).select("div");
						String[] dateTime = dateTimeDiv.first().text().split(" ");
						String date = dateTime[0];
						String time = dateTime[1];

						String opponent = rowElements.get(1).selectFirst("span").text();

						String sport = rowElements.get(2).select("span").first().text();

						Element locationLink;
						String mapLink;
						String location;
						try {
							locationLink = rowElements.get(3).selectFirst("a");
							mapLink = locationLink.attr("href");
							location = locationLink.selectFirst("span").text();
						} catch (NullPointerException e) {
							// Location may not be set
							mapLink = null;
							location = null;
						}

						String score = rowElements.get(4).selectFirst("div").selectFirst("span").text();

						games.add(new AthleticItem(sport, opponent, score, date, time, location, mapLink));
					} catch (Exception e) {
						// If it fails for one game keep trying
						e.printStackTrace();
					}
				}
				fin.add(new AtomicBoolean(true));
			} catch (IOException e) {
				e.printStackTrace();
				// Continue fetching other sports
				fin.add(new AtomicBoolean(false));
			}
		}
	}
}
