class MyWebSocket {
	constructor(callback, type) {
		
		this.type = type;
		////////// WebSocket //////////
		var wsHeader = "ws";
		if(location.protocol == 'https:'){
			wsHeader = "wss";
		}

		var connection  = new WebSocket(wsHeader + "://" + window.location.host + "/websocket");
		// When the connection is open, send some data to the server
		connection.onopen = function () {
			var obj = new Object();
			obj.type = 'UserClient';
			obj.sessionID = Cookies.get('silverbulletid');
			obj.application = '';
			obj.device = '';
			connection.send(JSON.stringify(obj));
		};

		// Log errors
		connection.onerror = function (error) {
		};
		
		var me = this;
		// Log messages from the server
		connection.onmessage = function (e) {
			try {
				var obj = JSON.parse(e.data);
				if (obj.type == me.type) {
			  		callback(obj.value);
			  	}
		  	}
		  	catch(e) {
		  		console.log(e);
		  	}
	    };
	    
	    this.setType = function(type) {
	    	this.type = type;
	    }
		/////////////////////////////////////////////	
	}

	changeType(type) {
		this.setType(type);
	}
}

class MyWebSocket2 {
	constructor() {
		this.listeners = new Map();
		////////// WebSocket //////////
		var wsHeader = "ws";
		if(location.protocol == 'https:'){
			wsHeader = "wss";
		}

		var connection  = new WebSocket(wsHeader + "://" + window.location.host + "/websocket");
		// When the connection is open, send some data to the server
		connection.onopen = function () {
			var obj = new Object();
			obj.type = 'UserClient';
			obj.sessionID = Cookies.get('silverbulletid');
			obj.application = '';
			obj.device = '';
			connection.send(JSON.stringify(obj));
		};

		// Log errors
		connection.onerror = function (error) {
		};
		
		var me = this;
		// Log messages from the server
		connection.onmessage = function (e) {
			console.log('listener count=' + me.listeners.size);
			try {
				var o = JSON.parse(e.data);
				for (let [listener, obj] of me.listeners) {
					if (o.type == obj.condition) {
						obj.listener(o.value);
					}
				}
		  	}
		  	catch(e) {
		  		console.log(e);
		  	}
	    };
		/////////////////////////////////////////////	
	}

	addListener(condition, listener) {
		var obj = new Object();
		obj.listener = listener;
		obj.condition = condition;
		
		this.listeners.set(listener, obj);
	}
	
	removeListener(listener) {
		this.listeners.delete(listener);
	}
	
	changeType(handler, type) {
		this.listeners.get(handler).condition = type;
	}
}
var websocket = new MyWebSocket2();