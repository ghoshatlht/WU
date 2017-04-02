package com.western.union.extractor;

import org.junit.Test;

import com.western.union.extractor.PRAAdressDetailsExtractor;
import com.western.union.extractor.config.Configuration;

public class PRAAddressDetailsExtractorIntegrationTest {
	
	@Test
	public void testRunSearchForDPO() {
		Configuration conf = getTestConfiguration("DPO");
		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
		extractor.run(conf);
	}
	
	@Test
	public void testRunSearchForPSD() {
		Configuration conf = getTestConfiguration("PSD");
		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
		extractor.run(conf);
	}
	
	@Test
	public void testRunSearchForReferenceNumber504630() {
		Configuration conf = getTestConfiguration("504630");
		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
		extractor.run(conf);
	}
	
	private static Configuration getTestConfiguration(final String searchWord) {
		String[] args = new String[]{"-" + Configuration.PARAMATER_SEARCH_WORD, searchWord};
		
		Configuration conf = new Configuration(args);
		conf.setSinkFile("src/test/resources/run/CBI-AddressDetails.csv");
		conf.setSinkHeader(true);
		conf.setPrintProperties(true);
		conf.setProcessLimit(-1);
		conf.setMsgAfterCount(2);
		return conf;
	}
}
