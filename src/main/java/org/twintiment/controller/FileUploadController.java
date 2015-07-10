package org.twintiment.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.twintiment.analysis.AnalysisManager;

@Controller 
@MultipartConfig
public class FileUploadController {

	@Autowired
	private AnalysisManager manager;

	@RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "content-type=multipart/*")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(
			MultipartHttpServletRequest request,
			HttpServletResponse response) {
		
		
		Iterator<String> iter = request.getFileNames();
		MultipartFile multiFile = request.getFile(iter.next());
		File file = null;
		try {
			// just to show that we have actually received the file
			System.out.println("File Length:" + multiFile.getBytes().length);
			System.out.println("File Type:" + multiFile.getContentType());
			String fileName = multiFile.getOriginalFilename();
			System.out.println("File Name:" + fileName);
			String path = request.getServletContext().getRealPath("/");

			// making directories for our required path.
			byte[] bytes = multiFile.getBytes();
			File directory = new File(path + "/datasets");
			directory.mkdir();
			// saving the file
			file = new File(directory.getAbsolutePath()
					+ System.getProperty("file.separator") + multiFile.getOriginalFilename());
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(file));
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		manager.addAvailableFile(file);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}
