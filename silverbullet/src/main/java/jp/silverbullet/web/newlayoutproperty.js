class NewLayoutProperty {
	constructor(div) {
		this.dialogId = div + "_dialog";
		this.mainDiv = div + "_main";
		$('#' + div).append('<div id="' + this.dialogId + '"></div>');
	
		$('#' + this.dialogId).append('<div id="' + this.mainDiv + '"></div>');
		
		this.titleId = div + "_title";
		$('#' + this.mainDiv).append('<div><label id="' + this.titleId + '"></label></div>');
		
		this.fieldId = div + "_field";
		$('#' + this.mainDiv).append('<div>Field Type: <select id="' + this.fieldId + '"></div>');
		var field_options = ['VALUE', 'TITLE', 'UNIT'];
		for (var option of field_options) {
			$('#' + this.fieldId).append($('<option>').text(option).val(option));
		}
				
		this.typeId = div + "_type";
		$('#' + this.mainDiv).append('<div>Widget Type: <select id="' + this.typeId + '"></div>');
		var type_options = ["CheckBox", "TextField", "ToggleButton", "TabPane", "Pane", "ComboBox", "Label", "StaticText", "Tab", "Button", "Chart", "Table", "Image", "Slider"];
		for (var option of type_options) {
			$('#' + this.typeId).append($('<option>').text(option).val(option));
		}
				
		this.cssKeys = div + "_cssKeys";
		this.cssAdd = div + "_cssAdd";
		$('#' + this.mainDiv).append('<div>Add new CSS: <button id="' + this.cssAdd + '">Add</button><select id="' + this.cssKeys + '"></div>');
//		$('#' + this.mainDiv).append('');
		
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/newGui/getCssKeys",
		   success: function(keys){
				for (var key of keys) {
					$('#' + me.cssKeys).append($('<option>').text(key).val(key));
				}
		   }
		});	
				
		this.cssId = div + "_css";
		$('#' + this.mainDiv).append('<div id="' + this.cssId + '"></div>');
		
		$('#' + this.dialogId).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Properties',
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "Close": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 400,
			height: 600
		});	
		
	}
	
	update(widget) {

		$('#' + this.titleId).text(widget.id + "." + widget.subId);
		$('#' + this.fieldId).val(widget.field);
		$('#' + this.typeId).val(widget.type);
		
		$('#' + this.cssId).empty();
		var table = new JsMyTable(this.cssId);
		table.appendRows(widget.css);
		
		var me = this;
		
		table.listenerChange = function(row, col, text) {
			var key = table.valueAt(row, 0);
			me.setCss(widget.widgetId, key, text);
		};
		
		$('#' + this.cssAdd).click(function() {
			me.setCss(widget.widgetId, $('#' + me.cssKeys).val(), "---");
		});
	}
	
	setCss(divid, key, value) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/newGui/setCss?divid=" + divid + "&key=" + key + "&value=" + value,
		   success: function(design){
	
		   }
		});	
	}
	
	show() {
		$('#' + this.dialogId).dialog('open');
	}
	
	hide() {
		$('#' + this.dialogId).dialog('close');
	}
}