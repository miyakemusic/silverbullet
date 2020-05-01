package jp.silverbullet.web.auth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleHandlerImpl implements GoogleHanlder {
	
	private String client_id;
	private String client_secret;
	private OkHttpClient httpClient;

	public GoogleHandlerImpl(Client client) {
		httpClient = new OkHttpClient();
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
        Map<String, String> formParams = new HashMap<>();
      
        formParams.put("client_id", client_id);
        formParams.put("client_secret", client_secret);
        if (redirectUri != null) {
        	formParams.put("redirect_uri", redirectUri);
        }
        
        formParams.put("grant_type", "authorization_code");
        formParams.put("access_type", "offline");
        formParams.put("code", code);
        
        System.out.println("client_id=" + client_id);
        System.out.println("client_secret=" + client_secret);
        System.out.println("code=" + code);
        
        final FormBody.Builder formBuilder = new FormBody.Builder();
        formParams.forEach((k, v) -> formBuilder.add(k, v));
        RequestBody requestBody = formBuilder.build();
        System.out.println("Request request = new Request.Builder()");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();


        System.out.println("OkHttpClient");
        Response responseOk = httpClient.newCall(request).execute();
        String json = responseOk.body().string();   
        
        System.out.println("Object Mapper");
        GoogleAccressTokenResponse response = new ObjectMapper().readValue(json, GoogleAccressTokenResponse.class);
        System.out.println("access token=" + response.access_token);
        return response;
	}

	@Override
	public PersonalResponse retrievePersonal(String accessToken) throws Exception {
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
                .get()
                .build();
        Response responseOk = httpClient.newCall(request).execute();
        String string  = responseOk.body().string();
		return new ObjectMapper().readValue(string, PersonalResponse.class);	
	}

//	@Override
//	public String getAuthUri(String redirectUri) {
//		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
//				+ "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";
//	}

	@Override
	public String getAuthUri(String redirectUri) {
		return "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirectUri
				+ "&scope=https://www.googleapis.com/auth/userinfo.profile" + 
				"%20https://www.googleapis.com/auth/drive.appdata" + 
				"%20https://www.googleapis.com/auth/drive.file" + 
				"&access_type=offline&approval_prompt=force";
	}

//	@Override
	public void postFile2(String access_token, String contentType, File file, String filename) {
      final RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), file);    
		
      Request request = new Request.Builder()
              .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=media")//&access_token=" + accessToken)
              .addHeader("Authorization", "Bearer " + access_token)
              .addHeader("Content-Type", contentType)
              .addHeader("Content-Length", String.valueOf(file.length()))
              .post(requestBody)
              .build();
      try {
			Response responseOk = httpClient.newCall(request).execute();
			System.out.println("Uploaded " + responseOk.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public void postFile(String access_token, String contentType, File file) {
//		RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("title", file.getName())
//                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse(contentType), file))
//                .build();		
        Map<String, String> param = new HashMap<>();
        param.put("title", file.getName());
		
		RequestBody requestBody = null;
//		try {
			requestBody = new MultipartBody.Builder("--foo_bar_baz")
			        .setType(MultipartBody.FORM)
/*			        .addPart(
			                Headers.of("title", file.getName()),

			                RequestBody.create(MediaType.parse("application/json"), "{\"title\":\"" + file.getName() + "\"}")
			        )*/
			        .addPart(RequestBody.create(MediaType.parse("application/json"), "{\"name\":\"" + file.getName() + "\"}"))
/*			        .addFormDataPart(
			                "media",
			                file.getName(),
			                RequestBody.create(MediaType.parse(contentType), file)
			        )*/
			        .addPart(RequestBody.create(MediaType.parse(contentType), file))
			        .build();
//		} catch (JsonProcessingException e1) {
//			e1.printStackTrace();
//		}
		

      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder()
              .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
              .addHeader("Authorization", "Bearer " + access_token)
              .addHeader("Content-Type", contentType)
              .addHeader("Content-Length", String.valueOf(file.length()))
              .post(requestBody)
              .build();

      try {
    	  System.out.println(request.toString());
    	  Response response = client.newCall(request).execute();
    	  System.out.println("Uploaded " + response.toString());
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	}	
	

}
