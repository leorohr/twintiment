package org.twintiment.dto;

import java.util.Date;
import java.util.List;

/**
 * A class used to transport data about a tweet from the server to the client.
 * Is marshalled to a JSON object.
 */
public class TweetDataMsg {
	private String message;
	private double sentiment;
	private double[] coords;
	private Date date;
	private List<String> hashtags;
	
	public String getMessage() {
		return message;
	}
	public double getSentiment() {
		return sentiment;
	}
	public double[] getCoords() {
		return coords;
	}
	public Date getDate() {
		return date;
	}
	public List<String> getHashtags() {
		return hashtags;
	}
	
	public TweetDataMsg(String message, double sentiment, double[] coords,
			Date date, List<String> hashtags) {
		this.message = message;
		this.sentiment = sentiment;
		this.coords = coords;
		this.date = date;
		this.hashtags = hashtags;
	}

}
