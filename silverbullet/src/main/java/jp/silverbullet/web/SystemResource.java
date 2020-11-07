package jp.silverbullet.web;

import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import jp.silverbullet.dev.BuilderModelHolder;
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
	
	@POST
	@Path("/postZip")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.TEXT_PLAIN) 
	public String postZip(@CookieParam("SilverBullet") String cookie, @QueryParam("filename") String filename, String base64) {
		byte[] bytes = Base64.getDecoder().decode(base64.split("base64,")[1]);
		SilverBulletServer.getStaticInstance().createFile(cookie, bytes, filename);
		return "OK";
	}
	
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
	@Path("/createDefaultAccound")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDefault() {
		String sessionName = String.valueOf(System.currentTimeMillis()); 
		String userSerial = BuilderModelHolder.DEFAULT_USER_SERIAL;

		String username = "silverbullet";
		String password = "silverbullet";
		
		PersonalResponse personal = createNative(username, password, "", "", "", userSerial, sessionName);
		if (personal == null) {
			return Response.serverError().build();
		}
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
		return Response.ok(new String()).
				cookie(newCookie)
				.build();	
	}
	
	@GET
	@Path("/updateDefaultAccount")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDefaultAccount(@CookieParam("SilverBullet") String cookie, 
			@QueryParam("application") final String application) {
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		if (!userStore.containsNativeUser(BuilderModelHolder.DEFAULT_USER_SERIAL)) {
			createDefault();
		}
		SilverBulletServer.getStaticInstance().copyConfigToDefault(userStore.getBySessionName(cookie).getId(), application);
		return Response.ok(new String())
				.build();	
	}
	
	private PersonalResponse createNative(String username, String password, String firstName,
			String familyName, String email, String useSerial, String sessionName) {
		
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		if (userStore.containsNativeUser(username)) {
			//return Response.serverError().build();
			return null;
		}
		
		PersonalResponse personal = new PersonalResponse();
		personal.name = username;
		personal.id = useSerial;
		personal.basicPassword = DigestUtils.shaHex(password);
		personal.email = email;
		personal.given_name = firstName;
		personal.family_name = familyName;
		
		userStore.put(sessionName, personal);
		
		return personal;
	}
	
	@GET
	@Path("/nativeCreate")
	@Produces(MediaType.TEXT_PLAIN)
	public Response nativeCreate(@QueryParam("username") final String username, 
			@QueryParam("password") final String password, @QueryParam("firstname") final String firstname,
			@QueryParam("familyname") final String familyname, @QueryParam("email") final String email) {
		
		String sessionName = String.valueOf(System.currentTimeMillis()); 

		PersonalResponse personal = createNative(username, password, firstname, familyname, email, sessionName, "Navive"+sessionName);
		if (personal == null) {
			return Response.serverError().build();
		}
		
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
		return Response.ok(new String()).
				cookie(newCookie)
				.build();	
	}
	
	@GET
	@Path("/nativeLogin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response nativeLogin(@QueryParam("username") final String username, 
			@QueryParam("password") final String password, @Context HttpServletRequest request) {
		
		System.out.println("nativeLogin " + username + "/" + password);
		
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		if (!userStore.containsNativeUser(username)) {
			return Response.serverError().build();
		}
		if (!userStore.matchesNativePassword(username, password)) {
			return Response.serverError().build();
		}

		String sessionName = request.getSession().getId();//String.valueOf(System.currentTimeMillis()); 

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
				PersonalResponse res = userStore.getBySessionName(cookie);
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

		String sessionName = request.getSession().getId();//String.valueOf(System.currentTimeMillis()); 

		SilverBulletServer.getStaticInstance().login(sessionName, personal);
		NewCookie newCookie = new NewCookie(new Cookie("SilverBullet", sessionName));
		
        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.YEAR, 1);
	
		return Response.ok(new KeyValue("name", personal.name, sessionName)).
				cookie(newCookie)
				.build();		
	}
		
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@CookieParam("SilverBullet") String cookie) {
		UserStore userStore = SilverBulletServer.getStaticInstance().getUserStore();
		
		userStore.remove(cookie);
		return Response.ok().build();
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
