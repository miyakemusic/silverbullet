class DependencyDesign {
	constructor(div) {
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
				var index = me.current.yTitle.indexOf(row);
				return me.current.enableMatrix[index][k-1].enabled;
			}
			else if (type == 'name') {
				var index = me.current.yTitle.indexOf(row);
				return me.current.enableMatrix[index][k-1].condition;
			}
		}
		
		var colDefValueTable = function(k, row, type) {
		}
		
		new MyWebSocket(function(msg) {				
				//var obj = JSON.parse(msg);			
				//var regName = obj.name;
				if (msg == 'MatrixChanged') {
					updateTable();
				}
			}
			, 'DEPDESIGN');
		
		var triggerId = div + "_trigger";
		var addTriggerId = div + "_addTrigger";
		var targetId = div + "_target";
		var addTargetId = div + "_addTarget";
		
		var addedTriggers = div + "_addedTriggers";
		var addedTargets = div + "_addedTargets";
		
		var updateAll = div + "_updateAll";
		$('#' + div).append('<button id="' + updateAll + '">Update</button>');
		$('#' + updateAll).click(function() {
			initIdSelection();
		   	initTable();
		   	updatePriorityEditor();
		});
		
		$('#' + div).append('<fieldset><legend>Trigger</legend><div>ID:<select id="' + triggerId + '"></select><button id="' + addTriggerId + '">Add</button><div>Remove:<span id="' + addedTriggers + '"></span></div></div></fieldset>');
		$('#' + div).append('<fieldset><legend>Target</legend><div>ID:<select id="' + targetId + '"></select><button id="' + addTargetId + '">Add</button><div>Remove:<span id="' + addedTargets + '"></span></div></div></fieldset>');
		
		$('#' + addTriggerId).click(function() {
			addId($('#' + triggerId).val(), "trigger");
		});
		$('#' + addTargetId).click(function() {
			addId($('#' + targetId).val(), "target");
		});		
				
		function addId(id, type) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/addId?id=" + id + "&type=" + type,
			   success: function(msg) {
			   	initOptions();
			   	initTable();
			   }
			});		
		}
		
		var updateId = div + "_update";
		$('#' + div).append('<button id="' + updateId + '">Update</button>');
		$('#' + updateId).click(function() {
		   	initTable();
		   	updatePriorityEditor();
		});
		
		var showAllId = div + "_showAll";
		$('#' + div).append('<button id="' + showAllId + '">Show All</button>');
		$('#' + showAllId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/showAll",
			   success: function(msg) {
			   	initOptions();
			   	initTable();
			   	updatePriorityEditor();
			   }
			});
		});	
		
		var switchId = div + "_switch";
		$('#' + div).append('<button id="' + switchId + '">Switch</button>');
		$('#' + switchId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/switch",
			   success: function(msg) {
			   	initOptions();
			   	initTable();
			   	updatePriorityEditor();
			   }
			});
		});
		
		var buttonId = div + "_build";
		$('#' + div).append('<button id="' + buttonId + '">Build (Normal)</button>');
		$('#' + buttonId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/build",
			   success: function(msg) {
			   }
			});
		});
		
		initIdSelection();
		
		function initIdSelection() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id2/ids",
			   success: function(msg) {
			   		$('#' + triggerId).empty();
			   		$('#' + targetId).empty();
			   		
			   		for (var i = 0; i < msg.length; i++) {
						var id = msg[i];
						$('#' + triggerId).append($('<option>').text(id).val(id));
						$('#' + targetId).append($('<option>').text(id).val(id));
					}
					$('#' + triggerId).change(function() {
						//setCombination();
					});
					$('#' + targetId).change(function() {
						//setCombination();
					});
					
					initOptions();
			   }
			});	
		}
		
		function initOptions() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getTargets",
			   success: function(msg){
				//$('#' + targetId).val(msg);
				$('#' + addedTargets).empty();
				for (var o of msg) {
					var removeButton = div + "_removeTarget_" + o;
					$('#' + addedTargets).append('<button id="' + removeButton + '">' + o + '</button>');
					$('#' + removeButton).click(function() {
						removeId($(this).text(), 'target');
					});
				}
				
			   }
			});	
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getTriggers",
			   success: function(msg){
				//$('#' + triggerId).val(msg);
				$('#' + addedTriggers).empty();
				for (var o of msg) {
					var removeButton = div + "_removeTrigger_" + o;
					$('#' + addedTriggers).append('<button id="' + removeButton + '">' + o + '</button>');
					$('#' + removeButton).click(function() {
						removeId($(this).text(), 'trigger');
					});
				}
			   }
			});	
		}
		
		function removeId(id, type) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/removeId?id=" + id + "&type=" + type,
			   success: function(msg){
			   	initOptions();
				initTable();
			   }
			});	
		}
		
		function setCombination() {
			var trigger = $('#' + triggerId).val();
			var target = $('#' + targetId).val();
						
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/setCombination?trigger=" + trigger + "&target=" + target,
			   success: function(msg){
				initTable();
			   }
			});				
		}
		var idMain = div + "_main";
		$('#' + div).append('<div id="' + idMain + '"></div>');
		
		var idEnabledTableDiv = idMain + "_enabledTable";
		$('#' + idMain).append('<div id="' + idEnabledTableDiv + '">enabled table</div>');
		var idValueTableDiv = idMain + "_valueTable";
		$('#' + idMain).append('<div id="' + idValueTableDiv + '">value table</div>');
		
		var priorityEditor = new PriorityEditor(idMain);
		
		function updatePriorityEditor() {
			priorityEditor.update();
		}
		
		initTable();
		
		function updateTable() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getSpec",
			   success: function(msg){
					replaceTable(msg);
			   }
			});		
			
			function replaceTable(msg) {
				me.current = msg;
				me.enabledTable.updateData();
			}
		}
		
		function initTable() {
			$('#' + idEnabledTableDiv).empty();
			$('#' + idValueTableDiv).empty();
			
			me.enabledTable = new JsMyTable(idEnabledTableDiv, colDefEnabledTable);
			
			me.enabledTable.checkListener = function(k, row, checked) {
				var rowIndex = me.current.yTitle.indexOf(row);
				var colIndex = k - 1;
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign/changeSpec?row=" + rowIndex + "&col=" + colIndex + "&checked=" + checked,
				   success: function(msg){
					
				   }
				});				
			};
			
			me.valueTable = new JsMyTable(idValueTableDiv, colDefValueTable);
			
			me.valueTable.listenerChange = function(row, k, value) {
				var rowIndex = me.current.yTitle.indexOf(row);
				var colIndex = k - 1;
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign/changeSpecValue?row=" + rowIndex + "&col=" + colIndex + "&value=" + value,
				   success: function(msg){
					
				   }
				});					
			};
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getEnableSpec",
			   success: function(msg){
					updateEnableList(msg);
			   }
			});	
						
			function updateEnableList(msg) {
				var titleRowEnabled = ['Enabled'];
				var titleRowValue = ['Value'];
				
				for (var row of msg.xTitle) {
					titleRowEnabled.push(row);
					titleRowValue.push(row);
				}
				me.enabledTable.appendRow('title', titleRowEnabled);
				me.valueTable.appendRow('title', titleRowValue);
				
				me.current = msg;
				
				for (var r = 0; r < msg.yTitle.length; r++){
					var col = msg.yTitle[r];
					var s = [];
					s.push(col);
					for (var i = 0; i < msg.xTitle.length; i++) {
						var v = msg.enableMatrix[r][i];
						s.push(v.condition);
					}
					me.enabledTable.appendRow(col, s);				
				}
				
				for (var r = 0; r < msg.yValueTitle.length; r++){
					var col = msg.yValueTitle[r];
					
					var val = [];
					val.push(col);
					for (var i = 0; i < msg.xTitle.length; i++) {
						var v = msg.valueMatrix[r][i];
						val.push(v);
					}
					me.valueTable.appendRow(col, val);	
				}
			}

		}
				
		var idPriority = div + "_priority";
		$('#' + div).append('<div id="' + idPriority + '"></div>');
	}
	
}
