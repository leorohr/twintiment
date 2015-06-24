package org.twintiment.controller;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.twintiment.analysis.AnalysisManager;
import org.twintiment.analysis.TwitterStreaming;

@Controller
public class FrontController implements ApplicationContextAware {
	
//	private ApplicationContext appContext; 
	@Autowired
	private AnalysisManager manager;
	
	@RequestMapping("/analysis")
	@ResponseBody
	public ModelAndView analysisView() {
		return new ModelAndView("analysis");
	}
	
	@RequestMapping(value = "/analysis/start_streaming", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> startStreaming(@RequestParam(value="filterTerms", required=true) String filterTerms) {
		//TODO check fiterterms for validity
		if(filterTerms.equals(""))
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		
		//manager = (AnalysisManager)appContext.getBean("AnalysisManager");
    	try {
			manager.setTweetSource(new TwitterStreaming(Arrays.asList(filterTerms.split(", | |,"))));
			manager.startAnalysis();
		} catch (IOException e) { e.printStackTrace(); }
		
    	return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/analysis/stop_streaming", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> stopStreaming() {
		manager.stopAnalysis(); 
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
//		this.appContext = appContext; TODO retrieve bean via context vs. autowire?
	}
	
}
