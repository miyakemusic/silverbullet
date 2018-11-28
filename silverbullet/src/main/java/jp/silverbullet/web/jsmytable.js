class JsMyTable {
	constructor(div) {
		this.div = div;
		this.tableId = div + '_table';
		$('#' + div).append('<table id="' + this.tableId + '"><thead></thead><tbody></tbody></table>');
		$('#' + this.tableId).addClass('smalltable');
		$('#' + this.tableId).css("table-layout","fixed");	
		this.listenerRemove = function() {};
	}
	
	setColWidth(width) {
		for (var i = 0; i < width.length; i++) {
			$('#' + this.tableId).append('<colgroup><col style="width:' + width[i] + 'x"></colgroup>');
		}
	}
	
	clear() {
		$('#' + this.tableId + ' > thead').empty();
		$('#' + this.tableId + ' > tbody').empty();
	}
	
	set listenerRemove(listenerRemove) {
		this._listenerRemove = listenerRemove;
	}
	
	get listenerRemove() {
		return this._listenerRemove;
	}
	
	set listenerChange(listenerChange) {
		this._listenerChange = listenerChange;
	}
	
	get listenerChange() {
		return this._listenerChange;
	}
	
	appendRows(rows) {
		var me = this;
		$('#' + me.tableId + ' > thead').append('<tr></tr>');
		$.each(rows[0], function(k, v) {
			$('#' + me.tableId + ' > thead tr:first').append('<td>' + k + '</td>');
		});	
		
	   	for (var row = 0; row < rows.length; row++) {
	   		var spec = rows[row];
	   		this.appendRow(row, spec);
	   	}	
	}
	
	appendRow(row, data) {
		var me = this;
	
		$('#' + this.tableId  + '> tbody').append('<tr></tr>');
		
		$.each(data, function(k, v) {
			if (v == null || v == '') {
				v = '--';
			}
			
			var labelId = me.div + '_L' + row + '_' + k;
			var editId = me.div + '_E' + row + '_' + k;
			
			var label = '<label id="' + labelId + '"></label>';
			var edit = '<input type="text" id="' + editId + '"></input>';
			$('#' + me.tableId + ' > tbody tr:last').append('<td>' + label + edit + '</td>');
			
			$('#' + labelId).text(v);
			$('#' + editId).val(v);
			
			$('#' + labelId).show();
			$('#' + editId).hide();
			
			$('#' + labelId).click(function() {
				$('#' + labelId).hide();
				$('#' + editId).show();			
			});
			$('#' + editId).keydown(function(event) {
				if (event.altKey) {
					if (event.which == 13) {
						$('#' + editId).val($('#' + editId).val() + '\n');
					}
				} 
				else if (event.which == 13) { // Enter
					$('#' + labelId).show();
					$('#' + editId).hide();
					$('#' + labelId).text($('#' + editId).val().replace('\n','<br>'));
					me.listenerChange(row, k, $('#' + editId).val());
				}
				else if (event.which == 27) { // Cancel
					$('#' + labelId).show();
					$('#' + editId).hide();
				}
			});	
			$('#' + editId).focusout(function() {
				$('#' + labelId).show();
				$('#' + editId).hide();
			});	
		});		
			
		var removeId = this.div + '_R' + row;
		$('#' + me.tableId + ' > tbody tr:last').append('<td><button id="' + removeId + '">Remove</button></td>');	
		$('#' + removeId).click(function() {
			me.listenerRemove(row);
		});
	}
	
}