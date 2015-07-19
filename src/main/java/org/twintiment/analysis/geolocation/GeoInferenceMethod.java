package org.twintiment.analysis.geolocation;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

public interface GeoInferenceMethod {
	public double[] getCoordinates(JsonNode tweet) throws IOException;
}
