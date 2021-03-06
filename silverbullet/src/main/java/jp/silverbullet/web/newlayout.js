class Widget {
	constructor(widget, parent, divid, ajaxRuntime) {
		this.widget = widget;
		this.divid = divid;
		this.parent = parent;
		this.ajaxRuntime = ajaxRuntime;
	}
	
	longPress(targetDiv, action) {
		var LONGPRESS = 1500;
		var timerId;
		var me = this;
		$('#' + targetDiv).on("mousedown touchstart",function() {
			timerId = setTimeout(function() {
		  		me.selector(me.widget.id, 0, action);
			}, LONGPRESS);
		}).on("mouseup mouseleave touchend",function() {
		  clearTimeout(timerId);
		});
	}
	
	accessor(setter, getter, selector) {
		this.setter =  setter;
		this.getter = getter;
		this.selector = selector;
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
	
	set actionEnabled(actionEnabled) {
		this._actionEnabled = actionEnabled
	}
	
	get actionEnabled() {
		return this._actionEnabled;
	}
}

class RegisterShortcut extends Widget {
	constructor(widget, parent, divid, buildSub, registerPath) {
		super(widget, parent, divid);
		
		$('#' + parent).append('<div id="' + divid + '"></div>');
		
//		$('#' + parent).css('border-style', 'dashed');
//		$('#' + parent).css('border-width', '1');
//		$('#' + parent).css('border-color', 'yellow');
		
		
		$('#' + divid).click(function() {
			if (widget.optional == '') {
				return;
			}
			
			var addr = widget.optional.split('@')[1];
			var bit = widget.optional.split('@')[0];
			
			$.ajax({
			   type: "GET", 
			   url: registerPath + "/triggerShortcut?regName=" + addr + "&bitName=" + bit,
			   success: function(options){
		
			   }
			});				
		});
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
		
		this.sliderId = divid + "_slider";
		//$('#' + parent).append('<div id="' + divid + '"><div id="' + this.customId + '" class="ui-slider-handle"></div></div>');

		this.minVal = divid + "_min";
		this.maxVal = divid + "_max";
		this.centerVal = divid + "_center";
		
		var tableId = divid + "_table";
		$('#' + parent).append('<div id="' + divid + '"><div>');
		$('#' + divid).append('<div><table id="' + tableId + '"><tr><td align="left" id="' + this.minVal + '"></td><td id="' + this.centerVal + '"></td><td align="right" id="' + this.maxVal + '"></td></tr></table></div>');
		//$('#' + divid).append('<div><div id="' + this.minVal + '"></div><div id="' + this.maxVal + '"></div></div>');
		
		$('#' + tableId).css('width', '100%');
		$('#' + tableId + " tr td").css('border-style', 'none');
		
		$('#' + divid).append('<div id="' + this.sliderId + '"></div>');
		
//		$('#' + this.minVal).css('text-aligh', 'left');
//		$('#' + this.minVal).css('float', 'left');
		$('#' + this.minVal).css('width', '33%');
//		$('#' + this.minVal).css('display', 'inline');
		
//		$('#' + this.maxVal).css('text-aligh', 'right');
		$('#' + this.maxVal).css('width', '33%');
//		$('#' + this.maxVal).css('float', 'right');
//		$('#' + this.maxVal).css('display', 'inline');
		
		$('#' + this.sliderId).css("width", "100%");
		$('#' + this.sliderId).css("height", "50%");
		
		this.prevMin = -100;
		this.prevMax = 100;
		this.prevStep = 1;
		
		var me = this;
//	    this.handle = $( "#" + this.customId);
		var stack = [];
	    $( "#" + this.sliderId ).slider({
	    	value: 0,
	    	min: me.prevMin,
	    	max: me.prevMax,
	    	step: me.prevStep,
	      create: function() {
//	        me.handle.text( $( this ).slider( "value" ) );
	      },
	      slide: function( event, ui ) {
	        //me.setter(me.widget.id, 0, ui.value);
	        stack.push(me.widget.id + ";" + ui.value);
	      }
	    });
	    
	    var timer = setInterval(function() {
	    	if (stack.length == 0) return;
	    	var id = stack[stack.length - 1].split(';')[0];
	    	var value = stack[stack.length - 1].split(';')[1];
	    	me.setter(id, 0, value);
	    	stack = [];
	    }, 200);
	}
	
	onUpdateValue(property) {
		if (this.dragging == true) {
			return;
		}
		var range = false;
		if (this.prevMax != property.max) {
			$('#' + this.sliderId).slider("option", "max", Number(property.max));
			$('#' + this.maxVal).text(property.max);
//			$('#' + this.centerVal).text((property.min + property.max)/2.0);
			range = true;
		}
		if (this.prevMin != property.min) {
			$('#' + this.sliderId).slider("option", "min", Number(property.min));
			$('#' + this.minVal).text(property.min);
//			$('#' + this.centerVal).text((property.min + property.max)/2.0);
			range = true;
		}

		if (this.prevStep != property.decimals) {
			var step = "0.";
			for (var i = 0; i < property.decimals - 1; i++) {
				step += "0";
			}
			step += "1";
			if (step == "0.") {
				step = "0";
			}
			$('#' + this.sliderId).slider("option", "step", Number(step));
		}
		$('#' + this.sliderId).slider("option", "value", Number(property.currentValue));
		
//		this.handle.text($('#' + this.divid).slider('value'));
		
		$('#' + this.centerVal).text(property.title + " " + property.currentValue);
		
		if (range) {
			$("input[type='range']").slider( "refresh" );
		}
				
		this.prevMin = property.min;
		this.prevMax = property.max;
		this.prevStep = property.decimals;
	}
}

class SbImage extends Widget {
	constructor(widget, parent, divid, ajaxRuntime) {
		super(widget, parent, divid, ajaxRuntime);
		
		this.canvasId = divid + "canvas";
		$('#' + parent).append('<div id="' + divid + '" width="100%" height="100%"><canvas id="' + this.canvasId + '"></canvas></div>');
		
		this.canvas = $("#" + this.canvasId)[0];
		var ctx = this.canvas.getContext('2d');
		this.image = new Image();
		var me = this;
		this.image.onload = function() {
			var xScale = me.canvas.clientWidth / me.image.naturalWidth;
			var yScale = me.canvas.clientHeight / me.image.naturalHeight;
			
			ctx.drawImage(me.image, 0, 0, me.image.naturalWidth, me.image.naturalHeight, 
				0, 0, me.image.naturalWidth * xScale, me.image.naturalHeight * yScale);
		}
	}
	
	onUpdateValue(property) {
		if (property.currentValue == '') {
			return;
		}
		
		var me = this;
		var wrapper = $('#' + me.divid)[0];
		
		var canvasWidth = me.canvas.width;
		var divWidth = $('#' + me.divid).width();
		if (me.canvas.width != $('#' + me.divid).width()) {
			$('#' + this.canvasId).attr('width', $('#' + me.divid).width());
			$('#' + this.canvasId).attr('height', $('#' + me.divid).height());
		}
			
		me.ajaxRuntime("getBlob?id=" + property.id + "&name=" + property.currentValue, function(blob) {
			me.image.src = blob.data;
		});		
	}
}

class StaticText extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		$('#' + parent).append('<div id="' + this.divid + '">' + widget.text + '</div>');
	}
	
	onUpdateValue(property) {
	}
}

