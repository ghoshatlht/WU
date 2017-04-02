package com.western.union.extractor.printer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.western.union.extractor.config.Configuration;
import com.western.union.extractor.data.AddressData;
import com.western.union.extractor.util.LoggerUtil;

public class CsvPrinter implements IPrinter {
	private static final Logger log = LoggerUtil.getLogger(CsvPrinter.class);
	
	private final PrintWriter csvFile;
	
	public CsvPrinter(final Configuration config) throws IOException {
		Path sinkFile = Paths.get(config.getSinkFile()).getParent();
		log.log(Level.INFO, "Ziel unter: " + config.getSinkFile());
		if (sinkFile != null) {
			Files.createDirectories(sinkFile);
		}
		
		csvFile = new PrintWriter(new File(config.getSinkFile()));
		if (config.getSinkHeader()) {
			csvFile.append("Name,Reference Number,Trading or brand name,business type,status,address 0, address 1, address 2, address 3, address 4, address 5, address 6,"
					+ "address 7,phone,fax,email,website,type,current status,effective date,sub status, agent status, insurance mediation\n");
		}
	}

	@Override
	public void print(final AddressData data) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(data.getName());
		builder.append(",").append(data.getReferenceNumber());
		builder.append(",").append(data.getTradingBrandName());
		builder.append(",").append(data.getBusinessType());
		builder.append(",").append(data.getStatus());
		builder.append(",").append(data.getAddress0());
		builder.append(",").append(data.getAddress1());
		builder.append(",").append(data.getAddress2());
		builder.append(",").append(data.getAddress3());
		builder.append(",").append(data.getAddress4());
		builder.append(",").append(data.getAddress5());
		builder.append(",").append(data.getAddress6());
		builder.append(",").append(data.getAddress7());
		builder.append(",").append(data.getPhone());
		builder.append(",").append(data.getFax());
		builder.append(",").append(data.getEmail());
		builder.append(",").append(data.getWebsite());
		builder.append(",").append(data.getType());
		builder.append(",").append(data.getCurrentStatus());
		builder.append(",").append(data.getEffectiveDate());
		builder.append(",").append(data.getSubStatus());
		builder.append(",").append(data.getAgentStatus());
		builder.append(",").append(data.getInsuranceMediation());
		csvFile.println(builder.toString());
	}
	
	@Override
	public void close() throws IOException {
		csvFile.flush();
		csvFile.close();
	}
}
