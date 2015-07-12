package org.twintiment.analysis;

import org.twintiment.dto.StatsDTO;
import org.twintiment.dto.TweetDataMsg;

public class AnalysisStatistics {

	private int numTweets = 0;
	private int numInferred = 0;
	private int numTagged = 0;
	private long avgTime = 0l;
	private double avgSentiment = 0.0d;
	private double maxDist = 0.0d;

	private final int NUM_TOP_TWEETS = 5;
	private TweetDataMsg[] topPosTweets = new TweetDataMsg[NUM_TOP_TWEETS]; 
	private TweetDataMsg[] topNegTweets = new TweetDataMsg[NUM_TOP_TWEETS]; 

	public AnalysisStatistics() {
	}

	public StatsDTO getDTO() {
		return new StatsDTO(numTweets, numInferred, numTagged, avgSentiment,
				maxDist, topPosTweets, topNegTweets, avgTime);
	}

	public int getNumTweets() {
		return numTweets;
	}

	public int getNumInferred() {
		return numInferred;
	}

	public int getNumTagged() {
		return numTagged;
	}

	public double getAvgSentiment() {
		return avgSentiment;
	}

	public double getMaxDist() {
		return maxDist;
	}

	public TweetDataMsg[] getTopPosTweets() {
		return topPosTweets;
	}

	public TweetDataMsg[] getTopNegTweets() {
		return topNegTweets;
	}

	public synchronized void incNumInferred() {
		++numInferred;
	}

	public synchronized void incNumTagged() {
		++numTagged;
	}

	public synchronized void setMaxDist(double maxDist) {
		this.maxDist = maxDist;
	}

	public synchronized void update(TweetDataMsg tweet) {
		update(tweet, -1l);
	}
	
	/**
	 * 
	 * @param tweet
	 * @param analysisTime The time it took for the tweet to be analysed (in ms)
	 */
	public synchronized void update(TweetDataMsg tweet, long analysisTime) {
		++numTweets;
		
		//Update avg sentiment
		updateSentiment(tweet.getSentiment());
		
		//Update avg analysis time
		if(analysisTime > 0) {
			avgTime = Math.round(avgTime * ((numTweets - 1) / (double)numTweets) + analysisTime/((double)numTweets));
		}
		
		//Update tweet top lists
		int i = NUM_TOP_TWEETS;
		if (tweet.getSentiment() > 0) {
			if (topPosTweets[i - 1] == null
					|| tweet.getSentiment() > topPosTweets[i - 1]
							.getSentiment()) { // positive tweet
				i--;
				while (i > 0
						&& (topPosTweets[i - 1] == null || tweet.getSentiment() > topPosTweets[i - 1]
								.getSentiment()))
					i--;
				topPosTweets[i] = tweet;
			}
		} else if (tweet.getSentiment() < 0) {
			if (topNegTweets[i - 1] == null
					|| tweet.getSentiment() < topNegTweets[i - 1]
							.getSentiment()) { // negative tweet
				i--;
				while (i > 0
						&& (topNegTweets[i - 1] == null || tweet.getSentiment() < topNegTweets[i - 1]
								.getSentiment()))
					i--;
				topNegTweets[i] = tweet;
			}
		}
		
	}
	
	/**
	 * Increases {@code numTweets} by one and updates the {@code avgSentiment}.
	 * The {@code avgSentiment} is updated by adding the new sentiment divided
	 * by the total number of tweets to the current sentiment average weighed
	 * with n-1/n (for n current tweets).
	 * @param sentiment
	 *            The sentiment of the new tweet. Used to keep the average
	 *            sentimen value up-to-date.
	 */
	private void updateSentiment(double sentiment) {
		avgSentiment = avgSentiment * ((numTweets - 1) / (double)numTweets) + sentiment/numTweets;
		avgSentiment = Math.round(avgSentiment*10000)/10000d; //round to fourth decimal place	
	}
	
	public void resetStats() {
		numTweets = 0;
		numTagged = 0;
		numInferred = 0;
		avgSentiment = 0.0d;
		maxDist = 0.0d;
		topPosTweets = new TweetDataMsg[NUM_TOP_TWEETS];
		topNegTweets = new TweetDataMsg[NUM_TOP_TWEETS];
	}
}
