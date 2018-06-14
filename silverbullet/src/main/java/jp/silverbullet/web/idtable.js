class IdTableManager {	
	constructor(idTable, subTable, addButton, addChoiceButton, propertyTypeCombo) {
		var types;
		var headers;
		var currentSelections = [];
		var currentId;
		var currentType;
		var selectionId;
	    var me = this;
	    
	    this.subid = "";
	    
        if (addButton == '') {
        	addButton = "tmp";
        }
       if (addChoiceButton == '') {
       		addChoiceButton = "tmp";	
       }
       
		$("#" + addButton).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addNew?type=" + $("#" + propertyTypeCombo).val(),
			   success: function(msg){
					updateMainTable();
			   }
			});		
		});
		
		$("#" + addChoiceButton).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/addChoice?id=" + currentId,
			   success: function(msg){
					updateSelectionTable(currentId);
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
				
				types = msg;
				
				$('#' + propertyTypeCombo + ' > option').remove();
				$('#' + propertyTypeCombo).change(function() {
				    updateMainTable();
				});

				for (var key in msg) {
					$('#' + propertyTypeCombo).append($('<option>').html(msg[key]).val(msg[key]));
				}
				types.splice(0, 1);
				updateMainTable();
		   }
		});
						
		function updateMainTable() {
			var propType = $("#" + propertyTypeCombo).val();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/properties?type=" + propType,
			   success: function(msg){
			   		headers = msg.header;
			   		
					$("#"+idTable).handsontable({
	//				  width: 1200,
					  height: 400,
					  manualColumnResize: true,
					  startRows: 1,
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
					  	currentId = getId(r);
					  	me.id = currentId;
					  	$("#currentId").text(currentId);
					  	
					  	currentType = getType(r);
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
                		 
						 updateValue(currentId, getParamName(colNumber), newValue);
                      },
	      		      cells: function (row, col, prop) { 	
				        var cellProperties = {};
				
				        if (headers[col] === 'type') {
				            cellProperties.type = 'dropdown';
				            cellProperties.source = types;
				        }
						else if (headers[col] == 'defaultKey') {
							cellProperties.type = 'dropdown';
							cellProperties.source = getSelections(row);
						}
				        return cellProperties;
				      }
	//				  fillHandle: true //possible values: true, false, "horizontal", "vertical"
					});
					$("#"+idTable).handsontable("loadData", msg.table);
			   }
			});	
		}
			
		function updateSelectionTable(id) {
			if (currentType != 'ListProperty') {
				$("#" + addChoiceButton).hide();
				$("#" + subTable).hide();
				return;
			}
			$("#" + addChoiceButton).show();
			$("#" + subTable).show();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/id/selection?id=" + id,
			   success: function(msg){
					$("#" + subTable).handsontable({
	//				  width: 1200,
					  height: 600,
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
						selectionId = $("#" + subTable).handsontable('getInstance').getDataAtCell(r, 0);
						me.subid = selectionId;
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
                		 changeSelection(currentId, selectionId, colNumber, newValue);
                	  }
	//				  fillHandle: true //possible values: true, false, "horizontal", "vertical"
					});
					currentSelections = msg;
					$("#" + subTable).handsontable("loadData", msg);
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
			   		updateSelectionTable(currentId);
			   }
			});			
		}
		
		function getId(row) {
			var id = $("#" + idTable).handsontable('getInstance').getDataAtCell(row, 1);
			return id;
		}

		function getType(row) {
			var id = $("#"+idTable).handsontable('getInstance').getDataAtCell(row, 2);
			return id;
		}
				
		function getSelections(row) {
			var arr = [];
			for (var i in currentSelections) {
				arr.push(currentSelections[i].id);
			}
			return arr;
		}
		
		function getParamName(col) {
			return headers[col];
		}	
	}
	get id() {
		return this._id;
	}
	set id(id) {
		this._id = id;
	}
		
	get subid() {
		return this._subid;
	}
	set subid(subid) {
		this._subid = subid;
	}
}