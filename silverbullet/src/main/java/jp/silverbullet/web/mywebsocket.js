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
			connection.send("RegisterAs:UserClient");
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
	    
		/////////////////////////////////////////////	
	}

}