class NewLayoutLibrary {
	constructor(div, selector) {
		var addId = div + "_add";
		$('#' + div).append('<button id="' + addId + '">Add</button>');
		$('#' + addId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/addRootPane",
			   success: function(types){
			   		updateList();
			   }
			});			
		});
		
		var listId = div + '_list';
		$('#' + div).append('<div id="' + listId + '"></div>');

		function updateList() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/getRootPanes",
			   success: function(types){
			   		$('#' + listId).empty();
					for (var option of types) {
						var id = div + "_" + option;
						$('#' + listId).append('<div><label id="' + id + '"></label></div>');
						$('#' + id).text(option);
						
						$('#' + id).click(function() {
							selector($(this).text());
						});
					}
			   }
			});	
		}
		
		updateList();
	}
}