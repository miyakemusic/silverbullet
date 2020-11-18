class PriorityEditor {
	constructor(divid, application) {
		this.depDesignPath = "//" + window.location.host + "/rest/" + application + "/dependencyDesign2";
		var me = this;
		
		var mainId = divid + "_main";
		$('#' + divid).append('<div class="priorityArea" id="' + mainId + '"></div>');
		
		this.onUpdate = function(ids) {
			buildNew(ids);
		};
		
		buildNew();
		
		var filterIds = [];
		function buildNew(ids) {
			if (ids != null) {
				filterIds = ids;
			}
			$.ajax({
			   type: "GET", 
			   url: me.depDesignPath + "/getDefinedPriorities",
			   success: function(priorities){
					build2(priorities, filterIds);
			   }
			});		
	
		}
		
		function build2(priorities, ids) {
			$.ajax({
			   type: "GET", 
			   url: me.depDesignPath + "/getPriorities",
			   success: function(msg){
			   	build(priorities, msg, ids);
			   }
			});	
		}
		
		function getTail(text) {
			var s = text.split('_');
			return s[s.length - 1];
		}
		
		function build(priorities, msg, filterIds) {
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
					if (!filterIds.includes(id)) {
						continue;
					}
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
				   url: me.depDesignPath + "/setPriority?id=" + id + "&value=" + num,
				   success: function(msg){
						buildNew();
				   }
				});	
			}
		}
	}
	
	update(ids) {
		this.onUpdate(ids);
	}
	
	path(application) {
		this.depDesignPath = "//" + window.location.host + "/rest/" + application + "/dependencyDesign2";
	}
}
