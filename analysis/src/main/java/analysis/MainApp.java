package analysis;

import java.io.IOException;

public class MainApp {
	
	public static void main(String[] args) {
		try {
			Analyser analyser = new Analyser("C:/Users/Leo/Desktop/tweets.json");
			
			while(analyser.hasNext()) {
				analyser.hasGeoTag();
				analyser.nextLine();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
