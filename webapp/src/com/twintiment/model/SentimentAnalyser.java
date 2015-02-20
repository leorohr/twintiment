package com.twintiment.model;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * A class responsible for the calculation of a sentiment score of strings based on sentiment dictionary files.
 * The <code>close</code>-method of this class should be invoked on every object, to ensure a safe termination
 * of the filestream.
 */
public class SentimentAnalyser implements Closeable {
	
	private final String FILEPATH = AppProperties.class.getClassLoader().getResource("/resources/labmt.csv").getFile();
	
	private File labMTFile;
	private CSVParser parser;
	private CSVRecord[] records;
	
	/**
	 * @throws FileNotFoundException if the sentiment dictionary file was not found. 
	 * @throws IOException if the sentiment dictionary file could not be parsed successfully.
	 */
	public SentimentAnalyser() throws FileNotFoundException, IOException  {
		
		labMTFile = new File(FILEPATH);
		
		parser = new CSVParser(new FileReader(labMTFile), CSVFormat.TDF.withHeader());
		records = parser.getRecords().toArray(new CSVRecord[0]);	
	}
	
	/**
	 * This method accepts a string parameter and uses the labMT dictionary to calculate
	 * the string's sentiment score.
	 * @param s The string to calculate the sentiment score for.
	 * @return The sentiment score for the provided string as double value.
	 */
	public double calculateSentiment(String s) {
		
		String[] words = s.toLowerCase().split(" ");
		double sentimentScore = 0;
		
		for(String word : words) {
		
			//TODO worthwhile to order the labmt file entries by alphabet and then apply better search?
			for(int i=0; i<records.length; i++)
			{
				if(records[i].get("word").equals(word)) {
					sentimentScore += Double.parseDouble(records[i].get("happiness_average"));
					break;
				}
			}
			
		}
			
		return sentimentScore;
	}

	/**
	 * This method should be called to make sure the filestream is closed correctly. 
	 */
	@Override
	public void close() {
		
		try {
			parser.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

}
