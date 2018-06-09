$(function() {	
	$(document).ready(function() {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/layout",
		   success: function(msg){
		   		$('#root').append('<div id=' + msg.unique + '></div>');
		   		$('#' + msg.unique).height(msg.height).width(msg.width);
		   		for (var i in msg.children) {
		   			var child = msg.children[i];
		   			if (child.widgetType == 'COMBOBOX') {
		   				$('#' + msg.unique).append(createComboBox(child));
		   			}
		   			else if (child.widgetType == 'TEXTFIELD') {
		   				$('#' + msg.unique).append(createTextField(child));
		   			}
		   		}
		   }
		});	
		
		function createComboBox(child) {
			var ret = '<div>child.title<SELECT id=' + child.unique'></SELECT></div>';
		}
		
		function createTextField(child) {
		}	
	});
})