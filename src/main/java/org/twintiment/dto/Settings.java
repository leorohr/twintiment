package org.twintiment.dto;

import java.io.Serializable;

import org.twintiment.analysis.geolocation.GeoUtils;

public class Settings implements Serializable {

	private static final long serialVersionUID = -216594605399053449L;

	private String clientID;
	private boolean includeAllTweets;
	private boolean fallbackGazetteer;
	private String[] filterTerms;
	private String[] hashTags;
	private String fileName;
	private int[] sentimentRange;
	private GeoUtils.LatLng[][] areas;
	public Settings() {
		
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String[] getFilterTerms() {
		return filterTerms;
	}
	
	public void setFilterTerms(String[] filterTerms) {
		this.filterTerms = filterTerms;
	}
	
	public String[] getHashTags() {
		return hashTags;
	}

	public void setHashTags(String[] hashTags) {
		this.hashTags = hashTags;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isIncludeAllTweets() {
		return includeAllTweets;
	}
	
	public void setIncludeAllTweets(boolean includeAllTweets) {
		this.includeAllTweets = includeAllTweets;
	}
	
	
	public boolean isFallbackGazetteer() {
		return fallbackGazetteer;
	}

	public void setFallbackGazetteer(boolean fallBackGazetteer) {
		this.fallbackGazetteer = fallBackGazetteer;
	}

	public int[] getSentimentRange() {
		return sentimentRange;
	}
	
	public void setSentimentRange(int[] sentimentRange) {
		this.sentimentRange = sentimentRange;
	}
	
	public GeoUtils.LatLng[][] getAreas() {
		return areas;
	}

	public void setCoordinateRanges(GeoUtils.LatLng[][] areas) {
		this.areas = areas;
	}	
}
