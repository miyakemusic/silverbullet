class JsSubWidget {
	constructor(baseId) {
		this.baseId = baseId;
	}
	
	setEditable(editable) {
	}
	
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	}
}

class JsRadio extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.baseId).empty();
		this.updateLayout(property);
	}
	
	updateValue2(property) {
		$('#' + this.baseId + ' input').prop('checked', false).checkboxradio('refresh');
		$('#' + property.currentSelectionId + '-' + 'radio' + this.baseId).prop('checked', true).checkboxradio('refresh');
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
		$('input[name="' + this.name + '"]').checkboxradio({
		    icon: false
		});
		this.updateValue2(property);
	}
	
	setDisabled(disabled) {
		$('#' + this.baseId + " input").checkboxradio( "option", "disabled", disabled );
	}
}

class JsComboBox extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
		this.unitId = 'unit' + this.baseId;
	}
	
	updateValue(property) {
		$('#' + this.baseId).empty();
		this.updateLayout(property);
	}
	
	updateValue2(property) {
		$('#' + this.comboId).val(property.currentSelectionId).selectmenu('refresh');
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		this.titleId = 'title' + this.baseId;
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		$('#' + this.titleId).text(property.title);
		
		this.comboId = 'combo' + this.baseId;
		$('#' + this.baseId).append('<SELECT id="' + this.comboId + '"></SELECT>');
		$('#' + this.baseId).append('<span id="' + this.unitId + '" class="unit"></span>');
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

		this.updateValue2(property);
	}
	
	setDisabled(disabled) {
		$('#' + this.comboId).selectmenu( "option", "disabled", disabled );
	}
}

class JsTextInput extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
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
		
		$('#' + this.baseId).append('<span class="title" id="' + this.titleId + '"></span>');
		this.textId = 'text' + this.baseId;
		$('#' + this.baseId).append('<input type="text" class="currentValue" id="' + this.textId + '">');
	
		$('#' + this.baseId).append('<label id="' + this.unitId + '" class="unit"></unit>');
		
		var me = this;
		$('#' + this.textId).change(function() {
			me.change($(this).val());
		});
		
		this.updateValue(property);	
	}
}

class JsLabel extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		$('#' + this.valueId).text(property.currentValue);
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		this.titleId = 'title' + this.baseId;
		this.valueId = 'value' + this.baseId;
		this.unitId = 'unit' + this.baseId;
		
		var html = '<span id="' + this.titleId + '" class="title"></span>:<span id="' + this.valueId + '" class="currentValue"></span><span id="' + this.unitId + '" class="unit"></span>';
		$('#' + this.baseId).append(html);
		this.updateValue(property);
	}
}

class JsMessageBox extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
	}
	
	updateValue(property) {
		$('#' + this.contentId).text(property.currentSelectionText);
		$('#' + this.dialogId).dialog('open');
	}
	
	updateLayout(property) {
		this.dialogId = 'dialog' + this.baseId;
		this.contentId = 'dialogcontent' + this.baseId;
		this.tmpId = 'tmp' + this.baseId;
		
		$('#' + this.baseId).append('<label id="' + this.tmpId + '">Message (' + property.id + ')</label>');
		$('#' + this.baseId).append('<div id="' + this.dialogId + '"><div id="' + this.contentId + '"></div></div>');
		$('#' + this.dialogId).dialog({
			  autoOpen: false,
			  title: property.title,
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "OK": function(){
			      $(this).dialog('close');
			    }
			  },
			width: 400,
			height: 300
		});	
		
		this.updateValue(property);
	}
	
	setEditable(enabled) {
		if (enabled == 'enabled') {
			$('#' + this.tmpId).show();
		}
		else {
			$('#' + this.tmpId).hide();
		}
	}
}

