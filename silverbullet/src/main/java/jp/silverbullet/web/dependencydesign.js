class DependencyDesign {
	constructor(div) {
		var table = new JsMyTable(div);
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
			table.appendRow(col, titleRow);
			
			for (var col of msg.colTitle) {
				var s = [];
				s.push(col);
				for (var i = 0; i < msg.rowTitle.length; i++) {
					s.push('');
				}
				table.appendRow(col, s);
			}
		}
	}
	
}
