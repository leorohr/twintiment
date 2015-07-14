package org.twintiment.analysis.sentiment;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

/**
 * Combines multiple sentiment analysis methods.
 */
@Service
public class SentimentAnalyser {
	
	private CoreNLPSentiment coreNLP;
	private LabMTSentiment labMT;
	
	public SentimentAnalyser() throws FileNotFoundException, IOException {
		coreNLP = new CoreNLPSentiment();
		labMT = new LabMTSentiment();	
	}
	
	public double calculateSentiment(String s) {
		return labMT.calculateSentiment(s);
	}

	@PreDestroy
	public void cleanup() {
		labMT.close();
	}
}
