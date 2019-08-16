package ml.dent.web;

import java.util.ArrayList;

import ml.dent.object.athletics.AthleticItem;

public class AthleticsFetcher {

	private ArrayList<AthleticItem> games;

	private boolean fetched;

	public AthleticsFetcher() {
		games = new ArrayList<>();
		fetched = false;
	}

	public ArrayList<AthleticItem> getGames() throws IllegalStateException {
		if (!fetched) {
			throw new IllegalStateException("populateGames must be called first!");
		}

		return games;
	}

	public void populateGames() {
		fetchFootball();
		fetchBasketball();
		fetchSoccer();
		fetchVollyball();
		fetched = true;
	}

	public void fetchFootball() {

	}

	public void fetchBasketball() {

	}

	public void fetchSoccer() {

	}

	public void fetchVollyball() {

	}

}
