package org.twintiment.analysis.geolocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.MultiPolygon;

public class NutsUtils {
	private CSVParser parser;
	private CSVRecord[] records;
	private ShapefileDataStore dataStore;
	private ContentFeatureSource featureSource;

	public NutsUtils() throws IOException, CQLException {

		// Shapefile
		dataStore = new ShapefileDataStore(getClass().getResource("/NUTS_2013_SHP/data/NUTS_RG_01M_2013.shp"));
		dataStore.setIndexed(true);
		String[] typeNames = dataStore.getTypeNames();
		String typeName = typeNames[0];
		featureSource = dataStore.getFeatureSource(typeName);
		
		// CSV
		parser = new CSVParser(new FileReader(new File(getClass().getResource("/NUTS_2013.csv").getFile())),
				CSVFormat.TDF.withHeader());
		records = parser.getRecords().toArray(new CSVRecord[0]);
	}

	/**
	 * @param coords
	 *            In lon/lat
	 * @return The most precise NUTS code or null if not found
	 * @throws CQLException
	 * @throws IOException
	 */
	public String getNUTSCode(double lon, double lat) throws CQLException,
			IOException {
		Filter filter = ECQL.toFilter("CONTAINS (the_geom, POINT(" + lon + " "
				+ lat + "))");
		try {
            ContentFeatureCollection collection = featureSource.getFeatures(filter);
			SimpleFeatureIterator iterator = collection.features();
			Feature maxStatLevelFt = null;
			int maxStatLevel = 0;
			while (iterator.hasNext()) {
				Feature f = iterator.next();
				Integer statLevel;
				if( (statLevel = (Integer)f.getProperty("STAT_LEVL_").getValue()) > maxStatLevel) {
					maxStatLevel = statLevel;
					maxStatLevelFt = f;
				}			
			}
		
			iterator.close();
		
		    return (maxStatLevelFt == null ?
				null :
				maxStatLevelFt.getProperty("NUTS_ID").getValue().toString());
        } catch(Exception e) { 
            System.out.println("Exception in getNUTSCode with lon/lat" + lon + "/" + lat);
        }
		return null;
	}
	

	/**
	 * @param nutsCode The NUTS code for which coordinates are to be determined.
	 * @return Coordinates in lon/lat
	 * @throws IOException
	 * @throws CQLException
	 */
	double[] getCoordinates(String nutsCode) throws IOException, CQLException {
		Filter filter = ECQL.toFilter("NUTS_ID = '" + nutsCode + "'");
		
	    ContentFeatureCollection collection = featureSource.getFeatures(filter);
		SimpleFeatureIterator iterator = collection.features();
		MultiPolygon mp = (MultiPolygon) iterator.next().getAttribute("the_geom");
		iterator.close();
		if(mp != null) {
			com.vividsolutions.jts.geom.Point p = mp.getCentroid();
			return new double[] { p.getX(), p.getY() };
		}		
		
		return null;
	}
	
	/**
	 * Matches all words in the tweet against the NUTS dictionary 
	 * and returns a list of all NUTS codes that were identified.
	 * @param tweet
	 * @return
	 */
	public ArrayList<String> matchTweet(String tweet) {
		ArrayList<String> ret = new ArrayList<String>();
		for(String word : tweet.split(" ")) {
			String nuts = getNUTSCode(word);
			if(nuts != null)
				ret.add(nuts);
		}
		
		return ret;
	}
	
	public String getNUTSCode(String word) {
		// Remove special characters
		word = word.replaceAll("[\\W]", "");

		CSVRecord match = null;
		for (CSVRecord r : records) {
			String desc = r.get("Description");
			if (desc.equalsIgnoreCase(word))
				return r.get(2);

			try {
				if (Pattern.matches("(?i)\\b" + word + "\\b", desc)) {
					if (match != null
							&& r.get("NUTS-Code").length() > match.get("NUTS-Code").length()) // take longest
						match = r;
					else if (match == null)
						match = r;
				}
			} catch (PatternSyntaxException e) {
				continue;
			}	
		}
		return (match == null ? null : match.get(2));
	}
}
