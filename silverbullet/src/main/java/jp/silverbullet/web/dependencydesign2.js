class DependencyDesign2 {
	constructor(div) {
		var triggers = [];
		var targets = [];
		var current;
		var priorityEditor;
		
		var me = this;
		var colDefEnabledTable = function(k, row, type) {
			if (type == 'type') {
				if (row != 'title' && k > 0) {
					return 'check';
				}
				else {
					return 'text';
				}
			}
			else if (type == 'checked') {
				var index = current.yTitle.indexOf(row);
				return current.enableMatrix[index][k-1].enabled;
			}
			else if (type == 'name') {
				var index = current.yTitle.indexOf(row);
				return current.enableMatrix[index][k-1].condition;
			}
		}
		
		var colDefValueTable = function(k, row, type) {
			if (type == 'type') {
				return 'text_button';
			}
			else if (type == 'checked') {

			}
			else if (type == 'text') {
				if (row == 'title') {
					return current.xTitle[current.xTitle.indexOf(k)];
				}
				var index = current.yTitle.indexOf(row);
				return current.valueMatrix[index][k-1];
			}		
		}		
		new MyWebSocket(function(msg) {				
				if (msg == 'MatrixChanged') {
					priorityEditor.update();
				}
			}
			, 'DEPDESIGN');
			
		var configPaneId = div + "_configPane";
		$('#' + div).append('<div id="' + configPaneId + '"></div>');
		var configId = div + "_config";
		var currentConfig;
		$('#' + configPaneId).append('Config: <select id="' + configId + '"></select>');
		$('#' + configId).change(function() {
			var select = $(this).val();
			getDependencyDesignConfig(select);
		});
				
		var dialogId = div + "_dialog";
		var nameId = div + "_name";
		$('#' + div).append('<div id="' + dialogId + '"><input type="text" id="' + nameId + '"></div>');
		
		var addConfigId = div + "_addConfig";
		$('#' + configPaneId).append('<button id="' + addConfigId + '">Add</button>');
		$('#' + addConfigId).click(function() {
			$('#' + nameId).val($('#' + configId).val());
			$('#' + dialogId).dialog('open');
		});
		
		var rotateId = div + "_rotate";
		$('#' + div).append('<button id="' + rotateId + '">Rotate</button>');
		$('#' + rotateId).click(function() {
			rotate();	
		});
		
		function rotate() {	
			var tmp = targets;
			targets = triggers;
			triggers = tmp;
			updateAll();
		}
		
		getDependencyDesignConfigList();
		
		// ID Pane //
		var idPane = div + "_idPane";
		$('#' + div).append('<div id="' + idPane + '"></div>');
		
		var idsId = div + "_ids";
		$('#' + idPane).append('ID <select id="' + idsId + '"></select>');
		initIdSelection();
		
		var idAddTrigger = div + "_addTrigger";
		$('#' + idPane).append('<button id="' + idAddTrigger + '">Add Trigger</button>');
		$('#' + idAddTrigger).click(function() {
			var id = $('#' + idsId).val();
			if (triggers.includes(id)) {
				return;
			}
			triggers.push(id);
			getMatrix();
			updateIdsPane();
		});
		
		var idAddTarget = div + "_addTarget";
		$('#' + idPane).append('<button id="' + idAddTarget + '">Add Target</button>');
		$('#' + idAddTarget).click(function() {
			var id = $('#' + idsId).val();
			if (targets.includes(id)) {
				return;
			}
			targets.push($('#' + idsId).val());
			getMatrix();
			updateIdsPane();
		});
			
		var triggerIdsPaneId = div + "_triggerIdsPane";
		$('#' + idPane).append('<div>Triggers: <div id="' + triggerIdsPaneId + '"></div></div>');
		
		var targetIdsPaneId = div + "_targetIdsPane";
		$('#' + idPane).append('<div>Targets: <div id="' + targetIdsPaneId + '"></div></div>');
		
		function updateAll() {
			getMatrix();
			updateIdsPane();
		}
		
		function updateIdsPane() {
			$('#' + triggerIdsPaneId).empty();
			$('#' + targetIdsPaneId).empty();
			for (var id of triggers) {
				$('#' + triggerIdsPaneId).append('<button class="triggerButton">' + id + '</button>');
			}
			for (var id of targets) {
				$('#' + targetIdsPaneId).append('<button class="targetButton">' + id + '</button>');
			}
			$('.triggerButton').click(function() {
				triggers.splice(triggers.indexOf($(this).text()), 1);
				updateAll();
			});
			$('.targetButton').click(function() {
				targets.splice(targets.indexOf($(this).text()), 1);
				updateAll();
			});	
		}
	
		///////////////////
		
		var idMain = div + "_main";
		$('#' + div).append('<div id="' + idMain + '"></div>');
				
		var idEnabledTableDiv = idMain + "_enabledTable";
		$('#' + idMain).append('<div id="' + idEnabledTableDiv + '">enabled table</div>');
		var idValueTableDiv = idMain + "_valueTable";
		$('#' + idMain).append('<div id="' + idValueTableDiv + '">value table</div>');
			
		var enabledTable = null;
		var valueTable = null;
		
		initTable();
		
		var idPriority = div + "_priority";
		$('#' + div).append('<div id="' + idPriority + '"></div>');	
		priorityEditor = new PriorityEditor(idPriority, 'dependencyDesign2');
		priorityEditor.update();
		
		function initTable() {
			$('#' + idEnabledTableDiv).empty();
			$('#' + idValueTableDiv).empty();
			
			enabledTable = new JsMyTable(idEnabledTableDiv, colDefEnabledTable);
			
			enabledTable.checkListener = function(k, row, enabled) {
				var rowIndex = current.yTitle.indexOf(row);
				var colIndex = k - 1;
				
				var target = row;
				var trigger = current.xTitle[k-1];
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign2/setSpecEnabled?trigger=" + trigger + "&target=" + target + "&enabled=" + enabled,
				   success: function(msg){
						priorityEditor.update();
						getMatrix();
				   }
				});				
			};
			
			valueTable = new JsMyTable(idValueTableDiv, colDefValueTable);
			
			valueTable.listenerChange = function(row, k, value) {
				var rowIndex = current.yTitle.indexOf(row);
				var colIndex = k - 1;
				
				var target = row;
				var trigger = current.xTitle[k-1];
				var encValue = encodeURIComponent(value);
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign2/setSpecValue?trigger=" + trigger + "&target=" + target + "&value=" + encValue,
				   success: function(msg){
						priorityEditor.update();
						getMatrix();
				   }
				});					
			};
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign2/getSpec",
			   success: function(msg){
	//				updateEnableList(msg);
			   }
			});	
		}
		
		function getDependencyDesignConfigList() {
			$.ajax({
				type: "GET", 
				url: "http://" + window.location.host + "/rest/dependencyDesign2/getDependencyDesignConfigList",
				success: function(response) {
					$('#' + configId).empty();
					for (var config of response) {
						$('#' + configId).append($('<option>').text(config).val(config));
					}
					
					if (currentConfig == null) {
						currentConfig = response[0];
					}
					else {
						$('#' + configId).val(currentConfig);
					}
				}
			});			
		}
		
		function getDependencyDesignConfig(name) {
			$.ajax({
				type: "GET", 
				url: "http://" + window.location.host + "/rest/dependencyDesign2/getDependencyDesignConfig?name=" + name,
				success: function(response) {
					triggers = response.triggers;
					targets = response.targets;
					getMatrix();
					updateIdsPane();
				}
			});			
		}			
				
		function getMatrix() {
			if (triggers.length == 0 || targets.length == 0) {
				return;
			}
			var triggersText = "";
			var targetsText = "";
			for (var s of triggers) {
				triggersText += s + ",";
			}
			for (var s of targets) {
				targetsText += s + ",";
			}
			
			$.ajax({
				type: "GET", 
				url: "http://" + window.location.host + "/rest/dependencyDesign2/getMatrix?triggers=" + 
					triggersText + "&targets=" + targetsText,
				success: function(response) {
					updateTable(response);
				}
			});	
		}

		function updateTable(msg) {
			var titleRowEnabled = ['Enabled'];
			var titleRowValue = ['Value'];
			
			for (var row of msg.xTitle) {
				titleRowEnabled.push(row);
				titleRowValue.push(row);
			}
			enabledTable.clear();
			valueTable.clear();
			
			enabledTable.appendRow('title', titleRowEnabled);
			valueTable.appendRow('title', titleRowValue);
			
			current = msg;
			
			for (var r = 0; r < msg.yTitle.length; r++){
				var col = msg.yTitle[r];
				var s = [];
				s.push(col);
				for (var i = 0; i < msg.xTitle.length; i++) {
					var v = msg.enableMatrix[r][i];
					s.push(v.condition);
				}
				enabledTable.appendRow(col, s);				
			}
			
			for (var r = 0; r < msg.yValueTitle.length; r++){
				var col = msg.yValueTitle[r];
				
				var val = [];
				val.push(col);
				for (var i = 0; i < msg.xTitle.length; i++) {
					var v = msg.valueMatrix[r][i];
					val.push(v);
				}
				valueTable.appendRow(col, val);	
			}
		}
					
		function initIdSelection() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id2/ids",
			   success: function(msg) {
			   		$('#' + idsId).empty();
			   		
			   		for (var i = 0; i < msg.length; i++) {
						var id = msg[i];
						$('#' + idsId).append($('<option>').text(id).val(id));
					}
			   }
			});	
		}
		
		function registerConfig() {
			var name = $('#' + nameId).val();
			currentConfig = name;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign2/updateDependencyDesignConfig?name=" + name +
			   	"&triggers=" + triggers + "&targets=" + targets,
			   success: function(msg) {
					getDependencyDesignConfigList();
			   }
			});			
		
		}
		
		$('#' + dialogId).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Name',
			  closeOnEscape: true,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	registerConfig();
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
	}
}