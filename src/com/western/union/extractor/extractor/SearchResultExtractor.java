package com.western.union.extractor.extractor;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.western.union.extractor.config.Configuration;
import com.western.union.extractor.data.AddressData;
import com.western.union.extractor.util.LoggerUtil;

import static com.western.union.extractor.util.XmlUtil.*;

public class SearchResultExtractor {
	private static final Logger log = LoggerUtil.getLogger(SearchResultExtractor.class);
	
	public static final String EL_SEARCH_WORD = "\\[SEARCHWORDS\\]";
	
	private final Configuration config;
	public SearchResultExtractor(final Configuration config) {
		this.config = config;
	}
	
	private Document getListHtmlDocument() throws IOException {
		Document doc = Jsoup.connect(buildSearchUrl(config))
				.userAgent(config.getUserAgent())
				.get();
		return doc;
	}
	
	private String buildSearchUrl(final Configuration config) {
		String url = config.getSourceUrl();
		
		url = url.replaceAll(EL_SEARCH_WORD, config.getSearchWord());
		
		return url;
	}
	
	public void forEach(final IDetailExtractor detail) {
		List<Element> linkList;
		try {
			Document doc = getListHtmlDocument();
			linkList = doc.select(config.getListCssQuery());
			
			if (linkList.size() > 0) {
				if (config.getProcessLimit() > 0) {
					linkList = linkList.subList(0, config.getProcessLimit());
				}
				log.log(Level.INFO, "Zu verarbeitende Daten: " + linkList.size());
				int msgAfterCount = config.getMsgAfterCount();
				
				int singleProcessLeft = msgAfterCount;
//				for (Element linkTag: linkList) {
				int listSize = linkList.size();
				for (int i = 0; i < listSize; i++) {
					Element linkTag = linkList.get(i);
					
					Element listData = getListData(linkTag);
					AddressData data = new AddressData();
					data.setName(getName(linkTag));
					data.setReferenceNumber(getReferenceNumber(listData));
					data.setLink(linkTag.attr("href"));
					data.setTradingBrandName(getTradingOrBrandNames(listData));
					data.setBusinessType(getBusinessType(listData));
					data.setStatus(getStatus(listData));
					
					detail.detailsFor(data);
					
					singleProcessLeft--;
					if (msgAfterCount > 0 && singleProcessLeft <= 0) {
						log.log(Level.INFO, "Verarbeitete Daten: " + (i + 1));
						singleProcessLeft = msgAfterCount;
					}
				}
				log.log(Level.INFO, "Verarbeitete Daten insgesamt: " + listSize);
			} else {
				log.log(Level.INFO, "Nur ein Datensatz");
				AddressData data = new AddressData();
				data.setLink(getDetailLink(doc));
				
				detail.detailsFor(data);
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim auslesen der Liste.", e);
		}
	}
	
	private String getDetailLink(final Document doc) {
		String detailLink = "";
		if (doc.toString().contains("window.location.href")) {
			Elements scripts = doc.select("script");
			for (Element script: scripts) {
				String data = script.data();
				if (data.toString().contains("window.location.href")) {
					Matcher redirectUrlMatcher = Pattern.compile(".*?window\\.location\\.href\\s*=\\s*['|\"]([\\w\\.:\\/\\?=\\&\\+\\%]+)['|\"].*?", 
							Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CHARACTER_CLASS).matcher(data);

					if (redirectUrlMatcher.matches() && redirectUrlMatcher.groupCount() > 0) {
						detailLink = redirectUrlMatcher.group(1);
					}
				}
			}
		}
		return detailLink;
	}
	
	private String getName(final Element linkTag) {
		return linkTag.text();
	}
	
	private String getTradingOrBrandNames(final Element listData) {
		return getText(listData.child(1));
	}
	
	private String getBusinessType(final Element listData) {
		return getText(listData.child(2));
	}
	
	private String getReferenceNumber(final Element listData) {
		return getText(listData.child(3));
	}
	
	private String getStatus(final Element listData) {
		String result = getText(listData.child(4));
		if (result == null) {
			result = "";
		}
		return result;
	}
	
	public Element getListData(final Element linkTag) {
		Element trTag = linkTag;
		
		while (!trTag.tagName().equals("tr") && trTag.parent() != null) {
			trTag = trTag.parent();
		}
		
		return trTag;
	}
}
