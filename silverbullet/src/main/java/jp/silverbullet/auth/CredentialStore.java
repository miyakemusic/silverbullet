package jp.silverbullet.auth;

import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;

public class CredentialStore {
	public static TokenResult tokenResult = null;
	/**
	 * Contains null or actually authorization flow.
	 */
	public static OAuth2CodeGrantFlow cachedFlow;
	private static ClientIdentifier clientIdentifier;
	
	public static TokenResult getTokenResult() {
	    //check();
	    return tokenResult;
	}
	
	public static ClientIdentifier getClientId() {
	    return clientIdentifier;
	}
	
	public static void setClientIdentifier(ClientIdentifier clientId) {
	    clientIdentifier = clientId;
	}
}
