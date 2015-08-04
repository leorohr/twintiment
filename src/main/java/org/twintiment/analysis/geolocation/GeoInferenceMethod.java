package org.twintiment.analysis.geolocation;

import java.io.IOException;

import org.geotools.filter.text.cql2.CQLException;

import weka.core.FastVector;
import weka.core.Instances;

import com.fasterxml.jackson.databind.JsonNode;

public interface GeoInferenceMethod {
	public Instances createInstances(JsonNode tweet, FastVector fvAttributes) throws CQLException, IOException;
}