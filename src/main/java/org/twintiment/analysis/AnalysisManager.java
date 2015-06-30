package org.twintiment.analysis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.sentiment.SentimentAnalyser;
import org.twintiment.vo.TweetDataMsg;
import org.twintiment.vo.TweetRateMsg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AnalysisManager {
	
	private final MessageSendingOperations<String> messagingTemplate;
	private BlockingQueue<TweetDataMsg> messageQueue = new LinkedBlockingQueue<TweetDataMsg>(10000);
	private boolean isStopped = false;
	private Thread transmissionThread, analysisThread;
	private TweetSource source;
	private SentimentAnalyser sentimentAnalyser;
	private long tweetCount = 0;
	
	@Autowired
	public AnalysisManager(MessageSendingOperations<String> messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
		try {
			sentimentAnalyser = new SentimentAnalyser();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startAnalysis() {
		
		this.isStopped = false;
		
		//TODO possibly multiple threads to analyse? depends on backend analysis implementation
		analysisThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
					try {
						analyseNextTweet();
					} catch (IOException e1) {
						e1.printStackTrace();
						continue;
					}	
				}
			}
		}, "AnalysisThread");
		analysisThread.start();
		
		transmissionThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				long now;
				while(!isStopped) {
					
					try {
						messagingTemplate.convertAndSend("/queue/data", messageQueue.take());
						
						//Send the tweet-rate every ten seconds
						now = System.currentTimeMillis();
						if((now - startTime) > 10000) {
							messagingTemplate.convertAndSend("/queue/tweet_rate", new TweetRateMsg(tweetCount, now));
							tweetCount = 0;
							startTime = System.currentTimeMillis();
						}
					} catch (MessagingException | InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}, "DataTransmissionThread");
		transmissionThread.start();
	}
	
	public void stopAnalysis() {
		this.isStopped = true;
		sentimentAnalyser.close();
		source.close();
	}
	
	/**
	 * Retrieve a new tweet from a source and feed it to the analysis chain.
	 * Add the resulting TweetDataMsg to the messageQueue.
	 * @throws IOException if the tweet taken from the source could not be parsed
	 * 			into a JsonNode object.					
	 */
	private void analyseNextTweet() throws IOException {
		//TODO set up analysis chain (based on user preferences?)		
			
		ObjectMapper mapper = new ObjectMapper(); 
		JsonNode tweet = mapper.readTree(source.getNextTweet());
		String text = tweet.findValue("text").asText();
		double[] coords = null;
		List<String> hashtags = null;
		
		//Extract HashTags
		JsonNode tagNode = tweet.findValue("entities").findValue("hashtags");
		hashtags = tagNode.findValuesAsText("text");
		
		//Get Sentiment
		double sentiment = sentimentAnalyser.calculateSentiment(text);
		
		//Get Coordinates
		try {
			coords = GeoLocator.getCoordinates(tweet);
		} catch (IOException e) { e.printStackTrace();	}
		
		//Get Date
		Date date = null;
		try {
			date = getTwitterDate(tweet.findValue("created_at").asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		messageQueue.offer(new TweetDataMsg(text, sentiment, coords,
				date, hashtags ));
		
		//Track number of processed tweets
		++tweetCount;
	}
	
	/**
	 * Parses the date of a tweet into a Java Date object.
	 * @param date The date as String in the format of Twitter's 'created_at'-field.
	 * @return A data object object 
	 */
	private Date getTwitterDate(String date) throws ParseException {

		final String TwitterFormat ="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(TwitterFormat, Locale.ENGLISH);
		sf.setLenient(true);
		return sf.parse(date);
	}

	
	public void setTweetSource(TweetSource source) {
		this.source = source;
	}
}
