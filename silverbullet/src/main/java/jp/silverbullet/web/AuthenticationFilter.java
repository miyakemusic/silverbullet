package jp.silverbullet.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import jp.silverbullet.web.auth.PersonalResponse;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter
{

	private static final String SYSTEMPATH = "/rest/";
	
	private static List<String> extPath = Arrays.asList(
			SYSTEMPATH + "login", SYSTEMPATH + "autoLogin", SYSTEMPATH + "newLogin", 
			SYSTEMPATH + "nativeLogin", SYSTEMPATH + "nativeCreate", 
			SYSTEMPATH + "getAuthUrl", SYSTEMPATH + "loginAndroid");
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {	  
		MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
		
		for (String path : extPath) {
			if (requestContext.getUriInfo().getRequestUri().getPath().equals(path)) {
				return;
			}
		}
		
		if (requestContext.getCookies().containsKey("SilverBullet")) {
			String cookie = requestContext.getCookies().get("SilverBullet").getValue();
//			System.out.println("Cookie:" + cookie);
			PersonalResponse rs = SystemResource.userStore.getByCookie(cookie);
			return;
		}
		else {
			//System.out.println("No Cookie");
		}
		
		List<String> code = queryParameters.get("code");
		if (code != null && code.size() > 0 && code.get(0).equals("forDebug")) {
			return;
		}
		
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Not Logged In.")
                .build());	
	}      

}
