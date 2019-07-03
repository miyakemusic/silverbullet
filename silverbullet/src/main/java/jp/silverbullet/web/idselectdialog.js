class IdSelectDialog {
	constructor(base, okClicked) {
		this.idSelectDialog = base + "idSelectDialog";
		
		$('#' + base).append('<div id="' + this.idSelectDialog + '"></div>');
		this.idEditor = new IdEditorClass(this.idSelectDialog);		
		var me = this;
		
		$('#' + this.idSelectDialog).dialog({
			  autoOpen: false,
			  title: 'ID Selector',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			    	var ids = [];
					ids.push(me.idEditor.getCurrentId());
			    	okClicked(ids, me.idEditor.getSelectionId());
			   		$(this).dialog('close');
			    	
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 1000,
			height: 700
		});			
		
	}
	
	showModal() {
		
		$('#' + this.idSelectDialog).dialog("open");
		this.idEditor.update();
	}
}