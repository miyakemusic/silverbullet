class DependencyClass {
	constructor(div) {
	
		$('#' + div).append('<div id="myDiagramDiv" style="width:1000px; height:300px; background-color: #DAE4E4;"></div>');
		$('#' + div).append('<div id="panel" style="width:1000px; height:100px; background-color: lightBlue;"></div>');
		$('#' + div).append('<div id="specSummary"></div>');
				
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
			'	<button class="copyValue" value="==">==</button>' +
			'	<button class="copyValue" value=">">></button>' +
			'	<button class="copyValue" value=">=">>=</button>' +
			'	<button class="copyValue" value="<"><</button>' +
			'	<button class="copyValue" value="<="><=</button>' +
			'	<button class="copyValue" value="!=">!=</button>' +
			'	<button class="copyValue" value="*any">*any</button>' +
			'	<button class="copyValue" value="*else">*else</button>' +
			'	<button class="copyValue" value="*script">*SCRIPT()</button>' +
			'	<button id="idSelector">ID Selector</button>' +
			'	<button id="choiceSelector">Choice Selector</button>' +
			'</div>');
	
		$('#' + div).append('<div id="idSelectorDialog">' +
				'<select id="propTypeDep"></select>' +
				'<div id="idTableDep"></div>' +
				'<div id="subTableDep"></div>' +
			'</div>');
			
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
			      var id = me.manager.id;
			      var subId = me.manager.subid;
			      var text;
			      if (subId != "") {
			      	text = '$' + id + ".Value" + '==' + '%' + subId;
			      }
			      else {
			      	text = '$' + id + '.Value';
			      }
			      $('#' + me.activeTextArea).val($('#' + me.activeTextArea).val() + text);
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
			me.activeTextArea = $(this).prop('id');
		});
		
		$('#conditionText').on('click', function(e) {
			me.activeTextArea = $(this).prop('id');
		});
		
		$("#idSelector").on('click', function(e) {
			me.manager = new IdTableManager('idTableDep', 'subTableDep', '', '', 'propTypeDep');
			$('#idSelectorDialog').dialog("open");
		});
		
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
			$('#depDialog').dialog("open");
		});
		
		$("#editSpec").on('click', function(e) {
			$('#valueText').val(me.currentValue);
			$('#conditionText').val(me.currentCondition);
			$('#depDialog').dialog("open");
		});
		
		$("#removeSpec").on('click', function(e) {
			removeSpec();
		});
		
		function updateMainTable() {
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/ids",
			   success: function(msg) {
					for (var index in msg.table) {
						var row = msg.table[index];
						var idPanel = row[1] + "_panel";

						$('#panel').append('<div id = "' + idPanel + '" class="depPane">' + 
							'<input class="small" id="' + row[1] + '" type="button" value="' + row[1] + '"/>' + '<br>'+
							row[2] + '<br>' + row[4] + '<br>' + row[3] + '</div>');						

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
						
						$(id).mouseover(function() {
							if (me.fromId != "") {
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
										
					me.depList = new DependencySpec(to, 'depList', function(element, value, condition) {
						me.currentElement = element;
						me.currentValue = value;
						me.currentCondition = condition;
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
			   url: "http://" + window.location.host + "/rest/dependencySpec/addSpec?id=" + id + "&element=" + element + "&value=" + encode(value) + "&condition=" + encode(condition),
			   success: function(msg){
				me.depList.update();
			   }
			});
		}
		
		function encode(s) {
			var ret = encodeURI(s).replace('+', '%2B');
			return ret;
		}
		
		function removeSpec() {
			var id = $('#targetId').text();
			var element = me.currentElement;
			var value = me.currentValue;
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/removeSpec?id=" + id + "&element=" + element + "&value=" + encode(value),
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
			   					   			
			   			$('#' + tableId).append('<colgroup><col style="width:10%;"></colgroup>');
						$('#' + tableId).append('<colgroup><col style="width:50%;"></colgroup>');
						$('#' + tableId).append('<colgroup><col style="width:40%;"></colgroup>');
			   			
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