class IdEditorClass {    
    constructor(div) {
    	var prefix = 'ideditorclass';
    	
    	var idAdd = prefix + "add";
    	var idPropType = prefix + "propType";
    	var idCurrentId = prefix + "currentid";
    	var idTable = prefix + "table";
    	var idAddChoice = prefix + "addChoice";
    	var idSubTable = prefix + "subTable";
    	
    	$('#' + div).append('<Button id="' + idAdd + '">Add</Button>');
    	
		$('#' + div).append('<select id="' + idPropType + '"></select>');
		$('#' + div).append('<div id="' + idCurrentId + '"></div>');
		$('#' + idCurrentId).text('ID');
		
		$('#' + div).append('<div id="' + idTable + '"></div>');

		$('#' + div).append('<Button id="' + idAddChoice + '">Add</Button>');
		$('#' + div).append('<div id="' + idSubTable + '"></div>');

		var me = this;

		$("#" + idAdd).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addNew?type=" + $("#" + idPropType).val(),
			   success: function(msg){
					updateMainTable();
			   }
			});		
		});
		
		$("#" + idAddChoice).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addChoice?id=" + me.currentId,
			   success: function(msg){
					updateSelectionTable(me.currentId);
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
				
				$('#' + idPropType + ' > option').remove();
				$('#' + idPropType).change(function() {
				    updateMainTable();
				});

				for (var key in msg) {
					$('#' + idPropType).append($('<option>').html(msg[key]).val(msg[key]));
				}
				me.types.splice(0, 1);
				updateMainTable();
		   }
		});
						
		function updateMainTable() {
			var propType = $("#" + idPropType).val();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/properties?type=" + propType,
			   success: function(msg){
			   		me.headers = msg.header;	
			   		
					$("#" + idTable).handsontable({
	//				  width: 1200,
					  height: 400,
					  manualColumnResize: true,
					  startRows: 10,
					  startCols: 10,
//					  rowHeaders: true,
					  colHeaders: msg.header,
					  minSpareRows: 1,
					  currentRowClassName: 'currentRow',
					  contextMenu: true,
					  colWidths: function(index) {
					        return [40, 200, 100, 200, 200, 100, 200, 200, 200, 200][index];
					  },
					  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
					  	me.currentId = getId(r);
					  	$("#" + idCurrentId).text(me.currentId);
					  	
					  	me.currentType = getType(r);
						updateSelectionTable(getId(r));
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
                		 
						 updateValue(me.currentId, getParamName(colNumber), newValue);
                      },
	      		      cells: function (row, col, prop) { 	
				        var cellProperties = {};
				
				        if (me.headers[col] === 'type') {
				            cellProperties.type = 'dropdown';
				            cellProperties.source = me.types;
				        }
						else if (me.headers[col] == 'defaultKey') {
							cellProperties.type = 'dropdown';
							cellProperties.source = getSelections(row);
						}
				        return cellProperties;
				      }
	//				  fillHandle: true //possible values: true, false, "horizontal", "vertical"
					});
					$("#" + idTable).handsontable("loadData", msg.table);
			   }
			});	
		}
			
		function updateSelectionTable(id) {
			if (me.currentType != 'ListProperty') {
				$("#" + idAddChoice).hide();
				$("#" + idSubTable).hide();
				return;
			}
			$("#" + idAddChoice).show();
			$("#" + idSubTable).show();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/selection?id=" + id,
			   success: function(msg){
					$("#" + idSubTable).handsontable({
	//				  width: 1200,
					  height: 400,
					  manualColumnResize: true,
					  startRows: 10,
					  startCols: 10,
					  colHeaders: ['ID', 'Comment', 'Caption'],
					  rowHeaders: true,
					  minSpareRows: 1,
					  colWidths: function(index) {
					        return [200, 100, 200, 200, 100, 200, 200, 200, 200][index];
					  },
					  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
						me.selectionId = $("#" + idSubTable).handsontable('getInstance').getDataAtCell(r, 0)
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
                		 changeSelection(me.currentId, me.selectionId, colNumber, newValue);
                	  }
	//				  fillHandle: true //possible values: true, false, "horizontal", "vertical"
					});
					me.currentSelections = msg;
					$("#" + idSubTable).handsontable("loadData", msg);
			   }
			});	
		}
		
		function updateValue(id, paramName, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/update?id=" + id + "&paramName=" + paramName + "&value=" + value,
			   success: function(msg){
			   }
			});			
		}
		
		function changeSelection(id, selectionId, paramName, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/updateChoice?id=" + id + "&selectionId=" + selectionId + "&paramName=" + paramName + "&value=" + value,
			   success: function(msg){
			   		updateSelectionTable(me.currentId);
			   }
			});			
		}
		
		function getId(row) {
			var id = $("#" + idTable).handsontable('getInstance').getDataAtCell(row, 1);
			return id;
		}

		function getType(row) {
			var id = $("#" + idTable).handsontable('getInstance').getDataAtCell(row, 2);
			return id;
		}
				
		function getSelections(row) {
			if (me.currentSelections == null) {
				return;
			}
			var arr = [];
			for (var i = 0; i < me.currentSelections.length; i++) {
				arr.push(me.currentSelections[i].id);
			}
			return arr;
		}
		
		function getParamName(col) {
			return me.headers[col];
		}
				
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
}