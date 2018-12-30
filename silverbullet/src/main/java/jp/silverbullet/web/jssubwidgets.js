class JsSubWidget {
	constructor(baseId, change) {
		this.baseId = 'baseof' + baseId;
		this.change = change;
		$('#' + baseId).append('<div id="' + this.baseId + '"></div>');
		$('#' + this.baseId).height('100%');
		$('#' + this.baseId).width('100%');
	}
	
	setEditable(boolean) { 
	}
		
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	}
	
	resize() {
	}
}

class JsRadio extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId, change);
	}
	
	updateValue(property) {
		$('#' + this.baseId).empty();
		$('#' + this.baseId).attr('title', property.id);
		this.updateLayout(property);
	}
	
	updateValue2(property) {
		$('#' + this.baseId + ' input').prop('checked', false).checkboxradio('refresh');
		$('#' + property.currentSelectionId + '-' + 'radio' + this.baseId).prop('checked', true).checkboxradio('refresh');
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
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
		super(baseId, change);
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
		$('#' + this.baseId).empty();
		this.titleId = 'title' + this.baseId;
		$('#' + this.baseId).append('<span class="title" id=' + this.titleId + '></span>');
		$('#' + this.titleId).text(property.title);
		
		this.comboId = 'combo' + this.baseId;
		$('#' + this.baseId).append('<SELECT id="' + this.comboId + '"></SELECT>');
		$('#' + this.baseId).append('<span id="' + this.unitId + '" class="unit"></span>');
 		$('#' + this.comboId).selectmenu({ width : 'auto'});
      		   		
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

		$('#' + this.baseId + ' > *').css({'margin':'2px'});
	
		$('#' + this.baseId).attr('title', property.id);
		
		this.updateValue2(property);
	}
	
	setDisabled(disabled) {
		$('#' + this.comboId).selectmenu( "option", "disabled", disabled );
	}
	
	resize() {
//	$('#' + this.comboId).selectmenu( "option", "width", $('#' + this.baseId).width() - $('#' + this.titleId).width() - 5);
	}
}

class JsTextInput extends JsSubWidget {
	constructor(baseId, change) {
		super(baseId, change);
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		$('#' + this.textId).val(property.currentValue);
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.titleId = 'title' + this.baseId;
		this.unitId = 'unit' + this.baseId;
		this.textId = 'text' + this.baseId;
		
		$('#' + this.baseId).append('<span class="title" id="' + this.titleId + '"></span>');
		this.textId = 'text' + this.baseId;
		$('#' + this.baseId).append('<input type="text" class="currentValue" id="' + this.textId + '">');
		$('#' + this.baseId).append('<label id="' + this.unitId + '" class="unit"></unit>');
		
		$('#' + this.textId).button().width(100);
		$('#' + this.baseId + ' > *').css({'margin':'2px'});
		
		var me = this;
		$('#' + this.textId).change(function() {
			me.change($(this).val());
		});
		
		this.updateValue(property);	
	}
}

class JsLabel extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
		$('#' + this.valueId).text(property.currentValue);
		$('#' + this.unitId).text(property.unit);
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.titleId = 'title' + this.baseId;
		this.valueId = 'value' + this.baseId;
		this.unitId = 'unit' + this.baseId;
		
		var html = '<span id="' + this.titleId + '" class="title"></span>:<span id="' + this.valueId + '" class="currentValue"></span><span id="' + this.unitId + '" class="unit"></span>';
		$('#' + this.baseId).append(html);
		
		$('#' + this.baseId + ' > *').css({'margin':'2px'});
		this.updateValue(property);
	}
}

class JsMessageBox extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		this.tmpId = 'tmp' + this.baseId;
		this.editable = true;
	}
	
	updateValue(property) {
		$('#' + this.contentId).text(property.currentValue);
		$('#' + this.dialogId).dialog('open');
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.dialogId = 'dialog' + this.baseId;
		this.contentId = 'dialogcontent' + this.baseId;
		
		
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
//		$('#' + this.tmpId).hide();
		this.updateValue(property);
		this.setEditable(this.editable);
	}
	
	setEditable(enabled) {
		this.editable = enabled;
		if (enabled == 'enable') {
			$('#' + this.tmpId).show();
		}
		else {
			$('#' + this.tmpId).hide();
		}
	}
}

