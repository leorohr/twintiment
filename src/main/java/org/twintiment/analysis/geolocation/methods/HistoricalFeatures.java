package org.twintiment.analysis.geolocation.methods;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.geotools.filter.text.cql2.CQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.twintiment.analysis.AnalysisManager;
import org.twintiment.analysis.AppProperties;
import org.twintiment.analysis.geolocation.GeoInferenceMethod;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.geolocation.NutsUtils;
import org.twintiment.analysis.geolocation.WekaUtils;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A {@link GeoInferenceMethod} that uses the historical features signal to create {@link Instance}s.
 * Historical features are based on the prior tweets of a user. To create the instances, Twitter API is 
 * contacted to retrieve the last 200 tweets of the user and use their locations to infer the current 
 * tweet's location. The probability attributes of the instances carry the probability of a prior tweet
 * from this user being from the corresponding NUTS region.
 */
public class HistoricalFeatures implements GeoInferenceMethod {
	
	private Twitter twitter;
	private NutsUtils nu = new NutsUtils();
	private AppProperties props = AppProperties.getAppProperties();
	private boolean waitForRate = false;
	private Date waitUntil = null;
	
	@Autowired
	private AnalysisManager manager;
	
	public HistoricalFeatures(AccessToken accessToken) throws CQLException, IOException {

		ConfigurationBuilder cb = new ConfigurationBuilder()
			.setOAuthConsumerKey(props.getConsumerKey())
			.setOAuthConsumerSecret(props.getConsumerSecret())
			.setOAuthAccessToken(accessToken.getToken())
			.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
			.setDebugEnabled(false);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	@Override
	public Instances createInstances(JsonNode tweet, FastVector fvAttributes) throws CQLException, IOException {

		if(waitForRate)
			if(new Date().before(waitUntil))
				return null;
			else waitForRate = false;
		
		// Get past 200 tweets of this tweet's user
		long uid = tweet.get("user").get("id_str").asLong();
		ResponseList<Status> results = null;
		try {
			results = twitter.getUserTimeline(uid, new Paging(1, 200));
		} catch (TwitterException e) {
			//Rate limit reached
			if(e.getStatusCode() == 429) {
				Logger log = Logger.getLogger(getClass().getName());
				log.info("Twitter rate limit reached! For the next " +
						e.getRateLimitStatus().getSecondsUntilReset() + " seconds " +
						"Historical Features will not be included in the location inference.");
				waitUntil = new Date(System.currentTimeMillis() + e.getRateLimitStatus().getSecondsUntilReset() * 1000);
				waitForRate = true;
				return null;
			}
			e.printStackTrace();
		}

		Instance inst = new Instance(fvAttributes.size());
		// Initialise all other attributes to 0
		inst.replaceMissingValues(new double[fvAttributes.size()]);
		
		// Update first half of instance attribute using the collected tweets
		for(Status s : results) {
			if(s.getGeoLocation() != null) {
				String nuts = nu.getNUTSCode(s.getGeoLocation().getLongitude(), s.getGeoLocation().getLatitude());
				if(nuts != null) {
					if(nuts.length() > 4) 
						nuts = nuts.substring(0, 4);
					Integer attrId = GeoLocator.nutsToVecPos.get(nuts);
					if(attrId != null)
						inst.setValue(attrId, inst.value(attrId) + 1);
				}
			}
		}
		
		// If prior tweets were located, create the 404 classification vectors 
		if(WekaUtils.attributeSum(inst) > 0d) {
			Instances set = new Instances("histft", fvAttributes, GeoLocator.nutsToVecPos.size());
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
