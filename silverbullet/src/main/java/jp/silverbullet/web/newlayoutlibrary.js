class NewLayoutLibrary {
	constructor(div, selector) {
		var addId = div + "_add";
		$('#' + div).append('<button id="' + addId + '">Add</button>');
		$('#' + addId).click(function() {			
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/addRootPane",
			   success: function(types){
			   		updateList();
			   }
			});			
		});
		
		var editId = div + "_edit";
		$('#' + div).append('<button id="' + editId + '">Edit</button>');
		
		var currentName = "";
		
		var dialogId = div + "_dialog";
		var nameId = div + "_name";
		$('#' + div).append('<div id="' + dialogId + '"><input type="text" id="' + nameId + '"></div>');
		$('#' + editId).click(function() {	
			$('#' + nameId).val(currentName);		
			$('#' + dialogId).dialog('open');
		});		
		
		var listId = div + '_list';
		$('#' + div).append('<div id="' + listId + '"></div>');

		function updateList() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/getRootPanes",
			   success: function(types){
			   		$('#' + listId).empty();
					for (var option of types) {
						var id = div + "_" + option;
						$('#' + listId).append('<div><label class="listItem" id="' + id + '"></label></div>');
						$('#' + id).text(option);

						$('#' + id).click(function() {
							$('.listItem').removeClass('checked');
							$(this).addClass('checked');
							currentName = $(this).text();
							selector($(this).text());
						});
					}
			   }
			});	
		}
		
		updateList();
		
		function changeName(oldName, newName) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/changeName?oldName=" + oldName + "&newName=" + newName,
			   success: function(types){
			   		updateList();
			   }
			});	
		}
		
		$('#' + dialogId).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Name',
			  closeOnEscape: true,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	changeName(currentName, $('#' + nameId).val());
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
		
		
	}
}