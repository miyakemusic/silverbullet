package jp.silverbullet.auth;

public class GoogleHandlerForTest implements GoogleHanlder {

	@Override
	public GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception {
		if (!code.equals("12345")) {
			throw new Exception();
		}
		GoogleAccressTokenResponse ret = new GoogleAccressTokenResponse();
		ret.access_token = "test_access_token";
		return ret;
	}

	@Override
	public GooglePersonalResponse retrievePersonal(String accessToken) {
		GooglePersonalResponse ret = new GooglePersonalResponse();
		ret.name = "Test User";
		ret.id = "testuser_000000";
		return ret;
	}

	@Override
	public String getAuthUri(String redirectUri) {
		//return "http://localhost:8081/auth.html";
		return redirectUri + "?code=12345";
	}
}
