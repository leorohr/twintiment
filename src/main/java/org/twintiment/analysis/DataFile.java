package org.twintiment.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataFile implements TweetSource {

	private Scanner scanner;
	private int lines = 0;
	
	public DataFile(String filepath) throws FileNotFoundException {
		
		File file = new File(filepath);
		scanner = new Scanner(file);
		
		//Kick off a thread to count the total number of lines in the file. 
		new Thread(new Runnable() {
			@Override
			public void run() {
				int lines = 0;
				Scanner s;
				try {
					s = new Scanner(file);
					while(s.hasNextLine()) {
						s.nextLine();
						++lines;
					}
				} catch (FileNotFoundException e) { e.printStackTrace(); }
				
				DataFile.this.setLines(lines);
			}
			
		}, "CountLines-"+file.getName()).start();
	}
	
	@Override
	public String getNextTweet() {
		String next = "";
		JsonNode js = null;
		//only return the string, if it is a valid tweet
		do {
			try {
				ObjectMapper mapper = new ObjectMapper();
				if(scanner.hasNextLine()) {
					next = scanner.nextLine();
					js = mapper.readTree(next);
				} else return null;
				
			} catch(Exception e) { e.printStackTrace(); }
		} while(js.findValue("created_at") == null);
		return next;
	}

	@Override
	public boolean hasNext() {
		
		return scanner.hasNextLine();
	}

	@Override
	public void close() {
		scanner.close();
	}
	
	public void setLines(int lines) {
		this.lines = lines;
	}
	
	public int getLines() {
		return this.lines;
	}

}
