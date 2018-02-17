package jp.silverbullet.web;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.Timer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public abstract class WebSocketClientHandler {
    private WebSocketClient client = new WebSocketClient();
	private Session session;
	private boolean closeRequested = false;
    
	public WebSocketClientHandler(String server, String port) throws Exception {
        connect(server, port);
	}

	private void connect(final String optBridge, final String port) throws Exception, IOException,
			InterruptedException, ExecutionException {

		URI uri = URI.create("ws://" + optBridge + ":" + port + "/websocket/");

        client.start();
        // The socket that receives events
        WebSocketAdapter socket = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(String message2)
            {
                super.onWebSocketText(message2);
                //System.out.println("Received TEXT message: " + message);
                onMessageReceived(message2);
            }

			@Override
			public void onWebSocketClose(int statusCode, String reason) {
				// TODO Auto-generated method stub
				super.onWebSocketClose(statusCode, reason);
				
				if (closeRequested) {
					return;
				}
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							connect(optBridge, port);
							System.out.println("reconnect");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				timer.setRepeats(false);
				timer.start();
			}
        };
        // Attempt Connect
        Future<Session> fut = client.connect(socket,uri);
        // Wait for Connect
        session = fut.get();
        
        if (session != null) {
        	login(System.getProperty("user.name"));
        }
	}
	
	abstract protected void onMessageReceived(String message2);
	
	public void login(String employeeNumber) {
        // Send a message
        try {
			session.getRemote().sendString("login:" + employeeNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		closeRequested  = true;
        // Close session
        try {
			session.close();
	        client.stop();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
