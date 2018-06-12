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
	
	createBase(selected) {
		var style = '';
		var title = '<span id=' + this.titleId + '></span>';
		var unit = '<span id=' + this.unitId + '></span>';
		var main = '';
		if (this.info.widgetType == 'COMBOBOX') {
			main = '<SELECT id=' + this.mainId + '></SELECT></div>';
		}
		else if (this.info.widgetType == 'TEXTFIELD') {
			main = '<input type="text" id=' + this.mainId + '>';
		}
		else if (this.info.widgetType == 'PANEL') {
			style = 'class="kacomaru" ';
		}
			
		$('#' + this.parent).append('<div id=' + this.baseId + ' ' + style + '>' + title + main + unit + '</div>');	
		
		$('#' + this.baseId).draggable({
			start : function (event , ui){
				console.log("start event start" );
				console.log(event , ui);
			} ,
			drag : function (event , ui) {
				console.log("drag event start" );
				console.log(event , ui);
			} ,
			stop : function (event , ui){
				console.log("stop event start" );
				console.log(event , ui);
			}
		});
		
	    $('#' + this.baseId).droppable({
	      drop: function( event, ui ) {
			
	      }
	    });
	    var baseId = this.baseId;
	    
//	    if (this.info.widgetType == 'PANEL') {
	    	$('#' + this.baseId).mouseenter(function(e){
		    	if (e.target.baseId == this.baseId) {
//					$(this).addClass('selected');
				}
			}).mouseout(function(e){
//				$(this).removeClass('selected');
				console.log('mouseout');
			}).click(function(e){
				$('div').removeClass('selected');
				$(this).addClass('selected');
				console.log(baseId);
				e.stopPropagation();
				selected(baseId);
			});
//	   	}
    	
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