class DependencyClass2 {

	constructor(div) {	
		var idButton = div + '_button';
		var depDialog = div + '_depDialog';
		var idSelectorDialog = div + '_idSelectorDialog';
		var idSelectorDialogContent = div + '_idSelectorDialogContent';
		
		$('#' + div).append('<button id="' + idButton + '">Dialog</button>');
		
		$('#' + idButton).click(function() {
			$('#' + depDialog).dialog("open");
			//showIdSelectDialog();
		});
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/getSpec?id=" + "ID_STRONG",
		   success: function(msg){
		   
		   		for (var i = 0; i < msg.list.length; i++) {
		   			createTable(msg.list[i].element, msg.list[i].rows);
		   		}
				
		   }
		});	
		
		function createTable(elementName, data) {
			var divName = div + '_' + elementName;
			$('#' + div).append('<div id="' + divName + '"><b>' + elementName + '</b></div>');
			var table = new JsMyTable(divName);
			table.setColWidth([100, 100, 100, 30]);
			table.appendRows(data);
		}
		
		$('#' + div).append('<div id="' + idSelectorDialog + '">' +
				'<div id="' + idSelectorDialogContent + '"></div>' + 
			'</div>');
		$('#' + idSelectorDialog).dialog({
			autoOpen:false,
		});
		
		$('#' + div).append('<div id="' + depDialog + '"></div>');
		$('#' + depDialog).append('<div id="newSpecDiv">' + 
			'<select id="targetIdElement"></select><br>	' +
			'<select id="valueBoolean"></select>' +
			'<textarea id="valueText" rows=5 cols=100></textarea>' +
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
		
		$('#' + depDialog).dialog({
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
				
		var idSelector = new IdEditorClass(idSelectorDialogContent);
		function showIdSelectDialog(result) {
			$('#' + idSelectorDialog).dialog({
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
			$('#' + idSelectorDialog).dialog("open");
			idSelector.update();
		}
	}
}