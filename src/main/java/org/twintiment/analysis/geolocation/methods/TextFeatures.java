package org.twintiment.analysis.geolocation.methods;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.geotools.filter.text.cql2.CQLException;
import org.twintiment.analysis.geolocation.GeoInferenceMethod;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.geolocation.NutsUtils;
import org.twintiment.analysis.geolocation.WekaUtils;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fasterxml.jackson.databind.JsonNode;

public class TextFeatures implements GeoInferenceMethod {

	private NutsUtils nu = new NutsUtils();

	public TextFeatures() throws FileNotFoundException, IOException, CQLException {
	}

	public String getPredictedNutsCode(JsonNode tweet, FastVector fvAttributes) {
		
		return "";
	}
	
	@Override
	public Instances createInstances(JsonNode tweet, FastVector fvAttributes) {
			
		Instance inst;
		// Fill the first set of attributes with the probability of a word being mapped to a NUTS region
		inst = new Instance(fvAttributes.size());
		
		// Initialise all other attributes to 0
		inst.replaceMissingValues(new double[fvAttributes.size()]);

		// Match text with NUTS file
		String text = tweet.get("text").asText();
		ArrayList<String> codes = nu.matchTweet(text);
		
		// Set attributes accordingly
		for(String nuts : codes) {
			if(nuts != null && nuts != "") {
				if(nuts.length() > 4) 
					nuts = nuts.substring(0, 4);

				Integer attrId = GeoLocator.nutsToVecPos.get(nuts);
				if(attrId != null)
					inst.setValue(attrId, inst.value(attrId) + 1);
			}
		}
		
		// If the tweet contained any words that matched a region,
		// create the instance set
		if(WekaUtils.attributeSum(inst) > 0d) {
			Instances set = new Instances("textft", fvAttributes, GeoLocator.nutsToVecPos.size());
			set.setClassIndex(set.numAttributes()-1);
			// Normalise instance values
			WekaUtils.normaliseInstance(inst);
						
			for(int pos=0; pos < GeoLocator.nutsToVecPos.size(); ++pos) {
				Instance fullInst = (Instance)inst.copy();
				fullInst.setDataset(set);
				// Use second part of the vector for classification vector
				fullInst.setValue(pos + GeoLocator.nutsToVecPos.size(), 1.0d);

				// Set class label
				fullInst.setClassMissing();
				
				
				set.add(fullInst);
			}
			return set;
		}
		
		return null;
	}

}
