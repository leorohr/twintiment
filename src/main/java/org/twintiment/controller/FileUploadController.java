package org.twintiment.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.twintiment.analysis.IAnalysisManager;

/**
 * Controller that handles all file uploads from the client. 
 */
@Controller 
@MultipartConfig
public class FileUploadController {

	@Autowired
	private IAnalysisManager manager;
	
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Exposes {@code POST /upload} endpoint. Uploaded files are stored in '/datasets'
	 * @param request
	 * @param response
	 * @return {@link HttpStatus#OK} if the upload was successfully completed.
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "content-type=multipart/*")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(
			MultipartHttpServletRequest request,
			HttpServletResponse response) {
		
		
		Iterator<String> iter = request.getFileNames();
		MultipartFile multiFile = request.getFile(iter.next());
		File file = null;
		try {
			String fileName = multiFile.getOriginalFilename();
			logger.info("Uploaded new Dataset");
			logger.info("File Name:" + fileName);
			logger.info("File Length:" + multiFile.getBytes().length);
			logger.info("File Type:" + multiFile.getContentType());
			String path = request.getServletContext().getRealPath("/");

			// making directories for our required path.
			byte[] bytes = multiFile.getBytes();
			File directory = new File(path + "/datasets");
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
