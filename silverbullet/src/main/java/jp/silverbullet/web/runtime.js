$(function() {	
	$(document).ready(function() {
		var map = new Map();
		
		////////// WebSocket //////////
		var connection  = new WebSocket("ws://localhost:8081/websocket");
		// When the connection is open, send some data to the server
		connection.onopen = function () {
//		  connection.send('Ping'); // Send the message 'Ping' to the server
		};
		
		// Log errors
		connection.onerror = function (error) {
		  console.log('WebSocket Error ' + error);
		};
		
		// Log messages from the server
		connection.onmessage = function (e) {
		  console.log('Server: ' + e.data);
		  
		  var ids = e.data.split(',');
		  for (var i in ids) {
		  	var widgets = map.get(ids[i]);
		  	for (var j in widgets) {
		  		widgets[j].update();
		  	}
		  }
		};
		/////////////////////////////////////////////
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/getLayout",
		   success: function(msg){
//		   		$('#root').append('<div id=' + msg.unique + '></div>');
//		   		$('#' + msg.unique).height(msg.height).width(msg.width);
		   		for (var i in msg.children) {
		   			var child = msg.children[i];
		   			//createWidget(msg.unique, child, child.widgetType)
		   			var widget = new JsWidget(child.id, child.widgetType, child.unique, 'root');
		   			if (map.get(child.id) == null) {
		   				map.set(child.id, []);
		   			}
		   			map.get(child.id).push(widget);
		   		}
		   }
		});	
		
		function createWidget(parent, child, type) {
			var text = '<span id=' + child.unique+'_title' + '></span>';
			
			if (type == 'COMBOBOX') {
				text += '<SELECT id=' + child.unique + '></SELECT></div>';
			}
			else if (type == 'TEXTFIELD') {
				text += '<input type="text" id=' + child.unique + '>';
			}
			
			text += '<span id=' + child.unique + '_unit'+ '></span>';
			
			text = '<div id=' + child.unique + '_panel' + '>' + text + '</div>';
			$('#' + parent).append(text);
			$('#' + child.unique + '_panel').draggable();
			
			$.ajax({
			   	type: "GET", 
			   	url: "http://" + window.location.host + "/rest/runtime/property?id=" + child.id,
			   	success: function(msg){
			   		$('#' + child.unique+'_title').text(msg.title);
			   		$('#' + child.unique+'_unit').text(msg.unit);
			   		if (type == 'COMBOBOX') {
				   		for (var i in msg.elements) {
				   			var element = msg.elements[i];
				   			$('#' + child.unique).append($('<option>', {
							    value: element.id,
							    text: element.title
							}));
				   		}
				   		$('#' + child.unique).val(msg.currentValue);
				   		$('#' + child.unique).on('change', function() {
							requestChange(msg.id, $('#' + child.unique).val());
						})
			   		}
			   		else if (type == 'TEXTFIELD') {
				   		$('#' + child.unique).val(msg.currentValue);
				   		$('#' + child.unique).on('change', function() {
							requestChange(msg.id, $('#' + child.unique).val());
						})
			   		}

			   	}
			});
		}
		
		function requestChange(id, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/currentValue?id="+id + "&value=" + value,
			   success: function(msg){

			   }
			});	
		}	
	});
})