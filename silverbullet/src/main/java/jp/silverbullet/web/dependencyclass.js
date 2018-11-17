class DependencyClass {
	constructor(div) {
		var idIds = div + 'ids';
		var idAlternative = div + "_alternative";
		
		$('#' + div).append('<div>ID: <select id="' + idIds + '"></select> Type:<select id="' + idAlternative + '">Alternative</select></div>');
		$('#' + idIds).change(function() {
			var id = $('#' + idIds).val();
			diagram.update(id);
			updateMainTable(id);
		});
		
		$('#' + idAlternative).append($('<option>').html('Normal').val('Normal'));
		$('#' + idAlternative).append($('<option>').html('Alternative').val('Alternative'))
		
		$('#' + idAlternative).change(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/switch?type=" + $('#' + idAlternative).val(),
			   success: function(msg){
		
			   }
			});	
		});		
		
		$('#' + div).append('<div id="myDiagramDiv" style="width:1000px; height:300px; background-color: #DAE4E4;"></div>');
		$('#' + div).append('<div id="idListPanel" style="width:1000px; height:100px; background-color: lightBlue;"></div>');
		$('#' + div).append('<div id="specSummary"></div>');
				
		$('#' + div).append('<button id="idCreateNewSpec">Create New Spec</button>');
		$('#idCreateNewSpec').click(function() {
			showIdSelectDialog(function(id, subid) {
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencySpec/createNew?id=" + id,
				   success: function(msg){
						//updateMainTable($('#' + idIds).val());
						updateComboBox();
				   }
				});		
			});
		});

		$('#' + div).append('<div id="depDialog"></div>');
		
		$('#depDialog').append('<div id="targetId"></div>');
		
		$('#depDialog').append('<div id="newSpecDiv">' + 
			'<select id="targetIdElement"></select><br>	' +
			'Value:<BR>' +
			'<select id="valueBoolean"></select>' +
			'<textarea id="valueText" rows=5 cols=100></textarea>' +
			'<br>' +
			'Condition:<BR>' +
			'<textarea id="conditionText" rows=5 cols=100></textarea>' +
			'<br>' +
			'<div>' +
			'	<button class="copyValue" value="true">true</button>' +
			'	<button class="copyValue" value="false">false</button>' +
			'	<button class="copyValue" value=" == ">==</button>' +
			'	<button class="copyValue" value=" > ">></button>' +
			'	<button class="copyValue" value=" >= ">>=</button>' +
			'	<button class="copyValue" value=" < "><</button>' +
			'	<button class="copyValue" value=" <= "><=</button>' +
			'	<button class="copyValue" value=" != ">!=</button>' +
			'	<button class="copyValue" value=" || ">||</button>' +
			'	<button class="copyValue" value=" ( ) ">()</button>' +
			'	<button class="copyValue" value="*any">*any</button>' +
			'	<button class="copyValue" value="*else">*else</button>' +
			'	<button class="copyValue" value="*script">*SCRIPT()</button>' +
			'	<button id="idSelector">ID Selector</button>' +
			'	<button id="choiceSelector">Choice Selector</button>' +
			'</div>');
	
		$('#' + div).append('<div id="idSelectorDialog">' +
				'<div id="idSelectorDialogContent"></div>' + 
			'</div>');

		$('#idSelectorDialog').dialog({
			autoOpen:false,
		});
			
		
		$('#' + div).append('<div id="choiceDialog">' +
			'<select id="choice"></select>' +
		'</div>');
	
		$('#' + div).append('<div id="depListDialog">' +
			'<div id="id"></div>' +
			'<button id="addSpec">Add</button>' +
			'<button id="editSpec">Edit</button>' +
			'<button id="removeSpec">Remove</button>' +
			'<div id="depList"></div>' +
		'</div>');

		
		var idSelector = new IdEditorClass('idSelectorDialogContent');
		
		var diagram = new DependencyDiagram('myDiagramDiv');

		var me = this;
		
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
			      $('#' + me.activeTextArea).val('%' + $('#choice').val());
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
			      if (me.nowEditing) {
			      	editDepSpec();
			      }
			      else {
			      	addDepSpec();
			      }
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 800,
//			height: 600
		});		
				
		$('#valueBoolean').append($('<option>').html('true').val('true'));
		$('#valueBoolean').append($('<option>').html('false').val('false'));
		$('#valueBoolean').hide();
		
		$('#valueText').on('click', function(e) {
			me.activeTextArea = $(this).prop('id');
		});
		
		$('#conditionText').on('click', function(e) {
			me.activeTextArea = $(this).prop('id');
		});
		
		$("#idSelector").on('click', function(e) {
			showIdSelectDialog(function(id, subid) {
				var text;
				if (subid != "") {
					text = '$' + id + ".Value" + '==' + '%' + subid;
				}
				else {
					text = '$' + id + '.Value';
				}
				$('#' + me.activeTextArea).val($('#' + me.activeTextArea).val() + text);			
			});
		});
		
		function showIdSelectDialog(result) {
			$('#idSelectorDialog').dialog({
				autoOpen:false,
				modal:true,
				width: 800,
				height: 600,
				
				buttons: {
					"OK": function(){
						$(this).dialog('close');
						var id = idSelector.currentId;
						var subId = idSelector.selectionId;
						result(id, subId);
				    }
				    ,
				    "Cancel": function(){
				      $(this).dialog('close');
				    }
				},
			});
			$('#idSelectorDialog').dialog("open");
			idSelector.update();
		}
		
		$("#choiceSelector").on('click', function(e) {
			$('#choiceDialog').dialog("open");
		});
		
		$('.copyValue').on('click', function(e){
			var curValue = $('#' + me.activeTextArea).val();
			$('#' + me.activeTextArea).val(curValue + $(this).val()); 
		});
		
		$("#addSpec").on('click', function(e) {
			$('#valueText').val('');
			$('#conditionText').val('');
			
			me.nowEditing = false;
			
			$('#depDialog').dialog("open");
		});
		
		$("#editSpec").on('click', function(e) {
			me.prevValue = me.currentValue;
			me.prevCondition = me.currentCondition;
			me.prevConfirmation = me.currentConfirmation;
			
			me.nowEditing = true;
			
			$('#valueText').val(me.currentValue);
			$('#conditionText').val(me.currentCondition);
			$('#targetIdElement').val(me.currentElement); 
			$('#depDialog').dialog("open");
		});
		
		$("#removeSpec").on('click', function(e) {
			removeSpec();
		});
		
		updateComboBox();
		function updateComboBox() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/ids?id=",
			   success: function(msg) {
			   		$('#' + idIds).empty();	
		   			for (var index in msg.table) {
						var row = msg.table[index];
						$('#' + idIds).append('<option text="' + row[1] + '" val="' + row[1] + '">' + row[1] + '</option>');
					}
					var last = msg.table[msg.table.length-1];
					updateMainTable(last[1]);
			   }
			});
		}
		
		function updateMainTable(selectedId) {
			$('#idListPanel').empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/ids?id=" + selectedId,
			   success: function(msg) {			   	
					for (var index in msg.table) {
						var row = msg.table[index];

						var idPanel = row[1] + "_panel";

						$('#idListPanel').append('<div id = "' + idPanel + '" class="depPane">' + 
							'<input class="small" id="' + row[1] + '" type="button" value="' + row[1] + '"/>' + '<br>'+
							row[0] + '<br>' + row[2] + '</div>');						

						var id = '#' + row[1];
						idPanel = '#' + idPanel;
						
						$(idPanel).click(function() {
							drawLines($(this).prop('id'));
						});
						
						$(id).mousedown(function() {
							me.fromId = $(this).prop('id');
							console.log("from id:" + me.fromId);
						});
						
						$(id).mouseup(function() {
							var toId = $(this).prop('id');
							console.log("to id:" + toId);
							modifySpec(me.fromId, toId);
							me.fromId = "";
						});					
						
						$(id).mousemove(function(e) {
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
										
					me.depList = new DependencySpec(to, 'depList', function(element, value, condition, confirmation) {
						me.currentElement = element;
						me.currentValue = value;
						me.currentCondition = condition;
						me.currentConfirmation = confirmation;
					});
					$('#triggerId').text(from);
					$('#id').text(to);
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
		
		updateMainTable('');
		
		function addDepSpecTmp() {
			var element = $('#targetIdElement').val();
			var value = $('#valueText').val();
			var condition= $('#conditionText').val();
			
			me.depList.add(element, value, condition);
		}
				
		function editDepSpec() {
			var id = $('#targetId').text();
			var element = $('#targetIdElement').val();
			var value = $('#valueText').val();
			var condition= $('#conditionText').val();
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/editSpec?id=" + id + "&element=" + element + 
			   	"&prevValue=" + encode(me.prevValue) + "&prevCondition=" + encode(me.prevCondition) + "&prevConfirmation=" + me.prevConfirmation + 
			   	"&value=" + encode(value) + "&condition=" + encode(condition) + "&confirmation=" + "",
			   success: function(msg){
				me.depList.update();
			   }
			});
		}
		
		function addDepSpec() {
			var id = $('#targetId').text();
			var element = $('#targetIdElement').val();
			var value = $('#valueText').val();
			var condition= $('#conditionText').val();
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/addSpec?id=" + id + "&element=" + element + "&value=" + encode(value) + "&condition=" + encode(condition),
			   success: function(msg){
				me.depList.update();
			   }
			});
		}
		
		function encode(s) {
	//		var ret = encodeURI(s).replace('+', '%2B');
			var ret = encodeURIComponent(s);
			return ret;
		}
		
		function removeSpec() {
			var id = $('#targetId').text();
			var element = me.currentElement;
			var value = me.currentValue;
			var condition = me.currentCondition;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/removeSpec?id=" + id + "&element=" + element + "&value=" + encode(value) + "&condition=" + encode(condition),
			   success: function(msg){
					me.depList.update();
			   }
			});
		}
		
		function drawLines(idPanel) {	
			var id = idPanel.replace('_panel', '').replace('#', '');
			var element = me.currentElement;
			var value = me.currentValue;
			
			diagram.update(id);
			
			updateSummarySpec(id);			
		}
		
		function updateSummarySpec(id) {	
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/relationSpecs?id=" + id,
			   success: function(msg) {
			   		$('#specSummary').empty();
			   		
			   		var index = 0;
			   		for (var id in msg) {
			   			var specs = msg[id];
			   			$('#specSummary').append('<b>' + id + '</b>');
			   			var tableId = 'specSummaryTable' + index;
			   			index++;
			   			
			   			$('#specSummary').append('<table id="' + tableId + '"><thead></thead><tbody></tbody></table>');
			   			$('#' + tableId).addClass('smalltable');
			   			
			   			$('#' + tableId).css("table-layout","fixed");
			   					   			
			   			$('#' + tableId).append('<colgroup><col style="width:30%;"></colgroup>');
						$('#' + tableId).append('<colgroup><col style="width:40%;"></colgroup>');
						$('#' + tableId).append('<colgroup><col style="width:30%;"></colgroup>');
			   			
			   			$('#' + tableId + ' > thead').append('<tr><th>Element</th><th>Value</th><th>Condition</th></tr>');

			   			for (var j = 0; j < specs.length; j++) {
			   				var spec = specs[j];
			   				$('#' + tableId + ' > tbody').append('<tr><td><div>' + spec.element + '</div></td><td><div>' + spec.value + '</div></td><td><div>' + spec.condition + '</div></td></tr>');
			   			}
			   			
			
			   		}
			   }
			});		
		}
	}
}