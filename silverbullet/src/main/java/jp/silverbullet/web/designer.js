class DesignerClass {
	constructor(div) {
		$('#' + div).append('<a href="http://' + window.location.host + '/runtime.html" target="_blank">runtime</a><br>');
	
		var customDef;
		
		var prefix = 'designer';
		var idToolbar = prefix + 'toolBar';
		var idAdd = prefix + 'add';
		var idAddPanel = prefix + 'addPanel';
		var idRemove = prefix + 'remove';
		var idAddDialog = prefix + 'addDialog';
		var idLayout = prefix + 'layout';
		var idWidgetType = prefix + 'widgetType';
		var idDependencyLog = prefix + 'dependency';
		var idEdit = prefix + 'edit';
				
		$('#' + div).append('<div id="' + idToolbar + '"></div>');
		$('#' + idToolbar).append('<input type="checkbox" id="' + idEdit + '">Edit');
		$('#' + idToolbar).append('<button id="' + idAdd + '">Add Id</button>');
		$('#' + idToolbar).append('<button id="' + idAddPanel + '">Add Panel</button>');
		$('#' + idToolbar).append('<button id="' + idAddTab + '">Add Tab</button>');
		$('#' + idToolbar).append('<button id="' + idRemove + '">Remove</button>');
		$('#' + idToolbar).append('<button id="' + idAddDialog + '">Add Dialog</button>');
		$('#' + idToolbar).append('<select id="' + idLayout + '"></select>');
		$('#' + idToolbar).append('<select id="' + idWidgetType + '"></select>');
		$('#' + idToolbar).append('<button id="' + idDependencyLog + '">Dependency</button>');
		
		var idToolbar2 = prefix + 'toolBar2';
		var idAddTab = prefix + 'addTab';
		var idStyleClass = prefix + 'styleClass';
		var idStyleClasses = prefix + 'styleClasses';
		var idAddStyleClass = prefix + 'addStyleClasses';
		var idCss = prefix + 'css';
		var idId = prefix + 'id';
		var idCustom = prefix + 'custom';
		var idPresentation = prefix + 'presentation';
		var idUpdate = prefix + 'update';
		var idClear = prefix + 'clear';
		var idCut = prefix + 'cut';
		var idPaste = prefix + 'paste';
		var idUid = prefix + 'udi';
		var idInfo = prefix + 'info';
		var idRoot = prefix + 'root';
		var idDialog = prefix + 'dialog';
		var idDialogPanel = prefix + 'dialogPanel';
		var idCustomPropTable = prefix + 'customPropTable';
		var idDependencyDialog = prefix + 'dependencyDialog';
		var idDependencyDialogPanel = prefix + 'dependencyDialogPanel';
		var idIndex = prefix + 'index';
		
		$('#' + div).append('<div id="' + idToolbar2 + '"></div>');
		
		var idControl = prefix + 'control';
		$('#' + idToolbar2).append('<div id="' + idControl + '"></div>');
		$('#' + idControl).append('<span>Style Class<input type="text" id="' + idStyleClass + '"></span>');
		$('#' + idControl).append('Class<select id="' + idStyleClasses + '"></select>');
		$('#' + idControl).append('<button id="' + idAddStyleClass + '">Add Style Class</button>');
		
		var idProp1 = prefix + 'prop1';
		$('#' + idToolbar2).append('<div id="' + idProp1 + '"></div>');		
		$('#' + idProp1).append('<span>CSS<input type="text" id="' + idCss + '"></span>');
//		$('#' + idProp1).append('<span>ID<input type="text" id="' + idId + '"></span>');
		$('#' + idProp1).append('<span>Index:<input type="text" id="' + idIndex + '"></span>');
		$('#' + idProp1).append('<span>Presentation<input type="text" id="' + idPresentation + '"></span>');
		
		var idProp2 = prefix + "prop2";
		$('#' + idToolbar2).append('<div id="' + idProp2 + '"></div>');	
		$('#' + idProp2).append('<button id="' + idUpdate + '">update</button>');
		$('#' + idProp2).append('<button id="' + idClear + '">clear</button>');
		$('#' + idProp2).append('<button id="' + idCut + '">Cut</button>');
		$('#' + idProp2).append('<button id="' + idPaste + '">Paste</button>');
		$('#' + idProp2).append('<span id="' + idUid + '">GUI id</span>');
		$('#' + idProp2).append('<span id="' + idInfo + '"></span>');
		
		var idExt = prefix + "ext";
		$('#' + idToolbar2).append('<div id="' + idExt + '"></div>');	
		$('#' + idExt).append('<label>Custom:</label>');
		$('#' + idExt).append('<span id="' + idCustom + '"></span>');
				
		var idMainDiv = prefix + 'main';
		$('#' + div).append('<div id="' + idMainDiv + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idRoot + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idDialog + '"><input type="text" id="' + idDialogPanel + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idDependencyDialog + '"><div id="' + idDependencyDialogPanel + '"></div></div>');

		$('#' + idMainDiv).append('<table id="' + idCustomPropTable + '"></table>');


		var me = this;
		var copiedDiv;
		
		var layout = new LayoutBuilder(idRoot, '', 
			function(widgetType, baseId, info) {
				$('#' + idWidgetType).val(widgetType);
				$('#' + idLayout).val(info.layout);
				$('#' + idStyleClass).val(info.styleClass);
				$('#' + idCss).val(info.css);
//				$('#' + idId).val(info.id);
				$('#' + idPresentation).val(info.presentation);
				$('#' + idCustom).val(info.custom);
				$('#' + idUid).text(baseId);
				$('#' + idIndex).val(info.index);
				updateCustomPropTable(widgetType, info.custom);
			},
			function(msg) {
				var str = '';
				for (var i = 0; i < msg.length; i++) {
					str += i + ':' + msg[i] + '<br>';
				}
				$('#' + idDependencyDialogPanel).html(str);
			}
		);
	
		var dialog = new IdSelectDialog(div, function(ids) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addWidget?id=" + ids + "&div=" + layout.selectedDiv,
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
			      layout.addDialog($('#' + idDialogPanel).val());
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
			
		$('#' + idDependencyDialog).dialog({
			  autoOpen: false,
			  title: 'Dependency Log',
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
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
			changeLayout($(this).val());
		});
		
		$('#' + idWidgetType).change(function() {
			changeWidgetType($("#" + idWidgetType).val());
		});
		
		$('#' + idAdd).click(function(e) {
			dialog.showModal();
		});
				
		initWebSocket();
		
		function initWebSocket() {
			new MyWebSocket(function(msg) {
				var ids = msg.split(',');
	      		layout.onPropertyUpdate(ids);
			}
			, 'VALUES');
		}
	
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/design/layoutTypes",
		   success: function(msg){
				for (var i in msg) {
					$("#" + idLayout).append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});	
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/design/allWidgetTypes",
		   success: function(msg){
				for (var i in msg) {
					$("#" + idWidgetType).append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});				

		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/design/getStyleClasses",
		   success: function(msg){
				for (var i in msg) {
					$("#" + idStyleClasses).append($("<option>").val(msg[i]).text(msg[i]));
				}
		   }
		});		
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/design/getCustromDefinition",
		   success: function(msg){
		       customDef = msg;
		       
				for (var i in msg) {
					var list = msg[i];
					for (var j in list) {
						var o = list[j];
						console.log(o);
					}
				}
		   }
		});			
		
		$('#' + idUpdate).click(function(e) {
			layout.updateUI();
		});
		
		$('#' + idAddPanel).click(function(e) {
			addPanel();
		});
		
		$('#' + idClear).click(function(e) {
			clearLayout();
		});	
		
		$('#' + idRemove).click(function(e) {
			removeWidget();
		});
		
		$('#' + idAddTab).click(function(e) {
			addTab();
		});
		
		$('#' + idStyleClass).keydown(function(e) {
		    if (e.keyCode == 13) {
		        setStyleClass($('#' + idStyleClass).val());
		    }
		});
		
		$('#' + idCss).keydown(function(e) {
		    if (e.keyCode == 13) {
		        setCss($('#' + idCss).val());
		    }
		});
		
