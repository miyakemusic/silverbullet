class IdSelectDialog {
	constructor(base, okClicked, application) {
		this.idSelectDialog = base + "idSelectDialog";
		
		$('#' + base).append('<div id="' + this.idSelectDialog + '"></div>');
		this.idEditor = new IdEditorClass(this.idSelectDialog, application);		
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
	
	path(application) {
		this.idEditor.rebuild(application);
	}
}