package org.twintiment.analysis;

import java.io.Closeable;

/**
 * Classes implementing this interface can be used by the {@link AnalysisManager} to
 * retrieve tweets from and feed the analysis process. 
 */
public interface TweetSource extends Closeable {
	public String getNextTweet();
	public boolean hasNext();
	public void close();
	
}
