package com.twintiment.presenter;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.twintiment.model.SentimentAnalyser;
import com.twintiment.model.TweetStreamer;
import com.twintiment.view.views.SearchView;

/**
 * @author leorohr
 * TODO	
 */
public class TwintimentPresenter implements TweetListener {
	private SearchView searchView;
	private TweetStreamer streamer;
	private static TwintimentPresenter instance;
	
	/**
	 * Implements the singleton pattern.
	 * @param searchView
	 * @return the instance of the presenter. Can be null.
	 */
	public static TwintimentPresenter getInstance(SearchView searchView) {
		if(instance == null)
			instance = new TwintimentPresenter(searchView);
		return instance;
	}
	
	private TwintimentPresenter(SearchView searchView) {
		this.searchView = searchView;
	}

	public void startStreaming() {
		
		try {
			streamer = new TweetStreamer(searchView.getFilterTerms());
		} catch (IOException e) {
			e.printStackTrace();
		}
		streamer.addListener(this);
		streamer.startStreaming();
	}
	
	public void stopStreaming() {
		streamer.stopStreaming();
	}

	/**
	 * Handles a new tweet. Retrieves the actual message from the JSON object and 
	 * calculates its sentiment.
	 * @param tweet The tweet that is taken from the queue.
	 */
	@Override
	public void newTweetArrived(JsonNode tweet) {		
		
		SentimentAnalyser sentimentAnalyser = null;
		try {
			sentimentAnalyser = new SentimentAnalyser();
			String text = tweet.findValue("text").asText();
				
			double score = sentimentAnalyser.calculateSentiment(text);
			System.out.print(text + ": " + score + "\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			sentimentAnalyser.close();
		}
		
	}
	
	

}
