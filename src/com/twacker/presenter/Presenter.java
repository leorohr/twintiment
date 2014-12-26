package com.twacker.presenter;

import com.twacker.model.TweetStreamer;
import com.twacker.view.views.SearchView;

/**
 * TODO
 * @author leorohr
 *
 */
public class Presenter {
	private SearchView searchView;
	private TweetStreamer streamer;
	
	public Presenter(SearchView searchView, TweetStreamer streamer) {
		this.searchView = searchView;
		this.streamer = streamer;
	}
	

}
