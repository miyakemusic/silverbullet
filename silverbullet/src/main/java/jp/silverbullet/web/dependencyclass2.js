class DependencyClass2 {
	constructor(div) {
		var me = this;
		this.rootId = div + "_root";
		this.idsId = me.rootId + '_ids';
		$('#' + div).append('<select id="' + this.idsId + '"></select>');
		
		var diagramActive = false;
		
		var idDiagramButton = div + "_diagamButton";
		$('#' + div).append('<button id="' + idDiagramButton + '">Show Diagram</button>');
		$('#' + idDiagramButton).click(function() {
			$('#' + idDiagram).dialog('open');
			diagramActive = true;
		});
		
		this.idAll = div + '_all';
		this.idLoop = div + '_loop';
		$('#' + div).append('Show All IDs on diagram<input type="checkbox" id="' + me.idAll + '"></input>');
		$('#' + me.idAll).change(function() {
			if (diagramActive == false) {
				return;
			}
			me.updateLink();
		});
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/getIds",
		   success: function(msg) {
		   		for (var i = 0; i < msg.length; i++) {
					var id = msg[i];
					$('#' + me.idsId).append($('<option>').text(id).val(id));
				}
				$('#' + me.idsId).change(function() {
					var sel = $(this).val();
					editor.update(sel);
					me.updateLink();
				});
		   }
		});	

		$('#' + div).append('<div id="' + me.rootId + '"></div>');
		var editor = new DependencySpecEditor(me.rootId);

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
		
		var idDiagram = me.rootId + '_diagram';
		$('#' + div).append('<div id="' + idDiagram + '"></div>');
		
		var idDiagramInfo = me.rootId + '_diagramInfo';
		var idDiagramContent = me.rootId + '_diagramContent';
		$('#' + idDiagram).append('<div><label id="' + me.idLoop + '"></label></div>');
		$('#' + idDiagram).append('<div id="' + idDiagramContent + '" style="width:1000px; height:300px; background-color: #DAE4E4;"></div>');
		$('#' + idDiagram).dialog({
			autoOpen: false,
			title: 'Diagram',
			closeOnEscape: true,
			modal: false,
			close: function() {
				diagramActive = false;
			},
			width: 800,
			height: 600
		});
		this.diagram = new DependencyDiagram2(idDiagramContent);	
	}
	
	updateLink() {
		var id = $('#' + this.idsId).val();
		var all = $('#' + this.idAll).prop('checked');
		
		if (all == true) {
			id = '';
		}
		
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
				
				var html = 'LOOP: ';
				for (var i = 0; i < msg.loops.length; i++) {
					html += msg.loops[i] + " / ";
				}
				$('#' + me.idLoop).html(html);

		   }
		});	
	}
}