package jp.silverbullet.auth;

import org.glassfish.jersey.server.ResourceConfig;

import jp.silverbullet.web.AuthenticationFilter;

public class CustomApplication extends ResourceConfig 
{
    public CustomApplication() 
    {
        packages("jp.silverbullet.auth");
//        register(LoggingFilter.class);
 //       register(GsonMessageBodyHandler.class);
 
        //Register Auth Filter here
        register(AuthenticationFilter.class);
    }
}
   