class Dialog extends Widget {
	constructor(widget, parent, divid, retreiveDesignDialog) {
		super(widget, parent, divid);
		
		$('#' + parent).append('<div id="' + divid + '" class="mybutton canpush"></div>');
		
		this.dialogId = divid + '_dialog';
	
		$('#' + parent).append('<div id="' + this.dialogId + '"></div>');
		
		var width = 600;
		var height = 400;
	
		var me = this;
				
				
		$('#' + divid).click(function() {
			$('#' + me.dialogId).dialog({
				  autoOpen: false,
				  title: me.property.title,
				  closeOnEscape: false,
				  modal: false,
				  buttons: {
				    "OK": function(){
				    	$('#' + me.dialogId).empty();
				    	$(this).dialog('destroy');
				    	me.setter(me.widget.id, 0, 'true');
				    },
				    "Cancel": function(){
				    	$('#' + me.dialogId).empty();
				    	$(this).dialog('destroy');
				    }
				  },
				width: width,
				height: height
			});
		
			if (me.actionEnabled) {
				$('#' + me.dialogId).empty();
				$('#' + me.dialogId).dialog('open');
				
				if (widget.optional.startsWith('$CONTENT')) {
					var name = widget.optional.split('=')[1];
					retreiveDesignDialog(name, me.dialogId);
				}
			}
		});
		
		function alternative(property) {
			if (property.currentValue == 'true') { 
				return 'false';
			}
			else {
				return 'true';
			}
		}
	}
	
	onUpdateValue(property) {
		$('#' + this.divid).text(property.title);
		this.property = property;
//		$('#' + this.dialogId).dialog('option', 'title', property.title);
		
//		$('#' + me.dialogId).dialog('title', property.title);
		
	}
}

