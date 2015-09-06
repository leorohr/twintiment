package org.twintiment.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.twintiment.dto.FileMetaDTO;
import org.twintiment.dto.Settings;

import twitter4j.auth.AccessToken;

public interface IAnalysisManager {
	public void runAnalysis();
	public void stopAnalysis();
	public Set<FileMetaDTO> getAvailableFiles();
	public void setSettings(Settings settings) throws IOException;
	public void setTweetSource(TweetSource source);
	public AnalysisStatistics getStats();
	public void addAvailableFile(File file);
	public AccessToken getAccessToken();
	public void setAccessToken(AccessToken accessToken);
	public void setup();
}
