package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

public class WebSocketBroadcaster {
    private static WebSocketBroadcaster INSTANCE = new WebSocketBroadcaster();
    private List<WebSocketObject> clients = new ArrayList<WebSocketObject>();
    
    private WebSocketBroadcaster(){}

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
		System.out.println(message);
		for(final WebSocketObject member: clients){
			new Thread() {
				@Override
				public void run() {
					member.getSession().getRemote().sendStringByFuture(message);
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

}