class Button extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.titleId = divid + 'title';
		this.valueId = divid + 'value';

		$('#' + parent).append('<div id="' + divid + '" class="mybutton canpush"></div>');
		var tableId = divid + "_tableId";
		$('#' + divid).append('<table id="' + tableId + '"></table>');
		$('#' + tableId).css('width', '100%');
		$('#' + tableId).css('height', '100%');
			
		if (widget.optional == 'notitle') {	
			$('#' + tableId).append('<tr><td><label id="' + this.valueId + '">' + '</label></td></tr>');
		}
		else {
			$('#' + tableId).append('<tr><td><label id="' + this.titleId + '" class="buttonTitle"></label></td></tr>');
			$('#' + tableId).append('<tr><td><label id="' + this.valueId + '" class="buttonValue"></label> </td></tr>');
		}
		
		$('#' + tableId + ' tr td').css('border', '0');
		$('#' + tableId + ' tr td').css('font', 'inherit');
		
		var me = this;

		$('#' + this.divid).click(function() {
			if (!$('#' + divid).hasClass('canpush')) {
				return;
			}
			
			if (me.property.type == 'List') {
				var next = me.nextItem(me.property);				
				me.setter(me.widget.id, 0, next.id);
			}
			else {
				me.setter(me.widget.id, 0, me.alternative(me.property));
			}
		});		
		this.longPress(divid, 'WAITFOR');
	}
	
	onUpdateValue(property) {
		this.property = property;
		$('#' + this.titleId).html(property.title);
				
		$('#' + this.divid).removeClass('disabled');
		$('#' + this.valueId).removeClass('disabled');
		$('#' + this.divid).addClass('canpush');
		
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
		else if (property.type == 'Action') {
		}
		else {
			$('#' + this.valueId).html(this.nextItem(property).title);
		}
		
		if(!property.enabled) {
			$('#' + this.divid).removeClass('canpush');
			$('#' + this.divid).addClass('disabled');
			$('#' + this.valueId).addClass('disabled');
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
	
	alternative(property) {
		if (property.currentValue == 'true') { 
			return 'false';
		}
		else {
			return 'true';
		}
	}
}

class Label extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		var me = this;
		
		this.labelId = divid + "_label";
		$('#' + this.parent).append('<div id="' + this.divid + '"><label id="' + this.labelId + '"></label></div>');	

		this.longPress(this.labelId, 'JUDGE');
	}
	
	onUpdateValue(property) {
		if (this.widget.field == 'TITLE') {
			$('#' + this.labelId).text(property.title);	
		}
		else if (this.widget.field == 'VALUE') {
			if (property.type == 'Boolean') {
				$('#' + this.labelId).text(property.currentValue == 'true' ? 'ON' : 'OFF');
			}
			else {
				$('#' + this.labelId).text(property.currentValue);	
			}
		}
		else if (this.widget.field == 'UNIT') {
			$('#' + this.labelId).text(property.unit);	
		}
		else if (this.widget.field == 'MIN') {
			$('#' + this.labelId).text(property.min);	
		}
		else if (this.widget.field == 'MAX') {
			$('#' + this.labelId).text(property.max);	
		}
		$('#' + this.labelId).css('display', 'inline-block');
	}
}

class TextField extends Widget {
	constructor(widget, parent, divid, type) {
		super(widget, parent, divid);
		this.textId = divid + "_textid";
		$('#' + this.parent).append('<div id="' + this.divid + '"><input type="' + type + '" id="' + this.textId + '"></div>');
		$('#' + this.textId).css("width", "100%");
		$('#' + this.textId).css("height", "100%");

		var me = this;
		
		$('#' + me.textId).keyboard({
			layout: 'qwerty',
			openOn: 'click',
			accepted : function(event, keyboard, el) {
				me.setter(me.widget.id, 0, $('#' + me.textId).val());
				$(this).removeClass();
			}
		});
	}
	
