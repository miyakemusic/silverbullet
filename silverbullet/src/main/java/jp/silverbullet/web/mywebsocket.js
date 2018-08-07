class MyWebSocket {
	
	constructor(callback, type) {
		this.type = type;
		////////// WebSocket //////////
		var connection  = new WebSocket("ws://localhost:8081/websocket");
		// When the connection is open, send some data to the server
		connection.onopen = function () {
		};
		
		// Log errors
		connection.onerror = function (error) {

		};
		
		var me = this;
		// Log messages from the server
		connection.onmessage = function (e) {
		
			var obj = JSON.parse(e.data);
			if (obj.type == me.type) {
		  		callback(obj.value);
		  	}
	    };
	    
		/////////////////////////////////////////////	
	}

}