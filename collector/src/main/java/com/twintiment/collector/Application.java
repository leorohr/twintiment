package com.twintiment.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.util.ArrayList;

public class Application {
	public static void main(String[] args) {
		
		TweetStreamer streamer;
		try {
			ArrayList<String> filterTerms = new ArrayList<String>();
			filterTerms.add("london");
			streamer = new TweetStreamer(filterTerms);
			streamer.startStreaming();
			
			PipedInputStream tweetStream = new PipedInputStream(streamer.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(tweetStream));
			
			String tweet;
			do {
				tweet = r.readLine();
				System.out.println(tweet);
				
			} while(tweet != null);
			
			r.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	} 

}
