package org.twintiment.analysis.sentiment;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PreDestroy;

public class SentimentAnalyser {

	private LabMTSentiment labMT;
	
	public SentimentAnalyser() throws FileNotFoundException, IOException {
		labMT = new LabMTSentiment();	
	}
	
	public double calculateSentiment(String s) {

		return labMT.normalisedSentiment(s);
	}

	@PreDestroy
	public void cleanup() {
		labMT.close();
	}
}
