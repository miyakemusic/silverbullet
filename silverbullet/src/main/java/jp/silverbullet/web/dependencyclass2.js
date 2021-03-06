class DependencyClass2 {
	constructor(div, application) {
		this.dependencyPath = "//" + window.location.host + "/rest/" + application + "/dependencySpec2";
		this.idPath = "//" + window.location.host + "/rest/" + application + "/id2";
		
		var me = this;
		this.rootId = div + "_root";
		this.idsId = me.rootId + '_ids';
		$('#' + div).append('<select id="' + this.idsId + '"></select>');
		$('#' + me.idsId).change(function() {
			updateAll();
		});
				
		websocket.addListener('ID', function(msg) {
			//updateIdList();
			me.updateIdList();
		});
				
		this.priorityId = div + '_priority';
		$('#' + div).append('Priority: <input type="text" id="' + this.priorityId + '"></input>');
		$('#' + this.priorityId).keydown(function(event) {
			if (event.which == 13) { // Enter
				$.ajax({
				   type: "GET", 
				   url: me.dependencyPath + "/setPriority?id=" + $('#' + me.idsId).val() + "&priority=" + $(this).val(),
				   success: function(msg){
				   }
				});				
			}
		});
				
		var diagramActive = false;
		
		var idDiagramButton = div + "_diagamButton";
		$('#' + div).append('<button id="' + idDiagramButton + '">Show Diagram</button>');
		$('#' + idDiagramButton).click(function() {
			me.updateLink();
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
		
		var idRestriction = div + '_restriction';
		$('#' + div).append('<button id="' + idRestriction + '">Restriction</button>');
		$('#' + idRestriction).click(function() {
			$.ajax({
			   type: "GET", 
			   url: me.dependencyPath + "/getRestrictions",
			   success: function(msg) {
			   }
			});			
		});
		
		var idPriorityList = div + '_priorityList';
		var idPriorityListDialog = div + '_priorityListDialog';
		$('#' + div).append('<button id="' + idPriorityList + '">Priority List</button>');
		$('#' + div).append('<div id="' + idPriorityListDialog + '"></div>');
		var priorityTable = new DependencyPriority(idPriorityListDialog);
				
		$('#' + idPriorityList).click(function() {
			priorityTable.update();
			$('#' + idPriorityListDialog).dialog('open');
		});	
				
		$('#' + idPriorityListDialog).dialog({
			  autoOpen: false,
			  title: 'Dependency Priority',
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
											
		this.updateIdList = function() {
			$.ajax({
			   type: "GET", 
			   url: me.idPath + "/ids",
	
			   success: function(msg) {
			   		var current = $('#' + me.idsId).val();
			   		$('#' + me.idsId).empty();
			   		for (var i = 0; i < msg.length; i++) {
						var id = msg[i];
						$('#' + me.idsId).append($('<option>').text(id).val(id));
					}

					
					$('#' + me.idsId).val(current);
			   }
			});	
		}
		
		//updateIdList();
		this.updateIdList();
			
		$('#' + div).append('<div id="' + me.rootId + '"></div>');
		this.editor = new DependencySpecEditor(me.rootId, application);
		
		function updateAll() {
			var sel = $('#' + me.idsId).val();
			me.editor.update(sel);
			me.updateLink();
			me.retrievePriority();
		}
		
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
	
	retrievePriority() {
		var me = this;
		var id = $('#' + this.idsId).val();
		$.ajax({
		   type: "GET", 
		   url: me.dependencyPath + "/getPriority?id=" + id,
		   success: function(msg){
		   	$('#' + me.priorityId).val(msg);
		   }
		});		
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
		   url: me.dependencyPath + "/getLinks?id=" + id,
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
	
	rebuild(application) {
		this.dependencyPath = "//" + window.location.host + "/rest/" + application + "/dependencySpec2";
		this.idPath = "//" + window.location.host + "/rest/" + application + "/id2";
		this.editor.path(application);
		this.updateIdList();
	}
}