class DependencySpec {	
		
	constructor(id, base, callback) {
		this.id = id;
		this.table = base + 'depTable';
		this.callback = callback;
		
		$('#' + base).append('<div id="' + this.table + '"></div>');
		this.update();
	}
	
	update() {
		this.createTable();
		this.createTable();
	}
	
	add(element, value, condition) {
		if (element == null) return;
		var obj = new Object();
		obj.element = element;
		obj.value = value;
		obj.condition = condition;
		obj.confirmation = "false";
		
		this.data.push(obj);
		this.updateTable();
	}
	
	createTable() {
		var me = this;

		$('#' + this.table).handsontable({
		  width: 1000,
		  height: 200,
		  manualColumnResize: true,
		  startRows: 10,
		  startCols: 10,
		  colHeaders: ['Element', 'Value', 'Condition', 'Confirmation'],
		  rowHeaders: true,
	//	  minSpareRows: 1,
		  colWidths: function(index) {
		        return [100, 100, 500, 100, 100, 200, 200, 200, 200][index];
		  },
		  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel) {
		  	var element = $("#" + me.table).handsontable('getInstance').getDataAtCell(r, 0);
		  	var value = $("#" + me.table).handsontable('getInstance').getDataAtCell(r, 1);
		  	var condition = $("#" + me.table).handsontable('getInstance').getDataAtCell(r, 2);
		  	var confirmation = $("#" + me.table).handsontable('getInstance').getDataAtCell(r, 3);
		  	
		  	me.callback(element, value, condition, confirmation);
	      },
	      afterChange: function(change, source) {
    	     if (source === 'loadData') {
                 return;
             }
    	  }
		});
						
		$.ajax({
			type: "GET", 
			url: "http://" + window.location.host + "/rest/dependencySpec/specTable?id=" + me.id,
			success: function(msg) {
				me.data = msg;
				me.updateTable();
			}
		});
	}
	
	updateTable() {
		var me = this;
		$('#' + me.table).handsontable("loadData", me.data);
		$('#' + me.table).handsontable('getInstance').render();
	}
}