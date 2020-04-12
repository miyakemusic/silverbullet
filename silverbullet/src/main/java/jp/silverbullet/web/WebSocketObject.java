package jp.silverbullet.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.LightProperty;
import jp.silverbullet.core.property2.RuntimeProperty;


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
//    	else if (message.startsWith("GetProperty:")) {
//    		String id = message.split(":")[1];
//    		RuntimeProperty prop = SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(id);
//    		LightProperty lightProp = new LightProperty();
//    		lightProp.currentValue = prop.getCurrentValue();
//    	}
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
    	WebSocketBroadcaster.getInstance().bye(this);
    }

    public Session getSession(){
        return this.session;
    }
}
