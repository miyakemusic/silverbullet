class DependencyClass2 {
	constructor(div) {
		var idsId = rootId + '_ids';
		$('#' + div).append('<select id="' + idsId + '"></select>');
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/getIds",
		   success: function(msg) {
		   		for (var i = 0; i < msg.length; i++) {
					var id = msg[i];
					$('#' + idsId).append($('<option>').text(id).val(id));
				}
				$('#' + idsId).change(function() {
				var sel = $(this).val();
					editor.update(sel);
				});
		   }
		});	
	
		var rootId = div + "_root";
		$('#' + div).append('<div id="' + rootId + '"></div>');
		var editor = new DependencySpecEditor(rootId);

		var idSpecDialog = div + "_specDialog";
		$('#' + div).append('<div id="' + idSpecDialog + '">' +
			'</div>');
		$('#' + idSpecDialog).dialog({
			  autoOpen: false,
			  title: 'Dependency Spec Editor',
			  closeOnEscape: true,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 800,
//			height: 600
		});	
		
		
	}
}