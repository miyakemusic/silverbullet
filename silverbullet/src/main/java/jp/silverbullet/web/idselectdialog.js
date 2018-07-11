class IdSelectDialog {
	constructor(base, okClicked) {
		$('#' + base).append('<div id="idSelectDialog"></div>');
		$('#idSelectDialog').append('<select id="propType"></select><button id="add">Add</button>');
		$('#idSelectDialog').append('<div id="idList"></div>');
		$('#idSelectDialog').append('<div id="subList">');
		
		var tableManager = new IdTableManager('idList', 'subList', 'add', null, 'propType');
		$('#idSelectDialog').dialog({
			  autoOpen: false,
			  title: 'ID Selector',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			    	var ids = [];
			    	ids.push(tableManager.id);
			    	okClicked(ids);
			   		$(this).dialog('close');
			    	
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 1000,
			height: 600
		});			
		
	}
	
	showModal() {
		$('#idSelectDialog').dialog("open");
	}
}