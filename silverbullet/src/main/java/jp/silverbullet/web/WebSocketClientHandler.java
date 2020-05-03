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

	private void connect(final String server, final String port) throws Exception, IOException,
			InterruptedException, ExecutionException {

		URI uri = URI.create("ws://" + server + ":" + port + "/websocket/");
//		client.setAsyncWriteTimeout(10000);
        client.start();
        // The socket that receives events
        WebSocketAdapter socket = new WebSocketAdapter() {
            @Override
            public void onWebSocketText(String message2)
            {
                super.onWebSocketText(message2);
                onMessageReceived(message2);
            }

			@Override
			public void onWebSocketClose(int statusCode, String reason) {
				// TODO Auto-generated method stub
				super.onWebSocketClose(statusCode, reason);
				
				if (closeRequested) {
					System.out.println("WebSocketClosed");
					return;
				}
				Timer timer = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							System.out.println("WebSocketClient reconnedting...");
							connect(server, port);
							onRecconected();
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
//        ClientUpgradeRequest request = new ClientUpgradeRequest();
//        request.addExtensions("DomainModel");
        
        // Attempt Connect
        Future<Session> fut = client.connect(socket, uri);
        // Wait for Connect
        session = fut.get();
        
//        if (session != null) {
//        	login(System.getProperty("user.name"));
//        }
	}
	
	abstract protected void onMessageReceived(String message2);
	abstract protected void onRecconected();
	
	public void login(String message) {
        // Send a message
        try {
			session.getRemote().sendString(message);
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

//	public UiProperty getUiProperty(String id2) {
//		session.getRemote().sendString("GetProperty:"+ id2, new WriteCallback() {
//			@Override
//			public void writeFailed(Throwable x) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void writeSuccess() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		return null;
//	}
}
