package org.twintiment.analysis;

import java.io.IOException;
import java.util.Properties;

public class AppProperties {
	
	private String consumerKey;
	private String consumerSecret;
	private String osmKey;
	
	public String getConsumerKey() {
		return consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}
	
	public String getOSMKey() {
		return osmKey;
	}

	// Singleton
	private static AppProperties instance;

	AppProperties() throws IOException {
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("/config.properties"));
		
		consumerKey = props.getProperty("consumerKey");
		consumerSecret = props.getProperty("consumerSecret");
		osmKey = props.getProperty("osmKey");
	}
	
	public static AppProperties getAppProperties() throws IOException {
		if(AppProperties.instance == null)
			AppProperties.instance = new AppProperties();
		
		return AppProperties.instance;
	}
	
	
}
