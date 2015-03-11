package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConcProcessor {
	
	private int lineCount = 0;
	private int taggedCount = 0;
	private long start = 0;
	private BlockingQueue<String> q = new LinkedBlockingQueue<String>();
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	
	public synchronized void incLineCount() {
		
		//Logging
		if(lineCount % 10000 == 0) {
			System.out.println("[" + ((double) System.currentTimeMillis() - start)/1000 + "s] " + lineCount);	
		}
		lineCount++;
	}
	
	public ConcProcessor(int threadCount) {
		
		for(int i=0; i < threadCount; i++)
			threads.add(new ProcessingThread());
	}

	public void runAnalysis(File f) {
		
		ReadingThread rThr = new ReadingThread(f); 
		
		start = System.currentTimeMillis();
		rThr.start();
		
		for(Thread t : threads)
			t.start();
		

		try {
			for(Thread t : threads)
				t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Read %d tweets.\n", lineCount);
		System.out.printf("Found %d tagged tweets.", taggedCount);
	}
	

	public synchronized void incTaggedCount() {
		taggedCount++;
	}
	
	public synchronized int getLineCount() {
		return lineCount;
	}
	
	
	private class ReadingThread extends Thread {
		
		private File file;
		
		public ReadingThread(File f) {
			super("ReadingThread");
			this.file = f;
		}
		
		@Override
		public void run() {
			
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				String currentLine = in.readLine();
				while(currentLine != null) {
					q.add(currentLine);
					currentLine = in.readLine();
				}
				
				//one for each thread
				for(int i=0; i<threads.size(); i++)
					q.add("EOF");
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	private class ProcessingThread extends Thread {		

		@Override
		public void run() {
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = null;
			try {
				String currentLine = q.take();
				while(!currentLine.equals("EOF")) {
					
					if(currentLine.equals("None")) {
						System.out.println("'None'-line detected at " + getLineCount());
						//Skip line
						incLineCount();
						currentLine = q.take();
						continue;
					} 
					
					root = mapper.readTree(currentLine);
					JsonNode val = root.findValue("coordinates");
					if(!val.isNull()) { //&& root.findValue("coordinates").isArray()) {
//						System.out.println("tagged: " + val.findValue("coordinates").get(0).asDouble() + " " + val.findValue("coordinates").get(1).asDouble());
						taggedCount++;
					}
					
					currentLine = q.take();
					incLineCount();
				} 
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
			System.out.println("[" + Thread.currentThread().getName() + " | " + ((double) System.currentTimeMillis() - start)/1000 + "s] " + "joined.");
		}	
	}

}
