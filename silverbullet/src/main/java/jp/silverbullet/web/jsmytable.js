

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
		$('#' + this.tableId).css('overflow-x', 'scrolll');
		$('#' + this.tableId).css('word-wrap', 'break-word');
//		$('#' + this.tableId).css('width', '1200px');
		
		if (className == null) {
			$('#' + this.tableId).addClass('smalltable');
		}
		else {
			$('#' + this.tableId).addClass(className);
		}
		$('#' + this.tableId).css("table-layout","fixed");	
		this.listenerRemove = function() {};
		
		this.allChecks = [];
		this.allTexts = new Map();
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
	
	set selectListener(selectListener) {
		this._selectListener = selectListener;
	}
	
	get selectListener() {
		return this._selectListener;
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
		
		this.allChecks = [];
		this.allTexts.clear();
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
		
		function createSelect(tdId, k, row, v) {
			var listId = 'list_' + tdId;
			$('#' + tdId).append('<select id="' + listId + '"></select>');
			var options = me.colDef(k, row, 'options');
			for (var i = 0; i < options.length; i++) {
				$('#' + listId).append($('<option>').html(options[i]).val(options[i]));
			}
			
			if (v == null) {
				v = me.colDef(k, row, 'text');
			}
			
			$('#' + listId).val(v);
			$('#' + listId).change(function() {
				if (me.selectListener != null) {
					me.selectListener(row, k, $(this).val());
				}
			});
		}
		
		function createButton(tdId, k, row, v) {
			var buttonId = 'button_' + tdId;
			
			var v2 = me.colDef(k, row, 'button');
			if (v2 != null ) {
				v = v2;
			}
			
//			if (v == null) {
//				v = me.colDef(k, row, 'button');
//			}
			$('#' + tdId).append('<button id="' + buttonId + '"></button>');
			$('#' + buttonId).text(v);
			$('#' + buttonId).css('width', '100%');
			$('#' + buttonId).css('height', '20px');
			$('#' + buttonId).css('text-align', 'left');
			$('#' + buttonId).click(function() {
				if (me.buttonListener != null) {
					me.buttonListener(row, k, $(this).text());
				}
			});		
		}
		
		function createEditableText(tdId, k, row, v) {
			var edit = new EditableText(tdId, v, function(value) {
				me.listenerChange(row, k, value);
			});
			
			var key = new Object();
			key.k = k;
			key.row = row;
			key.id = tdId;
			me.allTexts.set(key, edit);
		}
		
		function createCheckBox(tdId, k, row, v) {
			var checkId = 'check_' + tdId;
			var checkNameId = 'checkName_' + tdId;
			
			var obj = new Object();
			obj.k = k;
			obj.row = row;
			obj.id = checkId;
			me.allChecks.push(obj);
			
			$('#' + tdId).append('<input type="checkbox" id="' + checkId + '"><label id="' + checkNameId + '"></label>');
			
			var checked = me.colDef(k, row, 'checked');
			var checkName = me.colDef(k, row, 'checkname');
			$('#' + checkNameId).text(checkName);
			
			$('#' + checkId).prop('checked', checked);

			$('#' + checkId).click(function() {
				if (me.checkListener != null) {
					me.checkListener(row, k, $(this).prop('checked'));
				}
			});
		}
		
		$.each(data, function(k, v) {
			if (v == null || v == '') {
				v = '';
			}
			
			var tdId = me.div + '_' + row + '_' + k;
			tdId = tdId.replace(/\s/g, '');
			
			$('#' + me.tableId + ' > tbody tr:last').append('<td id="' + tdId + '"></td>');
			
			var types = me.colDef(k, row, 'type');
			
			for (var type of types.split(';')) {
				if (type == 'button') {
					createButton(tdId, k, row, v);
				}
				else if (type == 'select') {
					createSelect(tdId, k, row, v);
				}
				else if (type == 'check') {
					createCheckBox(tdId, k, row, v);
				}	
				else if (type == 'label') {
					$('#' + tdId).append('<label>' + v + '</label>');
				}
				else {	
					createEditableText(tdId, k, row, v);
				}		
			}
/*			
			if (type == 'button') {
				createButton(tdId, k, row, v);
			}
			else if (type == 'select') {
				createSelect(tdId, k, row, v);
			}
			else if (type == 'check') {
				createCheckBox(tdId, k, row, v);
			}
			else if (type == 'text_button' || type == 'select_button') {				
				var td2Id = tdId + "_2";

				$('#' + tdId).append('<div id="' + td2Id + '"></div>');
				
				if (type == 'text_button') {
					createEditableText(td2Id, k, row, v);
				}
				else if (type == 'select_button') {
					createSelect(td2Id, k, row);
				}
				
				createButton(td2Id, k, row);
			}
			else if (type == 'label') {
				$('#' + tdId).append('<label>' + v + '</label>');
			}
			else {	
				createEditableText(tdId, k, row, v);
			}
*/
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
		for (var [key, value] of this.allTexts) {
			var text = this.colDef(key.k, key.row, 'text');
			value.setText(text);
		}
	}
}

class JsMyTable2 {
	constructor(div, rowCount, colCount, header, value, onClick) {
		this.div = div;
		this.getRowCount = rowCount;
		this.getColCount = colCount;
		this.getHeader = header;
		this.getValue = value;
		this.onClick = onClick;
		
		this.tableId = div + "_table";
		$('#' + div).append('<table class="mytable" id="' + this.tableId + '"><thead class="scrollHead"></thead><tbody class="scrollBody"></tbody></table>');
//		$('#' + div).append('<table class="resizable" id="' + this.tableId + '"><thead></thead><tbody></tbody></table>');


//		$('#' + this.tableId).resizableColumns();
//		$('table.resizable').resizableColumns();
		
		$('.scrollBody').css("height", "100%");		
	}
	
	
	buildHeader() {
		$('#' + this.tableId + ' thead').empty();

		$('#' + this.tableId + ' > thead').append($('<tr>'));
		for (var col = 0; col < this.getColCount(); col++) {
			var headerId = this.createCellId(col);//"H" + col;
			$('#' + this.tableId + ' > thead tr:last').append('<th><div id="' +  headerId + '"></div></th>');
		}
	}
	updateHeader() {
		for (var col = 0; col < this.getColCount(); col++) {
			var headerId = this.createCellId(col);//"H" + col;
			$('#' + headerId).text(this.getHeader(col));
		}
	}
	
	createCellId(col, row) {
		return this.div + "R" + row + "C" + col;
	}
	
	buildBody() {
		$('#' + this.tableId + ' tbody').empty();
		
		var tbody = $('#' + this.tableId).find('tbody');

		for (var row = 0; row < this.getRowCount(); row++) {
			var html = '<tr>';
			for (var col = 0; col < this.getColCount(); col++) {
				var id = this.createCellId(col, row);
				html += '<td><label id="' + id + '"></lable></td>';
			}

			html += '</tr>';
			$('#' + this.tableId + ' tbody').append(html);
		}
				
		var me = this;
		$('#' + this.tableId + ' tbody tr').click(function() {
			var rowIndex = $('#' + me.tableId + ' tbody tr').index(this);
			me.onClick(rowIndex, 0);
		});

	}
	
	updateBody() {
		for (var row = 0; row < this.getRowCount(); row++) {
			for (var col = 0; col < this.getColCount(); col++) {
				var id = this.createCellId(col, row);//'R' + row + 'C' + col;
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

		$('.scrollBody').css("height", (tableHeight - headerHeight - 20) + "px");
	}
	
	update() {
		this.updateHeader();
		this.updateBody();
//		$('#' + this.tableId).resizableColumns();
		this.touched = true;
	}
	
	selectRow(row) {
	    $('.selected').removeClass('selected');
        $('#' + this.tableId + ' tbody tr').eq(row).addClass("selected");
	}
}
