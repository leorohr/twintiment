package com.twacker.presenter;

import java.io.IOException;

import com.twacker.model.TweetStreamer;
import com.twacker.view.views.SearchView;

/**
 * TODO make this a listener for new tweets from streamer
 * @author leorohr
 * TODO	
 */
public class TwackerPresenter {
	private SearchView searchView;
	private TweetStreamer streamer;
	private static TwackerPresenter instance;
	
	/**
	 * Implements the singleton pattern.
	 * @param searchView
	 * @return the instance of the presenter. Can be null.
	 */
	public static TwackerPresenter getInstance(SearchView searchView) {
		if(instance == null)
			instance = new TwackerPresenter(searchView);
		return instance;
	}
	
	private TwackerPresenter(SearchView searchView) {
		this.searchView = searchView;
	}

	public void startStreaming() {
		
		try {
			streamer = new TweetStreamer(searchView.getFilterTerms());
		} catch (IOException e) {
			e.printStackTrace();
		}
		streamer.startStreaming();
	}
	
	public void stopStreaming() {
		streamer.stopStreaming();
	}
	
	

}
