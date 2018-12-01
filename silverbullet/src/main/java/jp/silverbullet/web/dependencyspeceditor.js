class DependencySpecEditor {

	constructor(div) {	
		this.div = div;
		var idButton = div + '_button';
		var idSelectorDialog = div + '_idSelectorDialog';
		var idSelectorDialogContent = div + '_idSelectorDialogContent';
		
		this.depDialog = div + '_depDialog';
		this.valueText = div + '_valueText';

		var me = this;
					
		$('#' + div).append('<div id="' + idSelectorDialog + '">' +
				'<div id="' + idSelectorDialogContent + '"></div>' + 
			'</div>');
		$('#' + idSelectorDialog).dialog({
			autoOpen:false,
		});
		
		$('#' + div).append('<div id="' + this.depDialog + '"></div>');
		var idSelector = div + '_idSelector';
		var choiceSelector = div + '_choiceSelector';
		
		
		$('#' + this.depDialog).append('<div id="newSpecDiv">' + 
			'<textarea id="' + me.valueText + '" rows=5 cols=100></textarea>' +
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
			'	<button id="' + idSelector + '">ID Selector</button>' +
			'	<button id="' + choiceSelector + '">Choice Selector</button>' +
			'</div>');		
		$('.copyValue').on('click', function(e){
			var curValue = $('#' + me.valueText).val();
			$('#' + me.valueText).val(curValue + $(this).val()); 
		});
		
		$("#" + idSelector).on('click', function(e) {
			showIdSelectDialog(function(id, subid) {
				var text;
				if (subid != "") {
					text = '$' + id + '==' + '%' + subid;
				}
				else {
					text = '$' + id;
				}
				$('#' + me.valueText).val($('#' + me.valueText).val() + text);			
			});
		});		
		
		$("#" + choiceSelector).on('click', function(e) {
			$('#choiceDialog').dialog("open");
		});

		$('#' + div).append('<div id="' + choiceDialog + '">' +
			'<select id="choice"></select>' +
		'</div>');
			
		$('#' + this.depDialog).dialog({
			  autoOpen: false,
			  title: 'Dependency Editor',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			        me.currentValue = $('#' + me.valueText).val();
					me.commitValue();
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 800,
			height: 600
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
	
	commitValue() {
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/updateSpec?id=" + me.id + 
		   	'&element=' + me.currentElement + '&row=' + me.currentRow + '&col=' + me.currentColumn + '&value=' + me.currentValue,
		   success: function(msg){
				me.update(me.id);
		   }
		});				
	}
		
	createTable(elementName, data) {
		var me = this;
		var boleanFunction = function(col, arg) {
			if (arg == 'type') {
				if (col == 'Value') {
					return 'select';
				}
				else if (col == 'Trigger' || col == 'Condition') {
					return 'button';
				}
			}
			else if (arg == 'options') {
				if (col == 'Value') {
					return ['True', 'False'];
				}
			}
			return '';
		}
		var textFunction = function(col, arg) {
			if (arg == 'type') {
				return 'button';
			}
			return '';
		}
		
		var textDef = ['Value', 'Condition', 'Min', 'Max'];
		var func = boleanFunction;
		if (textDef.includes(elementName)) {
			func = textFunction;
		}
		var divName = this.div + '_' + elementName;
		$('#' + this.div).append('<div id="' + divName + '"><b>' + elementName + '</b></div>');
		
		var table = new JsMyTable(divName, func);
		
		table.setButtonListener(function(r, c, v) {
			me.currentRow = r;
			me.currentColumn = c;
			me.currentElement = elementName;
			
			$('#' + me.valueText).val(v);
			$('#' + me.depDialog).dialog("open");
		});
		table.setSelectListener(function(r, c, v) {
			me.currentRow = r;
			me.currentColumn = c;
			me.currentElement = elementName;
			me.currentValue = v;			
			me.commitValue();
		});			
		table.setColWidth([100, 100, 100, 30]);
		
		table.appendRows(data);
	}

	update(id) {
		$('#' + this.div).empty();
		
		this.id = id;
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/dependencySpec2/getSpec?id=" + id,
		   success: function(msg){
		   
		   		for (var i = 0; i < msg.list.length; i++) {
		   			me.createTable(msg.list[i].element, msg.list[i].rows);
		   		}
				
		   }
		});	
	}
}