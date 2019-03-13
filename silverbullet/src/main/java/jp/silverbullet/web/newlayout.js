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

class EditableWidget extends Widget {
	constructor(widget, parent, divid, buildSub) {
		super(widget, parent, divid);
		
		var labelId = divid + "_label";
		var containerId = divid + "_container";
		
		$('#' + parent).append('<div id="' + divid + '"></div>');
		$('#' + divid).append('<div id="' + containerId + '"></div>');
		$('#' + divid).append('<div id="' + labelId + '">' + widget.type + "." + widget.id + '</div>');
		
		$('#' + divid).css({"position":"relative", "border-width":"1", "border-style":"solid", "border-color":"black", "word-wrap":"break-word"});	
		
		$('#' + divid).draggable({
			start : function (event , ui){
			} ,
			drag : function (event , ui) {
			} ,
			stop : function (event , ui){
			} 
		});
		$('#' + divid).resizable({
	      stop: function( event, ui ) {
	      }
	    });    
	    					
		if (widget.widgets != null) {
			for (var w of widget.widgets) {
				buildSub(w, containerId);
			}
		}
	}
	
	onUpdateValue(property) {
	}
}

class Pane extends Widget {
	constructor(widget, parent, divid, buildSub) {
		super(widget, parent, divid);
		
		this.legendId = divid + 'legend';
		if (widget.caption != '') {
			$('#' + parent).append('<fieldset id="' + divid + '"><legend>' + widget.caption + '</legend></fieldset>');
		}
		else if (widget.id != '' && widget.subId == '') {
			$('#' + parent).append('<fieldset id="' + divid + '"><legend id="' + this.legendId + '"></legend></fieldset>');
		}
		else {
			$('#' + parent).append('<div id="' + divid + '"></div>');
		}
		
		for (var w of widget.widgets) {
			buildSub(w, divid);
		}
	}
	
	onUpdateValue(property) {
		if (this.widget.id != '') {
			if (this.widget.subId != '') {
				if (this.widget.subId == property.currentSelectionId) {
					$('#' + this.divid).css('display', 'inline-block');
				}
				else if (this.prevDisplay != ''){
					$('#' + this.divid).css('display', 'none');				
				}
			}
			else {
				$('#' + this.legendId).html(property.title);
			}
		}
	}
}

class TabPane extends Widget {
	constructor(widget, parent, divid, buildSub, addWidget) {
		super(widget, parent, divid);

		var content = '';
		var html = '<div id="' + divid + '">';
		html += '<ul>';

		for (var i = 0; i < widget.panes.length; i++) {
			var w = widget.panes[i];
			var href = divid + 'tabno' + i;
			html += '<li><a href="#' + href + '"><span>' + w.title + '</span></a></li>';
			
			content += '<div id="' + href + '"></div>';
		}
		html += '</ul>';

		$('#' + parent).append(html + content + '</div>');
		$('#' + divid).tabs();
		
		for (var i = 0; i < widget.panes.length; i++) {
			var child = widget.panes[i];
			addWidget(child.id, new Tab(child, divid));
			for (var w2 of child.widgets) {
				buildSub(w2, divid + 'tabno' + i);
			}
		}	
	}
}

class Tab {
	constructor(widget, tabPane) {
		this.widget = widget;
		this.tabPane = tabPane;
	}
	
	onUpdateValue(property) {
		if (this.widget.id != '') {
			for (var o of property.elements) {
				if (o.id == this.widget.subId) {
					$('#' + this.tabPane + ' ul:first li:eq(' + this.widget.tabIndex + ') a').text(o.title);
				}
			}
			if (property.currentSelectionId == this.widget.subId) {
				$('#' + this.tabPane).tabs("option", "active", this.widget.tabIndex);
			}
		}
	}
}

class Slider extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.customId = divid + 'custom';
		
		$('#' + parent).append('<div id="' + divid + '"><div id="' + this.customId + '" class="ui-slider-handle"></div></div>');

		this.prevMin = -100;
		this.prevMax = 100;
		
		var me = this;
	    this.handle = $( "#" + this.customId);
	    $( "#" + divid ).slider({
	    	value: 0,
	    	min: me.prevMin,
	    	max: me.prevMax,
	      create: function() {
	        me.handle.text( $( this ).slider( "value" ) );
	      },
	      slide: function( event, ui ) {
	        //me.handle.text( ui.value );
	        me.setter(me.widget.id, 0, ui.value);
	      }
	    });
	}
	
	onUpdateValue(property) {
		
		var range = false;
		if (this.prevMin != property.min) {
			$('#' + this.divid).slider("option", "min", property.min);
			range = true;
		}
		if (this.prevMax != property.max) {
			$('#' + this.divid).slider("option", "max", property.max);
			range = true;
		}
		$('#' + this.divid).slider("option", "value", property.currentValue);
		
		this.handle.text($('#' + this.divid).slider('value'));
		
		if (range) {
			$("input[type='range']").slider( "refresh" );
		}
				
		this.prevMin = property.min;
		this.prevMax = property.max;
	}
}

