class IdEditorClass {    
    constructor(div) {
    	var prefix = div + 'ideditorclass';
        	
    	var idAdd = prefix + "add";
    	var idRemove = prefix + "remove";
    	var idUpdate = prefix + "_update";
    	var idPropType = prefix + "propType";
    	var idCurrentId = prefix + "currentid";
    	var idTable = prefix + "table";
    	var idAddChoice = prefix + "addChoice";
    	var idSubTable = prefix + "subTable";
    	
    	$('#' + div).append('<Button id="' + idAdd + '">Add</Button>');
    	$('#' + div).append('<Button id="' + idRemove + '">Remove</Button>');
    	$('#' + div).append('<Button id="' + idUpdate + '">Update</Button>');
    	
		$('#' + div).append('<select id="' + idPropType + '"></select>');
		$('#' + div).append('<div id="' + idCurrentId + '"></div>');
		$('#' + idCurrentId).text('ID');
		
		$('#' + div).append('<div id="' + idTable + '"></div>');
  	 	$('#' + idTable).resizable(true);
   	
		$('#' + div).append('<Button id="' + idAddChoice + '">Add</Button>');
		$('#' + div).append('<div id="' + idSubTable + '"></div>');

		var selectionMap = new Map();
		var selectionId;
		var currentId;
		var types;
		var headers;
		var currentType;
		var currentSelections;
		
		var me = this;

		new MyWebSocket(function(msg) {
			var command = msg.split(':')[0];
			var id = msg.split(':')[1];
			
			if (command == 'Remove') {
				id = msg.split(':')[2];
				currentId = id;
			}
			
			if (command == 'Change' || command == 'Add' || command == 'Remove') {
				updateMainTable();
				
				if (id != '') {
					collectSelections(id, function(message) {
						updateSelectionTable(currentId);
					});
				}
			}
		}
		, 'ID');
			
		$("#" + idUpdate).on('click', function() {
			updateMainTable();
		});
		
		$("#" + idAdd).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/addNew?type=" + $("#" + idPropType).val(),
			   success: function(msg){
			   }
			});		
		});
		$("#" + idRemove).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/remove?id=" + currentId,
			   success: function(msg){
			   }
			});		
		});
				
		$("#" + idAddChoice).on('click', function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/addChoice?id=" + currentId,
			   success: function(msg){

			   },
			   error: function() {
			   
			   }
			});		
		});
		
		$.ajax({
		   type: "GET", 
		   url: "//" + window.location.host + "/rest/id2/typeNames",
		   success: function(msg){		
				var v = "";
				
				types = msg;
				
				$('#' + idPropType + ' > option').remove();
				$('#' + idPropType).change(function() {
				    updateMainTable();
				});

				for (var key in msg) {
					$('#' + idPropType).append($('<option>').html(msg[key]).val(msg[key]));
				}
				types.splice(0, 1);
				updateMainTable();
		   }
		});	
			
		function collectSelections(id, callback) {
			var me = this;
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/id2/selection?id=" + id,
				success: function(msg) {
					selectionMap[id] = msg;
					if (callback != null) {
						callback('success');
					}
				}
			});
		};
		
		function updateMainTable() {
			var propType = $("#" + idPropType).val();
			var me = this;
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/properties?type=" + propType,
			   success: function(msg){
			   		headers = msg.header;	
			   		
					$("#" + idTable).handsontable({
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
					  	currentId = getId(r);
					  	$("#" + idCurrentId).text("ID: " + currentId);
					  	
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
				
						for (var key in msg.options) {
							if (headers[col] == key) {
								var list = msg.options[key];
								cellProperties.type = 'dropdown';
				            	cellProperties.source = list;
							}
						}
	
						if (headers[col] == 'DefaultID') {
							cellProperties.type = 'dropdown';
							cellProperties.source = getSelections(row);
						}
				        return cellProperties;
				      }
					});
					$("#" + idTable).handsontable("loadData", msg.table);
			   }
			});	
		}
	
		function updateSelectionTable(id) {
			if (currentType != 'List') {
				$("#" + idSubTable).handsontable('clear');
				return;
			}
			
			var me = this;
			collectSelections(id, function() {
				updateSelectionTable_sub(id);
			});
		}
		
		function updateSelectionTable_sub(id) {
			var me = this;
			var msg = selectionMap[id];
			if (msg == null)return;
			
			$("#" + idSubTable).handsontable({
			  height: 400,
			  manualColumnResize: true,
			  startRows: 10,
			  startCols: 10,
			  colHeaders: msg.header, 
			  colWidths: function(index) {
			        return msg.widths[index];
			  },
			  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel){
				selectionId = $("#" + idSubTable).handsontable('getInstance').getDataAtCell(r, 1)
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
	    		 changeSelection(currentId, selectionId, paramName, newValue);
	    	  }
			});
			currentSelections = msg;
			$("#" + idSubTable).handsontable("loadData", msg.table);
		}
			
		function updateValue(id, paramName, value) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/update?id=" + id + "&paramName=" + paramName + "&value=" + value,
			   success: function(msg){
			   }
			});			
		}
			
		function changeSelection(id, selectionId, paramName, value) {
			var me = this;
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/id2/updateChoice?id=" + id + "&selectionId=" + selectionId + "&paramName=" + paramName + "&value=" + value,
			   success: function(msg){
			   		updateSelectionTable(currentId);
			   		updateSelectionTable(currentId);
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
			var id = getId(row);
			if (id == null)return;
			var options = selectionMap[id];
			if (options == null)return;
			var ret = [];
			for (var r of options.table) {
				ret.push(r[1]);
			}
			return ret;
		}
		
		function getParamName(col) {
			return headers[col];
		}	
		
		this.setSelectionId = function(id) {
			selectionId = id;
		};
		
		this.getSelectionId = function() {
			return selectionId;
		}
		
		this.setCurrentId = function(id) {
			currentId = id;
		}
		
		this.getCurrentId = function() {
			return currentId;
		}
		
		this.update = function() {
			selectionId = "";
			currentId = "";
			updateMainTable();		
		}
		
    	this.rebuilder = function(applicaton) {
			updateMainTable();
    	}
	}

	rebuild(applicaton) {
		this.rebuilder(applicaton);
	}
	
}

