class NewLayoutProperty {
	constructor(div) {
		this.dialogId = div + "_dialog";
		this.mainDiv = div + "_main";
		$('#' + div).append('<div id="' + this.dialogId + '"></div>');
	
		$('#' + this.dialogId).append('<div id="' + this.mainDiv + '"></div>');
		
		this.widgetId = div + "_widgetId";
		$('#' + this.mainDiv).append('Widget ID: <label id="' + this.widgetId + '"></label>');
					
		this.titleId = div + "_title";
		$('#' + this.mainDiv).append('<div><button id="' + this.titleId + '"></button></div>');
			
		var me = this;
			
		var idSelector = new IdSelectDialog(this.mainDiv, function(ids, subId) {
			if (subId == null) {
				subId = "";
			}
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/setId?divid=" + me.widget.widgetId + "&id=" + ids[0] + "&subId=" + subId,
			   success: function(keys){

			   }
			});			
		});
		$('#' + this.titleId).click(function() {
			idSelector.showModal();
		});
		
		this.fieldId = div + "_field";
		$('#' + this.mainDiv).append('<div>Field Type: <select id="' + this.fieldId + '"></div>');
//		var field_options = ['VALUE', 'TITLE', 'UNIT', 'MIN'];
//		for (var option of field_options) {
//			$('#' + this.fieldId).append($('<option>').text(option).val(option));
//		}
		$.ajax({
		   type: "GET", 
		   url: "//" + window.location.host + "/rest/newGui/getFieldTypes",
		   success: function(types){
				for (var option of types) {
					$('#' + me.fieldId).append($('<option>').text(option).val(option));
				}
		   }
		});	
						
		this.typeId = div + "_type";
		$('#' + this.mainDiv).append('<div>Widget Type: <select id="' + this.typeId + '"></div>');
