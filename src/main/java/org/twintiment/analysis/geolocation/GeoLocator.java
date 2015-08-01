package org.twintiment.analysis.geolocation;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeoLocator {
	
	GeoInferenceMethod text;
	GeoInferenceMethod hometown;
	GeoInferenceMethod historical;
	
	public GeoLocator() {
		try {
			text = new TextFeatures();
			hometown = new Hometown();
			historical = new HistoricalFeatures();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public double[] getCoordinates(JsonNode tweet) throws IOException {

		return hometown.getCoordinates(tweet);		
	}
}
