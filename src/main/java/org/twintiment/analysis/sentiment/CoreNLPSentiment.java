package org.twintiment.analysis.sentiment;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLPSentiment implements SentimentAnalysisMethod {

	private StanfordCoreNLP pipeline;
	
	public CoreNLPSentiment() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}
	
	//TODO combine sentiment of each sentence to overall sentiment of the tweet.
	//Weighted average?
	@Override
	public double calculateSentiment(String s) {

		int mainSentiment = 0;
		if (s != null && s.length() > 0) {
		    int longest = 0;
		    Annotation annotation = pipeline.process(s);
		    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		    for (CoreMap sentence : sentences) {
		        Tree tree = sentence
		                .get(SentimentAnnotatedTree.class);
//		        sentence.get(SentimentCoreAnnotations.SentimentClass.class); TODO whats that?
		        
		        int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
		        String partText = sentence.toString();
		        if (partText.length() > longest) {
		            mainSentiment = sentiment;
		            longest = partText.length();
		        }
		    }
		}
        return mainSentiment;
	}

}
