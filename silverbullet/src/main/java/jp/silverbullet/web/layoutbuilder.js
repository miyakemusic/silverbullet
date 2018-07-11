class LayoutBuilder {
	constructor (base, root) {
		this.root = root;
		this.base = base;
		this.map = new Map();
		this.widgetMap = new Map();
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
		   }
		});	
	}
	
	createWidget(parent, pane) {
		var me = this;
		
		var widget = new JsWidget(pane, parent, 
			function(id, info) {
				me.selectedDiv = id;
				var obj = me.widgetMap.get(id);
				if (obj != undefined) {
					$('#widgetType').val(obj.widgetType);
				}
				
				$('#layout').val(info.layout);
				$('#styleClass').val(info.styleClass);
				$('#css').val(info.css);
				$('#id').val(info.id);
			}
		);
		widget.editable(this.enableEdit);
		this.allWidgets.push(widget);
		   			
		if (pane.id != undefined && pane.id != '') {
			this.pushWidget(pane, widget);
		}
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
	
}