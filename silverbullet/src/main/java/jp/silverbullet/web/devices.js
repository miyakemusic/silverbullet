class Devices {	
	constructor(div) {
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
		new MyWebSocket(function(msg) {
			retreiveDevices();
		}
		, 'DEVICE');	
		
		
		retreiveDevices();
		
		function retreiveDevices() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/domain/devices",
				success: function(msg){
					$('#' + listId).empty();
					for (var o of msg) {
						$('#' + listId).append('<div><label>' + o + '</label></div>');
					}
			   }
			});
		}
	}
}