//		var type_options = ["CheckBox", "TextField", "ToggleButton", "TabPane", "Pane", "ComboBox", "Label", "StaticText", "Tab", "Button", "Chart", "Table", "Image", "Slider"];
//		for (var option of type_options) {
//			$('#' + this.typeId).append($('<option>').text(option).val(option));
//		}
		$.ajax({
		   type: "GET", 
		   url: "//" + window.location.host + "/rest/newGui/widgetTypes",
		   success: function(types){
				for (var option of types) {
					$('#' + me.typeId).append($('<option>').text(option).val(option));
				}
		   }
		});	
					
		this.cssKeys = div + "_cssKeys";
		this.cssAdd = div + "_cssAdd";
		$('#' + this.mainDiv).append('<fieldset><legend>Add new CSS</legend><button id="' + this.cssAdd + '">Add</button><select id="' + this.cssKeys + '"><input type="text" id="' + this.cssValue + '" value="10px"></fieldset>');

		$.ajax({
		   type: "GET", 
		   url: "//" + window.location.host + "/rest/newGui/getCssKeys",
		   success: function(keys){
				for (var key of keys) {
					$('#' + me.cssKeys).append($('<option>').text(key).val(key));
				}
		   }
		});	
				
		this.cssId = div + "_css";
		$('#' + this.mainDiv).append('<div id="' + this.cssId + '"></div>');
		
		this.layoutId = div + "_layout";
		$('#' + this.mainDiv).append('<div>Layout: <select id="' + this.layoutId + '"></div>');
		$('#' + me.layoutId).append($('<option>').text('HORIZONTAL').val('HORIZONTAL'));
		$('#' + me.layoutId).append($('<option>').text('VERTICAL').val('VERTICAL'));
		$('#' + me.layoutId).append($('<option>').text('ABSOLUTE').val('ABSOLUTE'));
		$('#' + me.layoutId).append($('<option>').text('NONE').val('NONE'));


		this.optionalId = div + "_optional";
		this.optionalCommitId = div + "_optionalCommit";
		$('#' + this.mainDiv).append('Optional:<input type="text" id="' + this.optionalId + '"><button id="' + this.optionalCommitId + '">Set</button>');
		
		function setOptional(optional) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/setOptional?divid=" + me.widget.widgetId + "&optional=" + optional,
			   success: function(result){
			   }
			});		
		}
		
		$('#' + this.optionalCommitId).click(function() {
				setOptional($('#' + me.optionalId).val());
		});
		
		
		this.copySizeId = div + "_copySize";
		$('#' + this.mainDiv).append('<button id="' + this.copySizeId + '">Copy Size</button>');
		$('#' + this.copySizeId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/copySize?divid=" + me.widget.widgetId,
			   success: function(result){
			   }
			});				
		});
				
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
			   url: "//" + window.location.host + "/rest/newGui/setWidgetType?divid=" + me.widget.widgetId + "&type=" + 
			   	$('#' + me.typeId).val(),
			   success: function(result){
			   }
			});			
		});
		
		$('#' + this.fieldId).change(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/setField?divid=" + me.widget.widgetId + "&field=" + 
			   		$('#' + me.fieldId).val(),
			   success: function(result){
			   }
			});			
		});
		
		var addWidget = div + "_addWidget";
		$('#' + this.mainDiv).append('<button id="' + addWidget + '">Add Widget</button>');
		$('#' + addWidget).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/addWidget?divid=" + me.widget.widgetId,
			   success: function(design){
		
			   }
			});			
		});

		var removeWidget = div + "_removeWidget";
		$('#' + this.mainDiv).append('<button id="' + removeWidget + '">Remove Widget</button>');
		$('#' + removeWidget).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/removeWidget?divid=" + me.widget.widgetId,
			   success: function(design){
		
			   }
			});			
		});
				
		$('#' + this.layoutId).change(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/setLayout?divid=" + me.widget.widgetId + "&layout=" + $('#' + me.layoutId).val(),
			   success: function(design){
		
			   }
			});			
		});
		
		var buttonArray = div + "_buttonArray";
		$('#' + this.mainDiv).append('<button id="' + buttonArray + '">Button Array</button>');
		$('#' + buttonArray).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/buttonArray?divid=" + me.widget.widgetId,
			   success: function(design){
		
			   }
			});		
		});
		
		var titledValue = div + "_titledValue";
		$('#' + this.mainDiv).append('<button id="' + titledValue + '">Titled Value</button>');
		$('#' + titledValue).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/newGui/titledInput?divid=" + me.widget.widgetId,
			   success: function(design){
	
			   }
			});		
		});
		
		var visibleCondition = div + "_visibleCondition";
		var visibleConditionSet = div + "_visibleConditionSet";
		$('#' + this.mainDiv).append('<input type="text" id="' + visibleCondition + '"><button id="visibleConditionSet">Set</button>');
		
		var registerShortcut = div + "_registerShortcut";
		var registerShoftcutSet =  div + "_registerShortcutSet";
		$('#' + this.mainDiv).append('<div><select id="' + registerShortcut + '"></select><button id="' + registerShoftcutSet + '">Set</button></div>');
		$('#' + registerShoftcutSet).click(function() {
			setOptional($('#' + registerShortcut).val());
		});
		
		
		
		updateRegisterShortcut();
		function updateRegisterShortcut() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/getShortCuts",
			   success: function(options){
					for (var option of options) {
						var s = option.bitName + "@" + option.regName;
						$('#' + registerShortcut).append($('<option>').text(s).val(s));
					}			
			   }
			});	
		}
	}
	
	update(widget) {
		if (widget.id != null) {
			var s = widget.id;
			if (widget.subId != null && widget.subId != '') {
				s += "." + widget.subId;
			}
			
			$('#' + this.titleId).text(s);
		}
		
		$('#' + this.widgetId).text(widget.widgetId);
		$('#' + this.fieldId).val(widget.field);
		$('#' + this.typeId).val(widget.type);
		$('#' + this.layoutId).val(widget.layout);
		
		this.widget = widget;
		
//		$('#' + this.cssId).empty();
		this.table.clear();
		this.table.appendRows(widget.css);
		
		$('#' + this.optionalId).val(widget.optional);
		
	}
	
	setCss(divid, key, value) {
		$.ajax({
		   type: "GET", 
		   url: "//" + window.location.host + "/rest/newGui/setCss?divid=" + divid + "&key=" + key + "&value=" + value,
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