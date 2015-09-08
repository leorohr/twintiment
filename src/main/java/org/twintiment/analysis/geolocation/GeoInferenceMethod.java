package org.twintiment.analysis.geolocation;

import java.io.IOException;

import org.geotools.filter.text.cql2.CQLException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface required for all methods that shall be used in the {@link GeoLocator}.
 */
public interface GeoInferenceMethod {
	/**
	 * Creates a {@link Instances} object with 404 {@link Instance}s  with {@link Attribute}s as 
	 * defined in fvAttributes for the tweet past as {@link JsonNode}. 
	 * Implementations of this method create the instances based on different features of the tweet 
	 * (e.g. textual features).
	 * @param tweet The tweet to create the instances for.
	 * @param fvAttributes The attributes of each instance.
	 * @return The collection of 404 instances for that tweet.
	 * @throws CQLException
	 * @throws IOException
	 */
	public Instances createInstances(JsonNode tweet, FastVector fvAttributes) throws CQLException, IOException;
}