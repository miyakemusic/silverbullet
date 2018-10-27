class DesignerClass {
	constructor(div) {

		var prefix = 'designer';
		var idToolbar = prefix + 'toolBar';
		var idAdd = prefix + 'add';
		var idAddPanel = prefix + '_addPanel';
		var idRemove = prefix + '_remove';
		var idAddDialog = prefix + '_addDialog';
		var idAddRegisterShortcut = prefix + '_addRegisterShortcut';
		var idLayout = prefix + 'layout';
		var idWidgetType = prefix + 'widgetType';
		var idDependencyLog = prefix + 'dependency';
		var idEdit = prefix + 'edit';
		var idAddTab = prefix + '_addTab';
		var idDialog = prefix + 'dialog';
		
		var idToolbar2 = prefix + 'toolBar2';
		var idStyleClass = prefix + '_styleClass';
		var idStyleClasses = prefix + 'styleClasses';
		var idFontSize = prefix + '_fontsize';
		var idAddStyleClass = prefix + 'addStyleClasses';
		var idCss = prefix + '_css';
		var idTop = prefix + '_top';
		var idLeft = prefix + '_left';
		var idWidth = prefix + '_width';
		var idHeight = prefix + '_height';
		var idId = prefix + '_id';
		var idCustom = prefix + 'custom';
		var idPresentation = prefix + '_presentation';
		var idUpdate = prefix + '_update';
		var idClear = prefix + '_clearLayout';
		var idCut = prefix + 'cut';
		var idPaste = prefix + 'paste';
		var idUid = prefix + 'udi';
		var idInfo = prefix + 'info';
		var idRoot = prefix + 'root';
		var idDialogPanel = prefix + 'dialogPanel';
		var idCustomPropTable = prefix + 'customPropTable';
		var idDependencyDialog = prefix + 'dependencyDialog';
		var idDependencyDialogPanel = prefix + 'dependencyDialogPanel';
		var idIndex = prefix + '_index';
		var idEditable = prefix + 'editable';
									
		var idBase = prefix + '_base';		
		var idNorth = prefix + 'north';
		var idWest = prefix + 'west';
		var idCenter = prefix + 'center';
		var idEast = prefix + 'east';
		var idSouth = prefix + 'south';
		
		$('#' + div).append('<div id="' + idBase + '"></div>');
		$('#' + idBase).css({'width':'100%', 'height':'100%'});
		
		var idFile = prefix + '_file';
		var idAddNewFile = prefix + '_addNewFile';
		var idRemoveFile = prefix + '_removeFile';
		$('#' + idBase).append('<div><select id="' + idFile + '"></select><button id="' + idAddNewFile + '">Add File</button><button id="' + idRemoveFile + '">Remove</button></div>');
		
		$('#' + idBase).append('<div id="' + idNorth + '" class="panel"></div>');
		$('#' + idBase).append('<div id="' + idCenter + '" class="panel"></div>');

		$('#' + idNorth).append('<a href="http://' + window.location.host + '/runtime.html" target="_blank">runtime</a>');

		var customDef;			
		
		$('#' + idNorth).append('<div id="' + idToolbar + '" class="panel"></div>');

		$('#' + idToolbar).append('<button id="' + idAdd + '">Add Id</button>');
		$('#' + idToolbar).append('<button id="' + idAddPanel + '" class="layoutAction">Add Panel</button>');
		$('#' + idToolbar).append('<button id="' + idAddTab + '" class="layoutAction">Add Tab</button>');
		$('#' + idToolbar).append('<button id="' + idAddDialog + '" class="layoutAction">Add Dialog</button>');
		$('#' + idToolbar).append('<button id="' + idAddRegisterShortcut + '">Add Register Shortcut</button>');
		$('#' + idToolbar).append('<button id="' + idDependencyLog + '">Dependency</button>');
		
		$('#' + idNorth).append('<div id="' + idToolbar2 + '" class="panel"></div>');
		$('#' + idToolbar2).append('<button id="' + idUpdate + '">Update</button>');
		$('#' + idToolbar2).append('<button id="' + idClear + '" class="layoutAction">Clear</button>');
		$('#' + idToolbar2).append('<button id="' + idCut + '">Cut</button>');
		$('#' + idToolbar2).append('<button id="' + idPaste + '">Paste</button>');
		$('#' + idToolbar2).append('<button id="' + idRemove + '" class="layoutAction">Remove</button>');
		$('#' + idToolbar2).append('<input type="checkbox" id="' + idEdit + '">Edit');

		$('#' + idCenter).append('<table><tr><td><div id="' + idEast + '" class="panel"></div></td><td valign="top"><div id="' + idWest + '" class="panel"></div></td></tr></table>');
	
		// Widget Propertty
		var idPropPane = prefix + "propPane";
		$('#' + idWest).append('<div id="' + idPropPane + '"></div>');
		$('#' + idPropPane).css({'background-color': 'lightgray', 'font-size':'12px', 'width':'200px', 'height':'100%'});
		
		$('#' + idPropPane).append('<div id="' + idId + '"></div>');
		$('#' + idPropPane).append('<div>type:<select id="' + idWidgetType + '"></select></div>');
		$('#' + idPropPane).append('<div>Layout:<select id="' + idLayout + '"></select></div>');
		
		$('#' + idPropPane).append('<div>Index:<input type="text" id="' + idIndex + '" class="widgetField"></div>');
		$('#' + idPropPane).append('<div>Caption<input type="text" id="' + idPresentation + '" class="widgetField"></div>');	
		
		$('#' + idPropPane).append('<div>Top:<input type="text" id="' + idTop + '" class="widgetField"></div>');
		$('#' + idPropPane).append('<div>Left:<input type="text" id="' + idLeft + '" class="widgetField"></div>');
		$('#' + idPropPane).append('<div>Width:<input type="text" id="' + idWidth + '" class="widgetField"></div>');
		$('#' + idPropPane).append('<div>Height:<input type="text" id="' + idHeight + '" class="widgetField"></div>');
		
		$('#' + idPropPane).append('<div>CSS<input type="text" id="' + idCss + '" class="widgetField"></div>');
		$('#' + idPropPane).append('<div>Style Class<input type="text" id="' + idStyleClass + '" class="widgetField"></div>');
		
		$('#' + idPropPane).append('<div>Font Size<input type="text" id="' + idFontSize + '" class="widgetField"></div>');
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
				$('#' + idId).text(info.id);
				$('#' + idPresentation).val(info.presentation);
//				$('#' + idCustom).val(info.custom);
				$('#' + idUid).text(baseId);
				$('#' + idIndex).val(info.index);
				$('#' + idFontSize).val(info.fontsize);
				$('#' + idEditable).prop('checked', info.editable);
				
				$('#' + idTop).val(info.top);
				$('#' + idLeft).val(info.left);
				$('#' + idWidth).val(info.width);
				$('#' + idHeight).val(info.height);
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
		   }
		});			
		
		$('#' + idUpdate).click(function(e) {
			layout.updateUI();
		});
			
		$('.layoutAction').click(function(e) {
			doGuiAction($(this).prop('id').split('_')[1]);
		});

		$('.widgetField').keydown(function(e) {
		    if (e.keyCode == 13) {
		    	var id = $(this).prop('id').split('_')[1];
		    	var val = $(this).val();
		        updateGuiProperty(id, val);
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

		// Register shortcut
		var idRegisterShortcutDiv = div + 'registerShortcutDiv';
		var idRegisterShortcutList = div + 'registerShortcutList';
		$('#' + div).append('<div id="' + idRegisterShortcutDiv + '"><select id="' + idRegisterShortcutList + '"></select></div>');

		$('#' + idAddRegisterShortcut).click(function(e) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getShortCuts",
			   success: function(msg){
					$('#' + idRegisterShortcutList).empty();
					for (var s of msg) {
						var text = s.bitName + "@" + s.regName;
						$('#' + idRegisterShortcutList).append($('<option>').val(text).text(text));
					}
			   }
			});		
			$('#' + idRegisterShortcutDiv).dialog("open");
		});
		
		function addRegisterShortcut(reg) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addRegisterShortcut?div=" + layout.getSelectedDiv() + '&register=' + reg,
			   success: function(msg){
					layout.updateUI();
			   }
			});		
		}
		
		$('#' + idRegisterShortcutDiv).dialog({
			  autoOpen: false,
			  title: 'Register Shortcut',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      addRegisterShortcut($('#' + idRegisterShortcutList).val());
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
		
		
		function addTab() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/addTab?div=" + layout.getSelectedDiv(),
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
		function doGuiAction(apiName) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/" + apiName + "?div=" + layout.getSelectedDiv(),
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
		
		getFiles();
		function getFiles() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/getFiles",
			   success: function(msg){
					$('#' + idFile).empty();
					for (var s of msg) {
						$('#' + idFile).append($("<option>").val(s).text(s));
					}
			   }
			});			
		}
		
		$('#' + idFile).change(function() {
			switchFile($(this).val());
		});
		function switchFile(filename) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/switchFile?filename=" + filename,
			   success: function(msg){
					layout.updateUI();
			   }
			});			
		}
		
		
		$('#' + idAddNewFile).click(function() {
			var dialog = new TextInputDialog(div, 'New Filename', 'Filename', function() {
				createNewFile(dialog.getText());
			});
			dialog.showModal();

		});
		
		$('#' + idRemoveFile).click(function() {
			var dialog = new CommonDialog(div, 'Confirm', 'Are you sure you want to remove?', function() {
				removeFile($('#' + idFile).val());
			});
			dialog.showModal();
		});
		
		function createNewFile(filename) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/createNewFile?filename=" + filename,
			   success: function(msg){
					getFiles();
			   }
			});	
		}
		
		function removeFile(filename) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/removeFile?filename=" + filename,
			   success: function(msg){
					getFiles();
			   }
			});	
		}
		
		$('#' + idEdit).trigger('click');

	}
}
