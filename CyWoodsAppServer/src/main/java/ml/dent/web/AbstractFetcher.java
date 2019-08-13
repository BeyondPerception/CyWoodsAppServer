package ml.dent.web;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class AbstractFetcher {
	protected Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0")
				.get();
	}
}
