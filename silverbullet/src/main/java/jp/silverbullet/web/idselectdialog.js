class IdSelectDialog {
	constructor(base, okClicked, application) {
		this.idSelectDialog = base + "idSelectDialog";
		
		$('#' + base).append('<div id="' + this.idSelectDialog + '"></div>');
			
		this.application = application;
		
	}
	
	showModal() {
		var me = this;
		this.idEditor = new IdEditorClass(this.idSelectDialog, me.application);	
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
			   		me.idEditor.close();
			    	$('#' + me.idSelectDialog).empty();
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			      me.idEditor.close();
			      $('#' + me.idSelectDialog).empty();
			    }
			  },
			width: 1000,
			height: 700
		});	
				
		$('#' + this.idSelectDialog).dialog("open");
		this.idEditor.update();
	}
	
	path(application) {
		this.application = application;
	}
}