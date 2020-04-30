class Devices {	
	constructor(div, direction) {
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
 		
		var contentId = div + "_content";
		$('#' + div).append('<div id="' + contentId + '">Main</div>');
		
		if (direction == 'horizontal') {
		}
		else if (direction == 'vertical'){
			$('#' + div).css({'vertical-align':'top'});
			$('#' + listId).css({'display':'inline-block','width':'150px', 'border-width':'1px', 'background-color':'lightgreen'});
			$('#' + contentId).css({'display':'inline-block', 'width':'800px', 
				'height':'600px', 'background-color':'white', 'vertical-align':'top'});
		}
		
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
						if (direction == 'horizontal') {
							$('#' + listId).append('<button class="deviceButton">' + o + '</button>');
						}
						else if (direction == 'vertical'){
							$('#' + listId).append('<div><button class="deviceItem deviceButton">' + o + '</button></div>');
						}
					}
					$('.deviceButton').on('click', function() {
						$('#' + contentId).empty();
						var device = $(this).text();
						retreiveUiEntry(device, function(result) {
							new NewLayout(contentId, result, device);
						});
						
					});	
			   }
			});
		}
		
		function retreiveUiEntry(device, result) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/domain/" + device + "/getUiEntry",
				success: function(msg){
					result(msg);
				}
			});
		}	
	}
}
class AllDevices {
	constructor(div) {
	
		var webSocket = new MyWebSocket(function(msg) {
			retreiveDevices();
		}
		, 'DEVICE');
		
		retreiveDevices();
		
		function retreiveDevices() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/domain/devices",
				success: function(msg){
					$('#' + div).empty();
					var tablestr = '<table>';
					for (var device of msg) {
						var contentId = div + '_' + device;
						//var buttonId = contentId + '_button';
						tablestr += '<tr><td><input type="checkbox" class="deviceButton" id="' + device + '" name="' + contentId + '">' + device + '</button></td><td id="' + contentId + '">_________________________________________________________________________________________________________________</td></tr>';

//						$('#' + contentId).css({'width':'800px','height':'500px'}); 
					}
					tablestr += '</table>';
					$('#' + div).append(tablestr);
					$('.deviceButton').click(function() {
						var checked = $(this).prop('checked');
						if (checked == true) {
							var device = $(this).prop('id');
							var contentId = $(this).prop('name');
							retreiveUiEntry(device, contentId, function(d, dv, result) {
								new NewLayout(d, result, dv);
							});
						}
						else {
							var contentId = $(this).prop('name');
							$('#' + contentId).empty();
						}
					});
			   }
			});
		}	
		function retreiveUiEntry(device, contentId, result) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/domain/" + device + "/getUiEntry",
				success: function(msg){
					result(contentId, device, msg);
				}
			});
		}		
	}
}
