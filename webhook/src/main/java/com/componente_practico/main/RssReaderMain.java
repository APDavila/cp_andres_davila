package com.componente_practico.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RssReaderMain {

	public static void main(String[] args) throws IllegalArgumentException, FeedException, IOException {
		URL url = new URL("http://rss.cnn.com/rss/edition_sport.rss");
		HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
		// Reading the feed
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(httpcon));
		List entries = feed.getEntries();
		Iterator itEntries = entries.iterator();
 
		while (itEntries.hasNext()) {
			SyndEntry entry = (SyndEntry)itEntries.next();
			System.out.println("Title: " + entry.getTitle());
			System.out.println("Link: " + entry.getLink());
			System.out.println("Author: " + entry.getAuthor());
			System.out.println("Publish Date: " + entry.getPublishedDate());
			System.out.println("Description: " + entry.getDescription().getValue());
			
			MediaEntryModule m = (MediaEntryModule)entry.getModule(MediaEntryModule.URI);
            System.out.println(m.getMediaGroups()[0].getContents()[0].getReference());  
			
			System.out.println();
		}

	}

}