class SbImage extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.canvasId = divid + "canvas";
		$('#' + parent).append('<div id="' + divid + '" width="100%" height="100%"><canvas id="' + this.canvasId + '"></canvas></div>');
		
		this.canvas = $("#" + this.canvasId)[0];
		var ctx = this.canvas.getContext('2d');
		this.image = new Image();
		var me = this;
		this.image.onload = function() {
		  ctx.drawImage(me.image, 0, 0);
		}
	}
	
	onUpdateValue(property) {
//		var canvas = $('#' + this.canvasId)[0];
		var wrapper = $('#' + this.divid)[0];
		
		var canvasWidth = this.canvas.width;
		var divWidth = $('#' + this.divid).width();
		if (this.canvas.width != $('#' + this.divid).width()) {
			$('#' + this.canvasId).attr('width', $('#' + this.divid).width());
			$('#' + this.canvasId).attr('height', $('#' + this.divid).height());
		}
		this.image.src = property.currentValue;
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

class Button extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.titleId = divid + 'title';
		this.valueId = divid + 'value';

		$('#' + parent).append('<div id="' + divid + '" class="mybutton canpush"><label id="' + this.titleId + '" class="buttonTitle"></label><br><label id="' + this.valueId + '" class="buttonValue"></label></div>');
//		$('#' + divid).css('line-height', '20px');
		var me = this;

		$('#' + this.divid).click(function() {
			if (me.property.elements.length > 0) {
				var next = me.nextItem(me.property);				
				me.setter(me.widget.id, 0, next.id);
			}
		});		
	}
	
	onUpdateValue(property) {
		this.property = property;
		$('#' + this.titleId).html(property.title);
				
		var me = this;
		if (property.type == 'Boolean') {
			if (property.currentValue == 'true') {
				$('#' + this.divid).removeClass('mybutton');
				$('#' + this.divid).addClass('mybutton checked');
				$('#' + this.valueId).html("OFF");
			}
			else {
				$('#' + this.divid).removeClass('mybutton checked');
				$('#' + this.divid).addClass('mybutton');
				$('#' + this.valueId).html("ON");
			}
			
		}
		else {
			$('#' + this.valueId).html(this.nextItem(property).title);
		}
	}
	
	nextItem(property) {
		var list = [];
		for (var e of property.elements) {
			if (!property.disabledOption.includes(property.currentValue)) {
				list.push(e.id);
			}
		}
		
		var index = list.indexOf(property.currentSelectionId);
		var val;
		index++;
		if (index >= list.length) {
			index = 0;
		}
		
		return property.elements[index];
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
		
		var me = this;
		$('#' + this.divid).keydown(function(event) {
			if (event.which == 13) {
				me.setter(me.widget.id, 0, $('#' + me.divid).val());
			}
		});
	}
	
	onUpdateValue(property) {
		$('#' + this.divid).val(property.currentValue);
		this.property = property;
		
		$('#' + this.divid).prop('disabled', !property.enabled);
	}
}

class CheckBox extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		var me = this;
		
		$('#' + this.parent).append('<div id="' + this.divid + '"><input type="checkbox" id="' + this.divid + 'check"><label id="' + this.divid + 'label"></label></div>');
		$('#' + this.divid).click(function() {
			me.setter(me.widget.id, 0, $('#' + me.divid + 'check').prop('checked'));
		});			
	}
	
	onUpdateValue(property) {
		$('#' + this.divid + 'check').prop('checked', property.currentValue == 'true');
		$('#' + this.divid + 'label').text(property.title);
	}
}

class ToggleButton2 extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.labelId = divid + 'label';
		$('#' + parent).append('<div id="' + divid + '"><label id="' + this.labelId + '"></label></div>');
		
		var me = this;
		
		$('#' + divid).click(function() {
			if ($('#' + divid).hasClass('disabled')) {
				return;
			}
			var value = '';
			
			if (me.isList()) {
				value = me.widget.subId;
			}
			else {
				if ($(this).hasClass('mybutton checked')) {
					value = 'false';
				}
				else {
					value = 'true';
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
			
			$('#' + this.divid).removeClass();
			if (property.disabledOption.includes(this.widget.subId)) {
				$('#' + this.divid).addClass('mybutton disabled');
			}
			else if (this.widget.subId == property.currentSelectionId) {
				$('#' + this.divid).addClass('mybutton checked');
			}
			else {
				$('#' + this.divid).addClass('mybutton');
			}
		}
		else {
			$('#' + this.labelId).html(property.title);
			$('#' + this.divid).removeClass();
			if (property.currentValue == 'true') {		
				$('#' + this.divid).addClass('mybutton checked');
			}
			else {
				$('#' + this.divid).addClass('mybutton');
			}
		}
	}
	
	isList() {
		return this.widget.subId != '';
	}	
}

class ToggleButton extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.labelId = divid + 'label';
		$('#' + parent).append('<input type="checkbox" id="' + divid + '"><label id="' + this.labelId + '" for="' + divid + '"></label>');
		var button = $('#' + divid).button();
		
		var me = this;
		
		$('#' + divid).change(function() {
			var value = '';
			
			if (me.isList()) {
				value = me.widget.subId;
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
			$('#' + this.divid).button('option', 'disabled', property.disabledOption.includes(this.widget.subId));
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
			if (!property.disabledOption.includes(e.id)) {
				var option = $('<option>').val(e.id).text(e.title);
				$('#' + this.divid).append(option);		
			}
		}
		$('#' + this.divid).val(property.currentSelectionId);
	}
}

class Chart extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);

		$('#' + parent).append('<div id="' + divid + '"></div>');
		var me = this;
		this.chart = new CanvasJS.Chart(divid, { 
			title: {
				text: ""
			},
			data: [
			{
				type: "line",
				dataPoints: null
			}
			]
		});
		this.chart.render();	
	}
	
	onUpdateValue(property) {
		var me = this;
		var index = 0;
		if (property.currentValue == 'REQUEST_AGAIN') {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + me.widget.id + '&index=' + index + '&ext=1001',
			   success: function(property){
			   		if (property == null) {
			   			return;
			   		}
					var trace = JSON.parse(property.currentValue);
					if (trace == null)return;
					var list = [];
					for (var i = 0; i < trace.y.length; i++) {
						list.push({y: parseFloat(trace.y[i])});
					}	
					me.chart.options.data[0].dataPoints = list;
					me.chart.render();   
			   }
			});
		}	
	}
}

