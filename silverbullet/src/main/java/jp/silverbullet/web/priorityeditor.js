class PriorityEditor {
	constructor(divid, url) {
	
		var mainId = divid + "_main";
		$('#' + divid).append('<div class="priorityArea" id="' + mainId + '"></div>');
		
		this.onUpdate = function() {
			buildNew();
		};
		
		buildNew();
		
		function buildNew() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/" + url + "/getDefinedPriorities",
			   success: function(priorities){
					build2(priorities);
			   }
			});		
	
		}
		
		function build2(priorities) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/" + url + "/getPriorities",
			   success: function(msg){
			   	build(priorities, msg);
			   }
			});	
		}
		
		function getTail(text) {
			var s = text.split('_');
			return s[s.length - 1];
		}
		
		function build(priorities, msg) {
			$('#' + mainId).empty();
			var firstIndex = priorities[0];
			var number = msg[firstIndex].value;
			var rowName = "";
			var emptyName = msg[firstIndex].value;
			$('#' + mainId).append('<div class="priorityLabel">High Priority</div>');
			
			emptyName = divid + "row_" + parseInt(Number(priorities[0]) + 10);
			$('#' + mainId).append('<div id="' + emptyName + '" class="priorityEmpty">' + getTail(emptyName) + '</div>');
			
			for (var i = 0; i < priorities.length; i++) {
				var priority = priorities[i];
				rowName = divid + "row_" + priority;
				$('#' + mainId).append('<div id="' + rowName + '" class="priorityRow"><div class="priorityNumber">' + priority + '</div></div>');
				
				if (i < priorities.length -1) {
					emptyName = divid + "row_" + parseInt((Number(priorities[i]) + Number(priorities[i+1]))/2);
					$('#' + mainId).append('<div id="' + emptyName + '" class="priorityEmpty">' + getTail(emptyName) + '</div>');
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
		    	var num = getTail(id);
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
				   url: "//" + window.location.host + "/rest/" + url + "/setPriority?id=" + id + "&value=" + num,
				   success: function(msg){
						buildNew();
				   }
				});	
			}
		}
	}
	
	update() {
		this.onUpdate();
	}
}
