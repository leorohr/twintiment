package org.twintiment.analysis.sentiment;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Responsible for determining the sentiment of a tweet. Can combine multiple 
 * {@link SentimentAnalysisMethod}s and return a (possibly weighted) combination
 * of the {@link SentimentAnalysisMethod#normalisedSentiment(String)} of each method.
 */
public class SentimentAnalyser {

	private LabMTSentiment labMT;
	
	public SentimentAnalyser() throws FileNotFoundException, IOException {
		labMT = new LabMTSentiment();	
	}
	
	/**
	 * Calculate the sentiment value for the text in {@code s}.
	 * @param s The text to get the sentiment value for.
	 * @return The sentiment value, normalised to values in [-2, 2].
	 */
	public double calculateSentiment(String s) {

		return labMT.normalisedSentiment(s);
	}

	/**
	 * Closes {@link SentimentAnalysisMethod}s.
	 */
	public void cleanup() {
		labMT.close();
	}
}
