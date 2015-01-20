package com.twintiment.presenter;

import com.fasterxml.jackson.databind.JsonNode;

public interface TweetListener {
	
	public void newTweetArrived(JsonNode tweet);

}
