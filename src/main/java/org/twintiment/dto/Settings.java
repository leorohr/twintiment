package org.twintiment.dto;

import java.io.Serializable;

import org.twintiment.analysis.geolocation.GeoUtils;

public class Settings implements Serializable {

	private static final long serialVersionUID = -216594605399053449L;

	private boolean includeAllTweets;
	private String filterTerms;
	private String fileName;
	private int[] sentimentRange;
	private GeoUtils.LatLng[][] areas;
	public Settings() {
		
	}

	public String getFilterTerms() {
		return filterTerms;
	}
	
	public void setFilterTerms(String filterTerms) {
		this.filterTerms = filterTerms;
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
