package jp.silverbullet.web;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

public class BuilderServer {
	
	public static void main(String[] arg) {
		new BuilderServer(8081, "http", new BuilderServerListener() {
			@Override
			public void onStarted() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private BuilderServerListener listener;
	
	public BuilderServer(final int port, String protocol, BuilderServerListener listener) {
		this.listener = listener;
		new Thread() {
			@Override
			public void run() {
				initializeWebServer(port, protocol);
			}
		}.start();
	}
	
	private void initializeWebServer(int port, String protocol) {
		Server server = null;//new Server(port);
        
		System.out.println(protocol);
		
		if (protocol.equals("https")) {
			server = new Server();
			SslContextFactory sslContextFactory = new SslContextFactory();
	        sslContextFactory.setKeyStorePath(System.getProperty("user.dir") + "/mykeystore.jks");
	        sslContextFactory.setKeyStorePassword("mypassword");
	        ServerConnector httpsConnector = new ServerConnector(server, sslContextFactory);
	        httpsConnector.setPort(port);
	        server.addConnector(httpsConnector);
		}
		else {
			server = new Server(port);
		}
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
        	e.printStackTrace();
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
        
        server.setHandler(handlers);

        try {
            server.start();
            listener.onStarted();
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
