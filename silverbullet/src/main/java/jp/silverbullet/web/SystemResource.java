package jp.silverbullet.web;

import java.net.URISyntaxException;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import jp.silverbullet.core.KeyValue;
import jp.silverbullet.core.property2.SvFileException;
import jp.silverbullet.web.auth.GoogleAccressTokenResponse;
import jp.silverbullet.web.auth.GoogleHandlerImpl;
import jp.silverbullet.web.auth.GoogleHanlder;
import jp.silverbullet.web.auth.PersonalResponse;

//@Path("/system")
@Path("/")
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
	public static UserStore userStore = new UserStore();
	
//	private GoogleHanlder googleHandler = new GoogleHandlerForTest();
	private GoogleHanlder googleHandler = new GoogleHandlerImpl(ClientBuilder.newClient());

	@GET
	@Path("/getAuthUrl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAuthUrl(@QueryParam("url") final String url) {
		String authUrl = googleHandler.getAuthUri(url);
		return Response.ok(new String(authUrl)).build();
	}
	
	@GET
	@Path("/autoLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response autoLogin(@CookieParam("SilverBullet") String cookie) {
		if (cookie != null) {
			if (userStore.containsCookie(cookie)) {
				PersonalResponse res = userStore.getByCookie(cookie);
				return Response.ok(new KeyValue("name", res.name)).build();
			}
		}
		return Response.ok(new KeyValue("name", "")).build();
	}

	@GET
	@Path("/newLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response newLogin(@QueryParam("code") final String code, @QueryParam("scope") final String scope,
			@QueryParam("redirectUri") final String redirectUri) throws Exception {

		GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);

		String access_token;
		PersonalResponse personal;
		access_token = accessToken.access_token;
		personal = googleHandler.retrievePersonal(access_token);
		personal.access_token = access_token;
		personal.auth_code = code;

		String sessionName = String.valueOf(System.currentTimeMillis()); 

		userStore.put(sessionName, personal);
		
//		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName, "/rest/", ""));
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
		return Response.ok(new KeyValue("name", personal.name)).
				cookie(newCookie)
				.build();		
	}
	
//	@GET
//	@Path("/login")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response login(@CookieParam("SilverBullet") String cookie, @QueryParam("code") final String code, @QueryParam("scope") final String scope,
//			@QueryParam("redirectUri") final String redirectUri) throws URISyntaxException {
//
//		try {
//			String access_token;
//			PersonalResponse personal;
//			
//			if (userStore.containsCookie(cookie)) {
//				access_token = userStore.get(cookie).access_token;
//				personal = userStore.get(cookie);
//				return Response.ok(new KeyValue("Complete", personal.name)).build();
//			}
//			else {
//				GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);
//				access_token = accessToken.access_token;
//				personal = googleHandler.retrievePersonal(access_token);
//				personal.access_token = access_token;
//				personal.auth_code = code;
//
//				String sessionName = String.valueOf(System.currentTimeMillis()); 
//
//				userStore.put(sessionName, personal);
//				
//				NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName, "/rest", ""));
//				//NewCookie newCookie = new NewCookie("SilverBullet", sessionName);
//				//NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
//				return Response.ok(new KeyValue("Complete", personal.name)).
//						cookie(newCookie)
//						.build();
//					
//			}
//			
//			//return new KeyValue("Complete", personal.name);	
//		}
//		catch (Exception e) {
////			e.printStackTrace();
//			String url = googleHandler.getAuthUri(redirectUri);
//			return Response.ok(new KeyValue("RedirectAuth", url)).build();
//			//return new KeyValue("RedirectAuth", url);
//		}
//	}
	
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@CookieParam("SilverBullet") String cookie) {
		userStore.remove(cookie);
		return Response.ok().build();
	}
		
	@GET
	@Path("/loginTmp")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login2(@QueryParam("code") final String code, @QueryParam("scope") final String scope,
			@QueryParam("redirectUri") final String redirectUri) throws URISyntaxException {
//	System.out.println("code=" + code);
		try {
			String access_token;

			if (authMap.stores(code)) {
				access_token = authMap.getByCode(code).access_token;
			}
			else {
				GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);
				access_token = accessToken.access_token;
			}
			PersonalResponse personal = googleHandler.retrievePersonal(access_token);
			personal.access_token = access_token;
			personal.auth_code = code;
			
			authMap.add(personal);
			
			String sessionName = String.valueOf(System.currentTimeMillis()); 
			
			return Response.ok(new KeyValue("Complete", personal.name)).
					cookie(new NewCookie("SilverBullet", sessionName)).build();
			
			//return new KeyValue("Complete", personal.name);	
		}
		catch (Exception e) {
//			e.printStackTrace();
			String url = googleHandler.getAuthUri(redirectUri);
			return Response.ok(new KeyValue("RedirectAuth", url)).build();
			//return new KeyValue("RedirectAuth", url);
		}
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/loginAndroid")
	public String loginAndroid(@QueryParam("auth") final String auth)
	{
		try {
			PersonalResponse personal = googleHandler.retrievePersonal(auth);
			personal.access_token = auth;
			personal.auth_code = auth;
			
			authMap.add(personal);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "OK";
	}
	
	@GET
	@Path("/undo")
	public String undo() {
		return "OK";
	}
	
	@GET
	@Path("/redo")
	public String redo() {
		return "OK";
	}
}
