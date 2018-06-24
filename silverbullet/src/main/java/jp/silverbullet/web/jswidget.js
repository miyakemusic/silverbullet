class JsWidget {
	constructor(info, parent, parentLayout, selected, layout) {
		this.info = info;
		
		this.baseId = 'base-' + info.unique;
		this.selected = selected;
		this.parent = parent;
		this.parentLayout = parentLayout;
		this.layout = layout;
		
		this.createBase(selected, layout);		
		
		this.updateLayout();
	}
	
	get baseId() {
		return this._baseId;
	}
	
	set baseId(baseId) {
		this._baseId = baseId;
	}
	
	setPosition(event) {
		var x = $('#' + this.baseId).position().left; //event.clientX
		var y = $('#' + this.baseId).position().top; // event.clientY
		
		var o = $('#' + this.baseId);
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/move?div="+this.baseId + "&x=" + x + "&y=" + y,
		   success: function(msg){

		   }
		});	
	}
	
	setSize() {
		var width = $('#' + this.baseId).width();
		var height = $('#' + this.baseId).height();
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/resize?div="+this.baseId + "&width=" + width + "&height=" +height,
		   success: function(msg){

		   }
		});	
	}
		
	createBase(selected, layout) {			
		$('#' + this.parent).append('<div id=' + this.baseId + '></div>');	
		$('#' + this.baseId).addClass('base');
		
		var me = this;
		if (this.info.widgetType == 'COMBOBOX') {
			this.subWidget = new JsComboBox(this.baseId, function(id) {
				me.requestChange(me.info.id, id);
			});
		}
		else if (this.info.widgetType == 'RADIOBUTTON') {
			this.subWidget = new JsRadio(this.baseId, function(id) {
				me.requestChange(me.info.id, id);
			});
		}
		else if (this.info.widgetType == 'TEXTFIELD') {
			this.subWidget = new JsTextInput(this.baseId, function(id) {
				me.requestChange(me.info.id, id);
			});
		}
		else if (this.info.widgetType == 'TOGGLEBUTTON') {
			this.subWidget = new JsToggleButton(this.baseId, function(id) {
				me.requestChange(me.info.id, id);
			});
		}
		else if (this.info.widgetType == 'ACTIONBUTTON') {
			this.subWidget = new JsActionButton(this.baseId, function(id) {
				me.requestChange(me.info.id, id);
			});
		}
		else if (this.info.widgetType == 'PANEL') {
			$('#' + this.baseId).addClass('panel');
		}
			
		if (this.info.left != null) {
			$('#' + this.baseId).css('left', this.info.left + 'px');
			$('#' + this.baseId).css('top', this.info.top + 'px');
		}
						
		if (this.info.width != 0) {
			$('#' + this.baseId).width(this.info.width);
		}
		if (this.info.height != 0) {
			$('#' + this.baseId).height(this.info.height);
		}
		
		this.setCss();
	}
	
	updateValue() {
		var id = this.info.id;
		var me = this;
		
		if (id == '') {
			console.log('id=' + id);
		}
		
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id,
		   success: function(property){
		   		if (me.subWidget != null) {
		   			me.subWidget.updateValue(property);
		   		}
		   }
		});
	}
	
	updateLayout() {
		if ((this.info.id == null) || (this.info.id == '')) return;
				
		var id = this.info.id;
		var me = this;
		
		if (id == '') {
			console.log('id=' + id);
		}
			
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id,
		   success: function(property){
		   		if (me.subWidget != null) {
		   			me.subWidget.updateLayout(property);
		   			me.setCss();
		   		}
		   }
		});	
		
	}
	
	setCss() {
		if (this.parentLayout == 'Flow Layout') {
			$('#' + this.baseId).css({'position':'relative', 'display':'inline'});
			$('#' + this.baseId + '>.title').css({'display':'inline'});		
		}
		else if (this.parentLayout == 'Absolute Layout') {
			$('#' + this.baseId).css({'position':'absolute', 'display':'inline-block'});
			$('#' + this.baseId + '>.title').css({'display':'inline-block', 'width':'40%'});	
		}
		else if (this.parentLayout == 'Vertical Layout') {
			$('#' + this.baseId).css({'position':'relative', 'display':'block'});	
			$('#' + this.baseId + '>.title').css({'display':'inline-block', 'width':'40%'});	
		}
	}
	
	requestChange(id, value) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/setValue?id="+id + "&value=" + value,
		   success: function(msg){

		   }
		});	
	}
	
	editable(enabled) {
	    var baseId = this.baseId;
	    var me = this;
	    if (this.info.widgetType == 'PANEL') {			
			$('#' + this.baseId).draggable({
				start : function (event , ui){
					//console.log("start event start" );
					//console.log(event , ui);
				} ,
				drag : function (event , ui) {
					//console.log("drag event start" );
					//console.log(event , ui);
					
//					$('#' + me.baseId).text($('#' + me.baseId).position().left + ',' + $('#' + me.baseId).position().top);
//					$('#' + me.baseId).css('font-size', '8px');
				} ,
				stop : function (event , ui){
					//console.log("stop event start" );
					console.log(event , ui);
					me.setPosition(event);
				} 
			});
			
			$('#' + this.baseId).draggable(enabled);
			
			$('#' + this.baseId).resizable({
		      resize: function( event, ui ) {
		      	me.setSize();
		      }
		    });
		    $('#' + this.baseId).droppable({
		      drop: function( event, ui ) {
				
		      }
		    });
		    
		    $('#' + this.baseId).resizable(enabled);
	   	}
    	
    	if (enabled == 'enable') {
	    	$('#' + this.baseId).click(function(e){
				$('.base').removeClass('selected');
				$(this).addClass('selected');
				e.stopPropagation();
				me.selected(baseId);
				me.layout(me.info.layout);
			});
		}
		else {
			$('#' + this.baseId).off('click');
		}
	}
}