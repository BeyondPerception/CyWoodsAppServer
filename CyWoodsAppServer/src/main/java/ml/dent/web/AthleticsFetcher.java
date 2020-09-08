package ml.dent.web;

import ml.dent.object.athletics.AthleticItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class AthleticsFetcher extends AbstractFetcher {

    private static final Map<String, String> SPORT_LINKS = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("Football (M)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=3&Tm=20709&L=1&Mt=0");
            put("Volleyball (F)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=5&Tm=20755&L=1&Mt=0");
            put("Basketball (M)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5e3c64f6-eabb-401d-8901-2da09a8500c8&S=1017&Sp=7&L=0&Mt=0");
            put("Basketball (F)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5E3C64F6-EABB-401D-8901-2DA09A8500C8&S=1017&Sp=26&Tm=20732&L=1&Mt=0");
            put("Soccer (M)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5e3c64f6-eabb-401d-8901-2da09a8500c8&S=1017&Sp=4&L=0&Mt=0");
            put("Soccer (F)", "https://www.rankonesport.com/Schedules/View_Schedule_Web.aspx?P=0&D=5e3c64f6-eabb-401d-8901-2da09a8500c8&S=1017&Sp=25&L=0&Mt=0");
        }
    });

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
        if (fin.size() == SPORT_LINKS.size()) {
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
        for (String sport : SPORT_LINKS.keySet()) {
            try {
                String LINK = SPORT_LINKS.get(sport);
                // Get the main page for a particular game
                Document gamePage = getDocument(LINK);

                // The actual list of games
                Elements gameTable = gamePage.selectFirst(".table > tbody:nth-child(1)").select("tr");

                // Using index to easily skip first row which is the header
                for (int i = 1; i < gameTable.size(); i++) {
                    try {
                        // I try to use selectFirst if it is the only tag, and then either get, first,
                        // or last if there are multiple tags
                        Element row = gameTable.get(i).selectFirst("td");
                        Elements rowElements = row.children();

                        Elements dateTimeDivs = rowElements.get(0).select("div");
                        String date = dateTimeDivs.first().selectFirst("span").ownText();
                        String time = dateTimeDivs.last().selectFirst("span").ownText();

                        String opponent = rowElements.get(2).selectFirst("span").text();

                        Element locationLink;
                        String mapLink;
                        String location;
                        try {
                            locationLink = rowElements.get(3).selectFirst("a");
                            location = rowElements.get(3).select("div").last().selectFirst("span").text();
                            mapLink = locationLink.attr("href").replace(" ", "+");
                        } catch (NullPointerException e) {
                            // Location may not be set
                            mapLink = null;
                            location = null;
                        }

                        String score = "-";
                        Element scoreSpan = rowElements.get(4).selectFirst("div").selectFirst("span");
                        if (scoreSpan != null) {
                            score = scoreSpan.text();
                        }

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
