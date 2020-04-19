class PropertyDebug {
	constructor(div) {
		var baseDiv = div + '_propertyDebug';
		$('#' + div).append('<div id="' + baseDiv + '"></div>');
		
		var buttonUpdate = baseDiv + "_update";
		$('#' + baseDiv).append('<button id="' + buttonUpdate + '">Update</button>');
		
		$('#' + buttonUpdate).click(function() {
			updateAll();
		});
		
		var table = new JsMyTable(baseDiv);
	
		function updateAll() {
			table.clear();
			$.ajax({
			   type: "GET", 
			   url: window.location.origin + "/rest/runtime/getProperties",
			   success: function(props){
				table.appendRows(props);
			   }
			});		
		}

	}
}