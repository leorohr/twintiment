package org.twintiment.analysis;

import java.io.Closeable;


public interface TweetSource extends Closeable {
	public String getNextTweet();
	public boolean hasNext();
	public void close();
	
}
