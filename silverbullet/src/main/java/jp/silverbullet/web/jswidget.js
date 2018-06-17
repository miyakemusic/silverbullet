class JsWidget {
	constructor(info, parent, selected) {
		this.info = info;
		
		this.baseId = 'base-' + info.unique;
		this.titleId = 'title-' + info.unique;
		this.unitId = 'unit-' + info.unique;
		this.mainId = 'main-' + info.unique;
		
		this.parent = parent;
		
		this.createBase(selected);		
		
		this.update();
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
	
	order() {
		var all = $('#' + this.baseId).find('.base');
		for (var i in all) {
			if (all[i].id == $('#' + this.baseId).id) {
				continue;
			}
			var y = $('#' + all[i].id).position().top;
			
			var thisY= $('#' + this.baseId).position().top;
			
			if (y < thisY) {
				$('#' + $(this).id).before($('#' + this.baseId));
			}
		}
	}
	
	createBase(selected) {
		var style = 'class="base"';
		var title = '<span class="title" id=' + this.titleId + '></span>'; //<span id=' + this.titleId + '></span>';
		var unit = '<span id=' + this.unitId + '></span>';
		var main = '';
		
		$('.title').css('display', 'inline-block');
		$('.title').css('width', '200px');
		
		if (this.info.widgetType == 'COMBOBOX') {
			main = '<SELECT id=' + this.mainId + '></SELECT></div>';
		}
		else if (this.info.widgetType == 'TEXTFIELD') {
			main = '<input type="text" id=' + this.mainId + '>';
		}
		else if (this.info.widgetType == 'PANEL') {
			style = 'class="base panel"';
			title = '';
			unit = '';
		}
			
		$('#' + this.parent).append('<div id=' + this.baseId + ' ' + style + '>' + title + main + unit + '</div>');	
		
		if (this.info.left != null) {
			$('#' + this.baseId).css('position:relative');
			$('#' + this.baseId).css('left', this.info.left + 'px');
			$('#' + this.baseId).css('top', this.info.top + 'px');
//			$('#' + this.baseId).text(this.info.left + "," + this.info.top);
		}
		
	    var baseId = this.baseId;
	    
	    if (this.info.widgetType == 'PANEL') {
			var me = this;
			$('#' + this.baseId).draggable({
				start : function (event , ui){
					//console.log("start event start" );
					//console.log(event , ui);
				} ,
				drag : function (event , ui) {
					//console.log("drag event start" );
					//console.log(event , ui);
					
//					$('#' + me.baseId).text($('#' + me.baseId).position().left + ',' + $('#' + me.baseId).position().top);
				} ,
				stop : function (event , ui){
					//console.log("stop event start" );
					console.log(event , ui);
		//			me.order();
					me.setPosition(event);
				}
			});
			$('#' + this.baseId).resizable({
		      resize: function( event, ui ) {
		      	me.setSize();
		      }
		    });
		    $('#' + this.baseId).droppable({
		      drop: function( event, ui ) {
				
		      }
		    });
	   	}
    	
    	$('#' + this.baseId).mouseenter(function(e){
//	    	if (e.target.baseId == this.baseId) {
//				$(this).addClass('selected');
//			}
		}).mouseout(function(e){
//			$(this).removeClass('selected');
			//console.log('mouseout');
		}).click(function(e){
			$('.base').removeClass('selected');
			$(this).addClass('selected');
			//console.log(baseId);
			e.stopPropagation();
			selected(baseId);
		});

		if (this.info.width != 0) {
			$('#' + this.baseId).width(this.info.width);
		}
		if (this.info.height != 0) {
			$('#' + this.baseId).height(this.info.height);
		}
	}
	
	update() {
		if (this.info.id == null) return;
		
		var id = this.info.id;
		var titleId = this.titleId;
		var unitId = this.unitId;
		var mainId = this.mainId;
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + id,
		   success: function(property){
				$('#' + titleId).text(property.title);
				$('#' + unitId).text(property.unit); 	
					
				$('#' + mainId).empty();
		   		for (var i in property.elements) {
		   			var element = property.elements[i];
		   			$('#' + mainId).append($('<option>', {
					    value: element.id,
					    text: element.title
					}));
		   		}
		   		$('#' + mainId).val(property.currentValue);
		   		$('#' + mainId).on('change', function() {
					me.requestChange(property.id, $('#' + mainId).val());
				})
		   }
		});	
	}
	
	requestChange(id, value) {
		$.ajax({
		   type: "GET", 
		   url: "http://" + window.location.host + "/rest/runtime/setValue?id="+id + "&value=" + value,
		   success: function(msg){

		   }
		});	
	}
}