//		$('#' + idId).keydown(function(e) {
//		    if (e.keyCode == 13) {
//		        setGuid($('#' + idId).val());
//		    }
//		});
		$('#' + idPresentation).keydown(function(e) {
		    if (e.keyCode == 13) {
		        setPresentation($('#' + idPresentation).val());
		    }
		});
		$('#' + idCustom).keydown(function(e) {
		    if (e.keyCode == 13) {
		        setCustom($('#' + idCustom).val());
		    }
		});
		$('#' + idAddDialog).click(function(e) {
			//$('#' + idDialog).dialog("open");
			addDialog('');
			
		});
		$('#' + idCut).click(function(e) {
			cut();
		});
		$('#' + idPaste).click(function(e) {
			paste();
		});
		$('#' + idAddStyleClass).click(function(e) {
			appendStyleClass();
		});	
		$('#' + idDependencyLog).click(function(e) {
			$('#' + idDependencyDialog).dialog("open");
		});
		
		function appendStyleClass() {
			setStyleClass($('#' + idStyleClass).val() + ' ' + $('#' + idStyleClasses).val());
		}
		
		function addTab() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addTab?div=" + layout.getSelectedDiv(),
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}	
		
		function clearLayout() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/clearLayout",
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}
		
		function changeLayout(layoutType) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setLayout?div=" + layout.getSelectedDiv() + '&layout=' + layoutType,
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}
		
		function changeWidgetType(widgetType) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setWidgetType?div=" + layout.getSelectedDiv() + '&widgetType=' + widgetType,
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}
		
		function removeWidget() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/remove?div=" + layout.getSelectedDiv(),
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}
				
		function addDialog(id) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addDialog?div=" + layout.getSelectedDiv() + '&id=' + id,
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}
	
		function setStyleClass(style) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setStyle?div=" + layout.getSelectedDiv() + "&style=" + style,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
		
		function setCss(css) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setCss?div=" + layout.getSelectedDiv() + "&css=" + css,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}	
			
		function setGuid(id) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setId?div=" + layout.getSelectedDiv() + "&id=" + id,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
	
		function setPresentation(presentation) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setPresentation?div=" + layout.getSelectedDiv() + "&presentation=" + presentation,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
	
		function setCustom(custom) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setCustom?div=" + layout.getSelectedDiv() + "&custom=" + custom,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
	
		function cut() {
			copiedDiv = layout.getSelectedDiv();
		}
	
		function paste() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/cutPaste?newBaseDiv=" + layout.getSelectedDiv() + '&itemDiv=' + copiedDiv,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}	
	
		function addPanel() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addPanel?div=" + layout.getSelectedDiv(),
			   success: function(msg){
					layout.updateUI();
			   }
			});	
		}	
		
		function updateCustomPropTable(widgetType, custom) {
		
			$('#' + idCustom).empty();
			
			$('#' + idCustomPropTable).empty();
			var list = customDef[widgetType];
			
			for (var i in list) {
				var pair = list[i];
				var idCustomElement = 'customElement_' + pair.key;
				$('#' + idCustom).append('<span>' + pair.key + '<input type="text" id="' + idCustomElement + '"></span>');
				$('#' + idCustomElement).val(custom[pair.key]);
				$('#' + idCustomElement).prop('name', pair.key);
				
				$('#' + idCustomElement).keydown(function(e) {
				    if (e.keyCode == 13) {
	//			    	custom.set($(this).prop('name'), $(this).val());
				    	//setCustom(custom);
				        setCustomElement($(this).prop('name'), $(this).val());
				    }
				});
		
				$('#' + idCustomPropTable).append('<tr><td>' + pair.key + '</td><td>' + pair.value + '</td></tr>');
			}			
		}
		
		function setCustomElement(customId, customValue) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/setCustomElement?div=" + layout.getSelectedDiv() + '&customId=' + customId + '&customValue=' + customValue,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
		
	//	$('#' + idEdit).prop("checked",true);
		$('#' + idEdit).trigger('click');
	}
}
