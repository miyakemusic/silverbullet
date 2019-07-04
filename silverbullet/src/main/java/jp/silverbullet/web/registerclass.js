class RegisterClass {
	constructor(div) {
		var selectId = div + '_depselect';		
		var showExternalId = div + '_external';
		
		new ListBox(div, 'Register Size', "http://" + window.location.host + "/rest/register2", "RegSize");
		
		var hardOrSimId = div + '_hardOrSim';
		
		$('#' + div).append('<select id="' + hardOrSimId + '"></input>');
		$('#' + hardOrSimId).append($('<option>').text('Hardware').val('Hardware'));
		$('#' + hardOrSimId).append($('<option>').text('Simulator').val('Simulator'));
		
		var simDiv = div + '_simDiv';
		$('#' + div).append('<div id="' + simDiv + '"></div>');
		//$('#' + simDiv).show();
		
		$('#' + simDiv).append('<div>Type:<select id="' + selectId + '"></select><button id="' + showExternalId + '">Show Map</button></div>');
		
		var regDiv = div + '_regDiv';
		$('#' + div).append('<div id="' + regDiv + '"></div>');
		
		$('#' + selectId).append($('<option>').text('Specification').val('Specification'));
		$('#' + selectId).append($('<option>').text('Map').val('Map'));
		
		var me = this;
		
		$('#' + selectId).change(function() {
			$('#' + regDiv).empty();
			if ($(this).val() == 'Specification') {
				me.registerSpec = new RegisterSpec(regDiv);
			}
			else {
				me.registerMap = new RegisterMap(regDiv);
			}
		});
		
		$('#' + hardOrSimId).change(function() {
			if ($(this).val() == 'Simulator') {
				$('#' + simDiv).show();
			}
			else {
				$('#' + simDiv).hide();
			}
		});
		$('#' + hardOrSimId).val('Simulator');
		
		new RegisterSpec(regDiv);
		
		var dialogId = div + '_mapDialog';
		var dialogPaneId = dialogId + '_pane';
		$('#' + div).append('<div id="' + dialogId + '"><div id="' + dialogPaneId + '">Map</div></div>');
		$('#' + dialogId).dialog({
			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Register Map',
			  closeOnEscape: false,
			  modal: false,
			width: 1200,
			height: 300
		});	
		
		$('#' + showExternalId).click(function() {
			$('#' + dialogPaneId).empty();
			$('#' + dialogId).dialog('open');
			new RegisterMap(dialogPaneId);
		});
		
		function setRegisterType(type) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register2/setRegisterType?type=" + type,
			   success: function(msg){
			   }
			});		
		}
	}
}