class ControllerClass {
	constructor(div) {		
		var idAdd = div + 'addController';
		$('#' + div).append('<button id="' + idAdd + '">Add</button>');
		$('#' + idAdd).click(function() {
			$.ajax({
				type: "GET", 
				url: window.location.origin + "/rest/controller/addNew",
					success: function(msg) {
						myTable.clear();
						myTable.appendRows(msg);
					}		
			});			
		});
		
		var myTable = new JsMyTable(div);
		myTable.listenerRemove = function(row) {
			removeRow(row);
		}
		
		myTable.listenerChange = function(row, name, value) {
			$.ajax({
				type: "GET", 
				url: window.location.origin + "/rest/controller/update?row=" + row + "&name=" + name + "&value=" + value,
					success: function(msg) {
						myTable.clear();
						myTable.appendRows(msg);
					}		
			});
		}
		
//		$.ajax({
//			type: "GET", 
//			url: window.location.origin + "/rest/controller/getSpecs",
//				success: function(msg) {
//					myTable.appendRows(msg);
//				}
//		});
		
		function removeRow(row) {
			$.ajax({
				type: "GET", 
				url: window.location.origin + "/rest/controller/remove?row=" + row,
					success: function(msg) {
						myTable.clear();
						myTable.appendRows(msg);
					}		
			});
		}
	}
}