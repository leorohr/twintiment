package org.twintiment.analysis.geolocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoLocator {

	private static final String OSM_KEY = "Fmjtd%7Cluu82q622h%2Cbn%3Do5-94zwqf";
	private static final String MAPS_URL = "http://open.mapquestapi.com/geocoding/v1/address?key="
											+ OSM_KEY + "&outFormat=json&location=";  
	
	/**
	 * Return either the user's hometown or the tweet's coordinates
	 * @param tweet The tweet to locate
	 * @return double array of size two containing lat/lon respectively; null if not resolved
	 * @throws IOException
	 */
	public static double[] getCoordinates(JsonNode tweet) throws IOException {
		
		//If tweet-coordinates are present, return them.
		JsonNode coords;
		if(!(coords = tweet.findValue("coordinates")).isNull()) {
			 //flip order. Twitter returns coords in lon/lat
			return new double[] { coords.get(1).asDouble(), coords.get(0).asDouble() };
		}
		
		//Otherwise lookup the user's hometown.
		return getCoordinates(tweet.findValue("user").findValue("location").asText());

	}
	
	/**
	 * Contacts the OSM Geocoding API to retreive lat/long coordinates based on a given address.
	 * @param address The address to lookup in human readable form.
	 * @return 	A double array of size two containing the latitude and longitude respectively.
	 * 			<code>null</code> if the response did not contain a location or is of bad
	 * 			(i.e. country-level) accuracy.
	 * @throws IOException
	 */
	public static double[] getCoordinates(String address) throws IOException {
		
		if(address.equals(""))
			return null;
		
		//Create request
		URL url = new URL(MAPS_URL + address.replace(' ', '+'));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		//If bad HTTP response throw exception
		if(connection.getResponseCode() != 200) {
			throw new ConnectException(connection.getResponseCode() + connection.getResponseMessage());
		}
		
		//Read response
		BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		
		while( (input = in.readLine()) != null)
			response.append(input);
		
		in.close();
		
		//Parse Json
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonTree = mapper.readTree(response.toString());
		if(jsonTree == null)
			throw new JsonMappingException("Could not parse following string: " + response.toString());
		
		//If search was not successful, return null
		if(jsonTree.findValue("statuscode").asInt() != 0)
			return null;
		
		//Drop the result if it is only country-level granularity.
		String qualityCode = jsonTree.findValue("geocodeQualityCode").asText();
		if(qualityCode.startsWith("A1"))
			return null;
		
		double[] coordinates = null;
		if(jsonTree != null) {
			JsonNode latLng = jsonTree.findValue("latLng");
			coordinates = new double[] { 
					latLng.findValue("lat").asDouble(),
					latLng.findValue("lng").asDouble()				
					};
		}
			
		return coordinates;
	}	
}
