$(function() {	
	$(document).ready(function() {
		var map = new Map();
		var selectedDiv;
		
		var dialog = new IdSelectDialog('control', function(ids) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/addWidget?id=" + ids + "&div=" + selectedDiv,
			   success: function(msg){
//					createWidget('root', msg);
					updateUI();
			   }
			});	
		});
		
		////////// WebSocket //////////
		var connection  = new WebSocket("ws://localhost:8081/websocket");
		// When the connection is open, send some data to the server
		connection.onopen = function () {
//		  connection.send('Ping'); // Send the message 'Ping' to the server
		};
		
		// Log errors
		connection.onerror = function (error) {
		  //console.log('WebSocket Error ' + error);
		};
		
		// Log messages from the server
		connection.onmessage = function (e) {
		  //console.log('Server: ' + e.data);
		  
		  var ids = e.data.split(',');
		  for (var i in ids) {
		  	var widgets = map.get(ids[i]);
		  	for (var j in widgets) {
		  		widgets[j].update();
		  	}
		  }
		};
		/////////////////////////////////////////////
		
		updateUI();
		
		function updateUI() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getLayout",
			   success: function(msg){
			   		$('#root').empty();
					createWidget('root', msg);
			   }
			});	
		}
		
		function createWidget(parent, pane) {
			var widget = new JsWidget(pane, parent, function(id) {
				selectedDiv = id;
			});
			pushWidget(pane.id, widget);
			
			for (var i in pane.children) {
				var child = pane.children[i];
				createWidget(widget.baseId, child);
			}
		}
		
		function pushWidget(id, widget) {
			if (map.get(id) == null) {
   				map.set(id, []);
   			}
   			map.get(id).push(widget);
		}
		
		function requestChange(id, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/currentValue?id="+id + "&value=" + value,
			   success: function(msg){

			   }
			});	
		}	
		$('#addId').click(function(e) {
			dialog.showModal();
		});
	});
})