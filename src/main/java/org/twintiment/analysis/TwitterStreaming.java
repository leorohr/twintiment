package org.twintiment.analysis;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;


/**
 * A {@link TweetSource} that uses the Hosebird Client library to access the Twitter
 * Streaming API. Incoming tweets are stored in the {@link TwitterStreaming#msqQueue} until they are retrieved
 * by calls to {@link TwitterStreaming#getNextTweet()}.
 */
public class TwitterStreaming implements TweetSource {
	
	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);
	private Client hosebirdClient;
	
	public TwitterStreaming(List<String> filterTerms, String accessToken, String accessTokenSecret) throws IOException {

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		
		//Filter for terms 
		hosebirdEndpoint.trackTerms(filterTerms);
		//Filter only english tweets
		hosebirdEndpoint.addQueryParameter("language", "en"); 

		AppProperties properties = AppProperties.getAppProperties();
		Authentication hosebirdAuth = new OAuth1(
				properties.getConsumerKey(), properties.getConsumerSecret(),
				accessToken, accessTokenSecret); 
				
		
		ClientBuilder builder = new ClientBuilder()
			.name("Twintiment-01")
			.hosts(hosebirdHosts)
			.authentication(hosebirdAuth)
			.endpoint(hosebirdEndpoint)
			.processor(new StringDelimitedProcessor(msgQueue));
		
		hosebirdClient = builder.build();
		hosebirdClient.connect();
	}
	
	/**
	 * Stop streaming without clearing the queue.
	 */
	public void stopStreaming() {
		hosebirdClient.stop();	
	}


	/**
	 * Returns, if available, the next tweet in the {@link TwitterStreaming#msgQueue}.
	 */
	@Override
	public String getNextTweet() {

		try {
			return msgQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean hasNext() {
		
		return !msgQueue.isEmpty();
	}

	/**
	 * Stop the stream and clear the queue.
	 */
	@Override
	public void close() {
		hosebirdClient.stop();
		msgQueue.clear();
	}
	
}
