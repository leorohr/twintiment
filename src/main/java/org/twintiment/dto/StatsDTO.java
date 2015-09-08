package org.twintiment.dto;

import java.io.Serializable;

import org.twintiment.analysis.AnalysisManager;
import org.twintiment.analysis.AnalysisStatistics;

/**
 * DTO to transfer the {@link AnalysisStatistics} from the {@link AnalysisManager} to the client.
 */
public class StatsDTO implements Serializable {

	private static final long serialVersionUID = 130377909851875282L;

	private int numTweets;
	private int numInferred;
	private int numTagged;
	private long avgTime;
	private double avgSentiment;
	private double maxDist;
	private TweetDataMsg[] topPosTweets;
	private TweetDataMsg[] topNegTweets;

	public StatsDTO(int numTweets, int numInferred, int numTagged,
			double avgSentiment, double maxDist, TweetDataMsg[] topPosTweets,
			TweetDataMsg[] topNegTweets, long avgTime) {
		this.numTweets = numTweets;
		this.numInferred = numInferred;
		this.numTagged = numTagged;
		this.avgSentiment = avgSentiment;
		this.maxDist = maxDist;
		this.topPosTweets = topPosTweets;
		this.topNegTweets = topNegTweets;
		this.avgTime = avgTime;
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
	
	public long getAvgTime() {
		return avgTime;
	}
}
