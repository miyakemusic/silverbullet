package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketBroadcaster {
    private static WebSocketBroadcaster INSTANCE = new WebSocketBroadcaster();
    private Map<String, List<WebSocketObject>> clients = new HashMap<>(); // key is userid
    private Map<String, Map<String, WebSocketObject>> domainModels = new HashMap<>();
    
    private WebSocketBroadcaster(){
    	senderThread.start();
    }

    public static WebSocketBroadcaster getInstance(){
        return INSTANCE;
    }

//
//	public Map<String, WebSocketObject> getDomainModels() {
//		return domainModels;
//	}

	public void registerAsBrowser(String sessionName, WebSocketObject client) {
		System.out.println("registerAsBrowser -> " + sessionName);
		String userid = SilverBulletServer.getStaticInstance().getUserStore().getBySessionName(sessionName).id;
		if (!this.clients.containsKey(userid)) {
			this.clients.put(userid, new ArrayList<WebSocketObject>());
		}
		this.clients.get(userid).add(client);
	}
	
	public void registerAsDevice(String userid, String name, WebSocketObject client) {
		if (!this.domainModels.containsKey(userid)) {
			this.domainModels.put(userid, new HashMap<String, WebSocketObject>());
		}
		this.domainModels.get(userid).put(name, client);			

		sendMessageAsync(userid, "DEVICE", "Added");
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
        for (Map.Entry<String, List<WebSocketObject>> entry : clients.entrySet()) {
        	for (WebSocketObject v : entry.getValue()) {
        		if (v.equals(socket)) {
        			entry.getValue().remove(v);
        			return;
        		}
        	}
        }

        for (String userid : this.domainModels.keySet()) {
        	Map<String, WebSocketObject> devices = this.domainModels.get(userid);
        	for (String device : devices.keySet()) {
        		WebSocketObject s = devices.get(device);
        		if (s.equals(socket)) {
        			devices.remove(device);
        			sendMessageAsync(userid, "DEVICE", "Removed");
        			return;
        		}
        	}
        }
    }

    /**
     * BroadCast to joined member
     * */
    protected void sendToAll(String userid, String message){
    	for (WebSocketObject member : clients.get(userid)) {
            member.getSession().getRemote().sendStringByFuture(message);
        }
    }
	
	public void sendMessage(String userid, final String message) {
		for(final WebSocketObject member: clients.get(userid)){
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
					send(tv.userid, tv.value, tv.type);
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	class TypeValue {
		public TypeValue(String userid, String type, String value) {
			this.userid = userid;
			this.type = type;
			this.value = value;
		}
		public String userid;
		public String type;
		public String value;
	}
	private BlockingQueue<TypeValue> queue = new LinkedBlockingQueue<>(10);
	public void sendMessageAsync(String userid, String type, String value) {
		try {
			queue.put(new TypeValue(userid, type, value));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void send(String userid, String ids, String currentType) {
//		System.out.println("sent   " + ids + "   " + currentType);
		try {
			String message = new ObjectMapper().writeValueAsString(new WebSocketMessage(currentType, ids));
			List<WebSocketObject> cs = clients.get(userid);
			if (cs == null) {
				return;
			}
			for(final WebSocketObject member: cs){
				if (member != null) {
					member.getSession().getRemote().sendStringByFuture(message);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendMessageToDomainModel(String userid, String device, String message) {
		Map<String, WebSocketObject> map = this.domainModels.get(userid);
		if (map == null) {
			return;
		}
		WebSocketObject obj = map.get(device);
		if (obj != null) {
			obj.getSession().getRemote().sendStringByFuture(message);
		}
//		for(final WebSocketObject member: this.domainModels.values()){
//			if (member != null) {
//				member.getSession().getRemote().sendStringByFuture(message);
//			}
//		}
	}

	public Map<String, WebSocketObject> getDomainModels(String userid) {
		return this.domainModels.get(userid);
	}

}
