class RegisterClass {
	constructor(div) {
		var selectId = div + '_depselect';		
		var showExternalId = div + '_external';
		$('#' + div).append('<div>Type:<select id="' + selectId + '"></select><button id="' + showExternalId + '">Show Map</button></div>');
		
		var regDiv = div + '_regDiv';
		$('#' + div).append('<div id="' + regDiv + '"></div>');
		
		$('#' + selectId).append($('<option>').text('Specification').val('Specification'));
		$('#' + selectId).append($('<option>').text('Map').val('Map'));
		$('#' + selectId).change(function() {
			$('#' + regDiv).empty();
			if ($(this).val() == 'Specification') {
				new RegisterSpec(regDiv);
			}
			else {
				new RegisterMap(regDiv);
			}
		});
		
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
//			  buttons: {
//			    "Close": function(){
//			    	$(this).dialog('close');
//			    }
//			  },
			width: 1200,
			height: 300
		});	
		
		$('#' + showExternalId).click(function() {
			$('#' + dialogId).dialog('open');
			new RegisterMap(dialogPaneId);
		});
	}
}