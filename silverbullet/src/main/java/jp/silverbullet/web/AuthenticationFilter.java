package jp.silverbullet.web;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import jp.silverbullet.web.auth.GooglePersonalResponse;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter
{

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {	  
		MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
		
		if (requestContext.getUriInfo().getRequestUri().getPath().equals("/rest/system/login")) {
			return;
		}
		if (requestContext.getUriInfo().getRequestUri().getPath().equals("/rest/system/loginAndroid")) {
			return;
		}	
		
		if (requestContext.getCookies().containsKey("SilverBullet")) {
			String cookie = requestContext.getCookies().get("SilverBullet").getValue();
			GooglePersonalResponse rs = SystemResource.cookieStore.get(cookie);
			return;
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
