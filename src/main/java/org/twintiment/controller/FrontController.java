package org.twintiment.controller;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.twintiment.analysis.TwintimentAccessToken;
import org.twintiment.analysis.AppProperties;
import org.twintiment.analysis.IAnalysisManager;
import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;
import org.twintiment.dto.StatsDTO;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Controller
public class FrontController {
	
	   
	/* Temporarily stores the access token and secret.
	 * Is used after the callback from twitter to request the oauth verifier */
	private TwintimentAccessToken tmpAccessToken;
	
	@Autowired
	private IAnalysisManager manager;	
	
	@RequestMapping(value="/twitter_callback", method={RequestMethod.GET, RequestMethod.POST})
	protected String twitterCallback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Twitter verification
		Twitter twitter = new TwitterFactory().getInstance();
        
		AppProperties props = AppProperties.getAppProperties();
		
        twitter.setOAuthConsumer(props.getConsumerKey(), props.getConsumerSecret());
        String verifier = request.getParameter("oauth_verifier");
        RequestToken requestToken = new RequestToken(tmpAccessToken.getToken(), tmpAccessToken.getTokenSecret());
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,verifier);
        manager.setAccessToken(accessToken);
        manager.setup();
		
		return "redirect:analysis";
	}
	
	@RequestMapping(value="/analysis", method=RequestMethod.GET)
	@ResponseBody
	public ModelAndView analysisView() {
		return new ModelAndView("analysis");
	}
	
	@RequestMapping(value = "/analysis/start_streaming", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> startStreaming(@RequestBody Settings settings) {
		
		if((settings.getFileName() == null || settings.getFileName().equals("")) && settings.getFilterTerms().length == 0) {
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		try {
			manager.setSettings(settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		manager.runAnalysis();
    	return new ResponseEntity<String>(HttpStatus.OK);
	}	
	
	@RequestMapping(value="/analysis/stop", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> stopStreaming() {
		
		manager.stopAnalysis(); 
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/files", method=RequestMethod.GET)
	@ResponseBody
	public Set<FileMetaDTO> getAvailableFiles() {
		
		return manager.getAvailableFiles();
	}
	
	@RequestMapping(value="/analysis/stats", method=RequestMethod.GET)
	@ResponseBody
	public StatsDTO getStats() {
		
		return manager.getStats().getDTO();
	}
    
    @RequestMapping(value="/login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loginView(HttpServletResponse response, HttpServletRequest request) {
        
        try {
        	AppProperties props = AppProperties.getAppProperties();
        	Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(props.getConsumerKey(), props.getConsumerSecret());
            
            RequestToken requestToken;
        	String callbackURL = "http://127.0.0.1:8080/Twintiment/twitter_callback";
			requestToken = twitter.getOAuthRequestToken(callbackURL);
			tmpAccessToken = new TwintimentAccessToken(requestToken.getToken(), requestToken.getTokenSecret());

	        String authUrl = requestToken.getAuthorizationURL();
	        request.setAttribute("authUrl", authUrl);
	        
        } catch (TwitterException | IOException e) {
        	e.printStackTrace();        
        }
	    
        return new ModelAndView("login");
	}
}
