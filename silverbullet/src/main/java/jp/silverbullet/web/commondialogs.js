class CommonDialog {
	constructor(div, title, message, okHandler) {
		this.dialogId = div + 'commonDialog';
		$('#' + div).append('<div id="' + this.dialogId + '">' + message + '</div>');	
		
		this.title = title; 
		this.okHandler = okHandler;
	}
	
	showModal() {
		var me = this;
		$('#' + this.dialogId).dialog({
			autoOpen: true,
			title: this.title,
			closeOnEscape: false,
			modal: true,
			buttons: {
				"OK": function(){
					me.okHandler();
					$(this).dialog('close');
				}
				,
				"Cancel": function(){
					$(this).dialog('close');
				}
			},
			width: 400,
			height: 300
		});	
	}
}

class TextInputDialog extends CommonDialog{
	constructor(div, title, message, okHandler) {
		super(div, title, message, okHandler);
		this.textId = this.dialogId + 'text';
		$('#' + this.dialogId).append('<input type="text" id="' + this.textId + '">');
	}
	
	getText() {
		return $('#' + this.textId).val();
	}
}
