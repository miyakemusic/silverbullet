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
					$(this).dialog('destroy');
					$('#' + me.dialogId).remove();
				}
				,
				"Cancel": function(){
					$(this).dialog('destroy');
					$('#' + me.dialogId).remove();
				}
			},
			width: 400,
			height: 300
		});	
	}
}

class TextInputDialog extends CommonDialog{
	constructor(div, title, message, defaultValue, okHandler) {
		super(div, title, message, okHandler);
		this.textId = this.dialogId + 'text';
		$('#' + this.dialogId).append('<input type="text" id="' + this.textId + '">');
		$('#' + this.textId).val(defaultValue);
	}
	
	getText() {
		return $('#' + this.textId).val();
	}
}
