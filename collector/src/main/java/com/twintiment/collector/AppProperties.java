package com.twintiment.collector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * TODO
 * @author leorohr
 *
 */
public class AppProperties {
	
	private String consumerKey;
	private String consumerSecret;
	private String accessToken;
	private String accessTokenSecret;
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	// Singleton
	private static AppProperties instance;

	AppProperties() throws IOException {
		Properties props = new Properties();
		props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		
		consumerKey = props.getProperty("consumerKey");
		consumerSecret = props.getProperty("consumerSecret");
		accessToken = props.getProperty("accessToken");
		accessTokenSecret = props.getProperty("accessTokenSecret");
	}
	
	public static AppProperties getAppProperties() throws IOException {
		if(AppProperties.instance == null)
			AppProperties.instance = new AppProperties();
		
		return AppProperties.instance;
	}
	
	
}
