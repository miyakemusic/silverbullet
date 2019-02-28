class EditableText {
	constructor(div, value, changed) {
		var labelId = 'label_' + div;
		var editId = 'edit_' + div;
		
		$('#' + div).append('<label id="' + labelId + '"></label>');
		$('#' + div).append('<input type="text" id="' + editId + '">');
		
		$('#' + editId).hide();
		
		$('#' + labelId).text(value);
		$('#' + editId).val(value);
		
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
				changed($('#' + editId).val());
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
	}
}

class JsMyTable {
	constructor(div, colDef) {
		if (colDef == null) {
			colDef = function(col, row, arg) {
				return "";
			}
		}
		this.colDef = colDef;
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
	
	setButtonListener(buttonListener) {
		this.buttonListener = buttonListener;
	}
	
	setSelectListener(selectListener) {
		this.selectListener = selectListener;
	}
	
	set checkListener(checkListener) {
		this._checkListener = checkListener;
	}

	get checkListener() {
		return this._checkListener;
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
	
	appendRows(rows, canRemove) {
		var me = this;
		$('#' + me.tableId + ' > thead').append('<tr></tr>');
		$.each(rows[0], function(k, v) {
			$('#' + me.tableId + ' > thead tr:first').append('<td>' + k + '</td>');
		});	
		
	   	for (var row = 0; row < rows.length; row++) {
	   		var spec = rows[row];
	   		this.appendRow(row, spec, canRemove);
	   	}	
	}
	
	appendRow(row, data, canRemove) {
		function getRow(s) {
			var tmp = s.split('_');
			return tmp[tmp.length - 2];
		}
		function getCol(s) {
			var tmp = s.split('_');
			return tmp[tmp.length - 1];
		}
		var me = this;
	
		$('#' + this.tableId  + '> tbody').append('<tr></tr>');
		
		$.each(data, function(k, v) {
			if (v == null || v == '') {
				v = '';
			}
			
			var tdId = me.div + '_' + row + '_' + k;
			var labelId = 'label_' + tdId;
			var editId = 'edit_' + tdId;
			
			$('#' + me.tableId + ' > tbody tr:last').append('<td id="' + tdId + '"></td>');
			
			var type = me.colDef(k, row, 'type');
			if (type == 'button') {
				var buttonId = 'button_' + tdId;
				$('#' + tdId).append('<button id="' + buttonId + '">' + me.colDef(k, row, 'name') + '</button>');
				$('#' + buttonId).text(v);
				
				$('#' + buttonId).click(function() {
					if (me.buttonListener != null) {
						me.buttonListener(getRow(tdId), getCol(tdId), v);
					}
				});
			}
			else if (type == 'select') {
				var listId = 'list_' + tdId;
				$('#' + tdId).append('<select id="' + listId + '"></select>');
				var options = me.colDef(k, row, 'options');
				for (var i = 0; i < options.length; i++) {
					$('#' + listId).append($('<option>').html(options[i]).val(options[i]));
				}
				$('#' + listId).val(v);
				$('#' + listId).change(function() {
					if (me.selectListener != null) {
						me.selectListener(getRow(tdId), getCol(tdId), $(this).val());
					}
				});

			}
			else if (type == 'check') {
				var checkId = 'check_' + tdId;
				var checkNameId = 'checkName_' + tdId;
				$('#' + tdId).append('<input type="checkbox" id="' + checkId + '"><span id="' + checkNameId + '"></span>');
				
				var checked = me.colDef(k, row, 'checked');
				$('#' + checkId).prop('checked', checked);
				new EditableText(checkNameId, me.colDef(k, row, 'name'), function(value) {
					me.listenerChange(row, k, value);
				});
				$('#' + checkId).click(function() {
					if (me.checkListener != null) {
						me.checkListener(k, row, $(this).prop('checked'));
					}
				});
			}
			else {
				$('#' + tdId).append('<label id="' + labelId + '"></label>');
				$('#' + tdId).append('<input type="text" id="' + editId + '"></input>');
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
			}
		});		
			
		if (canRemove != null && canRemove == true) {
			var removeId = this.div + '_R' + row;
			$('#' + me.tableId + ' > tbody tr:last').append('<td><button id="' + removeId + '">Remove</button></td>');	
			$('#' + removeId).click(function() {
				me.listenerRemove(row);
			});
		}
	}
	
}