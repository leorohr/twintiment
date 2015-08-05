package org.twintiment.analysis;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.geotools.filter.text.cql2.CQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.twintiment.analysis.geolocation.GeoLocator;
import org.twintiment.analysis.geolocation.GeoUtils;
import org.twintiment.analysis.sentiment.SentimentAnalyser;
import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;
import org.twintiment.dto.TweetDataMsg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Component
@Scope(value="session", proxyMode=ScopedProxyMode.INTERFACES)
public class AnalysisManager implements IAnalysisManager {
	
	private final MessageSendingOperations<String> messagingTemplate;
	private BlockingQueue<TweetDataMsg> messageQueue = new LinkedBlockingQueue<TweetDataMsg>(10000);
	private boolean isStopped = false;
	private Thread transmissionThread, analysisThread;
	private TweetSource source;
	private ObjectMapper mapper = new ObjectMapper();
	private HashSet<FileMetaDTO> availableFiles = new HashSet<FileMetaDTO>();
	private List<TweetDataMsg> tweets = Collections.synchronizedList(new ArrayList<TweetDataMsg>());
	private Settings settings;
	private AnalysisStatistics stats = new AnalysisStatistics();
	
	@Autowired
	@Qualifier("TwintimentTaskExecutor")
	private ThreadPoolTaskExecutor executor;
	
	@Autowired
	private SentimentAnalyser sentimentAnalyser;
	
	@Autowired
	private GeoLocator locator;
	
	@Autowired
	private ServletContext servletContext;
	
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
	
	public void runAnalysis() {
		
		this.isStopped = false;
		this.stats.resetStats();
		
		//Start a thread that continuously broadcasts messages from the queue to the channel
		transmissionThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isStopped) {
					
					try {
						messagingTemplate.convertAndSend("/queue/data-" + settings.getClientID(), messageQueue.take());
					} catch (MessagingException | InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}, "DataTransmissionThread");
		transmissionThread.start();
		
		analysisThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while(!isStopped) {
					if(source.hasNext()) {
						String tweet = source.getNextTweet();
						executor.submit(new Runnable() {

							@Override
							public void run() {
								try {
									AnalysisManager.this.analyseTweet(tweet);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						});
					}	
				}
			}		
		});
		analysisThread.start();
	}

	
	public void stopAnalysis() {		
		
		this.isStopped = true;
		source.close();
	}

	private void analyseTweet(String rawTweet) throws Exception {		
		
		long start = System.currentTimeMillis(); //used to track the avg analysis time
		JsonNode tweet = mapper.readTree(rawTweet);
		String text = tweet.get("text").asText();
		double[] coords = null;
		List<String> hashtags = null;
		boolean tagged = false;
		
		//Drop the tweet if it does not contain the specified hashtags
		if(settings.getHashTags().length > 0) {
			JsonNode entities = tweet.get("entities");
			boolean found = false;
			for(JsonNode ht : (ArrayNode)entities.get("hashtags")) {
				for(String s_ht : settings.getHashTags()) {
					if(s_ht.contains(ht.get("text").asText().toLowerCase()))
						found = true;
						break;
				}
				if(found)
					break;
			}
			
			if(!found) 
				return;
		}
		
		// Get coordinates
		JsonNode coordsNode;
		if(!(coordsNode = tweet.get("coordinates")).isNull()) {				
			//flip order. Twitter returns coords in lon/lat
			coords = new double[] { coordsNode.get("coordinates").get(1).asDouble(),
									coordsNode.get("coordinates").get(0).asDouble() };
			tagged = true;
		} else {
			try {
				coords = locator.getCoordinates(tweet);
			} catch (CQLException e) {
				e.printStackTrace();
			}
		}
		
		// If the locator did not infer coords, but the user chose to include gazetteer lookup
		if(coords == null && settings.isFallbackGazetteer())
			coords = locator.userLocationCoords(tweet);
		
		//Drop the tweet if no coordinates present and unlocated tweets are not to be shown 
		if((coords == null && !settings.isIncludeAllTweets())) {
			return;
		}

		//Drop tweet if it is not in selected area
		if(!pointInAnyRect(coords, settings.getAreas()))
			return;
		
		//Get Sentiment
		double sentiment = sentimentAnalyser.calculateSentiment(text);
		sentiment = Math.round(sentiment*100)/100d; //round to second decimal place
		
		//Drop the tweet if sentiment is out of selected range
		if(sentiment < settings.getSentimentRange()[0] || sentiment > settings.getSentimentRange()[1])
			return;
				
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
		if(tagged) 
			stats.incNumTagged();
		else if(!tagged && coords != null)
			stats.incNumInferred();
		
		//Update max. distance between tweets after ten new tweets arrived
		if(stats.getNumTweets() % 10 == 0) {
			
			executor.submit(new Runnable() {
				@Override
				public void run() {
					updateMaxDist();
				}
			});
		}
	}
	
	private boolean pointInAnyRect(double[] coords, GeoUtils.LatLng[][] rectangles) {
		if(rectangles == null)
			return true;
		GeoUtils.LatLng point = new GeoUtils.LatLng(coords[0], coords[1]);
		for(GeoUtils.LatLng[] rect : rectangles) {
			if(GeoUtils.pointInRect(point, rect))
				return true;
		}
		
		return false;
	}

	private void updateMaxDist() {
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
	
	/**
	 * Assumes that the passed {@link Settings} contain either a FileName to open 
	 * or FilterTerms for the live stream. If both are set, the FileName is used
	 * for the TweetSource object.
	 */
	public void setSettings(Settings settings) throws IOException {
		this.settings = settings;
		if(settings.getFileName() != null)
			this.source = new DataFile(servletContext.getRealPath("/datasets/" + settings.getFileName()));
		else if(settings.getFilterTerms() != null) 
			this.source = new TwitterStreaming(Arrays.asList(settings.getFilterTerms()));
		else throw new IOException("No TweetSource found.");
	}
	
	public AnalysisStatistics getStats() {
		return stats;
	}
}
