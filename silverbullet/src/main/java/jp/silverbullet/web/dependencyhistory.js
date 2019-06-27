class DependencyHistory {
	constructor(div) {
		var depHistButton = div + "_depHistButton";
		var depHistLog = div + "_depHistLog";
		var dialogId = div + "_depHistDialog";
		
		$('#' + div).append('<button id="' + depHistButton + '">Dependency Debug</button>');
		$('#' + depHistButton).click(function() {
			$('#' + dialogId).dialog('open');
		});

		
		$('#' + div).append('<div id="' + dialogId + '"><label id="' + depHistLog + '"></label></div>');
		$('#' + dialogId).dialog({
			  autoOpen: false,
			  title: "Dependency Debug",
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 600,
			height: 400
		});
				
		new MyWebSocket(function(msg) {
			$('#' + depHistLog).html(msg.replace('\n', '<br>'));
		}, 'DEBUG');
	}
	
	
	
}