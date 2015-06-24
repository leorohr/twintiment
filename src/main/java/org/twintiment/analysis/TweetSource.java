package org.twintiment.analysis;


public interface TweetSource {
	public String getNextTweet();
	public boolean hasNext();
	public void close();
	
}
