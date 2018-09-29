/**
 * 
 */
$(function() {
	
	class SvButton {
		constructor(parent, id) {
			$('#' + parent).append('<button id=' + id + '>' + id + '</button>');
			this.id = id;
		}
		update() {
			var _id = this.id;
			$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/test/caption?id="  + _id,
				   success: function(msg){
					   $("#" + _id).text(msg);  
				   }
			});	    				
		}
	}
	
	class SvCheckBox {
		constructor(parent, id) {
			$('#' + parent).append('<input type="checkbox"><span id=' + id + '_text></span><br>');
			this.id = id;
		}
		update() {
			var _id = this.id;
			$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/test/caption?id="  + _id,
				   success: function(msg){
					   $("#" + _id).text(msg);  
				   }
			});	    				
		}
	}
	
    function walkthrough(elements) {
    	for (i = 0; i < elements.length; i++) {
    	     var obj = elements[i];
    	  //   $('#inst').html($('#inst').html() + obj.id + " -> " + obj.widgetType + "<br>");
    	     var id = obj.id;
    	    var type = obj.widgetType;
    	    if (type == "Button") {
    	    	var button = new SvButton("area", id);
    	    	button.update();
//    	    	$("#area").append("<button id=" + id + "></button>");
//    	    	$('#' + id).on('click', function() {
//    	    		   onButtonClick(id);
//    	    		});
//    	    	getTitle(id, "");
    	    }
    	    else if (type == "Check Box") {
    	    	$("#area").append("<input type='checkbox'><span id=" + id + "_text></span><br>");
    	    	getTitle(id, "_text");
    	    }
    	    else if (type == "Text Box") {
    	    	$("#area").append("<span id=" + id + "_text></span>" + "<input type='text' id=" + id + "><br>");
    	    	getTitle(id, "_text");
    	    }   
    	    else if (type == "Radio Buttons") {
    	    	$("#area").append("<input type='radiobutton'><span id=" + id + "_text></span><br>");
    	    	getTitle(id, "_text");    	    	
    	    }
    	    else if (type == "Table") {
    	    	
    	    }
    	    else if (type == "Toggle Buttons") {
    	    	
    	    }
    	    else if (type == "Static Label") {
    	    	
    	    }
    	    else if (type == "One Button") {
    	    	
    	    }
    	    else if (type == "Function Key") {
    	    	
    	    }
    	    
		    if (obj.layout.elements.length > 0) {
    	     	walkthrough(obj.layout.elements);
    	     }
    	}
    };
    
    function onButtonClick(id) {
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/button?id="  + id,
			   success: function(msg){
			   }
			});		
    }
    
    function getTitle(id, appendix) {
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/caption?id="  + id,
			   success: function(msg){
				   $("#" + id + appendix).text(msg);  
			   }
			});	
    	
    }
    
	$(document).ready(function(){
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/test/layout",
		   success: function(msg){
		       walkthrough(msg.elements);	  	   
		   }
		});	
						
		$("#set").click(function(e) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/user/switchServerData?client="+$("#ip").val() + "&ext=" + $("#ext").val(),
			   success: function(msg){
	
			   }
			});		
		});
		
	});
});