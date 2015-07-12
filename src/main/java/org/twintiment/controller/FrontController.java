package org.twintiment.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.twintiment.analysis.DataFile;
import org.twintiment.analysis.IAnalysisManager;
import org.twintiment.analysis.TwitterStreaming;
import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;
import org.twintiment.dto.StatsDTO;

@Controller
public class FrontController {
	 
	@Autowired
	private IAnalysisManager manager;
	@Autowired
	private ServletContext servletContext;
	
	
	@RequestMapping("/analysis")
	@ResponseBody
	public ModelAndView analysisView() {
		return new ModelAndView("analysis");
	}
	
	@RequestMapping(value = "/analysis/start_streaming", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> startStreaming(@RequestBody Settings settings) {
		
		manager.setSettings(settings);
		
		if(settings.getFilterTerms().equals(""))
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		
    	try {
			manager.setTweetSource(new TwitterStreaming(Arrays.asList(settings.getFilterTerms().split(", | |,"))));
			manager.runAnalysis();
		} catch (IOException e) { e.printStackTrace(); }
		
    	return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/analysis/stop", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> stopStreaming() {
		
		manager.stopAnalysis(); 
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/analysis/start", method=RequestMethod.POST) 
	@ResponseBody
	public ResponseEntity<String> startAnalysis(@RequestBody Settings settings) {
		
		if(settings.getFileName() == null || settings.getFileName().equals("")) {
			return new ResponseEntity<String>(HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		manager.setSettings(settings);
		
		try {
			manager.setTweetSource(new DataFile(servletContext.getRealPath("/datasets/" + settings.getFileName())));
			manager.runAnalysis();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
}
