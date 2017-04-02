package com.western.union.extractor.extractor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.western.union.extractor.config.Configuration;
import com.western.union.extractor.data.AddressData;
import com.western.union.extractor.printer.IPrinter;
import com.western.union.extractor.util.LoggerUtil;

import static com.western.union.extractor.util.XmlUtil.*;

public class DetailExtractor {
	private static final Logger log = LoggerUtil.getLogger(DetailExtractor.class);
	
	private final Configuration config;
	
	public DetailExtractor(final Configuration config) {
		this.config = config;
	}
	
	public IDetailExtractor withPrinter(final IPrinter printer) {
		return new IDetailExtractor() {
			@Override
			public void detailsFor(final AddressData data) {

				try {
					Document detailData = Jsoup.connect(data.getLink()).userAgent(config.getUserAgent()).get();
					
					extractMissingListData(detailData, data);
					
					extractAddressDataTo(detailData, data);
					
					extractBasicDetailsDataTo(detailData, data);
					
					checkAuthorisationStatus(detailData, data);
					
					printer.print(data);
				} catch (IOException e) {
					log.log(Level.SEVERE, "Problem bei der Verbindung zu '" + data.getLink() + "'", e);
				}
			}
		};
	}
	
	private void checkAuthorisationStatus(final Document detailData, final AddressData data) {
		Elements authStatus = detailData.select(".AuthorisationStatus > h4");
		
		if (authStatus.size() > 0) {
			String statusText = getText(authStatus.get(0));
			if (statusText.contains("Unauthorised")) {
				data.setStatus(statusText);
			}
		}
	}
	
	private void extractMissingListData(final Document detailData, final AddressData data) {
		if (data.getName() == null || data.getName().trim().isEmpty()) {
			data.setName(getText(detailData.select(".RecordName")));
		}
		if (data.getReferenceNumber() == null || data.getReferenceNumber().trim().isEmpty()) {
			data.setReferenceNumber(getText(detailData.select(".ReferenceNumber > span")));
		}
	}
	
	private void extractBasicDetailsDataTo(final Document detailData, final AddressData data) {
		Elements basicDetails = detailData.select(".FirmBasicDetailsSection");
		for (Element basic: basicDetails) {
			String label = textFromChildAt(basic, 0);
			String value = getTextFromFirstTagInValuePart(basic);
			
			if (contains(label, "Type")) {
				data.setType(value);
			} else if (contains(label, "Current status")) {
				data.setCurrentStatus(value);
			} else if (contains(label, "Effective Date ")) {
				data.setEffectiveDate(value);
			} else if (contains(label, "Sub Status")) {
				data.setSubStatus(value);
			} else if (contains(label, "Insurance Mediation")) {
				data.setInsuranceMediation(value);
			} else if (contains(label, "Agent Status")) {
				data.setAgentStatus(value);
			}
		}
	}
	
	private String getTextFromFirstTagInValuePart(final Element element) {
		String result = null;
		if (element.children().size() == 1) {
			result = element.ownText().trim();
		} else {
			Element tmp = element.children().get(1);
			while (tmp.children().size() > 0) {
				tmp = tmp.children().get(0);
			}
			
			result = tmp.text().trim();
		}
		if (result == null) {
			result = "";
		}
		return result;
	}
	
	private void extractAddressDataTo(final Document detailData, final AddressData data) {
		Elements address = detailData.select(".addresssection");
		for (Element addressElement: address) {
//			System.out.println(" - addressElement: " + addressElement.children().get(0).text());
			
			if (addressElement.children().size() > 1 && addressElement.children().get(0).hasClass("addresslabel")) {
				String label = addressElement.children().get(0).text();
				String value = addressElement.children().get(1).text().trim();
				if (contains(label, "Phone")) {
					data.setPhone(value);
				} else if (contains(label, "Fax")) {
					data.setFax(value);
				} else if (contains(label, "Email")) {
					data.setEmail(value);
				} else if (contains(label, "Website")) {
					data.setWebsite(value);
				} else if (contains(label, "Address")) {
					Elements addressParts = addressElement.children().get(1).children();
					data.setAddress0(textFromChildAt(addressParts, 0));
					data.setAddress1(textFromChildAt(addressParts, 1));
					data.setAddress2(textFromChildAt(addressParts, 2));
					data.setAddress3(textFromChildAt(addressParts, 3));
					data.setAddress4(textFromChildAt(addressParts, 4));
					data.setAddress5(textFromChildAt(addressParts, 5));
					data.setAddress6(textFromChildAt(addressParts, 6));
					data.setAddress7(textFromChildAt(addressParts, 7));
				}
			}
		}
	}
}
