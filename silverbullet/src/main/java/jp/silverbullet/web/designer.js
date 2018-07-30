class DesignerClass {
	constructor(div) {
		var prefix = 'designer';
		var idToolbar = prefix + 'toolBar';
		var idAdd = prefix + 'add';
		var idAddPanel = prefix + 'addPanel';
		var idRemove = prefix + 'remove';
		var idAddDialog = prefix + 'addDialog';
		var idLayout = prefix + 'layout';
		var idWidgetType = prefix + 'widgetType';
		$('#' + div).append('<div id="' + idToolbar + '"></div>');
		$('#' + idToolbar).append('<button id="' + idAdd + '">Add Id</button>');
		
		$('#' + idToolbar).append('<button id="' + idAdd + '">Add</button>');
		$('#' + idToolbar).append('<button id="' + idAddPanel + '">Add Panel</button>');
		$('#' + idToolbar).append('<button id="' + idRemove + '">Remove</button>');
		$('#' + idToolbar).append('<button id="' + idAddDialog + '">Add Dialog</button>');
		$('#' + idToolbar).append('<select id="' + idLayout + '"></select>');
		$('#' + idToolbar).append('<select id="' + idWidgetType + '"></select>');
		
		
		var idToolbar2 = prefix + 'toolBar2';
		var idAddTab = prefix + 'addTab';
		var idStyleClass = prefix + 'styleClass';
		var idCss = prefix + 'css';
		var idId = prefix + 'id';
		var idCustom = prefix + 'custom';
		var idPresentation = prefix + 'presentation';
		var idEdit = prefix + 'edit';
		var idUpdate = prefix + 'update';
		var idClear = prefix + 'clear';
		var idCut = prefix + 'cut';
		var idPaste = prefix + 'paste';
		var idUid = prefix + 'udi';
		var idInfo = prefix + 'info';
		var idRoot = prefix + 'root';
		var idDialog = prefix + 'dialog';
		var idDialogPanel = prefix + 'dialogPanel';
		
		$('#' + div).append('<div id="' + idToolbar2 + '"></div>');
		$('#' + idToolbar2).append('<button id="' + idAddTab + '">Add Tab</button>');
		$('#' + idToolbar2).append('<span>Style Class<input type="text" id="' + idStyleClass + '"></span>');
		$('#' + idToolbar2).append('<span>CSS<input type="text" id="' + idCss + '"></span>');
		$('#' + idToolbar2).append('<span>ID<input type="text" id="' + idId + '"></span>');
		$('#' + idToolbar2).append('<span>Custom<input type="text" id="' + idCustom + '"></span>');
		$('#' + idToolbar2).append('<span>Presentation<input type="text" id="' + idPresentation + '"></span>');
		$('#' + idToolbar2).append('<input type="checkbox" id="' + idEdit + '">Edit');
		$('#' + idToolbar2).append('<button id="' + idUpdate + '">update</button>');
		$('#' + idToolbar2).append('<button id="' + idClear + '">clear</button>');
		$('#' + idToolbar2).append('<button id="' + idCut + '">Cut</button>');
		$('#' + idToolbar2).append('<button id="' + idPaste + '">Paste</button>');
		$('#' + idToolbar2).append('<div id="' + idUid + '"></div>');
		$('#' + idToolbar2).append('<div id="' + idInfo + '"></div>');
		$('#' + idToolbar2).append('<div id="' + idRoot + '"></div>');
		$('#' + idToolbar2).append('<div id="' + idDialog + '"><input type="text" id="' + idDialogPanel + '"></div>');

		var me = this;
		
		var layout = new LayoutBuilder(idRoot, '', function(widgetType, baseId, info) {
			$('#' + idWidgetType).val(widgetType);
			$('#' + idLayout).val(info.layout);
			$('#' + idStyleClass).val(info.styleClass);
			$('#' + idCss).val(info.css);
			$('#' + idId).val(info.id);
			$('#' + idPresentation).val(info.presentation);
			$('#' + idCustom).val(info.custom);
			$('#' + idUid).text(baseId);
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
	
		$('#' + idDialog).dialog({
			  autoOpen: false,
			  title: 'GUI ID',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			      layout.addDialog($('#' + idDialogPanelId).val());
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
			
		$('#' + idEdit).change(function() {
			var checked = $('#' + idEdit).prop('checked');
			if (checked == true) {
				me.enableEdit = 'enable';
			}
			else {
				me.enableEdit = 'disable';
			}
			layout.enableEdit = me.enableEdit;
		});
		
		$('#' + idLayout).change(function() {
			layout.changeLayout($("#" + idLayout).val());
		});
		
		$('#' + idWidgetType).change(function() {
			layout.changeWidgetType($("#" + idWidgetType).val());
		});
		
		$('#' + idAddId).click(function(e) {
			dialog.showModal();
		});
				
		initWebSocket();
		
		function initWebSocket() {
			new MyWebSocket(function(msg) {
				var ids = msg.split(',');
	      		layout.onPropertyUpdate(ids);
			}
			, 'VALUES');
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
					$("#" + idLayout).append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});	
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/allWidgetTypes",
		   success: function(msg){
				for (var i in msg) {
					$("#" + idWidgetType).append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});				
	
		
		$('#' + idUpdate).click(function(e) {
			layout.updateUI();
		});
		
		$('#' + idAddPanel).click(function(e) {
			layout.addPanel();
		});
		
		$('#' + idClear).click(function(e) {
			layout.clearLayout();
		});	
		
		$('#' + idRemove).click(function(e) {
			layout.removeWidget();
		});
		
		$('#' + idAddTab).click(function(e) {
			layout.addTab();
		});
		
		$('#' + idStyleClass).keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setStyleClass($('#' + idStyleClass).val());
		    }
		});
		
		$('#' + idCss).keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setCss($('#' + idCss).val());
		    }
		});
		
		$('#' + idId).keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setGuid($('#' + idId).val());
		    }
		});
		$('#' + idPresentation).keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setPresentation($('#' + idPresentation).val());
		    }
		});
		$('#' + idCustom).keydown(function(e) {
		    if (e.keyCode == 13) {
		        layout.setCustom($('#' + idCustom).val());
		    }
		});
		$('#' + idAddDialog).click(function(e) {
			$('#' + idDialog).dialog("open");
		});
		$('#' + idCut).click(function(e) {
			layout.cut();
		});
		$('#' + idPaste).click(function(e) {
			layout.paste();
		});
	}
}
