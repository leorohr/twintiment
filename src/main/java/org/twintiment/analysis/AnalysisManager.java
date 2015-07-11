package org.twintiment.analysis;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Component;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.geolocation.GeoUtils;
import org.twintiment.analysis.sentiment.SentimentAnalyser;
import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;
import org.twintiment.dto.TweetDataMsg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AnalysisManager {
	
	private final MessageSendingOperations<String> messagingTemplate;
	private BlockingQueue<TweetDataMsg> messageQueue = new LinkedBlockingQueue<TweetDataMsg>(10000);
	private boolean isStopped = false;
	private Thread transmissionThread, analysisThread;
	private TweetSource source;
	private ObjectMapper mapper = new ObjectMapper();
	private SentimentAnalyser sentimentAnalyser;
	private HashSet<FileMetaDTO> availableFiles = new HashSet<FileMetaDTO>();
	private List<TweetDataMsg> tweets = Collections.synchronizedList(new ArrayList<TweetDataMsg>());
	private Settings settings;
	
	@Autowired
	private AnalysisStatistics stats;
	
	@Autowired
	private GeoLocator locator;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	public AnalysisManager(MessageSendingOperations<String> messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
		
		try {
			sentimentAnalyser = new SentimentAnalyser();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostConstruct
	public void postConstruct() {

		//Populate the availableFiles-list
		File datasetDir = new File(servletContext.getRealPath("/datasets"));
		if(!datasetDir.exists()) {
			datasetDir.mkdir();
		}
		
		File[] files = datasetDir.listFiles();
		for(File f : files) {	
			availableFiles.add(new FileMetaDTO(f.getName(), f.length()));
		}
	}
	
	public void startAnalysis() {
		
		this.isStopped = false;
		this.stats.resetStats();
		
		//TODO possibly multiple threads to analyse
		analysisThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
					try {
						analyseNextTweet();
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}	
				}
			}
		}, "AnalysisThread");
		analysisThread.start();
		
		transmissionThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
					
					try {
						messagingTemplate.convertAndSend("/queue/data", messageQueue.take());
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
		
		long start = System.currentTimeMillis(); //used to track the avg analysis time
		JsonNode tweet = mapper.readTree(source.getNextTweet());
		String text = tweet.findValue("text").asText();
		double[] coords = null;
		List<String> hashtags = null;
		
		//Get Coordinates
		try {
			coords = locator.getCoordinates(tweet);
		} catch (IOException e) { e.printStackTrace();	}
		
		//Drop the tweet if no coordinates present  
		if(coords == null && !settings.isIncludeAllTweets()) {
			return;
		}
		
		//Extract HashTags
		JsonNode tagNode = tweet.findValue("entities").findValue("hashtags");
		hashtags = tagNode.findValuesAsText("text");
		
		//Get Sentiment
		double sentiment = sentimentAnalyser.calculateSentiment(text);
		sentiment = Math.round(sentiment*100)/100d; //round to second decimal place
		
		//Get Date
		Date date = null;
		try {
			date = getTwitterDate(tweet.findValue("created_at").asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		TweetDataMsg tweetMsg = new TweetDataMsg(text, sentiment, coords,
				date, hashtags); 
		messageQueue.offer(tweetMsg);
		
		tweets.add(tweetMsg); 
		
		//Update statistics
		stats.update(tweetMsg, System.currentTimeMillis()-start);
		
		//Update max. distance between tweets after ten new tweets arrived
		if(stats.getNumTweets() % 10 == 0) {
			updateMaxDist();
		}
	}
	
	private void updateMaxDist() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				double dist = 0.0d;
				for(int i=0; i<tweets.size(); ++i) {
					double[] coords1 = tweets.get(i).getCoords();
					if(coords1 == null) 
						continue;
					
					for(int j=i; j<tweets.size(); ++j) {
						
						double[] coords2 = tweets.get(j).getCoords();
						
						if(coords2 != null) {
							dist = GeoUtils.haversine(coords1[0], coords1[1], coords2[0], coords2[1]);
							if(dist > stats.getMaxDist()) {
								dist = Math.round(dist*100)/100d; //round to second decimal place
								stats.setMaxDist(dist);
							}
						}
					}
				}
			}
			
		}, "UpdateMaxDist").start();
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
	
	public void addAvailableFile(File file) {		
		availableFiles.add(new FileMetaDTO(file.getName(), file.length()));
	}
	
	public void setTweetSource(TweetSource source) {
		this.source = source;
	}
	
	public HashSet<FileMetaDTO> getAvailableFiles() {
		return this.availableFiles;
	}
	
	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
