package jp.silverbullet.web;

import javax.annotation.CheckForNull;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket
public class WebSocketObject {
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
 //       WebSocketBroadcaster.getInstance().join(this, session.getRemoteAddress().getAddress().getHostAddress());
    }

    @OnWebSocketMessage
    public void onText(String message) {
    	if (message.startsWith("RegisterAs:")) {
    		String target = message.split(":")[1];
    		WebSocketBroadcaster.getInstance().resigerAs(target, this);
    	}
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    	System.out.println("WebSocket closed");
    	WebSocketBroadcaster.getInstance().bye(this);
    }
    
    @OnWebSocketError
    public void onError(@CheckForNull Session session, Throwable error) throws Exception {
    	error.printStackTrace();
    }
    
    public Session getSession(){
        return this.session;
    }
}
