class IdEditorClass {    
    constructor(div) {
    	var prefix = div + 'ideditorclass';
    	
    	var idAdd = prefix + "add";
    	var idRemove = prefix + "remove";
    	this.idPropType = prefix + "propType";
    	this.idCurrentId = prefix + "currentid";
    	this.idTable = prefix + "table";
    	this.idAddChoice = prefix + "addChoice";
    	this.idSubTable = prefix + "subTable";
    	
    	$('#' + div).append('<Button id="' + idAdd + '">Add</Button>');
    	$('#' + div).append('<Button id="' + idRemove + '">Remove</Button>');
    	
		$('#' + div).append('<select id="' + this.idPropType + '"></select>');
		$('#' + div).append('<div id="' + this.idCurrentId + '"></div>');
		$('#' + this.idCurrentId).text('ID');
		
		$('#' + div).append('<div id="' + this.idTable + '"></div>');

		$('#' + div).append('<Button id="' + this.idAddChoice + '">Add</Button>');
		$('#' + div).append('<div id="' + this.idSubTable + '"></div>');

		var me = this;

		$("#" + idAdd).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addNew?type=" + $("#" + me.idPropType).val(),
			   success: function(msg){
					me.updateMainTable();
			   }
			});		
		});
		$("#" + idRemove).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/remove?id=" + me.currentId,
			   success: function(msg){
					me.updateMainTable();
			   }
			});		
		});
				
		$("#" + this.idAddChoice).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addChoice?id=" + me.currentId,
			   success: function(msg){
					me.updateSelectionTable(me.currentId);
			   },
			   error: function() {
			   
			   }
			});		
		});
		
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id/typeNames",
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
		this.updateMainTable();
	}
	
	updateMainTable() {
		var propType = $("#" + this.idPropType).val();
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id/properties?type=" + propType,
		   success: function(msg){
		   		me.headers = msg.header;	
		   		
				$("#" + me.idTable).handsontable({
				  height: 200,
				  manualColumnResize: true,
				  startRows: 10,
				  startCols: 10,
				  colHeaders: msg.header,
		//		  minSpareRows: 1,
				  currentRowClassName: 'currentRow',
				  contextMenu: true,
				  colWidths: function(index) {
				        return [40, 200, 100, 200, 200, 100, 200, 200, 200, 200][index];
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
			
			        if (me.headers[col] === 'type') {
			            cellProperties.type = 'dropdown';
			            cellProperties.source = me.types;
			        }
					else if (me.headers[col] == 'defaultKey') {
						cellProperties.type = 'dropdown';
						cellProperties.source = me.getSelections(row);
					}
			        return cellProperties;
			      }
				});
				$("#" + me.idTable).handsontable("loadData", msg.table);
		   }
		});	
	}

	updateSelectionTable(id) {
		var me = this;
		
		if (me.currentType != 'ListProperty') {
			$("#" + this.idAddChoice).hide();
			$("#" + this.idSubTable).hide();
			return;
		}
		$("#" + this.idAddChoice).show();
		$("#" + this.idSubTable).show();
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id/selection?id=" + id,
		   success: function(msg){
				$("#" + me.idSubTable).handsontable({
				  height: 400,
				  manualColumnResize: true,
				  startRows: 10,
				  startCols: 10,
				  colHeaders: msg.header, //['ID', 'Comment', 'Caption'],
				  rowHeaders: true,
///				  minSpareRows: 1,
				  colWidths: function(index) {
				        return [200, 100, 200, 200, 100, 200, 200, 200, 200][index];
				  },
				  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
					me.selectionId = $("#" + me.idSubTable).handsontable('getInstance').getDataAtCell(r, 0)
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
            		 me.changeSelection(me.currentId, me.selectionId, paramName, newValue);
            	  }
				});
				me.currentSelections = msg;
				$("#" + me.idSubTable).handsontable("loadData", msg.table);
		   }
		});	
	}
	
	updateValue(id, paramName, value) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id/update?id=" + id + "&paramName=" + paramName + "&value=" + value,
		   success: function(msg){
		   }
		});			
	}
		
	changeSelection(id, selectionId, paramName, value) {
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/id/updateChoice?id=" + id + "&selectionId=" + selectionId + "&paramName=" + paramName + "&value=" + value,
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
		if (this.currentSelections == null) {
			return;
		}
		var arr = [];
		for (var i = 0; i < this.currentSelections.table.length; i++) {
			arr.push(this.currentSelections.table[i][0]);
		}
		return arr;
	}
	
	getParamName(col) {
		return this.headers[col];
	}		
}

