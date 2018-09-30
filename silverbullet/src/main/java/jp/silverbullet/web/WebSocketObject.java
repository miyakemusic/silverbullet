package jp.silverbullet.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket
public class WebSocketObject {
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        WebSocketBroadcaster.getInstance().join(this, session.getRemoteAddress().getAddress().getHostAddress());
    }

    @OnWebSocketMessage
    public void onText(String message) {
    	WebSocketBroadcaster.getInstance().sendToAll(message);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    	WebSocketBroadcaster.getInstance().bye(this);
    }

    public Session getSession(){
        return this.session;
    }
}
