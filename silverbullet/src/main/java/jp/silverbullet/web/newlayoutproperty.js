class NewLayoutProperty {
	constructor(div) {
		this.dialogId = div + "_dialog";
		this.mainDiv = div + "_main";
		$('#' + div).append('<div id="' + this.dialogId + '"></div>');
	
		$('#' + this.dialogId).append('<div id="' + this.mainDiv + '"></div>');
		
		this.titleId = div + "_title";
		$('#' + this.mainDiv).append('<div><button id="' + this.titleId + '"></button></div>');
			
		var me = this;
			
		var idSelector = new IdSelectDialog(this.mainDiv, function(ids, subId) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/setId?divid=" + me.widget.widgetId + "&id=" + ids[0] + "&subId=" + subId,
			   success: function(keys){

			   }
			});			
		});
		$('#' + this.titleId).click(function() {
			idSelector.showModal();
		});
		
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
		$('#' + this.mainDiv).append('<fieldset><legend>Add new CSS</legend><button id="' + this.cssAdd + '">Add</button><select id="' + this.cssKeys + '"><input type="text" id="' + this.cssValue + '" value="10px"></fieldset>');

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
			
		this.table = new JsMyTable(this.cssId, null, 'middletable');
		this.table.listenerChange = function(row, col, text) {
			var key = me.table.valueAt(row, 0);
			me.setCss(me.widget.widgetId, key, text);
		};
		
		$('#' + this.cssAdd).click(function() {
			me.setCss(me.widget.widgetId, $('#' + me.cssKeys).val(), $('#' + me.cssValue).val());
		});
		
		$('#' + this.typeId).change(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/setWidgetType?divid=" + me.widget.widgetId + "&type=" + 
			   	$('#' + me.typeId).val(),
			   success: function(result){
			   }
			});			
		});
	}
	
	update(widget) {

		$('#' + this.titleId).text(widget.id + "." + widget.subId);
		$('#' + this.fieldId).val(widget.field);
		$('#' + this.typeId).val(widget.type);
		
		this.widget = widget;
		
//		$('#' + this.cssId).empty();
		this.table.clear();
		this.table.appendRows(widget.css);
		
		
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