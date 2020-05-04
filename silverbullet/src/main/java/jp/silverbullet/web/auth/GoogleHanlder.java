package jp.silverbullet.web.auth;

import java.io.File;

public interface GoogleHanlder {
	GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception;
	PersonalResponse retrievePersonal(String accessToken) throws Exception;
	String getAuthUri(String redirectUri);
	void postFile(String access_token, String type, String folder, File file);
}