class Table extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);

		this.headers = [];
		
//		this.tableid = 'table' + this.divid;
		$('#' + this.parent).append('<div id="' + this.divid + '"></div>');

		var headers = ['COL#1', 'COL#2', 'COL#3', 'COL#4'];
		this.createTable(headers);
	}
	
	onUpdateValue(property) {
		if (property.currentValue == '') {
			return;
		}
		var table = JSON.parse(property.currentValue);
		
		if (this.headers.length != table.headers.length) {
			this.createTable(table.headers);
		}
		this.headers = table.headers;
	
		var me = this;
				
		this.hot.updateSettings({
		    height: $('#' + me.divid).height()
		});	

		this.hot.loadData(table.data);
		
	}
		
	resize() {
		var me = this;

		this.hot.updateSettings({
		    height: $('#' + me.divid).height()
		});		
	}
			
	createTable(headers) {
//		var height = $('#' + this.divid).prop('height');
		$('#' + this.divid).handsontable({
		  manualColumnResize: true,
		  colHeaders: headers,
		  rowHeaders: true,
		});	
	
		this.hot = $('#' + this.divid).handsontable('getInstance');	
	}
}


class NewLayout {
	constructor(div) {
		var editId = div + "_edit";
		$('#' + div).append('<input type="checkbox" id="' + editId + '"><label>Edit</label>');
		$('#' + editId).click(function() {
			retreiveDesign();
		});
		
		var mainDiv = div + "_mainDiv";
		$('#' + div).append('<div id="' + mainDiv + '"></div>');
		
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
			$('#' + mainDiv).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/getDesign",
			   success: function(design){
			   		build(design, mainDiv);
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
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id="  +  id + "&index=" + index,
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
			   url: "http://" + window.location.host + "/rest/runtime/setValue?id=" + id + "&index=" + index + "&value=" + value,
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
				wrappedWidget = new TabPane(widget, parentDiv, divid, buildSub, addWidget);
			}
			else if (widget.type == 'CheckBox') {	
				wrappedWidget = new CheckBox(widget, parentDiv, divid);
			}
			else if (widget.type == 'TextField') {
				wrappedWidget = new TextField(widget, parentDiv, divid);
			}	
			else if (widget.type == 'ToggleButton') {
				wrappedWidget = new ToggleButton2(widget, parentDiv, divid);				
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
			else if (widget.type == 'Button') {	
				wrappedWidget = new Button(widget, parentDiv, divid);
			}
			else if (widget.type == 'Chart') {	
				wrappedWidget = new Chart(widget, parentDiv, divid);
			}
			else if (widget.type == 'Table') {	
				wrappedWidget = new Table(widget, parentDiv, divid);
			}					
			else if (widget.type == 'Image') {	
				wrappedWidget = new SbImage(widget, parentDiv, divid);
			}
			else if (widget.type == 'Slider') {	
				wrappedWidget = new Slider(widget, parentDiv, divid);
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

			var id = widget.id;
			addWidget(id, wrappedWidget);
			
			if ($('#' + editId).prop('checked')) {
				$('#' + divid).draggable({
					start : function (event , ui){
					} ,
					drag : function (event , ui) {
					} ,
					stop : function (event , ui){
						updatePosition(divid, ui.position.top, ui.position.left);
						
					} 
				});
				$('#' + divid).resizable({
			      stop: function( event, ui ) {
			      	updateSize(divid, ui.size.width, ui.size.height);
			      }
			    }); 
		    }
			
		}
		
		function updateSize(divid, width, height) {
		}
		
		function updatePosition(divid, top, left) {
		}
		
		function getId() {
			return 'div' + me.divNumber++;
		}

	}
}
