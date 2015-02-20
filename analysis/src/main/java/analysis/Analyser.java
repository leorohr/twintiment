package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Analyser {
	
	private File file;
	private BufferedReader in;
	private String currentLine;
	private ObjectMapper mapper;
	private JsonNode root;	
	
	public Analyser(String path) throws IOException {
		file = new File(path);
		in = new BufferedReader(new FileReader(file));
		currentLine = in.readLine();
		
		mapper = new ObjectMapper();
		root = mapper.readTree(currentLine);	
	}
	
	/**
	 * Advances the reader by one line in the input file.
	 * @throws IOException 
	 */
	public void nextLine() throws IOException {
		currentLine = in.readLine();
	}
	
	public boolean hasNext() {
		return currentLine != null;
	}
	
	public boolean hasGeoTag() {
		return root.findValue("location") != null;
	}

}
