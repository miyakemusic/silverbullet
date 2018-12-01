class DependencyClass2 {
	constructor(div) {
		var me = this;
		var rootId = div + "_root";
		var idsId = rootId + '_ids';
		$('#' + div).append('<select id="' + idsId + '"></select>');
		
		var idDiagramButton = div + "_diagamButton";
		$('#' + div).append('<button id="' + idDiagramButton + '">Show Diagram</button>');
		$('#' + idDiagramButton).click(function() {
			$('#' + idDiagram).dialog('open');
		});
		
		var idAll = div + '_all';
		this.idLoop = div + '_loop';
		$('#' + div).append('Show All IDs on diagram<input type="checkbox" id="' + idAll + '"></input><label id="' + me.idLoop + '"></label>');
		$('#' + idAll).change(function() {
			if ($(this).prop('checked') == true) {
				me.updateLink('');
			}
			else {
				me.updateLink($('#' + idsId).val());
			}
		});
		
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
					me.updateLink(sel);
				});
		   }
		});	

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
		
		var idDiagram = rootId + '_diagram';
		$('#' + div).append('<div id="' + idDiagram + '" style="width:1000px; height:300px; background-color: #DAE4E4;">Diagram</div>');
		$('#' + idDiagram).dialog({
			  autoOpen: false,
			  title: 'Diagram',
			  closeOnEscape: true,
			  modal: false,
//			  buttons: {
//			    "OK": function(){
//			      $(this).dialog('close');
//			    }
//			    ,
//			    "Cancel": function(){
//			      $(this).dialog('close');
//			    }
//			  },
			width: 800,
//			height: 600
		});
		this.diagram = new DependencyDiagram2(idDiagram);	
	}
	
	updateLink(id) {
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/getLinks?id=" + id,
		   success: function(msg){
			   var link = [];
			   var links = msg.links;
				for (var i in links) {
					var from = links[i].from;
					var to = links[i].to;

					var path = {from: links[i].from, to: links[i].to, text: links[i].type};
					link.push(path);
					
				}
				
				me.diagram.draw(link)
				
				if (msg.loop == true) {
					$('#' + me.idLoop).html('<font color="red"><b>loop</b></font>');
				}
				else {
					$('#' + me.idLoop).html('');
				}
		   }
		});	
	}
}