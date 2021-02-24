class DeviceList {
	constructor(div) {
			
		var buttonId = div + "_button";
		$('#' + div).append('<button id ="' + buttonId + '">Device</button>');
		
		var mainId = div + "_main";
		
		var map = new Map();
		
		function retreiveDevices() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/allapp/domain/devices",
				success: function(msg){
					$('#' + mainId).empty();
					
					for (var o of msg) {
						var device = o.deviceName;
						var application = o.applicationName;
						var serial = o.serialNo;
						var userName = o.currentUserName;
						var id = device + '_' + serial;
						
						$('#' + mainId).append('<button id="' + id + '">' + device + '(' + userName + ')' + '</button>');
						
						$('#' + id).click(function() {
							openWindow($(this).prop('id'), application);
						});
					}
				}
			});
		}
		
		function openWindow(id, application) {
			var device = id.split('_')[0];
			var serialNo = id.split('_')[1];
			
			var uiId = id + '_ui';
			$('#' + div).append('<div id ="' + uiId + '"></div>');
			
			$('#' + uiId).dialog({
				autoOpen: true,
				title: "Device List",
				closeOnEscape: false,
				modal: false,
				width: 800,
				height: 600,
				buttons: {
					"Close": function() {
						$(this).dialog('destroy');
						$('#' + uiId).remove();
					}
				}
			});

			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/domain/" + device + "/" + serialNo + "/getUiEntry",
				success: function(msg){
								
					new NewLayout(uiId, msg, device, serialNo, function(height) {
						$('#' + uiId).css({'min-height':height, 'overflow':'hidden'});
					}, application);
				}
			});

		}
		
		websocket.addListener('DEVICE', function(result) {
			retreiveDevices();
		});
		
		var dialogId = div + "_dialog";
		$('#' + buttonId).click(function() {
			$('#' + dialogId).dialog('open');
		});
		
		$('#' + div).append('<div id ="' + dialogId + '"><div id="' + mainId + '"></div></div>');
		
		$('#' + dialogId).dialog({
			autoOpen: false,
			title: "Device List",
			closeOnEscape: false,
			modal: false,
			width: 200,
			height: 600
		});
	}
}