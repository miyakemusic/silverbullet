class DependencyDesign2 {
	constructor(div, application) {
		var triggers = [];
		var targets = [];
		var current;

		this.dependencyDesignPath = "//" + window.location.host + "/rest/" + application + "/dependencyDesign2";
		this.idPath = "//" + window.location.host + "/rest/" + application + "/id2";
		
		var me = this;
		var colDefEnabledTable = function(k, row, type) {
			if (type == 'type') {
				if (row != 'title' && k > 0) {
					return 'check';
				}
				else {
					return 'label';
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
			var index = current.yValueTitle.indexOf(row);
			
			if (type == 'type') {
				if (k == 0 || row == 'title') {
					return 'label';
				}
				
				if (current.relationMatrix[index][k-1].candidates.length > 0) {
					return 'select;button;check';
				}
				return 'text;button;check';
			}
			else if (type == 'checked') {
				return current.relationMatrix[index][k-1].blockPropagation;
			}
			else if (type == 'checkname') {
				return "block propagation";
			}
			else if (type == 'text') {
				if (row == 'title') {
					return current.xTitle[current.xTitle.indexOf(k)];
				}
				
				var value = current.relationMatrix[index][k-1];
				return value.relation;
			}		
			else if (type == 'button') {
				if (k == 0 || row == 'title') {
					return '';
				}
				var value = current.relationMatrix[index][k-1];		
				return value.condition;
			}
			else if (type == 'options') {
				var value = current.relationMatrix[index][k-1];
				return value.candidates;
			}	
		}		
		websocket.addListener('DEPDESIGN', function(msg) {				
			if (msg == 'MatrixChanged') {
				updatePriorityEditor();
			}
		});
			
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
		
		var confirmSamePriority = div + "_confirm";
		$('#' + div).append('<input type="checkbox" id="' + confirmSamePriority + '">Confirm same priority');
		$('#' + confirmSamePriority).change(function() {
			setConfirmSamePriority($(this).prop('checked'));
		});
		function setConfirmSamePriority(enabled) {
			$.ajax({
			   type: "GET", 
			   url: me.dependencyDesignPath + "/confirmSamePriority?enabled=" + enabled,
			   success: function(msg){
	
			   }
			});			
		}
		
				
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
			updatePriorityEditor();
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
			updatePriorityEditor();
		});
			
		var triggerIdsPaneId = div + "_triggerIdsPane";
		$('#' + idPane).append('<div>Triggers: <div id="' + triggerIdsPaneId + '"></div></div>');
		
		var targetIdsPaneId = div + "_targetIdsPane";
		$('#' + idPane).append('<div>Targets: <div id="' + targetIdsPaneId + '"></div></div>');
		
		function updateAll() {
			getMatrix();
			updateIdsPane();
			updatePriorityEditor();
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
		this.priorityEditor = new PriorityEditor(idPriority, application);
		
		function updatePriorityEditor() {
			var prioTargets = [];
			for (var v of triggers) {
				prioTargets.push(v);
			}
			for (var v of targets) {
				prioTargets.push(v);
			}
			me.priorityEditor.update(prioTargets);
		}
		
		
		function initTable() {
			$('#' + idEnabledTableDiv).empty();
			$('#' + idValueTableDiv).empty();
			
			enabledTable = new JsMyTable(idEnabledTableDiv, colDefEnabledTable);
			
			enabledTable.checkListener = function(row, k, enabled) {
				var rowIndex = current.yTitle.indexOf(row);
				var colIndex = k - 1;
				
				var target = row;
				var trigger = current.xTitle[k-1];
				$.ajax({
				   type: "GET", 
				   url: me.dependencyDesignPath + "/setSpecEnabled?trigger=" + trigger + "&target=" + target + "&enabled=" + enabled,
				   success: function(msg){
						updatePriorityEditor();
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
				   url: me.dependencyDesignPath + "/setSpecValue?trigger=" + trigger + "&target=" + target + "&value=" + encValue,
				   success: function(msg){
						updatePriorityEditor();
						getMatrix();
				   }
				});					
			};
			
			valueTable.selectListener = function(row, k, value) {
				var rowIndex = current.yTitle.indexOf(row);
				var colIndex = k - 1;
				
				var target = row;
				var trigger = current.xTitle[k-1];
				var encValue = encodeURIComponent(value);
				$.ajax({
				   type: "GET", 
				   url: me.dependencyDesignPath + "/setSpecValue?trigger=" + trigger + "&target=" + target + "&value=" + encValue,
				   success: function(msg){
						updatePriorityEditor();
						getMatrix();
				   }
				});					
			};
			
			valueTable.checkListener = function(row, k, value) {
				var rowIndex = current.yTitle.indexOf(row);
				var colIndex = k - 1;
				var target = row;
				var trigger = current.xTitle[k-1];
				var enabled = value;
				$.ajax({
				   type: "GET", 
				   url: me.dependencyDesignPath + "/setBlockPropagation?trigger=" + trigger + "&target=" + target + "&enabled=" + enabled,
				   success: function(msg){
						updatePriorityEditor();
						getMatrix();
				   }
				});	
			}
			
			me.equationEditor = new EquationEditor(div, application);
			valueTable.setButtonListener(function(row, k, v) {
				me.equationEditor.show(v, function(value) {
					var rowIndex = current.yValueTitle.indexOf(row);
					var colIndex = k - 1;
					
					var target = row;
					var trigger = current.xTitle[k-1];			
					
					setCondition(trigger, target, value);
				});
			});
			
		}
		
		function setCondition(trigger, target, condition) {
			$.ajax({
			   type: "GET", 
			   url: me.dependencyDesignPath + "/setSpecValueCondition?trigger=" + 
			   	trigger + "&target=" + target + "&condition=" + condition,
			   success: function(msg){
					getMatrix();
			   }
			});	
		}
		
		this.getDependencyDesignConfigList = function() {
			$.ajax({
				type: "GET", 
				url: me.dependencyDesignPath + "/getDependencyDesignConfigList",
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
		me.getDependencyDesignConfigList();
		
		function getDependencyDesignConfig(name) {
			$.ajax({
				type: "GET", 
				url: me.dependencyDesignPath + "/getDependencyDesignConfig?name=" + name,
				success: function(response) {
					triggers = response.triggers;
					targets = response.targets;
					getMatrix();
					updateIdsPane();
					updatePriorityEditor();
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
				url: me.dependencyDesignPath + "/getMatrix?triggers=" + 
					triggersText + "&targets=" + targetsText,
				success: function(response) {
					updateTable(response);
				}
			});	
		}

		function updateTable(msg) {
			current = msg;
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
					var v = msg.relationMatrix[r][i];
					val.push(v.relation);
				}
				valueTable.appendRow(col, val);	
			}
		}
					
		function initIdSelection() {
			$.ajax({
			   type: "GET", 
			   url: me.idPath + "/ids",
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
			   url: me.dependencyDesignPath + "/updateDependencyDesignConfig?name=" + name +
			   	"&triggers=" + triggers + "&targets=" + targets,
			   success: function(msg) {
					me.getDependencyDesignConfigList();
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
	
	rebuild(application) {
		this.dependencyDesignPath = "//" + window.location.host + "/rest/" + application + "/dependencyDesign2";
		this.idPath = "//" + window.location.host + "/rest/"+ application + "/id2";
		this.getDependencyDesignConfigList();
		this.equationEditor.path(application);
		this.priorityEditor.path(application);
	}
}