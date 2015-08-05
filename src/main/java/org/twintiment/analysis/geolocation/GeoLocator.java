package org.twintiment.analysis.geolocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.twintiment.analysis.AppProperties;
import org.twintiment.analysis.geolocation.methods.HistoricalFeatures;
import org.twintiment.analysis.geolocation.methods.Hometown;
import org.twintiment.analysis.geolocation.methods.TextFeatures;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeoLocator {
	
	private GeoInferenceMethod text;
	private GeoInferenceMethod hometown;
	private GeoInferenceMethod historical;
	private FastVector fvAttributes;
	private NutsUtils nu;
	private AppProperties props;
	private String maps_url;
	
	public static HashMap<String, Integer> nutsToVecPos = new HashMap<>();
	
	private Classifier cls;
	
	public GeoLocator() {
	
		try {
			text = new TextFeatures();
			hometown = new Hometown();
			historical = new HistoricalFeatures();
			
			cls = (Classifier) SerializationHelper.read(new FileInputStream(getClass().getResource("/classifier.model").getFile()));
//			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getClass().getResource("/classifier.model").getFile()));
//			cls = (Classifier) ois.readObject();
//			ois.close();
			
			nu = new NutsUtils();
			props = AppProperties.getAppProperties();
			maps_url = "http://open.mapquestapi.com/geocoding/v1/address?key="
				+ props.getOSMKey() + "&outFormat=json&location=";
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load 4-letter NUTS-Codes
		ObjectMapper mapper = new ObjectMapper();
		String[] nutsCodes = null;
		try {
			nutsCodes = mapper.readValue(new File(getClass().getResource(
					"/NUTS_4letter.txt").getFile()), String[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < nutsCodes.length; i++) {
			nutsToVecPos.put(nutsCodes[i], i);
		}

		// Declare attributes; first half for probabilities (p_), second for
		// classification, second to last is class label, last is TID
		fvAttributes = new FastVector(2 *  nutsCodes.length + 2);
		for (int j = 0; j < nutsCodes.length; ++j) {
			fvAttributes.addElement(new Attribute("p_" + nutsCodes[j]));
		}
		// Second half; for classification (c_)
		for (int j = 0; j < nutsCodes.length; ++j) {
			fvAttributes.addElement(new Attribute("c_" + nutsCodes[j]));
		}
		// Class label
		fvAttributes.addElement(new Attribute("class"));		
	}
	
	public double[] getCoordinates(JsonNode tweet) throws Exception {
		
		Instances textft = text.createInstances(tweet, fvAttributes);
		Instances histft = historical.createInstances(tweet, fvAttributes);
		Instances htft = hometown.createInstances(tweet, fvAttributes);

		String textNuts = null;
		if(textft != null) {
			textNuts = getPredictedNutsCode(textft);	
		}
		String histNuts = null;
		if(histft != null)
			histNuts = getPredictedNutsCode(histft);
		String htNuts = null;
		if(htft != null)
			htNuts = getPredictedNutsCode(htft);

		//If two methods or more agree on the same NUTS, use that one
		//Otherwise the precedences are Hist>HT>Text
		String nuts = null; //the chosen nuts 
		if(histNuts != null && htNuts != null && histNuts.equals(htNuts) 
				|| histNuts != null && textNuts != null && histNuts.equals(textNuts))
			nuts = histNuts;
		else if(textNuts != null && htNuts != null && textNuts.equals(htNuts))
			nuts = textNuts;
		else if(histNuts != null)
			nuts = histNuts;
		else if(htNuts != null)
			nuts = htNuts;
		else if(textNuts != null)
			nuts = textNuts;

		if(nuts != null)
			return nu.getCoordinates(nuts);
		
		return null;		
	}
	
	/**
	 * Returns NUTS code with highes probability according to the loaded classifier.
	 * @param instances
	 * @return NUTS code as String or empty String null
	 * @throws Exception 
	 */
	public String getPredictedNutsCode(Instances instances) throws Exception {
		double maxProb = 0.0d;
		Instance prediction = null;
		for(int i=0; i<instances.numInstances(); ++i) {
			double prob = cls.distributionForInstance(instances.instance(i))[0]; 
			if(prob > maxProb) {
				maxProb = prob;
				prediction = instances.instance(i);
			}
		}
		
		//Search the second half of the instance (the classification part) for the nuts code 
		String nuts = null;
		for(int i=404; i<809; ++i) {
			if(prediction.value(i) == 1) {
				nuts = prediction.attribute(i).name();
				break;
			}
		}
		return nuts.substring(2);
	}

	/**
	 * Contacts the OSM Geocoding API to retreive lat/long coordinates based on the location field
	 * of the user who created the supplied tweet.
	 * @param address The address to lookup in human readable form.
	 * @return 	A double array of size two containing the latitude and longitude respectively.
	 * 			<code>null</code> if the response did not contain a location or is of bad
	 * 			(i.e. country-level) accuracy.
	 * @throws IOException
	 */
	public double[] userLocationCoords(JsonNode tweet) throws IOException {
		
		String address = tweet.get("user").get("location").asText();
		if(address.equals("") || address.equals(" "))
			return null;
		
		//Create request
		URL url = new URL(maps_url + address.replace(' ', '+'));
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
