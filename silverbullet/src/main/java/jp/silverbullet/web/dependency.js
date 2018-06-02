/**
 * 
 */
 
$(function() {	
	$(document).ready(function(){	
		var fromId = "";
		var manager;
		var activeTextArea;
		var map = new Map();
		
		$('#choiceDialog').dialog({
			  autoOpen: false,
			  title: 'Choice',
			  closeOnEscape: false,
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
			width: 300,
			height: 200
		});	
		
		$('#depDialog').dialog({
			  autoOpen: false,
			  title: 'Dependency Editor',
			  closeOnEscape: false,
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
			height: 600
		});		
		
		$('#idSelectorDialog').dialog({
			autoOpen:false,
			modal:true,
			width: 800,
			height: 600,
			
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			      var id = manager.id;
			      var subId = manager.subid;
			      var text;
			      if (subId != "") {
			      	text = '$' + id + ".Value" + '==' + '%' + subId;
			      }
			      else {
			      	text = '$' + id + '.Value';
			      }
			      $('#' + activeTextArea).val($('#' + activeTextArea).val() + text);
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
		});
		
		$('#valueBoolean').append($('<option>').html('true').val('true'));
		$('#valueBoolean').append($('<option>').html('false').val('false'));
		$('#valueBoolean').hide();
		
		$('#valueText').on('click', function(e) {
			activeTextArea = $(this).prop('id');
		});
		
		$('#conditionText').on('click', function(e) {
			activeTextArea = $(this).prop('id');
		});
		
		$("#idSelector").on('click', function(e) {
			manager = new IdTableManager('idTableDep', 'subTableDep', '', '', 'propTypeDep');
			$('#idSelectorDialog').dialog("open");
		});
		
		$("#choiceSelector").on('click', function(e) {
			$('#choiceDialog').dialog("open");
		});
		
		$('.copyValue').on('click', function(e){
			var curValue = $('#' + activeTextArea).val();
			$('#' + activeTextArea).val(curValue + $(this).val()); 
		});
		
		function updateMainTable() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/properties?type=All",
			   success: function(msg){
					for (var index in msg.table) {
						map.set(msg.table[index][1], msg.table[index]);
						var row = msg.table[index];
						$('#panel').append('<div id = "' + row[1] + "_panel" + '" class="kacomaru"><input class="button" id="' + row[1] + '" type="button" value="' + row[1] + '"/><br>' + 
							row[2] + '<br>' + row[4] + '<br>' + row[3] + '</div>');
						
						var id = '#' + row[1];
						$('#' + row[1] + "_panel").draggable();
						$(id).draggable();
						$(id).hover(
							function(){
								$(this).append('<div id="dragging">Changes</div>');
								$('#dragging').draggable();
							},
							function(){
								$('#dragging').remove();
							}
						);
						
						$(id).mousedown(function() {
							fromId = $(this).prop('id');
							console.log("from id:" + fromId);
				//			$(this).css("background-color", "green");
						});
						
						$(id).mouseup(function() {
							var toId = $(this).prop('id');
							console.log("to id:" + toId);
							modifySpec(fromId, toId);
							
				//			$('#' + fromId).css("background-color", "lightgray");
				//			$('#' + toId).css("background-color", "lightgray");
							fromId = "";
						});					
						
						$(id).mousemove(function(e) {
	//						$('#dragging').x(e.clientX).y(e.clientY);
							
						});
						
						$(id).mouseover(function() {
							if (fromId != "") {
				//				$(this).css("background-color", "yellow");
							}
						});	
					}
			   }
			});	
		}
		
		function modifySpec(from, to) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/elements?id=" + to,
			   success: function(msg){
			   		$('#targetId').text(to);
			   		
			   		$('#targetIdElement > option').remove();
			   		for (var i in msg.table) {
			   			$('#targetIdElement').append($('<option>').html(msg.table[i][0]).val(msg.table[i][0]));
			   		}
										
					$('#triggerId').text(from);
					$('#depDialog').dialog("open");
			   }
			});
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/selection?id=" + to,
			   success: function(msg){
					$('#choice' + ' > option').remove();
					for (var i in msg) {
						var val = msg[i].id;
						$('#choice').append($('<option>').html(val).val(val));
					}
			   }
			});
		}
		
		updateMainTable();
	});
});