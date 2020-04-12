package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.websocket.api.Session;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketBroadcaster {
    private static WebSocketBroadcaster INSTANCE = new WebSocketBroadcaster();
    private List<WebSocketObject> clients = new ArrayList<WebSocketObject>();
    private List<WebSocketObject> domainModels = new ArrayList<WebSocketObject>();
    
    private WebSocketBroadcaster(){
    	senderThread.start();
    }

    public static WebSocketBroadcaster getInstance(){
        return INSTANCE;
    }


	public void resigerAs(String target, WebSocketObject client) {
		if (target.equals(WebSocketClientHandler.DomainModel)) {
			this.domainModels.add(client);
		}
		else if (target.equals(WebSocketClientHandler.UserClient)) {
			this.clients.add(client);
		}
	}
	
    /**
     * Add Client
     * */
//    protected void join(WebSocketObject socket, String address){
//    	this.clients.add(socket);
//    }
    /**
     * Delete Client
     * */
    protected void bye(WebSocketObject socket){
        clients.remove(socket);
    }

    /**
     * BroadCast to joined member
     * */
    protected void sendToAll(String message){
        //for(WebSocketObject member: clients.keySet()){
    	for (WebSocketObject member : clients) {
            member.getSession().getRemote().sendStringByFuture(message);
        }
    }
	
	public void sendMessage(final String message) {
//		System.out.println(message);
		for(final WebSocketObject member: clients){
			new Thread() {
				@Override
				public void run() {
					if (member != null) {
						member.getSession().getRemote().sendStringByFuture(message);
					}
					
				}
			}.start();
			
		}
	}

//	private Object sync = new Object();
	private Thread senderThread = new Thread() {
		String toText(Set<String> set) {
			return set.toString().replace("[", "").replace("]", "").replace("\\s", "");
		}
		@Override
		public void run() {
			try {
				Set<String> tmp = new LinkedHashSet<>();
				while(true) {	
					
					TypeValue tv = queue.take();
					send(tv.value, tv.type);
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	class TypeValue {
		public TypeValue(String type, String value) {
			this.type = type;
			this.value = value;
		}
		public String type;
		public String value;
	}
	private BlockingQueue<TypeValue> queue = new LinkedBlockingQueue<>(10);
	public void sendMessageAsync(String type, String value) {
//		System.out.println("--" + value);
		try {
			queue.put(new TypeValue(type, value));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void send(String ids, String currentType) {
//		System.out.println("sent   " + ids + "   " + currentType);
		try {
			String message = new ObjectMapper().writeValueAsString(new WebSocketMessage(currentType, ids));
			for(final WebSocketObject member: clients){
				if (member != null) {
					member.getSession().getRemote().sendStringByFuture(message);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendMessageToDomainModel(String message) {
		//String message = new ObjectMapper().writeValueAsString(new WebSocketMessage(currentType, ids));
		for(final WebSocketObject member: this.domainModels){
			if (member != null) {
				member.getSession().getRemote().sendStringByFuture(message);
			}
		}
	}
}
