package jp.silverbullet.web.auth;

import java.io.File;
import java.util.List;

public interface ExternalStorageService {
	GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception;
	PersonalResponse retrievePersonal(String accessToken) throws Exception;
	String getAuthUri(String redirectUri);
	String postFile(String access_token, String type, String folder, File file);
	byte[] download(String access_token, String fileid);
	List<com.google.api.services.drive.model.File> getFileList(String access_token, String path);
	String downloadCompleted(String access_token, String fileid);
}
