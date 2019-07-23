class DependencySpecEditor {

	constructor(div) {	
		this.div = div;

		this.valueText = div + '_valueText';

		var me = this;
			
		me.equationEditor = new EquationEditor(div);
		
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
		var boleanFunction = function(col, row, arg) {
			if (arg == 'type') {
				if (col == 'Value') {
					return 'select';
				}
				else if (col == 'Trigger' || col == 'Condition') {
					return 'button';
				}
				else if (col == 'Silent Change') {
					return "select";
				}
			}
			else if (arg == 'options') {
				if (col == 'Value' || col == 'Silent Change') {
					return ['true', 'false'];
				}
			}
			return null;
		}
		var textFunction = function(col, row, arg) {
			if (arg == 'type') {
				return 'button';
			}
			return null;
		}
		
		var textDef = ['Value', 'Condition', 'Min', 'Max', 'ArraySize'];
		var func = boleanFunction;
		if (textDef.includes(elementName)) {
			func = textFunction;
		}
		var divName = this.div + '_' + elementName;
		
		var idCopyButton = this.div + '_copy_' + elementName;
		var idCopySelect = this.div + '_copyselect_' + elementName;
		$('#' + this.div).append('<div id="' + divName + '"><b>' + elementName + '</b><span>..........</span><button id="' + idCopyButton + '" name="' + elementName + '" class="small">Copy From</button><select id="' + idCopySelect + '" class="copySelect"></select></div>');
		$('#' + idCopyButton).click(function() {
			var to = $(this).prop('name');//.replace( me.div + '_copyselect_', ' ');
			var from = $('#' + idCopySelect).val();
	
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec2/copySpec?id=" + me.id + "&from=" + from + "&to=" + to,
			   success: function(msg){
			   	me.update(me.id);
			   }
			});			
		});
		
		var table = new JsMyTable(divName, func);
		
		table.setButtonListener(function(r, c, v) {
			me.currentRow = r;
			me.currentColumn = c;
			me.currentElement = elementName;
			
			$('#' + me.valueText).val(v);
	
			me.equationEditor.show(v, function(value) {
				$('#' + me.valueText).val(/*$('#' + me.valueText).val() +*/ value);	
				me.currentValue = $('#' + me.valueText).val();
				me.commitValue();
			});
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
				for (var i = 0; i < msg.list.length; i++) {
					var val = msg.list[i].element;
					var option = $('<option>').val(val).text(val);
					$('.copySelect').append(option);
				}
		   }
		});	
	}
}