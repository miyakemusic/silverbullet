class PriorityEditor {
	constructor(divid) {
	
		var mainId = divid + "_main";
		$('#' + divid).append('<div class="priorityArea" id="' + mainId + '"></div>');
		
		buildNew();
		
		function buildNew() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getDefinedPriorities",
			   success: function(priorities){
					build2(priorities);
			   }
			});		
	
		}
		
		function build2(priorities) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/dependencyDesign/getPriorities",
			   success: function(msg){
			   	build(priorities, msg);
			   }
			});	
		}
		
		function build(priorities, msg) {
			$('#' + mainId).empty();
			var number = msg[0].value;
			var rowName = "";
			var emptyName = msg[0].value;
			$('#' + mainId).append('<div class="priorityLabel">High Priority</div>');
			for (var i = 0; i < priorities.length; i++) {
				var priority = priorities[i];
				rowName = "row_" + priority;
				$('#' + mainId).append('<div id="' + rowName + '" class="priorityRow"><div class="priorityNumber">' + priority + '</div></div>');
				
				if (i < priorities.length -1) {
					emptyName = "row_" + parseInt((Number(priorities[i]) + Number(priorities[i+1]))/2);
					$('#' + mainId).append('<div id="' + emptyName + '" class="priorityEmpty">' + emptyName.split('_')[1] + '</div>');
				}

				var ids = msg[priority];
				
				for (var id of ids) {
					$('#' + rowName).append('<div class="priorityItem" id="' + id + '">' + id + '</div>');
				}
			}
			$('#' + mainId).append('<div class="priorityLabel">Low Priority</div>');
			
			$('.priorityItem').draggable();
			
			var drop = function( event, ui ) {
		    	var id = $(this).prop('id');
		    	var num = id.split('_')[1];
		    	var source = ui.draggable[0].id;
		    	setPriority(source, num);
		    }	
			$('.priorityRow').droppable({
				drop: drop
			});
			$('.priorityEmpty').droppable({
				drop: drop
			});
			
			function setPriority(id, num) {
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/dependencyDesign/setPriority?id=" + id + "&value=" + num,
				   success: function(msg){
						buildNew();
				   }
				});	
			}
		}
	}
}
