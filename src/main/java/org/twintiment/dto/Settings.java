package org.twintiment.dto;

import java.io.Serializable;

public class Settings implements Serializable {

	private static final long serialVersionUID = -216594605399053449L;

	private boolean includeAllTweets;
	private String filterTerms;
	private String fileName;
	//TODO add sentiment range and coordinate range
	public Settings() {
		
	}
	
	public String getFilterTerms() {
		return filterTerms;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean isIncludeAllTweets() {
		return includeAllTweets;
	}

	public void setFilterTerms(String filterTerms) {
		this.filterTerms = filterTerms;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setIncludeAllTweets(boolean includeAllTweets) {
		this.includeAllTweets = includeAllTweets;
	}
	
}
