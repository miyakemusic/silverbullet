/**
 * 
 */
 
$(function() {	
	$(document).ready(function(){	
		var fromId = "";
		var manager;
		var activeTextArea;
		var currentElement;
		var currentValue;
		var currentCondition;
		var depList;

		$('#depListDialog').dialog({
			  autoOpen: false,
			  title: 'Dependency Spec',
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
			width: 1000,
			height: 500
		});	
			
		$('#choiceDialog').dialog({
			  autoOpen: false,
			  title: 'Choice',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			      $('#' + activeTextArea).val('%' + $('#choice').val());
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
			      addDepSpec();
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 800,
//			height: 600
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
		
		$("#addSpec").on('click', function(e) {
			$('#valueText').val('');
			$('#conditionText').val('');
			$('#depDialog').dialog("open");
		});
		
		$("#editSpec").on('click', function(e) {
			$('#valueText').val(currentValue);
			$('#conditionText').val(currentCondition);
			$('#depDialog').dialog("open");
		});
		
		$("#removeSpec").on('click', function(e) {
			removeSpec();
		});
		
		function updateMainTable() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/properties?type=All",
			   success: function(msg) {
					for (var index in msg.table) {
						var row = msg.table[index];
						var idPanel = row[1] + "_panel";
						$('#panel').append('<div id = "' + idPanel + '" class="kacomaru"><input class="button" id="' + row[1] + '" type="button" value="' + row[1] + '"/><br>' + 
							row[2] + '<br>' + row[4] + '<br>' + row[3] + '</div>');
						
						var id = '#' + row[1];
						idPanel = '#' + idPanel;
						
						$(idPanel).draggable();
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
						
						$(idPanel).click(function() {
							drawLines($(this).prop('id'));
						});
						
						$(id).mousedown(function() {
							fromId = $(this).prop('id');
							console.log("from id:" + fromId);
						});
						
						$(id).mouseup(function() {
							var toId = $(this).prop('id');
							console.log("to id:" + toId);
							modifySpec(fromId, toId);
							fromId = "";
						});					
						
						$(id).mousemove(function(e) {
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
										
					depList = new DependencySpec(to, 'depList', function(element, value, condition) {
						currentElement = element;
						currentValue = value;
						currentCondition = condition;
					});
					$('#triggerId').text(from);
					$('#id').text(to);
//					$('#depListDialog').dialog('option', 'title', to);
					$('#depListDialog').dialog("open");	
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
					
		function addDepSpec() {
			var id = $('#targetId').text();
			var element = $('#targetIdElement').val();
			var value = $('#valueText').val();
			var condition= $('#conditionText').val();
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/addSpec?id=" + id + "&element=" + element + "&value=" + value + "&condition=" + condition,
			   success: function(msg){
				depList.update();
			   }
			});
		}
		
		function removeSpec() {
			var id = $('#targetId').text();
			var element = currentElement;
			var value = currentValue;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/removeSpec?id=" + id + "&element=" + element + "&value=" + value,
			   success: function(msg){
					depList.update();
			   }
			});
		}
		
		function drawLines(idPanel) {
			var id = idPanel.replace('_panel', '').replace('#', '');
			var element = currentElement;
			var value = currentValue;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/target?id=" + id,
			   success: function(msg){
			   		$('#message').html('');
			   		var canvas = $('canvas').get(0);
			   		
					if (canvas.getContext){ // 未サポートブラウザでの実行を抑止
					  var ctx = canvas.getContext('2d');
					  	ctx.clearRect(0, 0, 1000, 600);
				   		ctx.beginPath();
	
						for (var i in msg) {
							var from = msg[i].from.id + "(" + msg[i].from.element + ")";
							var to = msg[i].to.id + "(" + msg[i].to.element + ")";
							$('#message').html($('#message').html() + "<BR>" + from + ":" + to);
							
							var pos = $('#' + msg[i].from.id + '_panel').position();
							var width = $('#' + msg[i].from.id + '_panel').width();
							var height = $('#' + msg[i].from.id + '_panel').height();
							
							var pos2 = $('#' + msg[i].to.id + '_panel').position();
							var width2 = $('#' + msg[i].to.id + '_panel').width();
							var height2 = $('#' + msg[i].to.id + '_panel').height();
							
							ctx.moveTo(pos.left + width, pos.top + height/2 );
							ctx.lineTo(pos2.left, pos2.top + height2/2 );
							
							ctx.stroke();
						}
						ctx.closePath();
					}

			   }
			});
		}
		
	});

});