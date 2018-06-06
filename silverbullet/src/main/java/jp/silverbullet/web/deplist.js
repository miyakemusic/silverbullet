class DependencySpec {	
		
	constructor(id, table, callback) {
		this.createTable(id, table, callback);
		this.createTable(id, table, callback);
	}
	
	update() {
		this.createTable(this.id, this.table, this.callback);
	}
	
	createTable(id, table, callback) {
		this.id = id;
		this.table = table;
		this.callback = callback;
		
		$.ajax({
			type: "GET", 
			url: "http://" + window.location.host + "/rest/dependencySpec/specTable?id=" + id,
			success: function(msg) {
					$('#' + table).handsontable({
					  width: 1000,
					  height: 200,
					  manualColumnResize: true,
					  startRows: 10,
					  startCols: 10,
					  colHeaders: ['Element', 'Value', 'Condition', 'Confirmation'],
					  rowHeaders: true,
					  minSpareRows: 1,
					  colWidths: function(index) {
					        return [100, 100, 500, 100, 100, 200, 200, 200, 200][index];
					  },
					  afterSelection: function(r, c, r2, c2, preventScrolling, selectionLayerLevel) {
					  	var element = $("#" + table).handsontable('getInstance').getDataAtCell(r, 0);
					  	var value = $("#" + table).handsontable('getInstance').getDataAtCell(r, 1);
					  	var condition = $("#" + table).handsontable('getInstance').getDataAtCell(r, 2);
					  	callback(element, value, condition);
	      		      },
	      		      afterChange: function(change, source) {
                	     if (source === 'loadData') {
                             return;
                         }
                	  }
					});
					$('#' + table).handsontable("loadData", msg);
					$('#' + table).handsontable('getInstance').render();
			}
		});
	}

	constructor2(id, table) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencySpec/spec?id=" + id,
			   success: function(msg) {
			   	for (var targetElement in msg.depExpHolderMap) {
			   		var expressionHolderMap = msg.depExpHolderMap[targetElement].dependencyExpressionHolderMap;
			   		for (var selection in expressionHolderMap) {
			   			for (var iExpressionHolder in expressionHolderMap[selection]) {
			   				var expressionHolders = expressionHolderMap[selection][iExpressionHolder];
			   				for (var iExpressionHolder in expressionHolders) {
			   					var targetElemenet = expressionHolders[iExpressionHolder].targetElement;
			   					var settingDisabledBehavior = expressionHolders[iExpressionHolder].settingDisabledBehavior;
			 
			   					for (var value in expressionHolders[iExpressionHolder].expressions) {
			   						var expressions = expressionHolders[iExpressionHolder].expressions[value];
			   						for (var n in expressions) {
			   							var expression = expressions[n];
			   							for (var o in expression) {
			   								var obj = expression[o];
			   								var expressionText = obj.expression;
			   								var comfirm = obj.confirmRequired;
			   								console.log(expressionText);
			   							}
			   							
			   						}
			   						
			   					}
			   				}
			   				
	
			   				
			   			}
			   			
			   		}
			   	}
			   }
			});
	}
}