package com.western.union.extractor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.western.union.extractor.config.Configuration;
import com.western.union.extractor.extractor.DetailExtractor;
import com.western.union.extractor.extractor.SearchResultExtractor;
import com.western.union.extractor.printer.CsvPrinter;
import com.western.union.extractor.util.LoggerUtil;

public class PRAAdressDetailsExtractor {

	private static final Logger log = LoggerUtil.getLogger(PRAAdressDetailsExtractor.class);

	public static void main(String[] args) {
		Configuration config = new Configuration(args);
		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
		extractor.run(config);
	}

	public void run(final Configuration config) {

		try (CsvPrinter printer = new CsvPrinter(config)) {
			new SearchResultExtractor(config).forEach(new DetailExtractor(config).withPrinter(printer));
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Öffnen der CSV Datei '" + config.getSinkFile() + "'", e);
		}

		log.log(Level.INFO, "Bearbeitung abgeschlossen");
	}
}
