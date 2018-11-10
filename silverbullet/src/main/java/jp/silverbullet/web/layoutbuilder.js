class LayoutBuilder {
	constructor (base, root, callback, debugCallback) {
		this.idSet = new Set();
		this.root = root;
		this.base = base;
		this.map = new Map();
		this.widgetMap = new Map();
		this.callback = callback;
		this.debugCallback = debugCallback;
		this.enableEdit = 'disable';
		this.updateUI();
		
		initWebSocket();
		
		var me = this;
		function initWebSocket() {
			new MyWebSocket(function(msg) {
				var ids = msg.split(',');
	      		me.requestUpdate(ids);
			}
			, 'VALUES');
			new MyWebSocket(function(msg) {
				if (msg == 'layoutChanged') {
	      			me.updateUI();
	      		}
			}
			, 'DESIGN');
		}
		
	}
	
	setContextmenu() {
	    $('#' + this.base).contextmenu({
			delegate: '.Widget',
			autoFocus: true,
			preventContextMenuForPopup: true,
			preventSelect: true,
			taphold: true,
			menu: [
					{title: "Add as Test <kbd>[F2]</kbd>", cmd: "addTest"},
					{title: "Add as Command <kbd>Ctrl+D</kbd>", cmd: "addCommand"},
		      ],
			// Handle menu selection to implement a fake-clipboard
			select: function(event, ui) {
				var target = ui.target;
				switch(ui.cmd) {
				case "addTest":
					addTestQuery();
					break;
				case "addCommand":
					break;
				}
			},
			 // Implement the beforeOpen callback to dynamically change the entries
			beforeOpen: function(event, ui) {
				var $menu = ui.menu,
				$target = ui.target,
				extraData = ui.extraData; // passed when menu was opened by call to open()
				// Optionally return false, to prevent opening the menu now
			}
		});
		
		var me = this;
		function addTestQuery() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/addPropertyTest?div=" + me.getSelectedDiv(),
			   success: function(msg){
			   }
			});	
		}
	}
	
	updateUI() {
		this.allWidgets = [];
		this.map.clear();
		this.widgetMap.clear();
		
		var me = this;
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/design/getDesign?root=" + me.root,
		   success: function(msg){
		   		$('#' + me.base).empty();
		   		if (msg != null) {
		   			me.createWidget(me.base, msg);
		   			me.updateAllWidgetsValue();
		   		}
		   		
		   		me.setContextmenu();
		   }
		});	
		
	}
	
	updateAllWidgetsValue() {
	  	for (var j in this.allWidgets) {
	  		var widget = this.allWidgets[j];
			widget.updateValue();
	  	}			
	  	this.applyEnableEdit();
	}
		
	requestUpdate(ids) {
		for (var i = 0; i < ids.length; i++) {
			this.idSet.add(ids[i]);
		}
		var me = this;
		setTimeout(function() {
			for (let id of me.idSet) {
				var widgets = me.map.get(id);
			  	for (var j in widgets) {
					widgets[j].updateValue();
			  	}
			}
			me.idSet.clear();
		}, 10);
	}
	
	createWidget(parent, pane) {
		var me = this;
		
		var widget = new JsWidget(pane, parent, 
			function(div, info) {
				me.selectedDiv = div;
//				var obj = me.widgetMap.get(div);
//				var widgetType = "";
//				if (obj != null) {
//					widgetType = obj.widgetType;
//				}
//				me.callback(widgetType, div, info);
			},
			function(msg) {
				me.debugCallback(msg);
			}
		);
		widget.editable(this.enableEdit);
		this.allWidgets.push(widget);
		   			
		this.pushWidget(pane, widget);

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
		this.widgetMap.set(this.getRealId(widget.baseId), pane);
	}	
	
	set enableEdit(enableEdit) {
		this._enableEdit = enableEdit;
		this.applyEnableEdit();
	}
	
	get enableEdit() {
		return this._enableEdit;
	}
	
	applyEnableEdit() {
		for (var i in this.allWidgets) {
		    this.allWidgets[i].editable(this.enableEdit);
		};
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
	
	set selectedDiv(div) {
		this._selectedDiv = this.getRealId(div);

		var obj = this.widgetMap.get(this.selectedDiv);
		var widgetType = "";
		if (obj != null) {
			widgetType = obj.widgetType;
		}
		this.callback(widgetType, div, obj);
				
	}
}