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
 * TODO
 * @author leorohr
 */
public class TwitterStreaming implements TweetSource {
	
	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);
	private Client hosebirdClient;
	
	public TwitterStreaming(List<String> filterTerms) throws IOException {

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		
		//Filter for terms 
		hosebirdEndpoint.trackTerms(filterTerms);

		AppProperties properties = AppProperties.getAppProperties();
		Authentication hosebirdAuth = new OAuth1(
				properties.getConsumerKey(), properties.getConsumerSecret(),
				properties.getAccessToken(), properties.getAccessTokenSecret()); 
				
		
		ClientBuilder builder = new ClientBuilder()
			.name("Twintiment-01")
			.hosts(hosebirdHosts)
			.authentication(hosebirdAuth)
			.endpoint(hosebirdEndpoint)
			.processor(new StringDelimitedProcessor(msgQueue));
		
		hosebirdClient = builder.build();
		hosebirdClient.connect();
	}
		
	public void stopStreaming() {
		hosebirdClient.stop();	
	}


	@Override
	public String getNextTweet() {

		try {
			System.out.println("getnext");
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

	@Override
	public void close() {
		hosebirdClient.stop();
		msgQueue.clear();
	}
	
}
