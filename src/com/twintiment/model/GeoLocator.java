package com.twintiment.model;

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

	private static final String MAPS_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	
	/**
	 * Contacts the Google Maps API to retreive lat/long coordinates based on a given address.
	 * @param address The address to lookup in human readable form.
	 * @return 	A double array of size two containing the latitude and longitude respectively.
	 * 			<code>null</code> if the response did not contain a location
	 * @throws IOException
	 */
	public static double[] getCoordinates(String address) throws IOException {
		
		URL url = new URL(MAPS_URL + address.replace(' ', '+'));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		if(connection.getResponseCode() != 200) {
			throw new ConnectException(connection.getResponseCode() + connection.getResponseMessage());
		}
			
		BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
		String input;
		StringBuffer response = new StringBuffer();
		
		while( (input = in.readLine()) != null)
			response.append(input);
		
		in.close();
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonTree = mapper.readTree(response.toString());
		
		if(jsonTree == null)
			throw new JsonMappingException("Could not parse following string: " + response.toString());
		
		double[] coordinates = null;
		JsonNode locationNode = jsonTree.findValue("location");
		if(locationNode != null) {
			coordinates = new double[] { 
					jsonTree.findValue("location").findValue("lat").asDouble(),
					jsonTree.findValue("location").findValue("lng").asDouble()				
					};
		}
			
		return coordinates;
	}	
}
