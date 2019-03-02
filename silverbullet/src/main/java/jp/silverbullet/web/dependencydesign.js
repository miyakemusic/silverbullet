class DependencyDesign {
	constructor(div) {
		var me = this;
		var colDef = function(k, row, type) {
			if (type == 'type') {
				if (row != 'title' && k > 0) {
					return 'check';
				}
				else {
					return 'text';
				}
			}
			else if (type == 'checked') {
				var index = me.current.colTitle.indexOf(row);
				return me.current.value[index][k-1].enabled;
			}
			else if (type == 'name') {
				var index = me.current.colTitle.indexOf(row);
				return me.current.value[index][k-1].condition;
			}
			
		}
		
		var triggerId = div + "_trigger";
		var targetId = div + "_target";
		$('#' + div).append('<div>Trigger ID:<select id="' + triggerId + '"></select></div>');
		$('#' + div).append('<div>Target ID:<select id="' + targetId + '"></select></div>');
		
		var switchId = div + "_switch";
		$('#' + div).append('<button id="' + switchId + '">Switch</button>');
		$('#' + switchId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/switch",
			   success: function(msg) {
			   	initOptions();
			   	initTable();
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
		
		var alwaysTrueId = div + "_alwaysTrue";
		$('#' + div).append('<button id="' + alwaysTrueId + '">Build (Always True)</button>');
		$('#' + alwaysTrueId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/alwaysTrue",
			   success: function(msg) {
			   }
			});
		});
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id2/ids",
		   success: function(msg) {
		   		for (var i = 0; i < msg.length; i++) {
					var id = msg[i];
					$('#' + triggerId).append($('<option>').text(id).val(id));
					$('#' + targetId).append($('<option>').text(id).val(id));
				}
				$('#' + triggerId).change(function() {
					setCombination();
				});
				$('#' + targetId).change(function() {
					setCombination();
				});
				
				initOptions();
		   }
		});	
	
		function initOptions() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getTarget",
			   success: function(msg){
				$('#' + targetId).val(msg);
			   }
			});	
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getTrigger",
			   success: function(msg){
				$('#' + triggerId).val(msg);
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
		
		function initTable() {
			$('#' + idMain).empty();
			var table = new JsMyTable(idMain, colDef);
			table.checkListener = function(k, row, checked) {
				var rowIndex = me.current.colTitle.indexOf(row);
				var colIndex = k - 1;
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign/changeSpec?row=" + rowIndex + "&col=" + colIndex + "&checked=" + checked,
				   success: function(msg){
					
				   }
				});				
			};
			table.listenerChange = function(k, row, text) {
			};
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getSpec",
			   success: function(msg){
					updateList(msg);
			   }
			});	
			
			function updateList(msg) {
				var titleRow = [];
				titleRow.push('');
				for (var row of msg.rowTitle) {
					titleRow.push(row);
				}
				table.appendRow('title', titleRow);
				
				me.current = msg;
				
				for (var r = 0; r < msg.colTitle.length; r++){
					var col = msg.colTitle[r];
					var s = [];
					s.push(col);
					for (var i = 0; i < msg.rowTitle.length; i++) {
						var v = msg.value[r][i];
						s.push(v.condition);
					}
					table.appendRow(col, s);
				}
			}
		}
	}
	
}
