package com.twacker.model;

import java.io.IOException;
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

	AppProperties() {
		Properties props = new Properties();
					
		try {
			props.load(AppProperties.class.getClassLoader().getResourceAsStream("/resources/config.properties"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not read config file\n");
		}
		
		consumerKey = props.getProperty("consumerKey");
		consumerSecret = props.getProperty("consumerSecret");
		accessToken = props.getProperty("accessToken");
		accessTokenSecret = props.getProperty("accessTokenSecret");
	}
	
	public static AppProperties getAppProperties() {
		if(AppProperties.instance == null)
			AppProperties.instance = new AppProperties();
		
		return AppProperties.instance;
	}
	
	
}
