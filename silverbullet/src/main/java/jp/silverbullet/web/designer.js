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
		var idDependencyLog = prefix + 'dependency';
		var idEdit = prefix + 'edit';
		var idAddTab = prefix + 'addTab';
		var idDialog = prefix + 'dialog';
		
		var idToolbar2 = prefix + 'toolBar2';
		var idStyleClass = prefix + 'styleClass';
		var idStyleClasses = prefix + 'styleClasses';
		var idFontSize = prefix + 'fontsize';
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
		var idDialogPanel = prefix + 'dialogPanel';
		var idCustomPropTable = prefix + 'customPropTable';
		var idDependencyDialog = prefix + 'dependencyDialog';
		var idDependencyDialogPanel = prefix + 'dependencyDialogPanel';
		var idIndex = prefix + 'index';
		var idEditable = prefix + 'editable';
									
		var idBase = prefix + '_base';		
		var idNorth = prefix + 'north';
		var idWest = prefix + 'west';
		var idCenter = prefix + 'center';
		var idEast = prefix + 'east';
		var idSouth = prefix + 'south';
		
		$('#' + div).append('<div id="' + idBase + '"></div>');
		$('#' + idBase).css({'width':'100%', 'height':'100%'});
		
		$('#' + idBase).append('<div id="' + idNorth + '" class="panel"></div>');
		$('#' + idBase).append('<div id="' + idCenter + '" class="panel"></div>');

		$('#' + idNorth).append('<a href="http://' + window.location.host + '/runtime.html" target="_blank">runtime</a>');

		var customDef;			
		
		$('#' + idNorth).append('<div id="' + idToolbar + '" class="panel"></div>');

		$('#' + idToolbar).append('<button id="' + idAdd + '">Add Id</button>');
		$('#' + idToolbar).append('<button id="' + idAddPanel + '">Add Panel</button>');
		$('#' + idToolbar).append('<button id="' + idAddTab + '">Add Tab</button>');
		$('#' + idToolbar).append('<button id="' + idRemove + '">Remove</button>');
		$('#' + idToolbar).append('<button id="' + idAddDialog + '">Add Dialog</button>');
		$('#' + idToolbar).append('<button id="' + idDependencyLog + '">Dependency</button>');
		
		$('#' + idNorth).append('<div id="' + idToolbar2 + '" class="panel"></div>');
		$('#' + idToolbar2).append('<button id="' + idUpdate + '">Update</button>');
		$('#' + idToolbar2).append('<button id="' + idClear + '">Clear</button>');
		$('#' + idToolbar2).append('<button id="' + idCut + '">Cut</button>');
		$('#' + idToolbar2).append('<button id="' + idPaste + '">Paste</button>');
		$('#' + idToolbar2).append('<input type="checkbox" id="' + idEdit + '">Edit');

		$('#' + idCenter).append('<table><tr><td><div id="' + idEast + '" class="panel"></div></td><td valign="top"><div id="' + idWest + '" class="panel"></div></td></tr></table>');
	
		// Widget Propertty
		var idPropPane = prefix + "propPane";
		$('#' + idWest).append('<div id="' + idPropPane + '"></div>');
		$('#' + idPropPane).css({'background-color': 'lightgray', 'font-size':'12px', 'width':'200px', 'height':'100%'});
		
		$('#' + idPropPane).append('<div>type:<select id="' + idWidgetType + '"></select></div>');
		$('#' + idPropPane).append('<div>Layout:<select id="' + idLayout + '"></select></div>');
		
		$('#' + idPropPane).append('<div>Index:<input type="text" id="' + idIndex + '"></div>');
		$('#' + idPropPane).append('<div>Caption<input type="text" id="' + idPresentation + '"></div>');	
		
		$('#' + idPropPane).append('<div>CSS<input type="text" id="' + idCss + '"></div>');
		$('#' + idPropPane).append('<div>Style Class<input type="text" id="' + idStyleClass + '"></div>');
		
		$('#' + idPropPane).append('<div>Font Size<input type="text" id="' + idFontSize + '"></div>');
		$('#' + idPropPane).append('<div>Editable<input type="checkbox" id="' + idEditable + '"></div>');
		
		var idAddClassDiv= prefix+'addClassDiv';
		$('#' + idPropPane).append('<div id="' + idAddClassDiv + '" class="panel"></div>');
		$('#' + idAddClassDiv).append('Class<select id="' + idStyleClasses + '"></select>');
		$('#' + idAddClassDiv).append('<button id="' + idAddStyleClass + '">Add</button>');
		
		var idExt = prefix + "ext";
		$('#' + idPropPane).append('<div id="' + idExt + '" class="panel"></div>');	
		$('#' + idExt).append('<label>Custom:</label>');
