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

	@Override
	public double calculateSentiment(String s) {

		double score = 0d;
		int wordCount = 0;
		if (s != null && s.length() > 0) {
			Annotation annotation = pipeline.process(s);
			List<CoreMap> sentences = annotation
					.get(CoreAnnotations.SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				Tree tree = sentence.get(SentimentAnnotatedTree.class);

				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();

				int sentenceLength = partText.split(" ").length;
				wordCount += sentenceLength;
				score = sentiment * sentenceLength;
			}
		}
		return score / wordCount;
	}

	@Override
	public double normalisedSentiment(String s) {

		// The CoreNLP sentiment values range from 0 to 4, hence subtracting 2
		// scales them to the desired interval
		return calculateSentiment(s) - 2;
	}

}
