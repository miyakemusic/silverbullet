class DebugClass {
	constructor(div) {
		this.div = div;
		this.initialize();
	}
	
	initialize() {
		var me = this;
		var map = new Map();
		$('#' + this.div).empty();
		
		$('#' + this.div).append('<div id="debugDiv"></div>');
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/register/getShortCuts",
		   success: function(msg){
				updateList(msg);
		   }
		});	
		
		function updateList(list) {
			for (var i = 0; i < list.length; i++) {
				var item = list[i];
				var id = 'id' + i;
				var idCheck = 'check' + id;
				map.set(id, item);
				$('#debugDiv').append('<div><button id="' + id + '">' + item.regName + ' -- ' + item.bitName + '</button><input type="checkbox" name="' + id + '" + id="' + idCheck+ '">With Interrupt</input></div>');
				
				if (item.interrupt == true) {
					$('#' + idCheck).prop('checked', true);
				}
				
				$('#' + id).click(function() {
					sendClick($(this).prop('id'));
				});
				$('#' + idCheck).change(function() {
					changeCheckd($(this).prop('name'), $(this).val());
				});
			}
		}
		
		function changeCheckd(id, val) {
			var item = map.get(id);
						
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/setCheck?regName=" + item.regName + "&bitName=" + item.bitName + "&value=" + val,
			   success: function(msg){
					me.initialize();
			   }
			});				
		}
		
		function sendClick(id) {
			var item = map.get(id);
						
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/setCurrentValue?regName=" + item.regName + "&bitName=" + item.bitName + "&value=1",
			   success: function(msg){

			   }
			});			
			if (item.interrupt == true) {
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/register/interrupt",
				   success: function(msg){
	
				   }
				});			
			}	
		}
	}
}