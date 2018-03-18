package jp.silverbullet.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class BuilderServer {
	
	public BuilderServer(final int port) {
		new Thread() {
			@Override
			public void run() {
				initializeWebServer(port);
			}
		}.start();
	}
	
	private void initializeWebServer(int port) {
		Server server = new Server(port);
        	
		String xml = this.getClass().getPackage().getName().replace(".", "/") + "/web.xml";
		String resource = this.getClass().getPackage().getName().replace(".", "/");
        String xmlPath = "";
        String resourcePath = "";
        
        try {
	        xmlPath = this.getClass().
	     	       getClassLoader().getResource(xml).toExternalForm();
	        resourcePath = this.getClass().
	  	       getClassLoader().getResource(resource).toExternalForm();
        }
        catch (Exception e) {
        	xmlPath = xml;
        	resourcePath = resource;
        }
         
        HandlerCollection handlers = new HandlerCollection();
                
        // Jersey Servlet
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor(xmlPath);   
        webAppContext.setResourceBase(resourcePath);
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        handlers.addHandler(webAppContext);
     
        // websocket handler        
//        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        contextHandler.addServlet(new ServletHolder(new WebSocketServletImpl()), "/");
//        handlers.addHandler(contextHandler);
                
        server.setHandler(handlers);

        try {
            server.start();
             		
            server.join();
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
            server.stop();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
	}
}
