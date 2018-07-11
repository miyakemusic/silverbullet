$(function() {	
	$(document).ready(function() {		
		var enableEdit = 'disable';
		$('#layout').change(function() {
			changeLayout($("#layout").val());
		});
		$('#widgetType').change(function() {
			changeWidgetType($("#widgetType").val());
		});
		$('#edit').change(function() {
			var checked = $('#edit').prop('checked');
			if (checked == true) {
				enableEdit = 'enable';
			}
			else {
				enableEdit = 'disable';
			}
			for (var i in allWidgets) {
			    allWidgets[i].editable(enableEdit);
			};
		});
		
		$('#styleClass').keydown(function(e) {
		    if (e.keyCode == 13) {
		        setStyleClass($('#styleClass').val());
		    }
		});
		$('#css').keydown(function(e) {
		    if (e.keyCode == 13) {
		        setCss($('#css').val());
		    }
		});
		$('#id').keydown(function(e) {
		    if (e.keyCode == 13) {
		        setGuid($('#id').val());
		    }
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
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/allWidgetTypes",
		   success: function(msg){
				for (var i in msg) {
					$("#widgetType").append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});				
		var map = new Map();
		var widgetMap = new Map();
		var allWidgets = [];
		
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
			allWidgets = [];
			map.clear();
			widgetMap.clear();
			
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
				function(id, info) {
					selectedDiv = id;
					var obj = widgetMap.get(id);
					if (obj != undefined) {
						$('#widgetType').val(obj.widgetType);
					}
					
					$('#layout').val(info.layout);
					$('#styleClass').val(info.styleClass);
					$('#css').val(info.css);
					$('#id').val(info.id);
				}
			);
			widget.editable(enableEdit);
			allWidgets.push(widget);
			   			
			if (pane.id != undefined && pane.id != '') {
				pushWidget(pane, widget);
			}
			for (var i in pane.children) {
				var child = pane.children[i];
				createWidget(widget.baseId, child, pane.layout);
			}
		}
		
		function pushWidget(pane, widget) {
			if (map.get(pane.id) == null) {
   				map.set(pane.id, []);
   			}
   			map.get(pane.id).push(widget);
   			widgetMap.set(widget.baseId, pane);
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
		
		function setStyleClass(style) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setStyle?div=" + selectedDiv + "&style=" + style,
			   success: function(msg){
					updateUI();
			   }
			});			
		}
		function setCss(css) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setCss?div=" + selectedDiv + "&css=" + css,
			   success: function(msg){
					updateUI();
			   }
			});			
		}		
		function setGuid(guid) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setGuid?div=" + selectedDiv + "&guid=" + guid,
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
		function changeWidgetType(widgetType) {
			var div = selectedDiv;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setWidgetType?div=" + div + '&widgetType=' + widgetType,
			   success: function(msg){
					updateUI();
			   }
			});	
		}
	});
})