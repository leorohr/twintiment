package org.twintiment.analysis.sentiment;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * A class responsible for the calculation of a sentiment score of strings based
 * on sentiment dictionary files. The <code>close</code>-method of this class
 * should be invoked on every object, to ensure a safe termination of the
 * filestream.
 */
public class LabMTSentiment implements SentimentAnalysisMethod, Closeable {

	private final String FILEPATH = getClass().getResource("/labmt_sorted.csv")
			.getFile();

	private File labMTFile;
	private CSVParser parser;
	private CSVRecord[] records;

	/**
	 * @throws FileNotFoundException
	 *             if the sentiment dictionary file was not found.
	 * @throws IOException
	 *             if the sentiment dictionary file could not be parsed
	 *             successfully.
	 */
	public LabMTSentiment() throws FileNotFoundException, IOException {

		labMTFile = new File(FILEPATH);

		parser = new CSVParser(new FileReader(labMTFile),
				CSVFormat.TDF.withHeader());
		records = parser.getRecords().toArray(new CSVRecord[0]);
	}

	/**
	 * This method accepts a string parameter and uses the labMT dictionary to
	 * calculate the string's sentiment score. Words that can not be found in
	 * the dictionary are ignored and the sentiment is calculated as the
	 * average of the values of the resolved words.
	 * 
	 * @param s
	 *            The string to calculate the sentiment score for.
	 * @return The sentiment score for the provided string as double value.
	 */
	@Override
	public double calculateSentiment(String s) {

		String[] words = s.toLowerCase().split(" ");
		double sentimentScore = 0;
		int resolvedWords = 0;

		for (String word : words) {
			double wordScore = getHappinessAvg(word);
			if (wordScore > 0) {
				++resolvedWords;
				sentimentScore += getHappinessAvg(word);
			}
		}
		return sentimentScore / resolvedWords;
	}

	@Override
	public double normalisedSentiment(String s) {
		// The labMT sentiment values range from 1 to 9
		return -2 + 0.5 * (calculateSentiment(s) - 1);
	}

	/**
	 * Applies binary search to look for {@code word} in the labMT dictionary.
	 * Returns value of the happiness_average column.
	 *
	 * @param word
	 * @return the happiness_average value within range [1,9], 0 if not present
	 */
	private double getHappinessAvg(String word) {
		int lo = 0;
		int hi = records.length - 1;

		while (lo <= hi) {
			int mid = lo + (hi - lo) / 2;
			int cmp = records[mid].get("word").compareTo(word);
			if (cmp > 0)
				hi = mid - 1;
			else if (cmp < 0)
				lo = mid + 1;
			else
				return Double
						.parseDouble(records[mid].get("happiness_average"));
		}
		return 0d;
	}

	/**
	 * This method should be called to make sure the filestream is closed
	 * correctly.
	 */
	@Override
	public void close() {

		try {
			parser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