	onUpdateValue(property) {
		var str;
		if (this.widget.field == 'VALUE') {
			str  = property.currentValue;
		}
		else if (this.widget.field == 'TITLE') {
			str = property.title;
		}
		else if (this.widget.field == 'UNIT') {
			str = property.unit;
		}
				
		$('#' + this.textId).val(str);
		
		this.property = property;
		
		$('#' + this.textId).prop('disabled', !property.enabled);
		if (!property.enabled) {
			$('#' + this.textId).css('background-color', '#E6E6E6');
			$('#' + this.textId).css('color', '#808080');
		}
		else  {
			$('#' + this.textId).css('background-color', 'white');
			$('#' + this.textId).css('color', 'black');
		}

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

class ToggleButton extends Widget {
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
		if(property.enabled) {
			$('#' + this.divid).removeClass('disabled');
		}
		if (this.isList()) {
			for (var e of property.elements) {
				if (e.id == this.widget.subId) {
					$('#' + this.labelId).html(e.title);
				}
			}
			
			$('#' + this.divid).removeClass('mybutton disabled checked');
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
			$('#' + this.divid).removeClass('mybutton disabled checked');
			if (property.currentValue == 'true') {		
				$('#' + this.divid).addClass('mybutton checked');
			}
			else {
				$('#' + this.divid).addClass('mybutton');
			}
		}
		
		if(!property.enabled) {
			$('#' + this.divid).addClass('disabled');
		}
	}
	
	isList() {
		return this.widget.subId != '';
	}	
}

class ComboBox extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);
		
		this.comboId = divid + "_combo";
		$('#' + this.parent).append('<div id="' + divid + '"><select id="' + this.comboId + '"></select></div>');
		$('#' + this.comboId).css("width", "100%");
		$('#' + this.comboId).css("height", "100%");
		var me = this;
		$('#' + this.comboId).change(function() {
			me.setter(me.widget.id, 0, $(this).val());
		});
		
		this.longPress(this.comboId, 'WAITFOR');
	}
	
	onUpdateValue(property) {
		$('#' + this.comboId).empty();
		for (var e of property.elements) {
			if (!property.disabledOption.includes(e.id)) {
				var option = $('<option>').val(e.id).text(e.title);
				$('#' + this.comboId).append(option);		
			}
		}
		$('#' + this.comboId).val(property.currentSelectionId);
		
		$('#' + this.comboId).prop('disabled', !property.enabled);
	}
}

class ChartMine extends Widget {
	constructor(widget, parent, divid, ajaxRuntime) {
		super(widget, parent, divid, ajaxRuntime);
		
		$('#' + parent).append('<div id="' + divid + '"></div>');
		
		var width = 300;
		var height = 200;
		for (var css of widget.css) {
			if (css.key == "width") {
				width = css.value;
			}
			else if (css.key == "height") {
				height = css.value;
			}
		}
		this.chart = new MyChart(divid, width, height);
	
	}
	
	onUpdateValue(property) {
		var me = this;

		this.timeoutid = setTimeout(updateChart, 10);
		
		function updateChart() {			
			var widget = me.widget;
			var index = 0;
			var chart = me.chart;
			
			if (property.currentValue == 'REQUEST_AGAIN') {
				me.ajaxRuntime("getProperty?id=" + widget.id + '&index=' + index + '&ext=' + chart.getDataPoints(), function(property) {
			   		if (property == null) {
			   			return;
			   		}
					var trace = JSON.parse(property.currentValue);
					if (trace != null) {
 						me.chart.update(trace);
 					}
				});
			}	

		}
	}
}

class Chart extends Widget {
	constructor(widget, parent, divid, ajaxRuntime) {
		super(widget, parent, divid, ajaxRuntime);

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
			me.ajaxRuntime("getProperty?id=" + me.widget.id + '&index=' + index + '&ext=1001', function(property) {
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
			});
		}	
	}
}

class Table extends Widget {
	constructor(widget, parent, divid) {
		super(widget, parent, divid);

		this.headers = [];
		
		$('#' + this.parent).append('<div id="' + this.divid + '"></div>');

		var headers = ['COL#1', 'COL#2', 'COL#3', 'COL#4'];
		this.createTable(headers);
	}
	
	onUpdateValue(property) {
		if (property.currentValue == '') {
			return;
		}
		var me = this;
		
		me.ajaxRuntime("getProperty?id=" + me.widget.id + '&index=' + index, function(property) {
	   		if (property == null) {
	   			return;
	   		}
			var table = JSON.parse(property.currentValue);
			
			if (me.headers.length != table.headers.length) {
				me.createTable(table.headers);
			}
			me.headers = table.headers;
					
			this.hot.updateSettings({
			    height: $('#' + me.divid).height()
			});	
	
			me.hot.loadData(table.data);
		});		
	}
		
	createTable(headers) {
		$('#' + this.divid).handsontable({
		  manualColumnResize: true,
		  colHeaders: headers,
		  rowHeaders: true,
		});	
	
		this.hot = $('#' + this.divid).handsontable('getInstance');	
	}
}

class Table2 extends Widget {
	constructor(widget, parent, divid, ajaxRuntime) {
		super(widget, parent, divid, ajaxRuntime);
		
		$('#' + this.parent).append('<div id="' + this.divid + '"></div>');
		$('#' + this.divid).css('font', 'inherit');
		
		var me = this;
		this.table = new JsMyTable2(this.divid,
			function() { // row count
				return me.data.data.length;
			},
			function() { // col count
				return me.data.headers.length;
			},
			function(col) { // title
				return me.data.headers[col];
			},
			function(row, col) { // value
				return me.data.data[row][col];
			},
			function(row, col) {
				me.data.selectedRow = row;
				me.data.dataChanged = false;
				me.setter(widget.id, 0, JSON.stringify(me.data));
			}
		);
	}
	
