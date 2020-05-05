package jp.silverbullet.web.auth;

import java.io.File;

public interface ExternalStorageService {
	GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception;
	PersonalResponse retrievePersonal(String accessToken) throws Exception;
	String getAuthUri(String redirectUri);
	String postFile(String access_token, String type, String folder, File file);
	File download(String access_token, String fileid);
}
