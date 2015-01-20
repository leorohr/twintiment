package com.twintiment.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twintiment.presenter.TweetListener;
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
public class TweetStreamer {
	
	private List<TweetListener> listeners = new ArrayList<TweetListener>();
	//TODO size adequate?
	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	private SentimentAnalyser sentimentAnalyser = new SentimentAnalyser();
	private Client hosebirdClient;
	private Thread streamThread;
	
	
	public void addListener(TweetListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(TweetListener listener) {
		listeners.remove(listener);
	}
	
	public TweetStreamer(List<String> filterTerms) throws IOException {

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
	
	//TODO parallelize this with 
	//ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	public void startStreaming() {
		
		if(streamThread != null && streamThread.isAlive())
			return;
		
		streamThread = new Thread(new TweetStream(), "StreamThread");
		streamThread.start();
	}
	
	public void stopStreaming() {
	
		if(streamThread != null)
			streamThread.interrupt();
		hosebirdClient.stop();
		
		sentimentAnalyser.close();
	}
	
	private class TweetStream implements Runnable {

		@Override
		public void run() {
		    
			while(!hosebirdClient.isDone() && !Thread.currentThread().isInterrupted()) {
				try {
					String msg = msgQueue.take();
					//Parse JSON message and pass it to all listeners
					ObjectMapper mapper = new ObjectMapper();
					JsonNode tweet = mapper.readTree(msg);
					
					for(TweetListener l : listeners)
						l.newTweetArrived(tweet);
					
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
