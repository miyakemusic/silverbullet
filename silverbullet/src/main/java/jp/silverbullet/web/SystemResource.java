package jp.silverbullet.web;

import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import jp.silverbullet.SilverBulletServer;
import jp.silverbullet.auth.GoogleAccressTokenResponse;
import jp.silverbullet.auth.GoogleHandlerForTest;
import jp.silverbullet.auth.GoogleHandlerImpl;
import jp.silverbullet.auth.GoogleHanlder;
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
//	private GoogleHanlder googleHandler = new GoogleHandlerForTest();//new GoogleHandlerImpl(client);
	private GoogleHanlder googleHandler = new GoogleHandlerImpl(client);
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public KeyValue login2(@QueryParam("code") final String code, @QueryParam("scope") final String scope,
			@QueryParam("redirectUri") final String redirectUri) throws URISyntaxException {
	System.out.println("code=" + code);
		try {
			String access_token;

			if (authMap.stores(code)) {
				access_token = authMap.getByCode(code).access_token;
			}
			else {
				GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);
				access_token = accessToken.access_token;
			}
			GooglePersonalResponse personal = googleHandler.retrievePersonal(access_token);
			personal.access_token = access_token;
			personal.auth_code = code;
			
			authMap.add(personal);
			return new KeyValue("Complete", personal.name);	
		}
		catch (Exception e) {
			e.printStackTrace();
			String url = googleHandler.getAuthUri(redirectUri);
			return new KeyValue("RedirectAuth", url);
		}
	}
	
//	private GoogleAccressTokenResponse retrieveAccessToken(String code, String redirectUri) {
//		String client_id = "";
//		String client_secret = "";
//		
//        MultivaluedHashMap<String, String> formParams = new MultivaluedHashMap<>();
//        
//        formParams.putSingle("client_id", client_id);
//        formParams.putSingle("client_secret", client_secret);
//       // formParams.putSingle("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
//        formParams.putSingle("redirect_uri", redirectUri);
//        
//        formParams.putSingle("grant_type", "authorization_code");
//        formParams.putSingle("access_type", "offline");
//        formParams.putSingle("code", code);
//        
//        GoogleAccressTokenResponse response = client
//                .target("https://www.googleapis.com")
//                .path("/oauth2/v4/token")
//                .request()
//                .post(Entity.entity(formParams, MediaType.APPLICATION_FORM_URLENCODED_TYPE), GoogleAccressTokenResponse.class);
//                
//        System.out.println("access token=" + response.access_token);
//        return response;
//
//	}
//	
//	private GooglePersonalResponse retrievePersonal(String accessToken) {
//        // get user info
//        WebTarget target = client.target("https://www.googleapis.com")
//        	.path("/oauth2/v1/userinfo")
//        	.queryParam("alt", "json")
//        	.queryParam("access_token", accessToken);
//        
//		GooglePersonalResponse personal = target.request().get(GooglePersonalResponse.class);			
//		return personal;	
//	}
}
