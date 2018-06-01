/**
 * 
 */
 
$(function() {	
	$(document).ready(function(){	
		var fromId = "";
		var manager;
		
		$('#depDialog').dialog({
			  autoOpen: false,
			  title: 'jQuery Dialog Demo',
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
			      var text = id;
			      if (subId != "") {
			      	text += "." + subId;
			      }
			      $('#conditionText').val($('#conditionText').val() + text);
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
		
		$("#idSelector").on('click', function(e) {
			manager = new IdTableManager('idTableDep', 'subTableDep', '', '', 'propTypeDep');
			$('#idSelectorDialog').dialog("open");
		});
		
		$('.copyValue').on('click', function(e){
			var curValue = $('#valueText').val();
			$('#valueText').val(curValue + $(this).val()); 
		});
		
		function updateMainTable() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/properties?type=All",
			   success: function(msg){
					for (var index in msg.table) {
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

					$("#mainTable").handsontable({
					  width: 1000,
					  height: 200,
					  manualColumnResize: true,
	//				  startRows: 3,
					  startCols: 3,
					  rowHeaders: true,
					  colHeaders: msg.header,
					  minSpareRows: 1,
					  currentRowClassName: 'currentRow',
					  contextMenu: true,
					  colWidths: function(index) {
					        return [200, 200, 100, 200, 200, 100, 200, 200, 200, 200][index];
					  },
					  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
	      		      },
	      		      beforeChange: function(change, source) {
	      		      },
	      		      afterChange: function(change, source) {
                      },
	      		      cells: function (row, col, prop) { 	
				        var cellProperties = {};
				        return cellProperties;
				      }
					});
					$("#mainTable").handsontable("loadData", msg.table);

										
					$('#triggerId').text(from);
					$('#depDialog').dialog("open");
			   }
			});
			

		}
		
		updateMainTable();
	});
});