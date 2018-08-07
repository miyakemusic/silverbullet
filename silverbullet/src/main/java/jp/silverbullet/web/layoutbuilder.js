class LayoutBuilder {
	constructor (base, root, callback) {
		this.root = root;
		this.base = base;
		this.map = new Map();
		this.widgetMap = new Map();
		this.callback = callback;
		this.updateUI();
	}
	
	updateUI() {
		this.allWidgets = [];
		this.map.clear();
		this.widgetMap.clear();
		
		var me = this;
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/getDesign?root=" + me.root,
		   success: function(msg){
		   		$('#' + me.base).empty();
		   		me.createWidget(me.base, msg);
		   		me.updateAllWidgetsValue();
		   }
		});	
	}
	
	updateAllWidgetsValue() {
	  	for (var j in this.allWidgets) {
	  		var widget = this.allWidgets[j];
			widget.updateValue();
	  	}			
	}
		
	onPropertyUpdate(ids) {
		for (var i in ids) {
			var widgets = this.map.get(ids[i]);
		  	for (var j in widgets) {
				widgets[j].updateValue();
		  	}
		}
	}
	
	createWidget(parent, pane) {
		var me = this;
		
		var widget = new JsWidget(pane, parent, 
			function(div, info) {
				me.selectedDiv = div;
				var obj = me.widgetMap.get(div);
				var widgetType = "";
				if (obj != null) {
					widgetType = obj.widgetType;
				}
				me.callback(widgetType, div, info);
			}
		);
		widget.editable(this.enableEdit);
		this.allWidgets.push(widget);
		   			
//		if (pane.id != undefined && pane.id != '') {
			this.pushWidget(pane, widget);
//		}
		for (var i in pane.children) {
			var child = pane.children[i];
			this.createWidget(widget.baseId, child, pane.layout);
		}
	}
	
	pushWidget(pane, widget) {
		if (this.map.get(pane.id) == null) {
			this.map.set(pane.id, []);
		}
		this.map.get(pane.id).push(widget);
		this.widgetMap.set(widget.baseId, pane);
	}	
	
	set enableEdit(enableEdit) {
		this._enableEdit = enableEdit;
		for (var i in this.allWidgets) {
		    this.allWidgets[i].editable(enableEdit);
		};
	}
	
	get enableEdit() {
		return this._enableEdit;
	}
					
	getRealId(div) {
		var tmp = div.split('-');
		var ret = tmp[tmp.length-1];
		return ret;
	}
	
	getSelectedDiv() {
		return this.getRealId(this.selectedDiv);
	}
	
	getCopiedDiv() {
		return this.getRealId(this.copiedDiv);
	}
	
	get selectedDiv() {
		return this._selectedDiv;
	}
	
	set selectedDiv(val) {
		this._selectedDiv = val;
	}
}