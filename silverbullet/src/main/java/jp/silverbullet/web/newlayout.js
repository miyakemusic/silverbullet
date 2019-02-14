class Widget {
	constructor(widget, parent, divid) {
		this.widget = widget;
		this.divid = divid;
		this.parent = parent;
	}
	
	accessor(setter, getter) {
		this.setter =  setter;
		this.getter = getter;
	}
	
	onUpdateValue(property) {
		$('#' + this.divid).val(property.currentValue);
	}
	
	setValue(id, index, value) {
		this.setter(id, index, value);
	}
	
	getProperty(id, index, callback) {
		this.getter(id, index, callback);
	}
}

class Pane extends Widget {
	constructor(widget, parent, divid, callback) {
		super(widget, parent, divid);
		
		$('#' + parent).append('<div class="design" id="' + divid + '"></div>');
		for (var w of widget.widgets) {
			callback(w, divid);
		}
	}
}

class TabPane extends Widget {
	constructor(widget, parent, divid, callback) {
		super(widget, parent, divid);

		var content = '';
		var html = '<div id="' + divid + '">';
		html += '<ul>';

		for (var i = 0; i < widget.panes.length; i++) {
			var w = widget.panes[i];
			var href = divid + 'tabno' + i;
			html += '<li><a href="#' + href + '"><span>' + w.caption + '</span></a></li>';
			
			content += '<div id="' + href + '"></div>';
		}
		html += '</ul>';

		$('#' + parent).append(html + content + '</div>');
		$('#' + divid).tabs();
		
		for (var i = 0; i < widget.panes.length; i++) {
			var w = widget.panes[i];
			for (var w2 of w.widgets) {
				callback(w2, divid + 'tabno' + i);
			}
		}	
	}
}

class StaticText extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		$('#' + parent).append('<label>' + widget.text + '</label>');
	}
	
	onUpdateValue(property) {
	}
}

class Label extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		$('#' + this.parent).append('<label id="' + this.divid + '"></label>');	
	}
	
	onUpdateValue(property) {
		if (this.widget.field == 'TITLE') {
			$('#' + this.divid).text(property.title);	
		}
		else if (this.widget.field == 'VALUE') {
			if (property.type == 'Boolean') {
				$('#' + this.divid).text(property.currentValue == 'true' ? 'ON' : 'OFF');
			}
			else {
				$('#' + this.divid).text(property.currentValue);	
			}
		}
		else if (this.widget.field == 'UNIT') {
			$('#' + this.divid).text(property.unit);	
		}
		$('#' + this.divid).css('display', 'inline-block');
	}
}

class TextField extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		$('#' + this.parent).append('<input type="text" id="' + this.divid + '">');
	}
	
	onUpdateValue(property) {
		$('#' + this.divid).val(property.currentValue);
		
		var me = this;
		$('#' + this.divid).keydown(function(event) {
			if (event.which == 13) {
				me.setter(me.widget.id, 0, $('#' + me.divid).val());
			}
		});
	}
}

class CheckBox extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		var me = this;
		
		$('#' + this.parent).append('<div id="' + this.divid + '"><input type="checkbox" id="' + this.divid + 'check"><label id="' + this.divid + 'label"></label></div>');
		$('#' + this.divid + 'check').click(function() {
			me.setter(me.widget.id, 0, $(this).prop('checked'));
		});			
	}
	
	onUpdateValue(property) {
		$('#' + this.divid + 'check').prop('checked', property.currentValue == 'true');
		$('#' + this.divid + 'label').text(property.title);
	}
}

class ToggleButton extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.labelId = divid + 'label';
		$('#' + parent).append('<input type="checkbox" id="' + divid + '"><label id="' + this.labelId + '" for="' + divid + '"></label>');
		$('#' + divid).button();
		
		var me = this;
		
		$('#' + divid).change(function() {
			var value = '';
			
			if (me.isList()) {
				//if ($(this).prop('checked')) {
					value = me.widget.subId;
				//}
			}
			else {
				if ($(this).prop('checked')) {
					value = 'true';
				}
				else {
					value = 'false';
				}			
			}
			if (value != '') {
				me.setter(me.widget.id, 0, value);
			}
		});
	}
	
	onUpdateValue(property) {
		if (this.isList()) {
			for (var e of property.elements) {
				if (e.id == this.widget.subId) {
					$('#' + this.labelId).html(e.title);
				}
			}
			$('#' + this.divid).prop('checked', this.widget.subId == property.currentSelectionId).button('refresh');
		}
		else {
			$('#' + this.labelId).html(property.title);
			$('#' + this.divid).prop('checked', property.currentValue == 'true').button('refresh');
		}
	}
	
	isList() {
		return this.widget.subId != '';
	}
}
class ComboBox extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		$('#' + this.parent).append('<select id="' + divid + '"></select>');

		var me = this;
		$('#' + this.divid).change(function() {
			me.setter(me.widget.id, 0, $(this).val());
		});
	}
	
	onUpdateValue(property) {
		$('#' + this.divid).empty();
		for (var e of property.elements) {
			var option = $('<option>').val(e.id).text(e.title);
			$('#' + this.divid).append(option);		
		}
		$('#' + this.divid).val(property.currentSelectionId);
	}
}

