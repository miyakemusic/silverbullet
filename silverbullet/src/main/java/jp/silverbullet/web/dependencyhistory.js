class DependencyHistory {
	constructor(div) {
		var depHistButton = div + "_depHistButton";
		var depHistLog = div + "_depHistLog";
		var dialogId = div + "_depHistDialog";
		
		$('#' + div).append('<button id="' + depHistButton + '">Dependency Debug</button>');
		$('#' + depHistButton).click(function() {
			debugEnabled(true);
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
			    	debugEnabled(false);
			    	$(this).dialog('close');
			    	
			    }
			  },
			width: 600,
			height: 400
		});
				
		websocket.addListener('DEBUG', function(msg) {
			$('#' + depHistLog).html(msg.replace('\n', '<br>'));
		});
		
		function debugEnabled(enabled) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/runtime/dependencyDebug?enabled=" + enabled,
			   success: function(widget){

			   }
			});	
		}
		
	}
	
	
	
}