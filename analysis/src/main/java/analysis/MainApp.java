package analysis;

import java.io.File;



public class MainApp {
	
	public static void main(String[] args) {
	
		ConcProcessor proc = new ConcProcessor(2);
		proc.runAnalysis(new File("/Users/leorohr/Desktop/tweets.json"));
		
	}
}
