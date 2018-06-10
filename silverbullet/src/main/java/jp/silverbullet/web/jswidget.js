class JsWidget {
	constructor(id, type, unique, parent) {
		this.id = id;
		this.type = type;
		this.unique = unique;
		
		this.baseId = 'base' + unique;
		this.titleId = 'title' + unique;
		this.unitId = 'unit' + unique;
		this.mainId = 'main' + unique;
		
		this.parent = parent;
		
		this.createBase();		
		
		this.update();
	}
	
	createBase() {
		var title = '<span id=' + this.titleId + '></span>';
		var unit = '<span id=' + this.unitId + '></span>';
		var main = '';
		if (this.type == 'COMBOBOX') {
			main = '<SELECT id=' + this.mainId + '></SELECT></div>';
		}
		else if (this.type == 'TEXTFIELD') {
			main = '<input type="text" id=' + this.mainId + '>';
		}
		else if (this.type == 'PANEL') {
	//		main = '<div id=' + this.unique + '></div>';
		}
			
		$('#' + this.parent).append('<div id=' + this.baseId + '>' + title + main + unit + '</div>');
	}
	
	update() {
		if (this.id == null) return;
		
		var id = this.id;
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