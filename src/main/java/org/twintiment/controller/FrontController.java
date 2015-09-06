package org.twintiment.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.twintiment.analysis.IAnalysisManager;
import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;
import org.twintiment.dto.StatsDTO;

@Controller
public class FrontController {
	 
	@Autowired
	private IAnalysisManager manager;	
	
	@RequestMapping("/analysis")
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
}
