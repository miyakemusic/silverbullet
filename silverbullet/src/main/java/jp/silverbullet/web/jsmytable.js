class EditableText {
	constructor(div, value, changed) {
		var labelId = 'label_' + div;
		var editId = 'edit_' + div;
		
		$('#' + div).append('<div class="labelElement" id="' + labelId + '"></div>');

		$('#' + div).append('<input class="editElement" type="text" id="' + editId + '">');
		
		$('#' + editId).hide();
		
		setLabelValue(value);

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
				setLabelValue($('#' + editId).val().replace('\n','<br>'));
				
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
		
		function setLabelValue(value) {
			$('#' + labelId).text(value);
		}
	}
}

class JsMyTable {
	constructor(div, colDef, className) {
		if (colDef == null) {
			colDef = function(col, row, arg) {
				return "";
			}
		}
		this.colDef = colDef;
		this.div = div;
		this.tableId = div + '_table';
		$('#' + div).append('<table id="' + this.tableId + '"><thead></thead><tbody></tbody></table>');
		if (className == null) {
			$('#' + this.tableId).addClass('smalltable');
		}
		else {
			$('#' + this.tableId).addClass(className);
		}
		$('#' + this.tableId).css("table-layout","fixed");	
		this.listenerRemove = function() {};
		
		this.allChecks = [];
	}
	
	clear() {
		$('#' + this.tableId).empty();
	}
	
	valueAt(row, col) {
		return $('#' + this.tableId + ' tr').eq(row + 1).children('td').eq(col).text();
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
				
				var obj = new Object();
				obj.k = k;
				obj.row = row;
				obj.id = checkId;
				me.allChecks.push(obj);
				
				$('#' + tdId).append('<input type="checkbox" id="' + checkId + '"><span id="' + checkNameId + '"></span>');
				
				var checked = me.colDef(k, row, 'checked');
	
				$('#' + checkId).prop('checked', checked);

				$('#' + checkId).click(function() {
					if (me.checkListener != null) {
						me.checkListener(k, row, $(this).prop('checked'));
					}
				});
			}
			else {
				new EditableText(tdId, v, function(value) {
					me.listenerChange(row, k, value);
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
	
	updateData() {
		for (var checkbox of this.allChecks) {
			$('#' + checkbox.id).prop('checked', this.colDef(checkbox.k, checkbox.row, 'checked'));
		}
	}
}

class JsMyTable2 {
	constructor(div, rowCount, colCount, header, value) {
		this.div = div;
		this.getRowCount = rowCount;
		this.getColCount = colCount;
		this.getHeader = header;
		this.getValue = value;
		
		this.tableId = div + "_table";
		$('#' + div).append('<table class="mytable" id="' + this.tableId + '"><thead class="scrollHead"></thead><tbody class="scrollBody"></tbody></table>');

		$('.scrollBody').css("height", "100%");
		
	}
	
	
	buildHeader() {
		$('#' + this.tableId + ' thead').empty();

		$('#' + this.tableId + ' > thead').append($('<tr>'));
		for (var col = 0; col < this.getColCount(); col++) {
			var headerId = "H" + col;
			$('#' + this.tableId + ' > thead tr:last').append('<th><div id="' +  headerId + '"></div></th>');
		}
	}
	updateHeader() {
		for (var col = 0; col < this.getColCount(); col++) {
			var headerId = "H" + col;
			$('#' + headerId).text(this.getHeader(col));
		}
	}
	
	buildBody() {
		$('#' + this.tableId + ' tbody').empty();
		
		var tbody = $('#' + this.tableId).find('tbody');

		for (var row = 0; row < this.getRowCount(); row++) {
			var tr = tbody.append($('<tr>'));
			for (var col = 0; col < this.getColCount(); col++) {
				var id = 'R' + row + 'C' + col;
				tr.append('<td><label id="' + id + '"></lable></td>');
			}
		}
	}
	
	updateBody() {
		for (var row = 0; row < this.getRowCount(); row++) {
			for (var col = 0; col < this.getColCount(); col++) {
				var id = 'R' + row + 'C' + col;
				$('#' + id).text(this.getValue(row, col));
			}
		}		
	}
	
	build() {
		this.buildHeader();
		this.buildBody();
		var width = $('#' + this.tableId).width();
		var headerHeight = $('#' + this.tableId).find('thead').height();
		var bodyHeight = $('#' + this.tableId).find('tbody').height();
		var tableHeight = $('#' + this.div).height();
		
		if (width == 0)return;
		var colWidth = (width -65) / this.getColCount();
		$('#' + this.tableId + ' th,td').css("width", colWidth + "px");
		var bodyHeight = tableHeight - headerHeight;

		$('.scrollBody').css("height", (tableHeight - headerHeight) + "px");
	}
	
	update() {
		this.updateHeader();
		this.updateBody();
				
		this.touched = true;
	}
}
