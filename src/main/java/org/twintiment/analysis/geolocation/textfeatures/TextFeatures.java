package org.twintiment.analysis.geolocation.textfeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.twintiment.analysis.geolocation.GeoInferenceMethod;

import com.fasterxml.jackson.databind.JsonNode;

public class TextFeatures implements GeoInferenceMethod {

	private final String NUTS_FILEPATH = getClass().getResource(
			"/NUTS_2013_sorted_nuts_code.csv").getFile();

	private CSVParser parser;
	private CSVRecord[] records;
	private NUTSTree nutsTree;

	public TextFeatures() throws FileNotFoundException, IOException {

		parser = new CSVParser(new FileReader(new File(NUTS_FILEPATH)),
				CSVFormat.DEFAULT.withHeader());
		records = parser.getRecords().toArray(new CSVRecord[0]);
		nutsTree = new NUTSTree();
		for (CSVRecord r : records) {
			nutsTree.insert(r.get("NUTS-Code"), 0);
		}

	}

	@Override
	public double[] getCoordinates(JsonNode tweet) {
		String msg = tweet.get("text").asText();
		String[] words = msg.split(" ");

		for (String w : words) {
			CSVRecord r = matchRecord(w);
			if (r != null) {
				nutsTree.incValue(r.get("NUTS-Code"));
			}
		}
		// TODO add coordinates to NUTS file and in tree. Then return the coords
		// of the highest vector entry. Also: clean tree on 'toVector' call,
		// i.e. reset all values to 0.
		Map<String, Integer> vector = nutsTree.toVector(4);
		return null;
	}

	/**
	 * 
	 * @param location
	 * @return The index of a match in the NUTS file
	 */
	private CSVRecord matchRecord(String location) {
		// Remove special characters
		location = location.replaceAll("[\\W]", "");

		CSVRecord match = null;
		for (CSVRecord r : records) {
			String desc = r.get("Description");
			if (desc.equalsIgnoreCase(location))
				return r;

			boolean pattern_matched = false;
			try {
				pattern_matched = Pattern.matches("(?i)\\b" + location + "\\b",
						desc);
			} catch (PatternSyntaxException e) {
				continue;
			}
			if (pattern_matched) {
				if (match != null
						&& r.get("NUTS-Code").length() > match.get("NUTS-Code")
								.length()) // take highest
					match = r;
				else if (match == null)
					match = r;
			}
		}
		return match;
	}

}
