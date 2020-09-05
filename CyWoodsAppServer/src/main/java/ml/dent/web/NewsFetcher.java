package ml.dent.web;

import ml.dent.object.news.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewsFetcher extends AbstractFetcher {
    private static final String SCHOOL_NEWS_URL        = "https://cywoods.cfisd.net/en/news/school-news/";
    private static final String CRIMSON_CONNECTION_URL = "https://www.thecrimsonconnection.com/category/news/cy-woods/";
    private static final String DISTRICT_NEWS_URL      = "https://www.cfisd.net/en/news-media/district/";
    private static final String APP_NEWS               = "/efs/UpdateFiles/AppNews.txt";

    private Set<NewsItem> news;

    private boolean fetched;

    private AtomicBoolean[] fin;

    public NewsFetcher() {
        TreeSet<NewsItem> treeSet = new TreeSet<>();
        news = Collections.synchronizedSet(treeSet);
        fetched = false;
        fin = new AtomicBoolean[4];
        for (int i = 0; i < fin.length; i++) {
            fin[i] = new AtomicBoolean();
        }
    }

    // Returns true if this class has completed its task
    public boolean ready() {
        boolean res = true;

        for (AtomicBoolean bool : fin) {
            if (!bool.get()) {
                res = false;
                return res;
            }
        }
        return res;
    }

    public Set<NewsItem> getNews() throws IllegalStateException {
        if (!fetched) {
            throw new IllegalStateException("populateNews must be called first");
        }
        return news;
    }

    public void populateNews() throws Exception {
        boolean[] failed = new boolean[4];
        new Thread(() -> {
            try {
                fetchSchoolNews();
            } catch (Exception e) {
                // Continue trying to fetch
                failed[0] = true;
            }
            fin[0].set(true); // Thread completed
        }).start();

        new Thread(() -> {
            try {
                fetchDistrictNews();
            } catch (Exception e) {
                // Continue trying to fetch
                failed[1] = true;
            }
            fin[1].set(true);
        }).start();

        new Thread(() -> {
            try {
                fetchCrimsonConnection();
            } catch (Exception e) {
                // Continue trying to fetch
                failed[2] = true;
            }
            fin[2].set(true);
        }).start();

        new Thread(() -> {
            try {
                fetchAppNews();
            } catch (Exception e) {
                // Continue trying to fetch
                failed[3] = true;
            }
            fin[3].set(true);
        }).start();

        if (!Arrays.equals(failed, new boolean[]{true, true, true, true})) {
            fetched = true;
        }
        while (!ready()) {
            // block
        }
    }

    private void fetchSchoolNews() throws Exception {
        // Get the base news page
        Document newsPage = getDocument(SCHOOL_NEWS_URL);

        // Gets the list of news nested in the page
        Element newsList = newsPage.selectFirst("ul.news-index");

        // Gets each news item from the news list in an iterable form
        Elements newsItems = newsList.getElementsByClass("index-item");

        for (Element newsItem : newsItems) {
            // All the relevant info except the date is stored in this tag
            Element info = newsItem.getElementsByClass("item-title").first().selectFirst("a");
            String title = info.text();
            String url = info.attr("href");

            String date = newsItem.getElementsByClass("item-date").first().text();

            news.add(new SchoolNewsItem(title, date, url));
        }
    }

    // I didn't realize this would be the exact same code...
    private void fetchDistrictNews() throws Exception {
        // Get the base news page
        Document newsPage = getDocument(DISTRICT_NEWS_URL);

        // Gets the list of news nested in the page
        Element newsList = newsPage.selectFirst("ul.news-index");

        // Gets each news item from the news list in an iterable form
        Elements newsItems = newsList.getElementsByClass("index-item");

        for (Element newsItem : newsItems) {
            // All the relevant info except the date is stored in this tag
            Element info = newsItem.getElementsByClass("item-title").first().selectFirst("a");
            String title = info.text();
            String url = info.attr("href");

            String date = newsItem.getElementsByClass("item-date").first().text();

            news.add(new DistrictNewsItem(title, date, url));
        }
    }

    private void fetchCrimsonConnection() throws Exception {
        // Gets the base news page
        Document newsPage = getDocument(CRIMSON_CONNECTION_URL);

        // Gets the news items in an iterable form; sno-animate because they thought it
        // would be a good idea to animate them
        Elements newsItems = newsPage.selectFirst(".postarea").getElementsByClass("sno-animate");

        for (Element newsItem : newsItems) {
            // Again, pretty much all info except date is in here
            Element info = newsItem.selectFirst("h2").selectFirst("a");

            String title = info.text();
            String url = info.attr("href");

            String date = newsItem.getElementsByClass("categorydate").first().text();

            news.add(new CrimsonConnectionNewsItem(title, date, url));
        }
    }

    private void fetchAppNews() throws Exception {
        Scanner file = new Scanner(new File(APP_NEWS));

        while (file.hasNext()) {
            String date = file.nextLine();
            String title = file.nextLine();
            int priority = file.nextInt();
            file.nextLine();

            news.add(new AppNewsItem(title, date, priority));
        }
        file.close();
    }
}
