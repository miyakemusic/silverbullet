class IdEditorClass {    
    constructor(div) {
    	var prefix = div + 'ideditorclass';
        	
    	var idAdd = prefix + "add";
    	var idRemove = prefix + "remove";
    	var idUpdate = prefix + "_update";
    	this.idPropType = prefix + "propType";
    	this.idCurrentId = prefix + "currentid";
    	this.idTable = prefix + "table";
    	this.idAddChoice = prefix + "addChoice";
    	this.idSubTable = prefix + "subTable";
    	
    	$('#' + div).append('<Button id="' + idAdd + '">Add</Button>');
    	$('#' + div).append('<Button id="' + idRemove + '">Remove</Button>');
    	$('#' + div).append('<Button id="' + idUpdate + '">Update</Button>');
    	
		$('#' + div).append('<select id="' + this.idPropType + '"></select>');
		$('#' + div).append('<div id="' + this.idCurrentId + '"></div>');
		$('#' + this.idCurrentId).text('ID');
		
		$('#' + div).append('<div id="' + this.idTable + '"></div>');
  	 	$('#' + this.idTable).resizable(true);
   	
		$('#' + div).append('<Button id="' + this.idAddChoice + '">Add</Button>');
		$('#' + div).append('<div id="' + this.idSubTable + '"></div>');

		this.selectionMap = new Map();
		
		var me = this;

		new MyWebSocket(function(msg) {
			var command = msg.split(':')[0];
			var id = msg.split(':')[1];
			
			if (command == 'Remove') {
				id = msg.split(':')[2];
				me.currentId = id;
			}
			
			if (command == 'Change' || command == 'Add' || command == 'Remove') {
				me.updateMainTable();
				me.collectSelections(id, function(message) {
					me.updateSelectionTable(me.currentId);
				});
			}
		}
		, 'ID');
			
		$("#" + idUpdate).on('click', function() {
			me.updateMainTable();
		});
		
		$("#" + idAdd).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id2/addNew?type=" + $("#" + me.idPropType).val(),
			   success: function(msg){
//					me.updateMainTable();
			   }
			});		
		});
		$("#" + idRemove).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id2/remove?id=" + me.currentId,
			   success: function(msg){
//					me.updateMainTable();
			   }
			});		
		});
				
		$("#" + this.idAddChoice).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id2/addChoice?id=" + me.currentId,
			   success: function(msg){

			   },
			   error: function() {
			   
			   }
			});		
		});
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id2/typeNames",
		   success: function(msg){		
				var v = "";
				
				me.types = msg;
				
				$('#' + me.idPropType + ' > option').remove();
				$('#' + me.idPropType).change(function() {
				    me.updateMainTable();
				});

				for (var key in msg) {
					$('#' + me.idPropType).append($('<option>').html(msg[key]).val(msg[key]));
				}
				me.types.splice(0, 1);
				me.updateMainTable();
		   }
		});		
	}
	
	set selectionId(id) {
		this._selectionId = id;
	}
	
	get selectionId() {
		return this._selectionId;
	}
	
	set currentId(id) {
		this._currentId = id;
	}
	
	get currentId() {
		return this._currentId;
	}
	
	update() {
		this.selectionId = "";
		this.currentId = "";
		this.updateMainTable();
	}
	
	collectSelections(id, callback) {
		var me = this;
		$.ajax({
			type: "GET", 
			url: "http://" + window.location.host + "/rest/id2/selection?id=" + id,
			success: function(msg) {
				me.selectionMap[id] = msg;
				if (callback != null) {
					callback('success');
				}
			}
		});
	}
	
	updateMainTable() {
		var propType = $("#" + this.idPropType).val();
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id2/properties?type=" + propType,
		   success: function(msg){
		   		me.headers = msg.header;	
		   		
				$("#" + me.idTable).handsontable({
				  height: 400,
				  manualColumnResize: true,
				  startRows: 10,
				  startCols: 10,
				  colHeaders: msg.header,
				  currentRowClassName: 'currentRow',
				  contextMenu: true,
				  colWidths: function(index) {
				        return msg.widths[index];
				  },
				  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
				  	me.currentId = me.getId(r);
				  	$("#" + me.idCurrentId).text("ID: " + me.currentId);
				  	
				  	me.currentType = me.getType(r);
					me.updateSelectionTable(me.getId(r));
      		      },
      		      beforeChange: function(change, source) {
      		      },
      		      afterChange: function(change, source) {
            	     if (source === 'loadData') {
                         return;
                     }
                     var rowNumber = change[0][0];
            		 var colNumber = change[0][1];
            		 var newValue = change[0][3];
            		 
					 me.updateValue(me.currentId, me.getParamName(colNumber), newValue);
                  },
      		      cells: function (row, col, prop) { 	
			        var cellProperties = {};
			
					for (var key in msg.options) {
						if (me.headers[col] == key) {
							var list = msg.options[key];
							cellProperties.type = 'dropdown';
			            	cellProperties.source = list;
						}
					}

					if (me.headers[col] == 'DefaultID') {
						cellProperties.type = 'dropdown';
						cellProperties.source = me.getSelections(row);
					}
			        return cellProperties;
			      }
				});
				$("#" + me.idTable).handsontable("loadData", msg.table);
				
				for (var row of msg.table) {
					var id = row[1];
					me.collectSelections(id);
				}
		   }
		});	
	}

	updateSelectionTable(id) {
		var me = this;
		var msg = me.selectionMap[id];
		if (msg == null)return;
		
		$("#" + me.idSubTable).handsontable({
		  height: 400,
		  manualColumnResize: true,
		  startRows: 10,
		  startCols: 10,
		  colHeaders: msg.header, 
		  colWidths: function(index) {
		        return msg.widths[index];
		  },
		  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
			me.selectionId = $("#" + me.idSubTable).handsontable('getInstance').getDataAtCell(r, 1)
	      },
	      afterChange: function(change, source) {
    	     if (source === 'loadData') {
                 return;
             }
             var rowNumber = change[0][0];
    		 var colNumber = change[0][1];
    		 var newValue = change[0][3];
    		 var paramName;
    		 if (colNumber == 0) {
    		 	paramName = "id";
    		 }
    		 else if (colNumber == 1) {
    		    paramName = "comment";
    		 }
    		 else if (colNumber == 2) {
    		 	paramName = "title";
    		 }
    		 paramName = msg.header[colNumber];
    		 me.changeSelection(me.currentId, me.selectionId, paramName, newValue);
    	  }
		});
		me.currentSelections = msg;
		$("#" + me.idSubTable).handsontable("loadData", msg.table);
	}
		
	updateValue(id, paramName, value) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id2/update?id=" + id + "&paramName=" + paramName + "&value=" + value,
		   success: function(msg){
		   }
		});			
	}
		
	changeSelection(id, selectionId, paramName, value) {
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id2/updateChoice?id=" + id + "&selectionId=" + selectionId + "&paramName=" + paramName + "&value=" + value,
		   success: function(msg){
		   		me.updateSelectionTable(me.currentId);
		   		me.updateSelectionTable(me.currentId);
		   }
		});			
	}
		
	getId(row) {
		var id = $("#" + this.idTable).handsontable('getInstance').getDataAtCell(row, 1);
		return id;
	}

	getType(row) {
		var id = $("#" + this.idTable).handsontable('getInstance').getDataAtCell(row, 2);
		return id;
	}
				
	getSelections(row) {
		var id = this.getId(row);
		if (id == null)return;
		var options = this.selectionMap[id];
		if (options == null)return;
		var ret = [];
		for (var r of options.table) {
			ret.push(r[1]);
		}
		return ret;
	}
	
	getParamName(col) {
		return this.headers[col];
	}		
}

