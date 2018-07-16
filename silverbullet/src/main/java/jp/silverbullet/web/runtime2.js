$(function() {	
	$(document).ready(function() {				
		$( "#menu" ).menu();
			
		var layout = new LayoutBuilder('root', '', function(widgetType, baseId, info) {
			$('#widgetType').val(widgetType);
			$('#layout').val(info.layout);
			$('#styleClass').val(info.styleClass);
			$('#css').val(info.css);
			$('#id').val(info.id);
			$('#presentation').val(info.presentation);
			$('#custom').val(info.custom);
			$('#uid').text(baseId);
		});
	
		var dialog = new IdSelectDialog('control', function(ids) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/addWidget?id=" + ids + "&div=" + layout.selectedDiv,
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		});	
	
		$('#dialog').dialog({
			  autoOpen: false,
			  title: 'GUI ID',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			      layout.addDialog($('#dialogPanelId').val());
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
			
		$('#edit').change(function() {
			var checked = $('#edit').prop('checked');
			if (checked == true) {
				enableEdit = 'enable';
			}
			else {
				enableEdit = 'disable';
			}
			layout.enableEdit = enableEdit;
		});
		
		$('#layout').change(function() {
			layout.changeLayout($("#layout").val());
		});
		
		$('#widgetType').change(function() {
			layout.changeWidgetType($("#widgetType").val());
		});
		
		$('#addId').click(function(e) {
			dialog.showModal();
		});
				
		initWebSocket();
		
		function initWebSocket() {
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
			  var ids = e.data.split(',');
		      layout.onPropertyUpdate(ids);
		    };
			/////////////////////////////////////////////		
		
		}
	
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
	
		
		$('#update').click(function(e) {
			layout.updateUI();
		});
		
		$('#addPanel').click(function(e) {
			layout.addPanel();
		});
		
		$('#clear').click(function(e) {
			layout.clearLayout();
		});	
		
		$('#remove').click(function(e) {
			layout.removeWidget();
		});
		
		$('#addTab').click(function(e) {
			layout.addTab();
		});
		
		$('#styleClass').keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setStyleClass($('#styleClass').val());
		    }
		});
		
		$('#css').keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setCss($('#css').val());
		    }
		});
		
		$('#id').keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setGuid($('#id').val());
		    }
		});
		$('#presentation').keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setPresentation($('#presentation').val());
		    }
		});
		$('#custom').keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setCustom($('#custom').val());
		    }
		});
		$('#addDialog').click(function(e) {
			$('#dialog').dialog("open");
		});
		$('#cut').click(function(e) {
			layout.cut();
		});
		$('#paste').click(function(e) {
			layout.paste();
		});
	});
});