//		$('#' + idExt).append('<div id="' + idUid + '">GUI id</div>');
		$('#' + idExt).append('<div id="' + idInfo + '"></div>');	
		$('#' + idExt).append('<div id="' + idCustom + '"></div>');	
		///		
				
		var idMainDiv = prefix + 'main';
		$('#' + idEast).append('<div id="' + idMainDiv + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idRoot + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idDialog + '"><input type="text" id="' + idDialogPanel + '"></div>');
		$('#' + idMainDiv).append('<div id="' + idDependencyDialog + '"><div id="' + idDependencyDialogPanel + '"></div></div>');

//		$('#' + idMainDiv).append('<table id="' + idCustomPropTable + '"></table>');

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
//				$('#' + idCustom).val(info.custom);
				$('#' + idUid).text(baseId);
				$('#' + idIndex).val(info.index);
				$('#' + idFontSize).val(info.fontsize);
				$('#' + idEditable).prop('checked', info.editable);
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

		
		$('#' + idAdd).click(function(e) {
			dialog.showModal();
		});
				
		initWebSocket();
		
		function initWebSocket() {
			new MyWebSocket(function(msg) {
				var ids = msg.split(',');
	      		layout.requestUpdate(ids);
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
		   url: "http://" + window.location.host + "/rest/design/getWidgetTypes",
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
		        updateGuiProperty('styleClass', $('#' + idStyleClass).val());
		    }
		});
		
		$('#' + idCss).keydown(function(e) {
		    if (e.keyCode == 13) {
		        updateGuiProperty('css', $('#' + idCss).val());
		    }
		});


		$('#' + idPresentation).keydown(function(e) {
		    if (e.keyCode == 13) {
		        updateGuiProperty('presentation', $('#' + idPresentation).val());
		    }
		});
		$('#' + idFontSize).keydown(function(e) {
		    if (e.keyCode == 13) {
		        updateGuiProperty('fontsize', $('#' + idFontSize).val());
		    }
		});
				
		$('#' + idEditable).change(function() {
			updateGuiBooleanProperty('editable', $('#' + idEditable).prop('checked'));
		});
				
		function appendStyleClass() {
			updateGuiProperty('styleClass', $('#' + idStyleClass).val() + ' ' + $('#' + idStyleClasses).val());
		}
		
		$('#' + idLayout).change(function() {
			updateGuiProperty('layout', $('#' + idLayout).val());
		});
		
		$('#' + idWidgetType).change(function() {
			updateGuiProperty('widgetType', $("#" + idWidgetType).val());
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
			
		function updateGuiProperty(fieldType, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/updateGuiProperty?div=" + layout.getSelectedDiv() + "&propertyType=" + fieldType + "&value=" + value,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}	
		function updateGuiBooleanProperty(fieldType, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/updateGuiBooleanProperty?div=" + layout.getSelectedDiv() + "&propertyType=" + fieldType + "&value=" + value,
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
				$('#' + idCustom).append('<div>' + pair.key + '<input type="text" id="' + idCustomElement + '"></div>');
				$('#' + idCustomElement).val(custom[pair.key]);
				$('#' + idCustomElement).prop('name', pair.key);
				
				$('#' + idCustomElement).keydown(function(e) {
				    if (e.keyCode == 13) {
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
		
		$('#' + idEdit).trigger('click');

	}
}
