package org.twintiment.dto;

public class TopTweetsMsg {
	private TweetDataMsg[] topPosTweets;
	private TweetDataMsg[] topNegTweets;
	
	public TopTweetsMsg(TweetDataMsg[] topPosTweets, TweetDataMsg[] topNegTweets) {
		super();
		this.topPosTweets = topPosTweets;
		this.topNegTweets = topNegTweets;
	}
	public TweetDataMsg[] getTopPosTweets() {
		return topPosTweets;
	}
	public TweetDataMsg[] getTopNegTweets() {
		return topNegTweets;
	}
	
}
