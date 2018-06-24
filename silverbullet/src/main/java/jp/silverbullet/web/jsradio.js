class JsRadio {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.baseId + ' input').prop('checked', false).checkboxradio('refresh');
		$('#' + property.currentValue + '-' + 'radio' + this.baseId).prop('checked', true).checkboxradio('refresh');
	}
	
	updateLayout(property) {
		var html = '<fieldset>';
		html += '<legend>' + property.title + '</legend>';
		this.radiosetId = 'radioset' + this.baseId;
		html += '<div id="' + this.radiosetId + '">';
		var me = this;
		this.name = 'radio' + this.baseId;
		for (var i in property.elements) {
			var element = property.elements[i];
			var radioid = element.id + '-' + 'radio' + this.baseId;
			html += '<input type="radio" id="' + radioid + '" name="' + this.name + '"><label for="' + radioid + '">' + element.title + '</label>';
		}
		html += '</div>';
		html += '</fieldset>';
		$('#' + this.baseId).append(html);

		$('input[name="' + this.name + '"]').click(function() {
			var id = $(this).prop('id').split('-')[0];
			if (id == undefined) {
				Conlole.log('null');
			}
			me.change(id);
		});

		$( '#' + this.radiosetId ).buttonset();
//		$( '#' + this.radiosetId ).controlgroup({ icon: false});
		$('input[name="' + this.name + '"]').checkboxradio({
		    icon: false
		});
		this.updateValue(property);
	}
}

class JsComboBox {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.comboId).val(property.currentValue).selectmenu('refresh');
	}
	
	updateLayout(property) {
		this.titleId = 'title' + this.baseId;
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		$('#' + this.titleId).text(property.title);
		
//		$('#' + this.baseId + '>.title').css({'display':'inline-block', 'width':'40%'});	
		this.comboId = 'combo' + this.baseId;
		$('#' + this.baseId).append('<SELECT id=' + this.comboId + '></SELECT>');
 		$('#' + this.comboId).selectmenu();
      		   		
		for (var i in property.elements) {
   			var element = property.elements[i];
   			$('#' + this.comboId).append($('<option>', {
			    value: element.id,
			    text: element.title
			}));
   		}		 
   				   		
   		var me = this;
		$('#' + this.comboId).on( "selectmenuchange", function( event, ui ) {
			me.change(ui.item.value);
		} );


		this.updateValue(property);
	}
}

class JsTextInput {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		$('#' + this.textId).val(property.currentValue);
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		this.titleId = 'title' + this.baseId;
		this.unitId = 'unit' + this.baseId;
		
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		this.textId = 'text' + this.baseId;
		$('#' + this.baseId).append('<input type="text" id=' + this.textId + '>');
	
		$('#' + this.baseId).append('<label id="' + this.unitId + '"></unit>');
		
		this.updateValue(property);	
	}
}
class JsToggleButton {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		for (var i = 0; i < property.elements.length; i++) {
			if (property.elements[i].id == property.currentValue) {
				$('#' + this.buttonId).text(property.elements[i].title);
				if (i < property.elements.length-1) {
					
					this.nextId = property.elements[i+1].id;
				}
				else {
					this.nextId = property.elements[0].id;
				}
				break;
			}
		}
		
	}
	
	updateLayout(property) {
		this.buttonId = 'button' + this.baseId;
		this.titleId = 'title' + this.baseId;
		var html = '<fieldset><legend id=' + this.titleId + '>' + property.title + '</legend><button id="' + this.buttonId + '"></button></fieldset>';
		$('#' + this.baseId).append(html);
		$('#' + this.buttonId).button();
		
		var me = this;
		$('#' + this.buttonId).click(function() {
			me.change(me.nextId);
		});
		
		this.updateValue(property);
	}
}
class JsActionButton {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.buttonId).text(property.title);
	}
	
	updateLayout(property) {
		this.buttonId = 'button' + this.baseId;
		this.titleId = 'title' + this.baseId;
		var html = '<button id="' + this.buttonId + '"></button>';
		$('#' + this.baseId).append(html);
		$('#' + this.buttonId).button();
		this.updateValue(property);
	}
}