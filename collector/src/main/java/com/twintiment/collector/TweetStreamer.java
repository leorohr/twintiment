package com.twintiment.collector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedOutputStream;
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
public class TweetStreamer {

	//TODO size adequate?
	private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	private PipedOutputStream out = new PipedOutputStream();
	private Client hosebirdClient;
	private Thread streamThread;
	
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

	}
	
	public PipedOutputStream getOutputStream() {
		return out;
	}
	
	private class TweetStream implements Runnable {

		public void run() {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));
			
			while(!hosebirdClient.isDone() && !Thread.currentThread().isInterrupted()) {
				try {
					String msg = msgQueue.take();
//					System.out.println(msg);
					w.write(msg);
					
					
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
	
}
