class JsWidget {
	constructor(info, parent, selected, dependency) {
		this.info = info;
		
		this.baseId = parent + "-" + info.unique;
		this.selected = selected;
		this.parent = parent;
		
		if ($('#tab-' + this.baseId).length == 1) {
			this.baseId = 'tab-' + this.baseId;
		}
		else {
			$('#' + this.parent).append('<div id=' + this.baseId + '></div>');
		}
			
		this.createBase(dependency);		
		
		this.updateLayout();
	}
	
	get baseId() {
		return this._baseId;
	}
	
	set baseId(baseId) {
		this._baseId = baseId;
	}
	
	getRealBaseId() {
		var tmp = this.baseId.split('-');
		var ret = tmp[tmp.length-1];
		return ret;
	}
	
	setPosition(event) {
		var x = $('#' + this.baseId).position().left; //event.clientX
		var y = $('#' + this.baseId).position().top; // event.clientY
		var me = this;
		
		var div = me.getRealBaseId();
		
		var o = $('#' + this.baseId);
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/move?div="+ me.getRealBaseId() + "&x=" + x + "&y=" + y,
		   success: function(msg){

		   }
		});	
	}
	
	setSize() {
		var width = $('#' + this.baseId).width();
		var height = $('#' + this.baseId).height();
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/resize?div=" + me.getRealBaseId() + "&width=" + width + "&height=" +height,
		   success: function(msg){

		   }
		});	
		
		if (this.subWidget != null) {
			this.subWidget.resize();
		}
	}
		
	createBase(dependency) {			
		var me = this;
		if (this.info.widgetType == 'COMBOBOX') {
			this.subWidget = new JsComboBox(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'RADIOBUTTON') {
			this.subWidget = new JsRadio(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'TEXTFIELD') {
			this.subWidget = new JsTextInput(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'CHECKBOX') {
			this.subWidget = new JsCheckBox(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'TOGGLEBUTTON') {
			this.subWidget = new JsToggleButton(this.baseId, this.info, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'ACTIONBUTTON') {
			this.subWidget = new JsActionButton(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'CHART') {
			this.subWidget = new JsChart(this.baseId, this.info, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'TABLE') {
			this.subWidget = new JsTable(this.baseId, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'PANEL') {
			$('#' + this.baseId).addClass('panel');
			
			if (this.info.layout == 'Flow Layout') {
				$('#' + this.baseId).addClass('Flow');	
			}
			else if (this.info.layout == 'Absolute Layout') {
				$('#' + this.baseId).addClass('Absolute');	
			}
			else if (this.info.layout == 'Vertical Layout') {
				$('#' + this.baseId).addClass('Vertical');	
			}
		}
		else if (this.info.widgetType == 'TAB') {
			this.subWidget = new JsTabPanel(this.baseId, this.info, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}
		else if (this.info.widgetType == 'GUI_DIALOG') {
			this.subWidget = new JsDialogButton(this.baseId, this.info, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}	
		else if (this.info.widgetType == 'LABEL') {
			this.subWidget = new JsLabel(this.baseId, this.info, function(id) {
				me.requestChange(me.info.id, id, dependency);
			});
		}	
				
		$('#' + this.baseId).addClass('base');
		if (this.info.styleClass != null) {
			$('#' + this.baseId).addClass(this.info.styleClass);
		}
		
		this.applyLayout();
	}
	
	applyLayout() {
		if ($('#' + this.baseId).parent().hasClass('Flow')) {
			$('#' + this.baseId).css({'position':'relative', 'display':'inline'});
			$('#' + this.baseId + '>.title').css({'display':'inline'});
			$('#' + this.baseId + '*').css({'margin':'2px'});
			$('#' + this.baseId + '*').css({'padding':'1px'});
		}
		else if ($('#' + this.baseId).parent().hasClass('Absolute')) {
			$('#' + this.baseId).css({'position':'absolute', 'display':'inline-block'});
			$('#' + this.baseId + '>.title').css({'display':'inline-block', 'width':'40%'});
			this.applyPosition();	
		}
		else if ($('#' + this.baseId).parent().hasClass('Vertical')) {
			$('#' + this.baseId).css({'position':'relative', 'display':'block'});	
			$('#' + this.baseId + '>.title').css({'display':'inline-block', 'width':'40%'});
			$('#' + this.baseId + '>.currentValue').css({'display':'inline-block', 'width':'40%'});
			$('#' + this.baseId + '>.unit').css({'display':'inline-block', 'width':'10%'});	
		}
						
		this.applySize();
	
		if (this.info.css != null && this.info.css != '') {
			$('#' + this.baseId).css(this.info.css.split(',')[0] , this.info.css.split(',')[1]);
		}
	}
	
	applyPosition() {
		if (this.info.left != null) {
			$('#' + this.baseId).css('left', this.info.left + 'px');
			$('#' + this.baseId).css('top', this.info.top + 'px');
		}
	}
	
	applySize() {
		if (this.info.width != 0) {
			$('#' + this.baseId).width(this.info.width);
		}
		if (this.info.height != 0) {
			$('#' + this.baseId).height(this.info.height);
		}	
	}
	
	updateValue() {
		var id = this.info.id;
		var me = this;
		
		if (id == '') {
			console.log('id=' + id);
		}
		else {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id,
			   success: function(property){
			   		if (me.subWidget != null) {
			   			$('#' + me.baseId + ' > ').prop('disabled', !property.enabled);
			   			me.subWidget.updateValue(property);
			   		}
			   }
			});
		}
	}
	
	updateLayout() {
		if ((this.info.id == null) || (this.info.id == '')) return;
				
		var id = this.info.id;
		var me = this;

		if (this.info.widgetType == 'GUI_DIALOG') {
			this.subWidget.updateLayout(this.info);
			return;
		}
	
		
		if (id == '') {
			console.log('id=' + id);
		}	
		else {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id,
			   success: function(property){
			   		if (me.subWidget != null) {
			   			me.subWidget.updateLayout(property);
			   			me.applyLayout();
			   		}
			   }
			});	
		}
	}
	
	requestChange(id, value, dependency) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/setValue?id="+id + "&value=" + value,
		   success: function(msg){
				dependency(msg);
		   }
		});	
	}
	
	editable(enabled) {
	    var baseId = this.baseId;
	    var me = this;
	    if (this.info.widgetType == 'PANEL' || this.info.widgetType == 'TABLE' || this.info.widgetType == 'CHART'
	    	|| this.info.widgetType == 'TAB') {	
	    	if (this.info.widgetType == 'PANEL' || this.info.widgetType == 'TAB') {		
				$('#' + this.baseId).draggable({
					start : function (event , ui){
						//console.log("start event start" );
						//console.log(event , ui);
					} ,
					drag : function (event , ui) {
						//console.log("drag event start" );
						//console.log(event , ui);
										} ,
					stop : function (event , ui){
						//console.log("stop event start" );
						//console.log(event , ui);
						me.setPosition(event);
					} 
				});
				
				$('#' + this.baseId).draggable(enabled);		

			    $('#' + this.baseId).droppable({
			      drop: function( event, ui ) {
					
			      }
			    });		    
		    }
			$('#' + this.baseId).resizable({
		      stop: function( event, ui ) {
		      	me.setSize();
		      }
		    });
		    $('#' + this.baseId).resizable(enabled);
	   	}
    	
    	if (enabled == 'enable') {
	    	$('#' + this.baseId).click(function(e){
				$('.base').removeClass('selected');
				$(this).addClass('selected');
				e.stopPropagation();
				me.selected(baseId, me.info);
			});
		}
		else {
			$('.base').removeClass('selected');
			$('#' + this.baseId).off('click');
		}
	}
}