	onUpdateValue(property) {
		if (property.currentValue == '') {
			return;
		}
		var me = this;
		var index = 0;
		me.ajaxRuntime("getBlob?id=" + me.widget.id + '&index=' + index, function(blobData) {
	   		if (blobData == null) {
	   			return;
	   		}
			me.data = JSON.parse(blobData.data);
			
			if (me.data.dataChanged) {
				if (me.data.structureChanged) {
					me.table.build();
				}
				me.table.update();
			}
			me.table.selectRow(me.data.selectedRow);
		});		

	}
}

class NewLayout {
	constructor(divParent, rootName, device, serialNo, listener, application) {
		var propertyMap = new Map();
		var widgetMap = new Map();
		var divMap = new Map();

		var me = this;

		this.serialNo = serialNo;
		if (device == null) {
			this.device = 'NO_DEVICE';
		}
		else {
			this.device = device;
		}
		
		this.basePath = "//" + window.location.host + "/rest";
		this.newGuiPath = this.basePath + "/" + application + "/newGui";
		this.runtimePath = this.basePath + "/runtime/" + application + "/" + this.device + "/" + this.serialNo;
		this.domainPath = this.basePath + "/" + application + "/domain";
		this.registerPath = this.basePath + "/" + application + "/register2";	

		var div;
		
		var divBase = divParent + "_newLayout";
		if (rootName == null) {
			$('#' + divParent).append('<div id="' + divBase + '"></div>');
			
			var editId = divBase + "_edit";
			var actionId = divBase + "_action";
			var linkId = divBase + "_link";
			
			div = divBase + "_right";
		}
		else {
			div = divParent + "_div";
			$('#' + divParent).append('<div id="' + div + '"></div>');
		}
		
		if (rootName == null) {
			var divLeft = divParent + "_left";
			
//			$('#' + divBase).append('<a href="./runtime.html">runtime</a>');
			$('#' + divBase).append('<div id="' + divLeft + '"></div><div id="' + div + '"></div>');
			$('#' + divLeft).css('display', 'inline-block');
			$('#' + divLeft).css('width', '100px');
			$('#' + divLeft).css('height', '1000px');
			$('#' + divLeft).css('vertical-align', 'top');
			$('#' + divLeft).css('border-style', 'solid');
			
			$('#' + div).css('display', 'inline-block');
			$('#' + div).css('width', '90%');
			$('#' + div).css('height', '1000px');
			$('#' + div).css('vertical-align', 'top');
			$('#' + div).css('border-style', 'solid');
		}
		
		me.currentRoot = "";
		
		var valueHandler = function(msg) {
			var ids = msg.split(',');
			for (var idindex of ids) {
				var id = idindex.split('#')[0];
				var index = idindex.split('#')[1];
				retrieveProperty(id, index, function(property) {
					propertyMap[property.id] = property;
					for (var widget of getWidget(property.id)) {
						widget.onUpdateValue(property);
					}
				});
			}
		};
		
		var domain = me.device;
		if (me.serialNo != null) {
			domain += '.' + me.serialNo;
		}
		websocket.addListener('VALUES@' + domain, valueHandler);

		var messageDialogId = div + "_messageDialog";
		var messageId = div + "_message";
		var messageHandler = function(msg) {
			if (msg == '@CLOSE@') {
				$('#' + messageDialogId).dialog('close');
			}
			else {
				var obj = JSON.parse(msg);
				$('#' + messageDialogHtmllId).html(obj.html);

				$('#' + messageDialogControlId).empty();
				for (var c of obj.controls.controls) {
					$('#' + messageDialogControlId).append('<button id="' + c.id + '">' + c.title + '</button>');
					$('#' + c.id).click(function() {
//						$('#' + messageDialogId).dialog('close');
						replyMessage(obj.messageId, $(this).prop('id'));
					});
				}
				
				$('#' + messageDialogId).dialog('open');
				$('#' + messageDialogId).dialog('option', 'position',
					{ my: 'left top', at: 'left top', of: $('#' + div)}
				);
			}
		}
		
		websocket.addListener('MESSAGE@' + me.device, messageHandler);
		
		$('#' + divParent).bind('DOMSubtreeModified', function(){
			if( $('#' + divParent).is(':empty') ) {  
				websocket.removeListener(valueHandler);
				websocket.removeListener(messageHandler);
				websocket.removeListener(designHandler);
			}
		});		
		
		this.retreiveDesign = function(finished) {
			widgetMap.clear();
			divMap.clear();
			
			$('#' + mainDiv).empty();
			var link = $('#' + linkId).prop('checked') || !$('#' + editId).prop('checked');
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/getDesign?root=" + me.currentRoot + "&link=" + link,
			   success: function(pane){
			   		build(pane, mainDiv);
			   		if (finished != null) {
			   			finished();
			   		}
			   }
			});	
		};
		
		if (rootName == null) {
			this.library = new NewLayoutLibrary(divLeft, function(selectedRoot) {
				me.currentRoot = selectedRoot;
				me.retreiveDesign();
			}, me.newGuiPath);

			$('#' + div).append('<input type="checkbox" id="' + editId + '"><label>Edit</label>');
			$('#' + editId).click(function() {
				me.retreiveDesign();
				$('#' + actionId).prop('disabled', !$('#' + editId).prop('checked'));
			});		
			
			$('#' + div).append('<input type="checkbox" id="' + actionId + '"><label>Action</label>');
			$('#' + actionId).prop('checked', true);
			$('#' + actionId).click(function() {
				me.retreiveDesign();
			});	
	
			$('#' + div).append('<input type="checkbox" id="' + linkId + '"><label>Link</label>');
			$('#' + linkId).prop('checked', false);
			$('#' + linkId).click(function() {
				me.retreiveDesign();
			});
					
			var defaultValueId = div + "_default";
			$('#' + div).append('<button id="' + defaultValueId + '">Default</button>');
			$('#' + defaultValueId).click(function() {
				$.ajax({
				   type: "GET", 
				   url: me.runtimePath + "/defaultValues",
				   success: function(widget){
	
				   }
				});	
			});
			
			new DependencyHistory(div);
			
			this.propertyWindow = new NewLayoutProperty(div, me.newGuiPath, me.registerPath, application);
					
			var devicesId = div + "_devices";
			$('#' + div).append('<select id="' + devicesId + '"></select>');
			$('#' + devicesId).change(function() {
				me.device = $(this).val();
				websocket.changeType(valueHandler, 'VALUES@' + me.device);
				websocket.changeType(messageHandler, 'MESSAGE@' + me.device);
			});

			websocket.addListener('DEVICE', function(msg) {
				updateDevicesList();
			});		
			
			updateDevicesList();
			function updateDevicesList() {	
				$.ajax({
					type: "GET", 
					url: me.domainPath + "/devices",
					success: function(msg){
						$('#' + devicesId).empty();
						for (var o of msg) {
							$("#" + devicesId).append($("<option>").val(o).text(o));
							if (me.device == null) {
								me.device = o;
								valueWebSocket.changeType('VALUES@' + me.device);
								messageWebSocket.changeType('MESSAGE@' + me.device);
							}
						}
						$('#' + devicesId).val(me.device);
				   }
				});	
			}		
		}
		else {
			getDesignByName(rootName, true, true, listener);
		}
		
		var mainDiv = div + "_mainDiv";
		$('#' + div).append('<div id="' + mainDiv + '"></div>');
		
		this.divNumber = 0;
		
		var designHandler = function(msg) {
			var type = msg.split(':')[0];
			var value = msg.split(':')[1];
			var tmp = value.split(',');
			var divid = tmp[0];
			var var1 = tmp[1];
			var var2 = tmp[2];
			
			if (type == 'CSS') {
				retreiveWidget(divid, function() {
					$('#' + divMap.get(divid).widgetId).css(var1, var2);
					selectEditable(divid);
				});
				
			}
			else if (type == 'TYPE' || type == 'ID' || type == 'FIELD'|| type == 'LAYOUT') {
				me.retreiveDesign(function() {
					selectEditable(divid);
				});
			}		

		};
		websocket.addListener('UIDESIGN', designHandler);
						
		var messageDialogHtmllId = messageDialogId + "_html";
		var messageDialogControlId = messageDialogId + "_control";
		
		$('#' + div).append('<div id="' + messageDialogId + '"><label id="' + messageId + '"></label></div>');
		$('#' + messageDialogId).append('<div id="' + messageDialogHtmllId + '">control</div>');
		$('#' + messageDialogId).append('<div id="' + messageDialogControlId + '">control</div>');
		
		$('#' + messageDialogId).dialog({
			  autoOpen: false,
			  title: "Message",
			  closeOnEscape: false,
			  modal: false,
/*			  buttons: {
			    "OK": function(){
			    	replyDialog("", "OK");
			    	$(this).dialog('close');
			    	
			    },
			    "Cancel": function(){
			    	replyDialog("", "Cancel");
			    	$(this).dialog('close');
			    }
			  },
*/
			width: 600,
			height: 400
		});
				
		function replyDialog(messageId, reply) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/replyDialog?messageId=" + messageId + "&reply=" + reply,
			   success: function(widget){

			   }
			});	
		}
		
		function replyMessage(messageId, reply) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/replyMessage?messageId=" + messageId + "&reply=" + reply,
			   success: function(widget){

			   }
			});	
		}
		
		function retreiveWidget(divid, finished) {
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/getWidget?divid=" + divid,
			   success: function(widget){
			   		divMap.set(divid, widget);
			   		if (me.propertyWindow != null) {
			   			me.propertyWindow.update(divMap.get(divid));
			   		}
			   		if (finished != null) {
			   			finished();
			   		}
			   }
			});	
		}
		
		function getWidget(id) {
			if (widgetMap.get(id) == null) {
				return [];
			}
			return widgetMap.get(id);
		}
		
		function getWidgetByDiv(divid, result) {
			var ret = divMap.get(divid);
			if (ret != null) {
				result(ret);
			}
			else {
				retreiveWidget(divid, function() {
					result(divMap.get(divid));
				});
			}
		}
				
		function retreiveDesignDialog(name, div) {			
			var link = $('#' + linkId).prop('checked') || !$('#' + editId).prop('checked');
			//getDesignByName(name, link, false, function(height){});
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/getDesign?root=" + name + "&link=" + link + "&initPos=false",
			   success: function(pane){
			   		//listener(getCss(pane.css, 'height'));
			   		build(pane, div);
			   }
			});	
		}
				
		function getDesignByName(name, link, initPos, listener) {
			$.ajax({
			   type: "GET", 
			   //url: me.newGuiPath + "/getDesignByName?name=" + name + "&link=" + link + "&initPos=" + initPos,
			   url: me.newGuiPath + "/getDesign?root=" + name + "&link=" + link + "&initPos=" + initPos,
			   success: function(pane){
			   		listener(getCss(pane.css, 'height'));
			   		build(pane, div);
			   }
			});	
		}
		
		function getCss(csses, key) {
			for (var css of csses) {
				if (css.key == key) {
					return css.value;
				}			
			}
		}
		
		function ajaxRuntimePath(path, result) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/" + path,
			   success: function(msg){
			   	result(msg);
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
		
		function selectWidget(id, index, action) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/justSelect?id="  +  id + "&index=" + index + "&action=" + action,
			   success: function(property){
			   	
			   }
			});		
		}
		
		function retrieveProperty(id, index, callback) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/getProperty?id="  +  id + "&index=" + index,
			   success: function(property){
			   		callback(property);
			   }
			});		
		}
		
		function addWidget(id, widget) {
			divMap.set(widget.widget.widgetId, widget.widget);
		
			if (id == '') {
				return;
			}
			
			if (widgetMap.get(id) == null) {
				widgetMap.set(id, []);
			}
			widgetMap.get(id).push(widget);
		}
		
		function setValue(id, index, value) {
			$.ajax({
			   type: "GET", 
			   url: me.runtimePath + "/setValue?id=" + id + "&index=" + index + "&value=" + value,
			   success: function(property){
			   }
			});			
		}
		
		function selectEditable(divid) {			
			getWidgetByDiv(divid, function() {
				$('.editable').removeClass('editableSelected');
				$('#' + divid).addClass('editableSelected');
				me.propertyWindow.update(divMap.get(divid));
			});
		}
		
		function trimDivid(divid) {
			//return divid;
			var tmp = divid.split('-');
			return tmp[tmp.length-1];
		}
		
		function build(pane, div) {
			buildSub(pane, div);
			
			for (var [id, val] of widgetMap) {
				if (id == '') {
					continue;
				}
				
				getProperty(id, 0, function(prop) {
					for (var w of widgetMap.get(prop.id)) {
						w.onUpdateValue(prop);
					}
				});
			}
			if ($('#' + editId).prop('checked')) {
				me.propertyWindow.show();
				$('.editable').click(function(e) {
					$('.editable').removeClass('editableSelected');
					$(this).addClass('editableSelected');
					e.stopPropagation();
					
					var divid = trimDivid($(this).prop('id'));
					
					me.propertyWindow.update(divMap.get(divid));
				});
			}
			else {
				if (me.propertyWindow != null) {
					me.propertyWindow.hide();
				}
			}
		}
		
		function buildSub(widget, parentDiv) {
			// adding parentDiv in order to avoid ID duplication. for Dialog pane.
			var divid = parentDiv + '-' + widget.widgetId;

			if (parentDiv.includes('dialog')) {
				divid = "dialog_" + divid;
			}
			var wrappedWidget = new Widget(widget, parentDiv, divid);

			if (widget.type == 'Pane') {
				wrappedWidget = new Pane(widget, parentDiv, divid, buildSub);
			}
			else if (widget.type == 'Debug_Register') {
				wrappedWidget = new RegisterShortcut(widget, parentDiv, divid, buildSub, me.registerPath);
			}		
			else if (widget.type == 'TabPane') {
				wrappedWidget = new TabPane(widget, parentDiv, divid, buildSub, addWidget);
			}
			else if (widget.type == 'CheckBox') {	
				wrappedWidget = new CheckBox(widget, parentDiv, divid);
			}
			else if (widget.type == 'TextField') {
				wrappedWidget = new TextField(widget, parentDiv, divid, 'text');
			}
			else if (widget.type == 'Password') {
				wrappedWidget = new TextField(widget, parentDiv, divid, 'password');
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
			else if (widget.type == 'Button') {	
				wrappedWidget = new Button(widget, parentDiv, divid);
			}
			else if (widget.type == 'Chart') {	
				wrappedWidget = new ChartMine(widget, parentDiv, divid, ajaxRuntimePath);
			}
			else if (widget.type == 'Table') {	
				wrappedWidget = new Table2(widget, parentDiv, divid, ajaxRuntimePath);
			}					
			else if (widget.type == 'Image') {	
				wrappedWidget = new SbImage(widget, parentDiv, divid, ajaxRuntimePath);
			}
			else if (widget.type == 'Slider') {	
				wrappedWidget = new Slider(widget, parentDiv, divid);
			}
			else if (widget.type == 'Dialog') {	
				wrappedWidget = new Dialog(widget, parentDiv, divid, retreiveDesignDialog);
			}
			
			var actionEnabled = !$('#' + editId).prop('checked') || $('#' + actionId).prop('checked');							
			wrappedWidget.accessor(
				function(id, index, value) {
					if (!actionEnabled) {
						return;
					}
					setValue(id, index, value);
				},
				function(id, index, callback) {
					getProperty(id, index, callback);
				},
				function(id, index, action) {
					selectWidget(id, index, action);
				}
			);
			
			wrappedWidget.actionEnabled = actionEnabled;
			
			var id = widget.id;
			addWidget(id, wrappedWidget);
			
			for (var css of widget.css) {
				$('#' + divid).css(css.key, css.value);			
			}
			
			if ($('#' + editId).prop('checked')) {

				$('#' + divid).draggable({
					start : function (event , ui){
				
					} ,
					drag : function (event , ui) {
			
					} ,
					stop : function (event , ui){
						updatePosition(trimDivid(divid), $('#' + divid).position().top + "px", $('#' + divid).position().left + "px");						
					},
				});

				$('#' + divid).resizable({
			      stop: function( event, ui ) {
			      	updateSize(trimDivid(divid), ui.size.width, ui.size.height);
			      },
			      grid: 5, 
			    }); 
					
				$('#' + divid).droppable({
					greedy: true,
					drop: function( event, ui ) {
			           changeParent(ui.draggable[0].id, trimDivid(divid));
			           event.stopPropagation();
			        }
			    });	
	    
			    $('#' + divid).addClass('editable');

		    }

		}
		
		function changeParent(divid, parentId) {
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/changeParent?divid=" + divid + "&parent=" + parentId,
			   success: function(ret){
			   }
			});	
		}
		
		function updateSize(divid, width, height) {
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/setSize?divid=" + divid + "&width=" + width + "&height=" + height,
			   success: function(ret){
			   }
			});	
		}
		
		function updatePosition(divid, top, left) {
			$.ajax({
			   type: "GET", 
			   url: me.newGuiPath + "/move?divid=" + divid + "&top=" + top + "&left=" + left,
			   success: function(ret){
			   }
			});	
		}
		
		function getId() {
			return 'div' + me.divNumber++;
		}

	}
	
	rebuild(application) {
		this.newGuiPath = this.basePath + "/" + application + "/newGui";
		this.runtimePath = this.basePath + "/runtime/" + application + "/" + this.device + "/" + this.serialNo;
		this.domainPath = this.basePath + "/" + application + "/domain";
		this.registerPath = this.basePath + "/" + application + "/register2";	
		this.library.path(this.newGuiPath);
		this.propertyWindow.path(this.newGuiPath, this.registerPath, application);
		
		this.library.update();
	}
	
	setRuntimePath(path) {
		this.runtimePath = this.basePath + "/" + path;
	}
}
