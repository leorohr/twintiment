package org.twintiment.vo;

import java.util.Date;

public class TweetRateMsg {
	
	private long tweets;
	private long date;
	
	public long getTweets() {
		return tweets;
	}
	public long getDate() {
		return date;
	}

	public TweetRateMsg(long tweets, Date date) {
		this(tweets, date.getTime());
	}
	
	public TweetRateMsg(long tweets, long date) {
		this.tweets = tweets;
		this.date = date;
	}
}
