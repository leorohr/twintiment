package org.twintiment.analysis;

public class TwintimentAccessToken {
    
	private String token;
    private String tokenSecret;
        
    public TwintimentAccessToken(String token, String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}
    
	public String getTokenSecret() {
        return tokenSecret;
    }
    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