class NewLayout {
	constructor(div) {
		var propertyMap = new Map();
		var widgetMap = new Map();
		
		this.divNumber = 0;
		var me = this;
		retreiveDesign();
		
		new MyWebSocket(function(msg) {
			var ids = msg.split(',');
			for (var idindex of ids) {
				var id = idindex.split('#')[0];
				var index = idindex.split('#')[1];
				retrieveProperty(id, index, function(property) {
					propertyMap[property.id] = property;
					for (var widget of getWidgetMap(property.id)) {
						widget.onUpdateValue(property);
					}
				});
			}
		}, 'VALUES');
		
		function getWidgetMap(id) {
			if (widgetMap[id] == null) {
				return [];
			}
			return widgetMap[id];
		}
		
		function retreiveDesign() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/getDesign",
			   success: function(design){
			   		build(design, div);
			   }
			});	
		}
		
		function getProperty(id, index, callback) {
			if (propertyMap[id] != null) {
				callback(propertyMap[id]);
			}
			else {
				retrieveProperty(id, index, function(property){
					propertyMap[property.id] = property;
					callback(propertyMap[property.id]);
				});
			}
		}
		
		function retrieveProperty(id, index, callback) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id.replace('$', '') + "&index=" + index,
			   success: function(property){
			   		callback(property);
			   }
			});		
		}
		
		function addWidget(id, widget) {
			if (widgetMap[id] == null) {
				widgetMap[id] = [];
			}
			widgetMap[id].push(widget);
		}
		
		function setValue(id, index, value) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/setValue?id=" + id.replace('$', '') + "&index=" + index + "&value=" + value,
			   success: function(property){
			   }
			});			
		}
		
		function build(design, div) {
			for (var pane of design.panes) {
				buildSub(pane, div);
			}
			
			for (var id in widgetMap) {
				if (id == '') {
					continue;
				}
				
				getProperty(id, 0, function(prop) {
					for (var w of widgetMap[prop.id]) {
						w.onUpdateValue(prop);
					}
				});
			}
		}
		
		function buildSub(widget, parentDiv) {
			var divid = getId();
			
			var wrappedWidget = new Widget(widget, parentDiv, divid);

			if (widget.type == 'Pane') {
				wrappedWidget = new Pane(widget, parentDiv, divid, buildSub);
			}		
			else if (widget.type == 'TabPane') {
				wrappedWidget = new TabPane(widget, parentDiv, divid, buildSub);
			}
			else if (widget.type == 'CheckBox') {	
				wrappedWidget = new CheckBox(widget, parentDiv, divid);
			}
			else if (widget.type == 'TextField') {
				wrappedWidget = new TextField(widget, parentDiv, divid);
			}	
			else if (widget.type == 'ToggleButton') {
				wrappedWidget = new ToggleButton(widget, parentDiv, divid);				
			}	
			else if (widget.type == 'ComboBox') {	
				wrappedWidget = new ComboBox(widget, parentDiv, divid);
			}			
			else if (widget.type == 'Label') {	
				wrappedWidget = new Label(widget, parentDiv, divid);
			}		
			else if (widget.type == 'StaticText') {	
				wrappedWidget = new StaticText(widget, parentDiv, divid);
			}
			wrappedWidget.accessor(
				function(id, index, value) {
					setValue(id, index, value);
				},
				function(id, index, callback) {
					getProperty(id, index, callback);
				}
			);
			
			for (var css of widget.css) {
				$('#' + divid).css(css.key, css.value);
			}
			if (widget.width != -1) {
				$('#' + divid).width(widget.width);
			}
			
			var id = widget.id.replace('$', '');
			addWidget(id, wrappedWidget);
			
		}
		
		function getId() {
			return 'div' + me.divNumber++;
		}

	}
}
