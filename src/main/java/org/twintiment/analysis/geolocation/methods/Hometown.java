package org.twintiment.analysis.geolocation.methods;

import java.io.IOException;

import org.geotools.filter.text.cql2.CQLException;
import org.twintiment.analysis.geolocation.GeoInferenceMethod;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.geolocation.NutsUtils;
import org.twintiment.analysis.geolocation.WekaUtils;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Uses the hometown feature as signal to create instances. The {@code location} field of each tweet is
 * ran against the NUTS dictionary and the instances are created based on the locations that can be resolved
 * to NUTS codes. Only one probability attribute will ever be set to 1, all others will be 0.
 */
public class Hometown implements GeoInferenceMethod {
	
	private NutsUtils nu = new NutsUtils();
	
	public Hometown() throws CQLException, IOException {
	}

	@Override
	public Instances createInstances(JsonNode tweet, FastVector fvAttributes) {
	
		Instance inst;		
		// Fill the first set of attributes with the NUTS region of the hometown, if resolved
		inst = new Instance(fvAttributes.size());

		// Initialise all other attributes to 0
		inst.replaceMissingValues(new double[fvAttributes.size()]);

		String location = tweet.get("user").get("location").asText();
		String locNuts = nu.getNUTSCode(location);
		
		if(locNuts != null && locNuts != "") {
			if(locNuts.length() > 4) 
				locNuts = locNuts.substring(0, 4);

			Integer attrId = GeoLocator.nutsToVecPos.get(locNuts);
			if(attrId != null)
				inst.setValue(attrId, inst.value(attrId) + 1);
		}
		
		// If the location  matched a region, create the 404 instances 
		if(WekaUtils.attributeSum(inst) > 0d) {
			Instances set = new Instances("htft", fvAttributes, GeoLocator.nutsToVecPos.size());
			set.setClassIndex(set.numAttributes()-1);
			
			// Normalise instance values
			WekaUtils.normaliseInstance(inst);
			
			// Fill classification part
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
