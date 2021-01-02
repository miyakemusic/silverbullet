
class Devices {	
	constructor(div, direction) {
		var listId = div + "_deviceList";
		$('#' + div).append('<div id="' + listId + '"></div>');
 		
		var contentId = div + "_content";
		$('#' + div).append('<div id="' + contentId + '">Main</div>');
		
		var deviceId = div + "_device";
		$('#' + contentId).append('<div id="' + deviceId + '"></div>');

/*			
		var toolId = div + "_tool";
		$('#' + contentId).append('<div id="' + toolId + '"></div>');
	
		var uploadId = div + "_upload";
		var fileId = div + "_file";
		$('#' + toolId).append('<input type="file" id="' + fileId + '"><button id="' + uploadId + '">Upload</button>');

		$('#' + uploadId).click(function() {
			postFile(me.device);
		});
*/		

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
		
		var uiList = [];
		
		var me = this;
		function retreiveDevices() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/allapp/domain/devices",
				success: function(msg){
					for (var ui of uiList) {
						$('#' + ui).dialog('destroy');
					}
					uiList.splice(0);
					$('#' + deviceId).empty();
					for (var o of msg) {
						var device = o.split(',')[0];
						var application = o.split(',')[1];
						
						retreiveUiEntry(application, device, function(result, application, device) {
							var deviceIdOne = deviceId + '_' + device;
							var deviceIdOneButton = deviceIdOne + '_button';
							var deviceUiWindows = deviceIdOne + "_uiPane";
							$('#' + deviceId).append('<div id=' + deviceUiWindows + '></div>');
							
																		
							$('#' + deviceId).append('<button id="' + deviceIdOneButton + '">' + device + '</button><div id="' + deviceIdOne + '"></div>');
							
							$('#' + deviceIdOneButton).click(function() {
								registerUiEntry(application, $(this).text());
							});
		
							if (result != '') {
								uiList.push(deviceUiWindows);
								new NewLayout(deviceUiWindows, result, device, function(height) {
									$('#' + deviceIdOne).css({'min-height':height, 'overflow':'hidden'});
									
									$('#' + deviceUiWindows).dialog({
										autoOpen: true,
										title: device,
										closeOnEscape: false,
										modal: false,
										width: 600,
										height: 400
									});
								}, application);
							}
						});					
					}
			   }
			});
		}
		
		var selectedDevice;		
		var selectedApplication;
		var uiDialog = div + '_uiDialog';
		var uiSelect = div + '_uiSelect';
		$('#' + div).append('<div id="' + uiDialog + '"></div>');
		$('#' + uiDialog).dialog({
			  autoOpen: false,
			  title: "UI",
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	setUiEntry();
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 600,
			height: 400
		});
		
		$('#' + uiDialog).append('<select id="' + uiSelect + '"></select>');
		function registerUiEntry(application, device) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/newGui/getRootPanes",
				success: function(msg){
					$('#' + uiSelect).empty();
					for (var o of msg) {
						var option = $('<option>').val(o).text(o);
						$('#' + uiSelect).append(option);		
					}
					
				}
			});	
			selectedDevice = device;
			selectedApplication = application;
			$('#' + uiDialog).dialog('open');
		}
		
		function setUiEntry() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + selectedApplication + "/domain/" + 
					selectedDevice + "/setUiEntry?ui=" + $('#' + uiSelect).val(),
				success: function(msg){
					retreiveDevices();
				}
			});
		}
		
		function retreiveUiEntry(application, device, result) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/domain/" + device + "/getUiEntry",
				success: function(msg){
					result(msg, application, device);
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
		
		var dialogId = div + "_dialog";
		$('#' + div).append('<div id="' + dialogId + '"></div>');
		$('#' + dialogId).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Automator',
			  closeOnEscape: false,
			  modal: false,
			width: 1200,
			height: 300
		});	
		var automatorId = div + "_automator";
		$('#' + div).append('<button id=' + automatorId + '>Automator</button>');
		$('#' + automatorId).click(function() {
			$('#' + dialogId).dialog('open');
		});
		
		var script = div + "_script";
		var record = div + "_record";
		$('#' + dialogId).append('<button id="' + record + '">Record</button>');
		$('#' + record).click(function() {
			if ($(this).text() == "Record") {
				$(this).text("Stop");
				$.ajax({
					type: "GET", 
					url: "//" + window.location.host + "/rest/allapp/domain/record",
					success: function(msg){
	
					}
				});
			}
			else {
				$(this).text("Record");
				$.ajax({
					type: "GET", 
					url: "//" + window.location.host + "/rest/allapp/domain/script",
					success: function(msg){
						$('#' + script).empty();
						var lines = '';
						for (var s of msg) {
							lines += s + '\n';
						}
						$('#' + script).text(lines);
					}
				});
			}

		});
		var copy = div + "_copy";
		$('#' + dialogId).append('<button id="' + copy + '">Copy to script</button>');
		
		var reload = div + "_reload";
		$('#' + dialogId).append('<div><textarea readonly id="' + script + '"></textarea></div>');
		
		var playbackScript = div + "_playbackScript";
		var playback = div + "_playback";
		var save = div + "_save";
		var list = div + "_list";
		var toolbar = div + "_toolbar";
		$('#' + dialogId).append('<div id="' + toolbar + '"></div>');	
		$('#' + toolbar).append('<button id="' + playback + '">Play Back</button>');	
		$('#' + toolbar).append('<button id="' + save + '">Save</button>');	
		$('#' + toolbar).append('<select id="' + list + '"></select></div>');	
		$('#' + dialogId).append('<div><textarea id="' + playbackScript + '"></textarea></div>');

		$('#' + list).change(function() {
			retreiveScript($('#' + list).val());
		});
		
		function retreiveScript(name) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/allapp/domain/script?name=" + name,
				success: function(msg){
					var lines = "";
					for (var line of msg) {
						lines += line + "\n";
					}
					$('#' + playbackScript).val(lines);
				}
			});
		}
		
		var saveDialogId = div + "_savedialog";
		$('#' + div).append('<div id="' + saveDialogId + '"></div>');
		$('#' + saveDialogId).dialog({
			  autoOpen: false,
			  title: 'Automator',
			  closeOnEscape: false,
			  modal: false,
			width: 500,
			height: 200
		});	
		var saveName = div + "_saveName";
		var saveButton = div + "_saveButton";
		
		$('#' + saveDialogId).append('<input type="text" id="' + saveName + '"></input>');
		$('#' + saveDialogId).append('<button id="' + saveButton + '">Save</button>');
		
		$('#' + save).click(function() {
			$('#' + saveDialogId).dialog('open');
		});
		$('#' + saveButton).click(function() {
			registerScript($('#' + saveName).val());
			$('#' + saveDialogId).dialog('close');
			updateScriptList();
		});
						
		$('#' + copy).click(function() {
			$('#' + playbackScript).val($('#' + script).val());
		});
		
		$('#' + playback).click(function() {
			playbackFunction();
		});
	
		updateScriptList();
		
		function updateScriptList() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/allapp/domain/scriptList",
				success: function(msg){
					$('#' + list + ' > option').remove();
					for (var i in msg) {
						var val = msg[i];
						$('#' + list).append($('<option>').html(val).val(val));
					}
					
					retreiveScript(msg[0]);
				}
			});
		}
		
		function playbackFunction() {
			$.ajax({
	            url: "//" + window.location.host + "/rest/allapp/domain/playback",
	            type: 'POST',
	            contentType: 'text/plain',
				data: $('#' + playbackScript).val(),
				processData: false
	        })
	        .done(function( data ) {
	
	        });		
		}
		
		function registerScript(name) {
			$.ajax({
	            url: "//" + window.location.host + "/rest/allapp/domain/saveScript?name=" + name,
	            type: 'POST',
	            contentType: 'text/plain',
				data: $('#' + playbackScript).val(),
				processData: false
	        })
	        .done(function( data ) {
	
	        });		
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
						tablestr += '<td><input type="checkbox" class="deviceButton" id="' + device + '" name="' + contentId + '">' + device + '</td><td id="' + contentId + '"></td>';
					
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
							retreiveUiEntry(device, contentId, function(d, application, device, result) {
								new NewLayout(d, result, device);
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
		function retreiveUiEntry(application, device, contentId, result) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/domain/" + device + "/getUiEntry",
				success: function(msg){
					result(contentId, device, msg);
				}
			});
		}		
	}
}
