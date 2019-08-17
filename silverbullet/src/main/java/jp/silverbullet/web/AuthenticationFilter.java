package jp.silverbullet.web;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter
{

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {	  
		MultivaluedMap<String, String> queryParameters = requestContext.getUriInfo().getQueryParameters();
		
		List<String> code = queryParameters.get("code");
		
		if (requestContext.getUriInfo().getRequestUri().getPath().equals("/rest/system/login")) {
			return;
		}
		else if (code.size() > 0){
			if (SystemResource.authMap.stores(code.get(0))) {
				return;
			}
//			for (GooglePersonalResponse res : SystemResource.authMap.values()) {
//				if (res.auth_code.equals(code.get(0))) {
//					return;
//				}
//			}
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
	                .entity("Not Logged In.")
	                .build());
		}
	}      

}
