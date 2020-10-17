package jp.silverbullet.web;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;

import jp.silverbullet.core.KeyValue;
import jp.silverbullet.web.auth.GoogleAccressTokenResponse;
import jp.silverbullet.web.auth.GoogleHandlerImpl;
import jp.silverbullet.web.auth.ExternalStorageService;
import jp.silverbullet.web.auth.PersonalResponse;

//@Path("/system")
@Path("/")
public class SystemResource {
//	private GoogleHanlder googleHandler = new GoogleHandlerForTest();
	public static ExternalStorageService googleHandler = new GoogleHandlerImpl(ClientBuilder.newClient());
//	public static AuthStore authMap = new AuthStore();
	
	@GET
	@Path("/newApplication")
	@Produces(MediaType.TEXT_PLAIN) 
	public String createNewApplication(@CookieParam("SilverBullet") String cookie) {
		SilverBulletServer.getStaticInstance().newApplication(cookie);
		return "OK";
	}

	@GET
	@Path("/getApplications")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getApplications(@CookieParam("SilverBullet") String cookie) {
		List<String> list = SilverBulletServer.getStaticInstance().getApplications(cookie);
		return list;
	}

	
	@GET
	@Path("/nativeCreate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response nativeCreate(@QueryParam("username") final String username, 
			@QueryParam("password") final String password, @QueryParam("firstname") final String firstname,
			@QueryParam("familyname") final String familyname, @QueryParam("email") final String email) {
		
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		if (userStore.containsNativeUser(username)) {
			return Response.serverError().build();
		}

		PersonalResponse personal = new PersonalResponse();
		personal.name = username;
		personal.id = "Native" + String.valueOf(System.currentTimeMillis());
		personal.basicPassword = DigestUtils.shaHex(password);
		personal.email = email;
		personal.given_name = firstname;
		personal.family_name = familyname;
		String sessionName = String.valueOf(System.currentTimeMillis()); 

		userStore.put(sessionName, personal);
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
		return Response.ok(new String()).
				cookie(newCookie)
				.build();	
	}
	
	@GET
	@Path("/nativeLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response nativeLogin(@QueryParam("username") final String username, 
			@QueryParam("password") final String password) {
		
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		if (!userStore.containsNativeUser(username)) {
			return Response.serverError().build();
		}
		if (!userStore.matchesNativePassword(username, password)) {
			return Response.serverError().build();
		}
//		PersonalResponse personal = new PersonalResponse();
//		personal.name = username;

		String sessionName = String.valueOf(System.currentTimeMillis()); 

		userStore.updateCookie(username, sessionName);
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
		return Response.ok(new KeyValue("name", username, sessionName)).
				cookie(newCookie)
				.build();	
		
	}
	
	@GET
	@Path("/getAuthUrl")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getAuthUrl(@QueryParam("url") final String url, @QueryParam("service") final String service) {
		String authUrl = "";
		if (service.equals("google")) {
			authUrl = googleHandler.getAuthUri(url);
		}
		else if (service.equals("silverbullet")) {
			authUrl = "./login.html?redirectUri=" + url;
		}
		return Response.ok(new String(authUrl)).build();
	}
	
	@GET
	@Path("/autoLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response autoLogin(@CookieParam("SilverBullet") String cookie) {
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		if (cookie != null) {
			if (userStore.containsCookie(cookie)) {
				PersonalResponse res = userStore.getBySessionID(cookie);
				return Response.ok(new KeyValue("name", res.name)).build();
			}
		}
		return Response.ok(new KeyValue("name", "")).build();
	}

	@GET
	@Path("/newLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response newLogin(@QueryParam("code") final String code, @QueryParam("scope") final String scope,
			@QueryParam("redirectUri") final String redirectUri, @QueryParam("service") final String service,
			@Context HttpServletRequest request) throws Exception {

		GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);

		String access_token;
		PersonalResponse personal;
		access_token = accessToken.access_token;
		personal = googleHandler.retrievePersonal(access_token);
		personal.access_token = access_token;
		personal.auth_code = code;

		String sessionId = request.getSession().getId();//String.valueOf(System.currentTimeMillis()); 

		SilverBulletServer.getStaticInstance().login(sessionId, personal);
		
		//NewCookie(Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly)
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionId));
		
        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.YEAR, 1);
        
 //       System.out.println(cl.getTime());
//		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionId), "Session ID", -1, cl.getTime(), true, true);
		
		return Response.ok(new KeyValue("name", personal.name, sessionId)).
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
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		userStore.remove(cookie);
		return Response.ok().build();
	}
		
//	@GET
//	@Path("/loginTmp")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response login2(@QueryParam("code") final String code, @QueryParam("scope") final String scope,
//			@QueryParam("redirectUri") final String redirectUri) throws URISyntaxException {
//
//		try {
//			String access_token;
//
//			if (authMap.stores(code)) {
//				access_token = authMap.getByCode(code).access_token;
//			}
//			else {
//				GoogleAccressTokenResponse accessToken = googleHandler.retrieveAccessToken(code, redirectUri);
//				access_token = accessToken.access_token;
//			}
//			PersonalResponse personal = googleHandler.retrievePersonal(access_token);
//			personal.access_token = access_token;
//			personal.auth_code = code;
//			
//			authMap.add(personal);
//			
//			String sessionName = String.valueOf(System.currentTimeMillis()); 
//			
//			return Response.ok(new KeyValue("Complete", personal.name)).
//					cookie(new NewCookie("SilverBullet", sessionName)).build();	
//		}
//		catch (Exception e) {
//			String url = googleHandler.getAuthUri(redirectUri);
//			return Response.ok(new KeyValue("RedirectAuth", url)).build();
//		}
//	}
	
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
//	@Path("/loginAndroid")
//	public String loginAndroid(@QueryParam("auth") final String auth)
//	{
//		try {
//			PersonalResponse personal = googleHandler.retrievePersonal(auth);
//			personal.access_token = auth;
//			personal.auth_code = auth;
//			
//			authMap.add(personal);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return "OK";
//	}
	
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
