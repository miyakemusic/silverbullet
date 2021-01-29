class TestSpec {
	constructor(div) {
	
		var toolBarId = div + "_toolbar";
		$('#' + div).append('<div id="' + toolBarId + '"></div>');
		var createDemoId = div + "_createDemo";
		$('#' + toolBarId).append('<button id="' + createDemoId + '">Create Demo</button>');
		$('#' + createDemoId).click(function() {
			createDemo();
		});
		
		var getTestSpecId = div + "_getTestSpec";
		$('#' + toolBarId).append('<button id="' + getTestSpecId + '">Redraw</button>');
		$('#' + getTestSpecId).click(function() {
			retrieveTest();
		});
		
		var scriptDialogId = div + '_scriptDialog';
		var scriptTableId = div + '_scriptDialog';
		var createScriptId = div + "_createScript";
		$('#' + toolBarId).append('<button id="' + createScriptId + '">Create Script</button>');
		$('#' + createScriptId).click(function() {
			createScript(function(script) {
				updateScriptTable(script);
				$('#' + scriptDialogId).dialog('open');
			});
		});
		
		function updateScriptTable(script) {
//			$('#' +scriptTableId).empty();
//			$('#' + scriptTableId).append('<tr><td>Node Name</td><td>Direction</td><td>Port Name</td><td>Test Side</td><td>Test Method</td></tr>');

			$('#' + scriptTableId + ' > tbody').empty();
//			var row = "";
			for (var o of script.spec) {
				//var sortFields = ['nodeName', 'portDirection', 'testSide', 'portName', 'testMethod'];
				var row = '<tr><td>'+o.nodeName+'</td><td>'+o.portDirection+'</td><td>'+o.testSide+'</td><td>'+o.portName+'</td><td>'+o.testMethod+'</td></tr>';
				$('#' + scriptTableId + ' > tbody').append(row);
//				$('#' + scriptTableId).append(row);
			}
//			$('#' + scriptTableId + ' tbody').after(row);
//			$('#' + scriptTableId + ' > tbody:first').append(row);
			$('#' + scriptTableId + ' td').each(function() {
			 	$(this).css('width', "150px");
			});
			
			$('#' + scriptList).text('');

			var str = '';
			for ( var line of script.script) {
				str += line + "\n";
			}

			$('#' + scriptList).text(str);
		}
		
		var mainId = div + "_main";
		$('#' + div).append('<div id="' + mainId + '"></div>');
		
		var canvasId = div + '_canvas';
		$('#' + mainId).append('<canvas id="' + canvasId + '" width="1000" height="2000"></canvas>');
		$('#' + mainId).css('position','relative');
		
		$('#' + canvasId).css('position','absolute');
		$('#' + canvasId).css('top','0px');
		$('#' + canvasId).css('left','0px');
			
		
		function drawLine(x1, y1, x2, y2) {
		    var canvas = $("#" + canvasId);
		    var ctx = canvas[0].getContext("2d");
		    ctx.beginPath();
		    ctx.moveTo( x1, y1);
		    ctx.lineTo(x2, y2);
		    ctx.stroke();
		    return false;
		}	
		
		function drawRect(x, y, width, height) {
			var canvas = $("#" + canvasId);
			var ctx = canvas[0].getContext("2d");
			ctx.beginPath();
			ctx.rect(x, y, width, height);
			ctx.stroke();
		}
		
		function drawString(text, x, y) {
			var canvas = $("#" + canvasId);
			var ctx = canvas[0].getContext("2d");
			ctx.fillText(text, x, y); 
		}
		
		function drawDiagram(msg) {
			recursive(msg.rootNode, 'root', 0);
		}
		
		function recursive(node, parent, layer) {
			var divId = parent.name + '_' + node.name;
			var tag = '<div id="' + divId + '"><table><tr><td><button>'+ node.name + '</button></td><td>';
			for (var v in node.subNodes) {
				tag += '<div><button>' + v + '</button></div>';
			}
			tag += '</td></tr></table></div>';
			 
			$('#' + div).append(tag);
			
			var left = layer * 200;
			$('#' + divId).css({'position':'relative', 'width':'200px', 'left': left + 'px'});
			
			for (var key in node.subNodes) {
				var v = node.subNodes[key];
				var div_t = '#div' + layer;
				
				recursive(v, node, layer+1);
			}
		}
		
		function drawDiagram2(msg) {
			var outputId = divId + "_outoutButton";
			for (var node of msg.allNodes) {
				var divId = node.id;
				var tag = '<div id="' + divId + '"><table><tr><td><button>'+ node.name + '</button></td><td>';
				for (var v of node.output) {
					
					tag += '<div><button class="' + outputId + '">' + v + '</button></div>';
				}
				tag += '</td></tr></table></div>';
				 
				$('#' + div).append(tag);
				
				$('#' + divId).css({'position':'absolute'});
				$('#' + divId).css({'width':node.width + 'px', 'left': node.left + 'px', 'top': node.top + 100 + 'px', 'height':node.height + 'px'});
				
				
			}
			
			var hoffset = -25;
			var voffset = 40;
			for (var line of msg.allLines) {
				drawLine(line.x1 + hoffset, line.y1 + voffset, line.x2 + hoffset, line.y2 + voffset);
			}
			
			$('.' + outputId).css({'height':node.unitHeight + 'px'});	
		}
		
		var checked = [];
		
		var offset = 20;
		function drawDiagram3(msg) {
//			$('#' + mainId).empty();
			
			for (var node of msg.allNodes) {
				drawRect(node.left, node.top + offset, node.width, node.height);
				//drawString(node.name, node.left + 10, node.top + offset - 5);
				var nodeCheckDiv = div + "_checkDiv" + node.id;
				var nodeCheck = div + "_check" + node.id;
				$('#' + mainId).append('<div id="' + nodeCheckDiv + '"><input type="checkbox" id="'+nodeCheck+ '" value="'+node.id+'">' + node.name + '</div>');
				$('#' + nodeCheckDiv).css('position', 'absolute');
				$('#' + nodeCheckDiv).css('top', node.top + 'px');
				$('#' + nodeCheckDiv).css('left', node.left + 'px');
				
				$('#' + nodeCheck).click(function() {
					if ($(this).prop('checked') == true) {
						checked.push($(this).attr('value'));
					}
					else {
						var index = checked.indexOf($(this).attr('value'));
						checked.splice(index, 1);
					}
				});
				
				for (var o of node.input) {
					createButton(o.id, o.direction, node.name, o.name, o.left, o.top + offset, o.width, o.height);
				}
				
				for (var o of node.output) {
					createButton(o.id, o.direction, node.name, o.name, o.left, o.top + offset, o.width, o.height);
				}
			}
			
			for (var line of msg.allLines) {
				drawLine(line.x1, line.y1 + offset, line.x2, line.y2 + offset);
			}
		}
				
		var beingSelectedPort;
		var dialogId = mainId + '_dialog';
		$('#' + mainId).append('<div id="' + dialogId + '"></div>');
		var leftId = dialogId + '_left';
		var rightId = dialogId + '_right';
		
		var leftCaption = leftId + '_cap';
		var rightCaption = rightId + '_cap';
		
		var comboConnector = dialogId + '_connector';
		$('#' + dialogId).append('Connector Type: <select id="' + comboConnector + '"></select>');
//		var options = ['-', 'SC/UPC', 'SC/APC', 'FC/UPC', 'FC/APC', 'LC/UPC', 'LC/APC', 'MPO12', 'MPO24', 'ODC-2', 'ODC-4', 'Pushlok'];
		retrieveConnectors(function(list) {
			for (var o of list) {
				$('#' + comboConnector).append($('<option>').text(o).val(o));
			}
		});
		
		$('#' + dialogId).append('<table><thead><td><label id="' + leftCaption + '">Left Side</td><td><label id="'+rightCaption+'">Right Side</label></td></thead><tbody><tr><td><div id="' + leftId + '"></div></td><td><div id="' + rightId + '"></div></td></tr></tbody></table>');
		
		var left_tests = [];
		var leftAdd = div + '_leftAdd';
		var comboIdLeft = dialogId + '_comboLeft';
		$('#' + leftId).append('<select id="' + comboIdLeft + '"></select><button id="' + leftAdd + '">Add</button>');
		

		var leftTests = div + '_leftTests';
		$('#' + leftId).append('<div id="' + leftTests + '"></div>');
		$('#' + leftAdd).click(function() {
			left_tests.push($('#' + comboIdLeft).val());
			$('#' + leftTests).append('<div>' + $('#' + comboIdLeft).val() + '</div>');
		});
		
		var right_tests = [];
		var rightAdd = div + '_rightAdd';
		var comboIdRight = dialogId + '_comboRight';
		$('#' + rightId).append('<select id="' + comboIdRight + '"></select><button id="' + rightAdd + '">Add</button>');		

		retrieveTestMethods(function(options) {
			for (var o of options) {
				$('#' + comboIdLeft).append($('<option>').text(o).val(o));
				$('#' + comboIdRight).append($('<option>').text(o).val(o));
			}
		});
		
		var rightTests = div + '_rightTests';
		$('#' + rightId).append('<div id="' + rightTests + '"></div>');
		$('#' + rightAdd).click(function() {
			right_tests.push($('#' + comboIdRight).val());
			$('#' + rightTests).append('<div>' + $('#' + comboIdRight).val() + '</div>');
		});
		
		$('#' + rightId).css('width', '200px');
		
		function createButton(id, direction, nodeText, portText, x, y, width, height) {
			$('#' + mainId).append('<button value="' + direction + '" + name="' + nodeText + '_' + portText + '" id="' + id + '"></button>');
			$('#' + id).text(portText);
			$('#' + id).css({'position':'absolute'});
			$('#' + id).css('left',x + 'px');
			$('#' + id).css('top',y + 'px');
			$('#' + id).css('width',width + 'px');
			$('#' + id).css('height',height + 'px');
			$('#' + id).css('font', '10px sans-serif');
			
			$('#' + id).click(function() {
				beingSelectedPort = $(this).prop('id');
//				if ($(this).attr('value').startsWith('in')) {
					$('#' + rightCaption).text('Inside Port (Device side)');
					$('#' + leftCaption).text('Outside Port (Fiber side)');
//				}
//				else {
//					$('#' + leftCaption).text('Device Side');
//					$('#' + rightCaption).text('Fiber Side');
//				}
				
				left_tests.splice(0);
				right_tests.splice(0);
				
				var title = $(this).prop('name');
				$('#' + dialogId).dialog({ title: title });
				$('#' + dialogId).dialog('open');
				
				retrievePortConfig(beingSelectedPort, function(portConfig) {
					$('#' + leftTests).empty();
					$('#' + rightTests).empty();
					$('#' + comboConnector).val(portConfig.connector);
					for (var v of portConfig.insideTest) {
						$('#' + leftTests).append('<div>' + v + '</div>');
						left_tests.push(v);
					}
					for (var v of portConfig.outsideTest) {
						$('#' + rightTests).append('<div>' + v + '</div>');
						right_tests.push(v);
					}					
				});
			});
		}
		
		function createDemo() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/createDemo",
				success: function(msg){
					//drawDiagram3(msg);
				}
			});	
		}
		
		function retrieveTest() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/getTestSpec",
				success: function(msg){
					drawDiagram3(msg);
				}
			});	
		}
		
		function retrieveConnectors(callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/connectors",
				success: function(msg){
					callback(msg);
				}
			});	
		}
		
		function retrievePortConfig(id, callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/portConfig?id=" + id,
				success: function(msg){
					callback(msg);
				}
			});	
		}
		function postPortConfig(id, testConfig) {
			$.ajax({
				url: "//" + window.location.host + "/rest/testSpec/postPortConfig?id=" + id,
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify(testConfig),
				processData: false
			})
			.done(function( data ) {

			});	
		}		
		function copyToAll(id, testConfig) {
			$.ajax({
				url: "//" + window.location.host + "/rest/testSpec/copyConfig?id=" + id,
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify(testConfig),
				processData: false
			})
			.done(function( data ) {

			});	
		}

		function createScript(callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/createScript?id=" + checked,
				success: function(msg){
					callback(msg);
				}
			});			
		}
		
		function doSortBy(sortBy, callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/sortBy?sortBy=" + sortBy,
				success: function(msg){
					callback(msg);
				}
			});
		}
		
		function retrieveTestMethods(callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/testMethods",
				success: function(msg){
					callback(msg);
				}
			});
		}
		
		function registerScript() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/registerScript",
				success: function(msg){
				}
			});
		};
		
		function commitPortConfig() {
			postPortConfig(beingSelectedPort, createPortConfig());
		}
		function createPortConfig() {
			var portConfig = new Object();
			portConfig.connector = $('#' + comboConnector).val();
			portConfig.insideTest = left_tests;
			portConfig.outsideTest = right_tests;
			return portConfig;
		}
		
		$('#' + dialogId).dialog({
			autoOpen: false,
			title: 'Test Target',
			closeOnEscape: true,
			modal: true,
			buttons: {
				"Copy to All": function(){
					copyToAll(beingSelectedPort, createPortConfig());
					$(this).dialog('close');
				},
				"OK": function(){
					commitPortConfig();
					$(this).dialog('close');
				}
				,
				"Cancel": function(){
					$(this).dialog('close');
				}
			},
			width: 600,
			height: 400
		});	
		
		var sortBy = div + '_sortby';
		var sortDo = div + '_sortdo';
		var scriptList = div + '_scriptList';
		var register = div + '_register';
		
		$('#' + div).append('<div id="'+scriptDialogId+'"></div>');
		$('#' + scriptDialogId).append('<div><select id="'+sortBy+'"></select><button id="'+sortDo+'">SORT</button><button id="' + register + '">Register Script</button></div>');
		$('#' + scriptDialogId).append('<div><table id="'+scriptTableId+'"></table></div>');
		$('#' + scriptDialogId).append('<div><textarea id="'+scriptList+'"></textarea></div>');
		
		
		
		$('#' + scriptTableId).append('<thead><tr><td>nodeName</td><td>portDirection</td><td>testSide</td><td>portName</td><td>testMethod</td></tr></thead><tbody></tbody>');
	
		var sortFields = ['nodeName', 'portDirection', 'testSide', 'portName', 'testMethod'];
		for (var o of sortFields) {
			$('#' + sortBy).append($('<option>').text(o).val(o));
		}
		$('#' + sortDo).click(function() {
			doSortBy($('#' + sortBy).val(), function(script) {
				updateScriptTable(script);
			});
		});
		
		$('#' + register).click(function() {
			registerScript();
		});
		
		$('#' + scriptDialogId).dialog({
			autoOpen: false,
			title: 'Test Target',
			closeOnEscape: true,
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
			width: 600,
			height: 400
		});	
	}
}