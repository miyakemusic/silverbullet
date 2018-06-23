$(function() {	
	$(document).ready(function() {
//		$( "#radioset" ).buttonset();
		
		$('#layout').change(function() {
			changeLayout($("#layout").val());
		});
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/layoutTypes",
		   success: function(msg){
				for (var i in msg) {
					$("#layout").append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});	
					
		var map = new Map();
		var selectedDiv;

		var dialog = new IdSelectDialog('control', function(ids) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/addWidget?id=" + ids + "&div=" + selectedDiv,
			   success: function(msg){
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
		  		if (e == 'layoutChanged') {
		  			widgets[j].updateLayout();
			    }
				else {
					widgets[j].updateValue();
				}
		  		
		  	}
		  }
		};
		/////////////////////////////////////////////
		
		updateUI();
		
		function updateUI() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getDesign",
			   success: function(msg){
			   		$('#root').empty();
					createWidget('root', msg, 'Absolute Layout');
			   }
			});	
		}
		
		function createWidget(parent, pane, parentLayout) {
			var widget = new JsWidget(pane, parent, parentLayout, 
				function(id) {
					selectedDiv = id;
				},
				function(layout) {
					$('#layout').val(layout);
				}
			);
			if (pane.id != undefined && pane.id != '') {
				pushWidget(pane.id, widget);
			}
			for (var i in pane.children) {
				var child = pane.children[i];
				createWidget(widget.baseId, child, pane.layout);
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
		
		function removeWidget() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/remove?div=" + selectedDiv,
			   success: function(msg){
					updateUI();
			   }
			});	
		}
		
		$('#addId').click(function(e) {
			dialog.showModal();
		});
		
		$('#addPanel').click(function(e) {
			addPanel();
		});
		
		$('#remove').click(function(e) {
			removeWidget();
		});
		
		function addPanel() {
			var div = selectedDiv;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/addPanel?div=" + div,
			   success: function(msg){
					updateUI();
			   }
			});	
		}
		function changeLayout(layout) {
			var div = selectedDiv;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setLayout?div=" + div + '&layout=' + layout,
			   success: function(msg){
					updateUI();
			   }
			});	
		}
	});
})