class Devices {	
	constructor(div) {
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
		$('#' + listId).css({'display':'inline-block','width':'200px', 'border-width':'1px', 'border-color':'black'});
 		
		var contentId = div + "_content";
		$('#' + div).append('<div id="' + contentId + '">Main</div>');
		$('#' + contentId).css({'display':'inline-block','width':'600px'});
		
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
						$('#' + listId).append('<div><button>' + o + '</button></div>');
					}
			   }
			});
		}
	}
}