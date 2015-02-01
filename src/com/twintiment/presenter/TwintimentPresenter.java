package com.twintiment.presenter;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;
import com.twintiment.model.SentimentAnalyser;
import com.twintiment.model.TweetStreamer;
import com.twintiment.view.views.MainView;
import com.vaadin.ui.UI;

/**
 * @author leorohr
 * TODO	
 */
public class TwintimentPresenter implements TweetListener, Serializable {
	
	private static final long serialVersionUID = 8526370213359196037L;
	private MainView mainView;
	private TweetStreamer streamer;
	private static TwintimentPresenter instance;
	
	/**
	 * Implements the singleton pattern.
	 * @param searchView
	 * @return the instance of the presenter. Can be null.
	 */
	public static TwintimentPresenter getInstance(MainView mainView) {
		if(instance == null)
			instance = new TwintimentPresenter(mainView);
		return instance;
	}
	
	private TwintimentPresenter(MainView mainView) {
		this.mainView = mainView;
	}

	public void startStreaming() {
		
		try {
			streamer = new TweetStreamer(mainView.getFilterTerms());
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
//			System.out.print(text + ": " + score + "\n");
			//Push change to UI
			UI.getCurrent().access(() -> mainView.addTableRow(new Object[] {text, score}));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			sentimentAnalyser.close();
		}
		
	}

}