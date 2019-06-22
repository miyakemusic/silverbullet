package jp.silverbullet.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.ArrayQueue;
import org.eclipse.jetty.util.BlockingArrayQueue;

public class WebSocketBroadcaster {
    private static WebSocketBroadcaster INSTANCE = new WebSocketBroadcaster();
    private List<WebSocketObject> clients = new ArrayList<WebSocketObject>();
    
    private WebSocketBroadcaster(){
    	senderThread.start();
    }

    public static WebSocketBroadcaster getInstance(){
        return INSTANCE;
    }

    /**
     * Add Client
     * */
    protected void join(WebSocketObject socket, String address){
    	this.clients.add(socket);
    }
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
//
//	public void login(String employeeNumber, WebSocketObject webSocketObject) {
//        clients.put(webSocketObject, employeeNumber);
//	}
	
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
//        for(WebSocketObject member: clients.keySet()){
//            String empNum = clients.get(member);
//            if (empNum.equals(employeeNumber)) {
//            	member.getSession().getRemote().sendStringByFuture(message);
//            }
//        }		
	}

//	private Object sync = new Object();
	private Thread senderThread = new Thread() {
		@Override
		public void run() {
			try {

				while(true) {	
					
					TypeValue tv = queue.take();
					
					Iterator<TypeValue> it = queue.iterator();
					
					String ids = tv.value;
					String currentType = tv.type;
					while(it.hasNext()) {	
						TypeValue tv2 = it.next();
						if (!currentType.isEmpty() && !tv2.type.equals(currentType)) {
							send(ids, currentType);							
							ids = "";
						}
						for (int i = 0; i < queue.size(); i++) {
							ids += "," + tv2.value;
						}
						
						currentType = tv2.type;
					}
	
					if (!ids.isEmpty()) {
						send(ids, currentType);			
					}
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
}
