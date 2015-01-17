package com.twintiment.presenter;

import java.io.IOException;

import com.twintiment.model.TweetStreamer;
import com.twintiment.view.views.SearchView;

/**
 * TODO make this a listener for new tweets from streamer
 * @author leorohr
 * TODO	
 */
public class TwintimentPresenter {
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
		streamer.startStreaming();
	}
	
	public void stopStreaming() {
		streamer.stopStreaming();
	}
	
	

}
