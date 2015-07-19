package org.twintiment.analysis.geolocation;

import com.fasterxml.jackson.databind.JsonNode;

public class HistoricalFeatures implements GeoInferenceMethod {

	@Override
	public double[] getCoordinates(JsonNode tweet) {
		/*
		 * 1) Get user's prior tweets (only those with GPS Tag)
		 * 2) Find 3 tweets within close range (e.g. 30km?)
		 * 3) Assume this location (or the center of the three) for the new tweet 
		 */
		return null;
	}
	

}
