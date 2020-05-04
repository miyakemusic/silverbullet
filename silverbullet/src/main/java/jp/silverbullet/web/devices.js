
class Devices {	
	constructor(div, direction) {
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
 		
		var contentId = div + "_content";
		$('#' + div).append('<div id="' + contentId + '">Main</div>');
		
		var deviceId = div + "_device";
		$('#' + contentId).append('<div id="' + deviceId + '"></div>');
		
		var toolId = div + "_tool";
		$('#' + contentId).append('<div id="' + toolId + '"></div>');
		
		var uploadId = div + "_upload";
		var fileId = div + "_file";
//		$('#' + toolId).append('<form id="my_form"><input type="file" id="' + fileId + '"><button id="' + uploadId + '">Upload</button></form>');
		$('#' + toolId).append('<input type="file" id="' + fileId + '"><button id="' + uploadId + '">Upload</button>');

		$('#' + uploadId).click(function() {
			postFile(me.device);
		});
		

		if (direction == 'horizontal') {
		}
		else if (direction == 'vertical'){
			$('#' + div).css({'vertical-align':'top'});
			$('#' + listId).css({'display':'inline-block','width':'150px', 'border-width':'1px', 'background-color':'lightgreen'});
			$('#' + contentId).css({'display':'inline-block', 'width':'800px', 
				'height':'600px', 'background-color':'white', 'vertical-align':'top'});
		}
		
		$('#' + div).append('<div id="' + listId + '"></div>');
	
		websocket.addListener('DEVICE', function(result) {
			retreiveDevices();
		});
		
		retreiveDevices();
		
		var me = this;
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
						$('#' + deviceId).empty();
						me.device = $(this).text();
						retreiveUiEntry(me.device, function(result) {
							new NewLayout(deviceId, result, me.device);
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
		
		function postFile(device) {
			var file = $('#' + fileId)[0].files[0];
			var reader = new FileReader();
			reader.readAsDataURL(file);
			reader.onload = function(event) {
				$.ajax({
		            url: "//" + window.location.host + "/rest/" + device + "/runtime" + "/postFile?filename=" + file.name,
		            type: 'POST',
		            contentType: 'text/plain',
					data: event.target.result,
					processData: false
		        })
		        .done(function( data ) {
		
		        });			
			}
		}
	}
}
class AllDevices {
	constructor(div) {
				
		var columnCountId = div + "_columnCount";
		$('#' + div).append('Column Count: <select id="' + columnCountId + '"></select>');
		$('#' + columnCountId).append($('<option />').val('1').text('1'));
		$('#' + columnCountId).append($('<option />').val('2').text('2'));
		$('#' + columnCountId).append($('<option />').val('3').text('3'));
		$('#' + columnCountId).append($('<option />').val('4').text('4'));
		$('#' + columnCountId).change(function() {
			retreiveDevices($(this).val());
		});
		
		var mainId = div + "_mainId";
		$('#' + div).append('<div id="' + mainId + '"></div>');
		
		retreiveDevices(1);

		webSocket.addListener(function(msg) {
			retreiveDevices($('#' + columnCountId).val());
		});
				
		function retreiveDevices(columns) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/domain/devices",
				success: function(msg){
					$('#' + mainId).empty();
					
					var col = 0;
					var tablestr = '<table><tr>';
					for (var device of msg) {
						var contentId = div + '_' + device;
						tablestr += '<td><input type="checkbox" class="deviceButton" id="' + device + '" name="' + contentId + '">' + device + '</button></td><td id="' + contentId + '"></td>';
					
						col++;
						if (col == columns) {
							col = 0;
							tablestr += '</tr><tr>';
						}
					}
					tablestr += '</tr></table>';
					$('#' + mainId).append(tablestr);
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
