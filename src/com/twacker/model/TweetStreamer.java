package com.twacker.model;

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


public class TweetStreamer {
	
	//TODO size adequate?
	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	private Client hosebirdClient;
	
	public TweetStreamer() {

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		
		//Filter for terms
		/*List<Long> followings = Lists.newArrayList(1234L, 566788L);
		List<String> terms = Lists.newArrayList("twitter", "api");
		hosebirdEndpoint.followings(followings);
		hosebirdEndpoint.trackTerms(terms);*/
		
		//TODO read from auth file
		Authentication hosebirdAuth = new OAuth1(
				"sluZCq72QNUQfrsgMgRezcl7f", "NmfXC5ccLdlSIilRgT825UQwVfzzk1V2irD3QBLLc4cxRs1uyo", 
				"430920084-02poPEuNN8DSXF3TNuDO142PbkzfQ6slHAm6cAUg", "SmveEc94Di4W9Zhm4uKe71VOHIdVhtr7OKwIo2HytbyNI");
		
		ClientBuilder builder = new ClientBuilder()
			.name("Twacker-01")
			.hosts(hosebirdHosts)
			.authentication(hosebirdAuth)
			.endpoint(hosebirdEndpoint)
			.processor(new StringDelimitedProcessor(msgQueue));
		
		hosebirdClient = builder.build();
		
		hosebirdClient.connect();
		
	}
	
	//TODO parallelize this
	//ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	public void startStreaming() {
	
		
		Thread t = new Thread(new TweetStream());
		t.start();
		
		try { Thread.sleep(5000);
		} catch (InterruptedException e) {	e.printStackTrace(); }
		
		t.interrupt();

	}
	
	private class TweetStream implements Runnable {

		@Override
		public void run() {
			while(!hosebirdClient.isDone() && !Thread.currentThread().isInterrupted()) {
				try {
					System.out.println(msgQueue.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			hosebirdClient.stop(); //TODO this has to go somewhere else
			
		}

		
	}
	
}
