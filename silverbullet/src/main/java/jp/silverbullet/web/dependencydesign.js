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
		
		var table = new JsMyTable(div, colDef);
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