class JsToggleButton extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
		this.custom = info.custom;
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		var nextTitle;
		for (var i = 0; i < property.elements.length; i++) {
			if (property.elements[i].id == property.currentSelectionId) {
				if (i < property.elements.length-1) {	
					this.nextId = property.elements[i+1].id;
					nextTitle = property.elements[i+1].title;
				}
				else {
					this.nextId = property.elements[0].id;
					nextTitle = property.elements[0].title;
				}
				$('#' + this.buttonId).html('<div class="fbTitle">' + property.title + '</div>' + '<div class="fbValue">' + nextTitle + property.unit + '</div>');
				break;
			}
		}
		
	}
	
	updateLayout(property) {
		this.buttonId = 'button' + this.baseId;
		this.titleId = 'title' + this.baseId;
		var html;
		if (this.custom["frame"] == true) {
			html = '<fieldset><legend id=' + this.titleId + '>' + property.title + '</legend><button id="' + this.buttonId + '"></button></fieldset>';
		}
		else {
			html = '<button id="' + this.buttonId + '"></button>';
		}
		$('#' + this.baseId).append(html);
		$('#' + this.buttonId).button();
		
		var me = this;
		$('#' + this.buttonId).click(function() {
			me.change(me.nextId);
		});
		
		this.updateValue(property);
	}
	
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	}
}
class JsActionButton extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
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
	
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	}
}

class JsCheckBox extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
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
	
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	}
}

class JsChart extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
		this.info = info;
	}
	
	updateValue(property) {
		var me = this;
		if (property.currentValue == 'REQUEST_AGAIN') {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/runtime/getProperty?id=" + me.info.id + '&ext=501',
			   success: function(property){
			   		if (property.currentValue != null) {
						me.updateChart(property);
					}
			   }
			});
		}
	}
	
	updateLayout(property) {
		var html = '<canvas id="chart" height="200" width="400"></canvas>';
		$('#' + this.baseId).append(html);
		
		this.updateValue(property);
	}
	
	updateChart(property) {
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
				//label: property.title, //'My First dataset',
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
//				title: {
//					display: false,
//					text: property.title
//				},
				animation: false,
				legend: { display: false },
			}
		});
	}
}

class JsTable extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId);
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

class JsDialogButton extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.buttonId = 'button' + this.baseId;
		this.dialogId = 'dialog' + this.baseId;
		
		this.contentId = 'content' + this.baseId;
		this.change = change;
		
		this.updateLayout(info);
	}
		
	updateLayout(info) {
		var root = info.custom["target_gui_id"];
		$('#' + this.baseId).append('<Button id="' + this.buttonId + '">' + info.custom['caption'] + '</Button>');
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

class JsTabPanel extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId);
		this.baseId = baseId;
		this.change = change;
		this.info = info;
		this.tabTitleMap = new Map();
		this.tabIndexMap = new Map();
		
		var relid = 'tab_relation_id';
		
		this.parent = $('#' + this.baseId).parent().attr('id');
		$('#' + this.baseId).remove();			
		var tab = '<ul>';
		var content = '';
		for (var i = 0; i < this.info.children.length; i++) {
			var child = this.info.children[i];

			var contentId = 'tab-' + this.baseId + "-" + child.unique;
			var tabTitleId = 'title' + contentId;
			try {
				var custom = child.custom;
				
				var relation = custom[relid];
				this.tabTitleMap[relation] = tabTitleId;
				this.tabIndexMap[relation] = i;
			}
			catch (e) {
				tabTitleId = child.presentation;
			}

			var tabTitle = '<span id="' + tabTitleId + '"></span>';
			tab += '<li><a href="#' + contentId + '">' + tabTitle + '</a></li>';
			content += '<div id="' + contentId + '" class="panel TabContent"></div>';
		}
		tab +="</ul>";
		
		$('#' + this.parent).append('<div id="' + this.baseId + '">' + tab + content + '</div>');
		$('#' + this.baseId).tabs();

		console.log("tab:" + this.baseId);
		
	}
	
	updateValue(property) {
		var index = this.tabIndexMap[property.currentValue];
		if (index != undefined) {
			$('#' + this.baseId).tabs("option", "active", index);
		}
	}
	
	updateLayout(property) {
		for (var index = 0 ; index < property.elements.length; index++) {
			var titleId = this.tabTitleMap[property.elements[index].id];
			if (titleId != undefined) {
				$('#' + titleId).text(property.elements[index].title);
			}
		}
	}
}
