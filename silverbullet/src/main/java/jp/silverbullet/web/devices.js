class Devices {	
	constructor(div) {
		$('#' + div).css({'vertical-align':'top'});
		
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
		$('#' + listId).css({'display':'inline-block','width':'200px', 'border-width':'1px', 'background-color':'lightgreen'});
 		
		var contentId = div + "_content";
		$('#' + div).append('<div id="' + contentId + '">Main</div>');
		$('#' + contentId).css({'display':'inline-block', 'width':'1000px', 
			'height':'800px', 'background-color':'lightgray', 'vertical-align':'top'});
		
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
						$('#' + listId).append('<div><button class="deviceItem">' + o + '</button></div>');
					}
					$('.deviceItem').on('click', function() {
						new NewLayout(contentId, 'OTDR');
					});	
			   }
			});
		}
1
2
3
	
	}
}