package jp.silverbullet.web;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.auth.GoogleAccressTokenResponse;
import jp.silverbullet.auth.GooglePersonalResponse;
import jp.silverbullet.property2.SvFileException;

@Path("/system")
public class SystemResource {
	@GET
	@Path("/save")
	@Produces(MediaType.TEXT_PLAIN) 
	public String Save() {
		SilverBulletServer.getStaticInstance().save();
		return "OK";
	}
	
	@GET
	@Path("/generateSource")
	@Produces(MediaType.TEXT_PLAIN) 
	public String generateSource() {
		SilverBulletServer.getStaticInstance().generateSource();
		return "OK";
	}
	
	@GET
	@Path("/saveParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String saveParameters(@QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel().saveParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/loadParameters")
	@Produces(MediaType.TEXT_PLAIN) 
	public String loadParameters(@QueryParam("filename") final String filename) {
		try {
			SilverBulletServer.getStaticInstance().getBuilderModel().loadParameters(filename);
		} catch (SvFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	public static AuthStore authMap = new AuthStore();
	
	private Client client = ClientBuilder.newClient();
	@GET
	@Path("/login2")
	@Produces(MediaType.APPLICATION_JSON)
	public KeyValue login(@QueryParam("code") final String code, @QueryParam("scope") final String scope) throws URISyntaxException {

		System.out.println(code);
		KeyValue ret = new KeyValue();
		
		String redirectUri = "http://localhost:8081";///login.html";		
		String url = "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=1095025795016-u18jdnil89cmp2841a67n5jumoigk6fm.apps.googleusercontent.com&redirect_uri=" + redirectUri + "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";
		ret.setKey("RedirectAuth");
		ret.setValue(url);

		if (!code.isEmpty()) {
			
			
			String client_id = "1095025795016-u18jdnil89cmp2841a67n5jumoigk6fm.apps.googleusercontent.com";
			String client_secret = "TFOM1Sz4CdjFxuGg9xvK4bBH";
			
	        MultivaluedHashMap<String, String> formParams = new MultivaluedHashMap<>();
	        
	        formParams.putSingle("client_id", client_id);
	        formParams.putSingle("client_secret", client_secret);
	       // formParams.putSingle("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
	        formParams.putSingle("redirect_uri", redirectUri);
	        
	        formParams.putSingle("grant_type", "authorization_code");
	        formParams.putSingle("access_type", "offline");
	        formParams.putSingle("code", code);
	        
	        try {
		        GoogleAccressTokenResponse response = client
		                .target("https://www.googleapis.com")
		                .path("/oauth2/v4/token")
		                .request()
		                .post(Entity.entity(formParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE), GoogleAccressTokenResponse.class);
		                
		        System.out.println("access token=" + response.access_token);
		           
		        // get user info
		        WebTarget target = client.target("https://www.googleapis.com")
		        	.path("/oauth2/v1/userinfo")
		        	.queryParam("alt", "json")
		        	.queryParam("access_token", response.access_token);
		        
				try {
					GooglePersonalResponse personal = target.request().get(GooglePersonalResponse.class);
					ret.setKey("Complete");
					ret.setValue(personal.name);
//					System.out.println("ID:" + personal.id);
					
					personal.access_token = response.access_token;
					personal.auth_code = code;
					
					authMap.add(personal);
					return ret;
				    
				} catch (Exception e) {
//				    e.printStackTrace();
				}		        
			        	
	        }
	        catch (Exception e) {
//	        	e.printStackTrace();
	        }
		}

		return ret;
	}
	
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public KeyValue login2(@QueryParam("code") final String code, @QueryParam("scope") final String scope) throws URISyntaxException {
		String redirectUri = "http://localhost:8081";
		
		try {
			String access_token;

			if (authMap.stores(code)) {
				access_token = authMap.getByCode(code).access_token;
			}
			else {
				GoogleAccressTokenResponse accessToken = retrieveAccessToken(code, redirectUri);
				access_token = accessToken.access_token;
			}
			GooglePersonalResponse personal = retrievePersonal(access_token);
			personal.access_token = access_token;
			personal.auth_code = code;
			
			authMap.add(personal);
			return new KeyValue("Complete", personal.name);	
		}
		catch (Exception e) {
			String url = "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=1095025795016-u18jdnil89cmp2841a67n5jumoigk6fm.apps.googleusercontent.com&redirect_uri=" + redirectUri + "&scope=https://www.googleapis.com/auth/userinfo.profile&access_type=offline&approval_prompt=force";	
			return new KeyValue("RedirectAuth", url);
		}
	}
	
	private GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) {
		String client_id = "1095025795016-u18jdnil89cmp2841a67n5jumoigk6fm.apps.googleusercontent.com";
		String client_secret = "TFOM1Sz4CdjFxuGg9xvK4bBH";
		
        MultivaluedHashMap<String, String> formParams = new MultivaluedHashMap<>();
        
        formParams.putSingle("client_id", client_id);
        formParams.putSingle("client_secret", client_secret);
       // formParams.putSingle("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
        formParams.putSingle("redirect_uri", redirectUri);
        
        formParams.putSingle("grant_type", "authorization_code");
        formParams.putSingle("access_type", "offline");
        formParams.putSingle("code", code);
        
        GoogleAccressTokenResponse response = client
                .target("https://www.googleapis.com")
                .path("/oauth2/v4/token")
                .request()
                .post(Entity.entity(formParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE), GoogleAccressTokenResponse.class);
                
        System.out.println("access token=" + response.access_token);
        return response;

	}
	
	private GooglePersonalResponse retrievePersonal(String accessToken) {
        // get user info
        WebTarget target = client.target("https://www.googleapis.com")
        	.path("/oauth2/v1/userinfo")
        	.queryParam("alt", "json")
        	.queryParam("access_token", accessToken);
        
		GooglePersonalResponse personal = target.request().get(GooglePersonalResponse.class);			
		return personal;	
	}
}
