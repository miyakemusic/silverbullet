package jp.silverbullet.web.auth;

public interface GoogleHanlder {
	GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception;
	GooglePersonalResponse retrievePersonal(String accessToken) throws Exception;
	String getAuthUri(String redirectUri);
}
