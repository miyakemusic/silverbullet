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
		this.unitId = 'unit' + this.baseId;
	}
	
	updateValue(property) {
		$('#' + this.comboId).val(property.currentValue).selectmenu('refresh');
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		this.titleId = 'title' + this.baseId;
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		$('#' + this.titleId).text(property.title);
		
		this.comboId = 'combo' + this.baseId;
		$('#' + this.baseId).append('<SELECT id=' + this.comboId + '></SELECT>');
		$('#' + this.baseId).append('<span id="' + this.unitId + '"></span>');
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
		this.textId = 'text' + this.baseId;
		
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		this.textId = 'text' + this.baseId;
		$('#' + this.baseId).append('<input type="text" id=' + this.textId + '>');
	
		$('#' + this.baseId).append('<label id="' + this.unitId + '"></unit>');
		
		var me = this;
		$('#' + this.textId).change(function() {
			me.change($(this).val());
		});
		
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
		var nextTitle;
		for (var i = 0; i < property.elements.length; i++) {
			if (property.elements[i].id == property.currentValue) {
//				$('#' + this.buttonId).text(property.elements[i].title);
				if (i < property.elements.length-1) {	
					this.nextId = property.elements[i+1].id;
					nextTitle = property.elements[i+1].title;
				}
				else {
					this.nextId = property.elements[0].id;
					nextTitle = property.elements[0].title;
				}
				$('#' + this.buttonId).text(nextTitle);
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

class JsCheckBox {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		$('#' + this.checkId).val(property.currentValue);
	}
	
	updateLayout(property) {
		this.checkId = 'check' + this.baseId;
		this.titleId = 'title' + this.baseId;
		
		var html = '<input type="checkbox" id="' + this.checkId + '"' + ' name="' + this.checkId + '"><label for="' + this.checkId + '" id=' + this.titleId + '></label>';
		$('#' + this.baseId).append(html);
		$('#' + this.checkId).checkboxradio();
		this.updateValue(property);
	}
}

class JsChart {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		if (property.currentValue == '') {
			return;
		}
		
		var datasets = [];
		var dataset = [];
//		dataset.label = "data";
		var data = [];
		
		var trace = JSON.parse(property.currentValue);
		for (var i = 0; i < trace.y.length; i++) {
			var e = Object();
			e.x = i;
			e.y = trace.y[i];
			data.push(e);
		}
		
		var scatterChartData = {
			datasets: [{
				label: property.title, //'My First dataset',
				data: data,
				showLine: true,
				showPoint: false,
				fill: false,
				borderWidth: 1,
				pointRadius: 0,
				borderColor: '#ff0000'
			}]
		};
		var ctx = document.getElementById('chart').getContext('2d');
		Chart.Scatter(ctx, {
			data: scatterChartData,
			options: {
				title: {
					display: true,
					text: property.title
				},
				animation: false,
			}
		});
	}
	
	updateLayout(property) {
		var html = '<canvas id="chart" height="200" width="400"></canvas>';
		$('#' + this.baseId).append(html);
		
		this.updateValue(property);
	}
}

class JsTable {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.change = change;
		this.headers = [];
	}
	
	updateValue(property) {
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
		    height: $('#' + me.baseId).height()
		});	

		this.hot.loadData(table.data);
	}
	
	updateLayout(property) {
		this.tableid = 'table'+this.baseId;
		$('#' + this.baseId).append('<div id="' + this.tableid + '"></div>');
//		$('#' + this.baseId).css('margin', '0px');

		var headers = ['COL#1', 'COL#2', 'COL#3', 'COL#4'];
		this.createTable(headers);
		
		this.updateValue(property);
	}
	
	resize() {
		var me = this;
		this.hot.updateSettings({
		    height: $('#' + me.baseId).height()
		});		
	}
	
	createTable(headers) {
//		var height = $('#' + this.baseId).prop('height');
		$('#' + this.tableid).handsontable({
		  manualColumnResize: true,
//		  width: 700,
//		  height: 400,
//		  startRows: 3,
//		  startCols: 10,
		  colHeaders: headers,
		  rowHeaders: true,
		  minSpareRows: 1
		});	
	
		this.hot = $('#' + this.tableid).handsontable('getInstance');	
	}
}

class JsDialogButton {
	constructor(baseId, change) {
		this.baseId = baseId;
		this.buttonId = 'button' + this.baseId;
		this.dialogId = 'dialog' + this.baseId;
		
		this.contentId = 'content' + this.baseId;
		this.change = change;
	}
		
	updateLayout(info) {
		var root = info.id;
		$('#' + this.baseId).append('<Button id="' + this.buttonId + '">' + info.presentation + '</Button>');
		$('#' + this.buttonId).button();
		$('#' + this.baseId).append('<div id="' + this.dialogId + '"><div id="' + this.contentId + '"></div></div>');
							
		$('#' + this.dialogId).dialog({
			  autoOpen: false,
			  title: 'Dialog',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			    }
			    ,
			    "Cancel": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
		
		var me = this;
		$('#' + this.buttonId).click(function() {
			var layout = new LayoutBuilder(me.contentId, root);
			$('#' + me.dialogId).dialog('open');
		});
	}
}