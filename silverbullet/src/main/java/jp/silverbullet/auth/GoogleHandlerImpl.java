package jp.silverbullet.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleHandlerImpl implements GoogleHanlder {
	
	private Client client;
	private String client_id;
	private String client_secret;

	public GoogleHandlerImpl(Client client) {
		this.client = client;
		
		try {
			List<String> lines = Files.readAllLines(Paths.get("./code"));
			this.client_id = lines.get(0);
			this.client_secret = lines.get(1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) throws Exception {		
//        MultivaluedHashMap<String, String> formParams = new MultivaluedHashMap<>();
        Map<String, String> formParams = new HashMap<>();
      
        formParams.put("client_id", client_id);
        formParams.put("client_secret", client_secret);
       // formParams.putSingle("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
        formParams.put("redirect_uri", redirectUri);
        
        formParams.put("grant_type", "authorization_code");
        formParams.put("access_type", "offline");
        formParams.put("code", code);
        
        System.out.println("client_id=" + client_id);
        System.out.println("client_secret=" + client_secret);
        System.out.println("code=" + code);
        
 /*       GoogleAccressTokenResponse response = client
                .target("https://www.googleapis.com")
                .path("/oauth2/v4/token")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(formParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE), GoogleAccressTokenResponse.class);
 */
        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach((k, v) -> formBuilder.add(k, v));
        RequestBody requestBody = formBuilder.build();
        System.out.println("Request request = new Request.Builder()");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        System.out.println("OkHttpClient");
        Response responseOk = client.newCall(request).execute();
        String json = responseOk.body().string();   
        
        System.out.println("Object Mapper");
        GoogleAccressTokenResponse response = new ObjectMapper().readValue(json, GoogleAccressTokenResponse.class);
        System.out.println("access token=" + response.access_token);
        return response;
	}

	@Override
	public GooglePersonalResponse retrievePersonal(String accessToken) throws Exception {
        // get user info
 /*       WebTarget target = client.target("https://www.googleapis.com")
        	.path("/oauth2/v1/userinfo")
        	.queryParam("alt", "json")
        	.queryParam("access_token", accessToken);
        
		GooglePersonalResponse personal = target.request().get(GooglePersonalResponse.class);	
*/
		OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = client.newCall(request).execute();
		return new ObjectMapper().readValue(responseOk.body().string(), GooglePersonalResponse.class);	
	}

	@Override
	public String getAuthUri(String redirectUri) {
		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
				+ "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";
	}


}
