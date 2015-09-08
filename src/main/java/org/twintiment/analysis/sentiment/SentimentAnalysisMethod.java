package org.twintiment.analysis.sentiment;

public interface SentimentAnalysisMethod {
	
	/**
	 * Calculates the sentiment of a string.
	 * @param s
	 *            String to analyse.
	 * @return double value representing the sentiment
	 */
	public double calculateSentiment(String s);

	/**
	 * Calculates the sentiment of a string and normalises the value to the
	 * interval [-2,2], -2 being the most negative and +2 the most positive. 
	 * @param s
	 *            The string to analyse
	 * @return double value within [-2,2]
	 */
	public double normalisedSentiment(String s);
}