class JsToggleButton extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
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
		$('#' + this.baseId).empty();
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

class JsCssButton extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
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
				//$('#' + this.buttonId).html('<div class="fbTitle">' + property.title + '</div>' + '<div class="fbValue">' + nextTitle + property.unit + '</div>');
				$('#' + this.buttonId).text(nextTitle);
				break;
			}
		}
		
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.buttonId = 'button' + this.baseId;
		this.titleId = 'title' + this.baseId;
		var html = '<div id=' + this.titleId + '>' + property.title + '</div><div id="' + this.buttonId + '" class="fbTitle"></div>';

		$('#' + this.baseId).append(html);
		
		var me = this;
		$('#' + this.buttonId).click(function() {
			me.change(me.nextId);
		});
		$('#' + this.titleId).click(function() {
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
		super(baseId, change);
	}
	
	updateValue(property) {
		$('#' + this.buttonId).text(property.title);
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
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
		super(baseId, change);
	}
	
	updateValue(property) {
		$('#' + this.titleId).text(property.title);
//		$('#' + this.checkId).prop('checked',property.currentValue == 'true').button('refresh');
		$('#' + this.checkId).prop('checked',property.currentValue == 'true');

	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.checkId = 'check' + this.baseId;
		this.titleId = 'title' + this.baseId;
		
		var html = '<input type="checkbox" id="' + this.checkId + '"' + ' name="' + this.checkId + '"><label for="' + this.checkId + '" id=' + this.titleId + '></label>';
		$('#' + this.baseId).append(html);
//		$('#' + this.checkId).button();
		
		var me = this;
		$('#' + this.checkId).change(function() {
			me.change($(this).prop('checked'));
		});
		
		this.updateValue(property);
	}
	
	setDisabled(disabled) {
		$('#' + this.baseId + ' > ').prop('disabled', disabled);
	//	$('#' + this.checkId + ' > ').prop('disabled', disabled);
	}
}

class JsChartCanvasJs extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		this.info = info;
	}
	
	updateValue(property) {
		var me = this;
		if (property.currentValue == 'REQUEST_AGAIN') {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/getProperty?id=" + me.info.id + '&index=' + me.info.index + '&ext=1001',
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
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		var me = this;
		this.chart = new CanvasJS.Chart(this.baseId, { 
			title: {
				text: property.title
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
	
	resize() {
		if (this.chart != null)  {
			this.chart.render();
		}
	}
}

class JsChart extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		this.info = info;
	}
	
	updateValue(property) {
		var me = this;
		if (property.currentValue == 'REQUEST_AGAIN') {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/getProperty?id=" + me.info.id + '&index=' + me.info.id + + '&ext=501',
			   success: function(property){
			   		if (property.currentValue != null) {
						me.updateChart(property);
					}
			   }
			});
		}
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
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
		super(baseId, change);

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
		$('#' + this.baseId).empty();
		this.tableid = 'table'+this.baseId;
		$('#' + this.baseId).append('<div id="' + this.tableid + '"></div>');

		var headers = ['COL#1', 'COL#2', 'COL#3', 'COL#4'];
		this.createTable(headers);
		
		this.resize();
		this.updateValue(property);
	}
	
	resize() {
		var me = this;

		this.hot.updateSettings({
		    height: $('#' + me.baseId).height()
		});		
	}
			
	createTable(headers) {
		var height = $('#' + this.baseId).prop('height');
		$('#' + this.tableid).handsontable({
		  manualColumnResize: true,
//		  width: 700,
//		  height: height,
//		  startRows: 3,
//		  startCols: 10,
		  colHeaders: headers,
		  rowHeaders: true,
//		  stretchH: 'all'
//		  minSpareRows: 1
		});	
	
		this.hot = $('#' + this.tableid).handsontable('getInstance');	
	}
}

class JsDataTable extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		this.info = info;
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

		$('#' + this.tableid + ' tbody').empty();
		for (var row of table.data) {
//			this.table.row.add(row).draw(false);
			var line = '<tr>';
			for (var col of row) {
				line += '<td>' + col + '</td>';
			}
			line += '</tr>';
			$('#' + this.tableid + ' tbody').append(line);
		}
