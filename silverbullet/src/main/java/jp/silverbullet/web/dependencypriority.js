class DependencyPriority {

	constructor(div) {
		this.dependencyPath = "//" + window.location.host + "/rest/dependencySpec2";
		
		var idPriorityTable = div + "_priorityTable";
		$('#' + div).append('<div id="' + idPriorityTable + '">Priority Table</div>');
		
		this.table = new JsMyTable(idPriorityTable);
		var me = this;
		this.table.listenerChange = function(r, c, v) {
			var obj = me.data[r];
			$.ajax({
			   type: "GET", 
			   url: this.dependencyPath + "/setPriority?id=" + obj.id + '&priority=' + v,
			   success: function(msg){
			   }
			});	
		};
	}
	
	update() {
		this.table.clear();
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: this.dependencyPath + "/getPriorityList",
		   success: function(msg){
		   	me.data = msg;
		   	me.table.appendRows(msg);
		   }
		});	
	}
}