//		this.table.draw();

//		if (this.table != null) {
//			this.table.destroy();
//		}
		var me = this;
		this.table = $('#' + this.tableid).DataTable({
			ordering: false,
			paging: false,
			retrieve: true,
			searching: false,
	        scrollY:     this.info.height +   "px",
	        scrollX:        true,
	        scrollCollapse: true,
	        info: false,
	        'drawCallback': function () {
			        $( '#' + me.tableid + ' tbody tr td' ).css( 'padding', '1px 1px 1px 1px' );
			    }
		});	
		
	}
	
	updateLayout(property) {
		$('#' + this.baseId).empty();
		this.tableid = this.baseId + "_table";
		$('#' + this.baseId).append('<table id="' + this.tableid + '"><thead><tr><td></td></tr></thead><tbody></tbody></table>');
						
		var headers = ['COL#1', 'COL#2', 'COL#3', 'COL#4'];
		this.createTable(headers);

		this.updateValue(property);
	}
	createTable(headers) {
		$('#' + this.tableid + ' thead').empty();
		$('#' + this.tableid + ' thead').append($('<tr>'));
		for (var i = 0; i < headers.length; i++) {
			var head = headers[i];
			$('#' + this.tableid + ' thead tr').append("<th>"+ head +"</th>");
		}
	}
}
class JsDialogButton extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		
		this.buttonId = 'button' + this.baseId;
		this.dialogId = 'dialog' + this.baseId;
		
		this.contentId = 'content' + this.baseId;
		
		this.updateLayout(info);
	}
		
	updateLayout(info) {
		$('#' + this.baseId).empty();
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
	
	updateLayout_new(info) {
		$('#' + this.baseId).empty();
		var root = info.custom["target_gui_id"];
		$('#' + this.baseId).append('<Button id="' + this.buttonId + '">' + info.custom['caption'] + '</Button>');
		$('#' + this.buttonId).button();
		var me = this;
		
		$('#' + this.baseId).append('<div id="' + this.contentId + '" class="FlowLayout"></div>');
		$('#' + me.buttonId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/design/getDesign?root=" + root,
			   success: function(msg){
			   	buildPane(msg.unique, msg.parentDiv, me);
			   }
			});
		});
			
		function buildPane(unique, parent, me) {
			var dialogId = '#' + unique;
			
			$('#' + parent).detach('#' + unique);
			$('#' + unique).appendTo('#' + me.contentId);
			$('#' + unique).top(0).left(0);
			
			$('#' + me.contentId).dialog({
				  autoOpen: false,
				  title: 'Dialog',
				  closeOnEscape: false,
				  modal: true,
				  buttons: {
				    "OK": function(){
				      $(this).dialog('close');
				      $('#' + me.baseId).detach('#' + unique);
				      $('#' + unique).appendTo('#' + parent);
				    }
				    ,
				    "Cancel": function(){
				      $(this).dialog('close');
				      $('#' + me.baseId).detach('#' + unique);
				      $('#' + unique).appendTo('#' + parent);
				      
				    }
				  },
				  width: 500,
				  height: 400
			});	
			$('#' + me.contentId).dialog('open');
		}

	}
}

class JsTabPanel extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		
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
		$('#' + this.baseId).empty();
		for (var index = 0 ; index < property.elements.length; index++) {
			var titleId = this.tabTitleMap[property.elements[index].id];
			if (titleId != undefined) {
				$('#' + titleId).text(property.elements[index].title);
			}
		}
	}
}

class JsRegisterShortcut extends JsSubWidget {
	constructor(baseId, info, change) {
		super(baseId, change);
		
		$('#' + this.baseId).addClass('register');
		var reg = info.custom['register_shortcut'];
		var regName = reg.split('@')[1];
		var bitName = reg.split('@')[0];
		$('#' + this.baseId).click(function() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/triggerShortcut?regName=" + regName + "&bitName=" + bitName,
			   success: function(msg){

			   }
			});			
		});
	}
	updateValue(property) {
	}
	updateLayout(property) {
		$('#' + this.baseId).append('<div>REGISTER</div>');
	}
}