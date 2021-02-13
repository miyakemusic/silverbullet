class TestSpec {
	constructor(div) {
	
		var toolBarId = div + "_toolbar";
		$('#' + div).append('<div id="' + toolBarId + '"></div>');
		var createDemoId = div + "_createDemo";
		
		var projectName = div + "_projectName";
		$('#' + toolBarId).append('Project: <select id="' + projectName + '">');
		retreiveProjectList(function(options) {
			for (var o of options) {
				$('#' + projectName).append($('<option>').text(o).val(o));
			}
		});
		
		$('#' + toolBarId).append('<button id="' + createDemoId + '">Create Demo</button>');
		$('#' + createDemoId).click(function() {
			createDemo();
		});
		
		var getTestSpecId = div + "_getTestSpec";
		$('#' + toolBarId).append('<button id="' + getTestSpecId + '">Redraw</button>');
		$('#' + getTestSpecId).click(function() {
			retrieveTest();
			retrievePortState();
		});
		
		var scriptDialogId = div + '_scriptDialog';
		var scriptTableId = div + '_scriptDialog';
		var createScriptId = div + "_createScript";
		var createReportId = div + "_createReport";
		var updateStateId = div + "_updateState";
		$('#' + toolBarId).append('<button id="' + createScriptId + '">Create Script</button>');
		$('#' + toolBarId).append('<button id="' + createReportId + '">Create Report</button>');
		$('#' + toolBarId).append('<button id="' + updateStateId + '">Update State</button>');
		
		$('#' + createScriptId).click(function() {
			createScript(function(script) {
				updateScriptTable(script);
				$('#' + scriptDialogId).dialog('open');
			});
		});
		$('#' + updateStateId).click(function() {
			retrievePortState();
		});
						
		function updateScriptTable(script) {
			$('#' + scriptTableId + ' > tbody').empty();
			for (var o of script.spec) {
				var row = '<tr><td>'+o.nodeName+'</td><td>'+o.portDirection+'</td><td>'+o.testSide+'</td><td>'+o.portName+'</td><td>'+o.testMethod+'</td></tr>';
				$('#' + scriptTableId + ' > tbody').append(row);
			}
			$('#' + scriptTableId + ' td').each(function() {
			 	$(this).css('width', "250px");
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
		
		var checked = [];
		
		$('#' + createReportId).click(function() {
			createReport(checked);
		});
		
		var offset = 20;
		function drawDiagram3(msg) {		
			for (var node of msg.allNodes) {
				drawRect(node.left, node.top + offset, node.width, node.height);
				var nodeCheckDiv = div + "_checkDiv" + node.id;
				var nodeCheck = div + "_check" + node.id;
				$('#' + mainId).append('<div id="' + nodeCheckDiv + '"><input type="checkbox" id="'+nodeCheck+ '" value="'+node.id+'"></div>');
				var reportLink = div + node.id + "report";
				$('#' + nodeCheckDiv).append('<a href="' + "//" + window.location.host + resourcepath() + "/report?nodeId=" + node.id + '" target="_blank">' + node.name + '</a>');
//				$('#' + nodeCheckDiv).append('<label id="' + reportLink + '">' + node.name + '</label>');
//				$('#' + reportLink).click(function() {
//					window.open("//" + window.location.host + resourcepath() + "/report?nodeId=" + node.id, "window1","width=800,height=600");
//				});
				
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
		retrieveConnectors(function(list) {
			for (var o of list) {
				$('#' + comboConnector).append($('<option>').text(o).val(o));
			}
		});
		
		var portDetailTable = div + "_portDetailTable";
		$('#' + dialogId).append('<table id="' + portDetailTable + '"><thead><td><label id="' + leftCaption + '">Left Side</td><td><label id="'+rightCaption+'">Right Side</label></td></thead><tbody><tr><td><div id="' + leftId + '"></div></td><td><div id="' + rightId + '"></div></td></tr></tbody></table>');
		$('#' + portDetailTable + ' td').each(function() {
		 	$(this).css('width', "300px");
		 	$(this).css('vertical-align', 'top');
		});
				
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

				$('#' + rightCaption).text('Inside Port (Device side)');
				$('#' + leftCaption).text('Outside Port (Fiber side)');
				
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
						var portResult = div + "_device_" + v.replace(/\s/g, '');
						left_tests.push(v);
						
						$('#' + leftTests).append('<div><h2>[' + v + ']</h2></div><div id="' + portResult + '"></div>');
						retreivePortResult(beingSelectedPort, v, 'Device Side', portResult);
					}
					for (var v of portConfig.outsideTest) {
						var portResult = div + "_fiber_" + v.replace(/\s/g, '');
						right_tests.push(v);
						
						$('#' + rightTests).append('<div><h2>[' + v + ']</h2></div><div id="' + portResult + '"></div>');
						retreivePortResult(beingSelectedPort, v, 'Fiber Side', portResult);
					}	
					$('#' + leftTests).css('vertical-align', 'top');
					$('#' + rightTests).css('vertical-align', 'top');				
				});
			});
		}
		
		function createReport(target) {
			window.open( "//" + window.location.host + resourcepath() + "/report?nodeId=" + target, '_blank');
		}
		
		function retreiveProjectList(callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/projectList",
				success: function(msg){
					callback(msg);
				}
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
		
		function retreivePortResult(portId, testMethod, side, div) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + resourcepath() + "/result?portId=" + portId + "&side=" + side + "&testMethod=" + testMethod,
				success: function(msg){
					$('#' + div).append(msg);
				}
			});			
		}
		
		function retrieveTest() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + resourcepath() + "/getTestSpec",
				success: function(msg){
					drawDiagram3(msg);
				}
			});	
		}
		
		
		setInterval(blinkCurrent, 1000);
		var latestTest;
		var blink = true;
		function blinkCurrent() {
			if (latestTest == null) {
				return;
			}
			var color;
			if (blink) {
				color = 'black';
			}
			else {
				color = 'white';
			}
			$('#' + latestTest.portId).css('color', color);
			blink = !blink;
		}
		
		function retrievePortState() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + resourcepath() + "/portState",
				success: function(msg){
					for (var o of msg) {
						var color = 'gray';
						if (o.portState == 'COMPLETE_PASS') {
							color = 'lightgreen';
						}
						else if (o.portState == 'COMPLETE_FAIL') {
							color = 'red';
						}
						else if (o.portState == 'ON_GOING') {
							color = 'lightblue';
						}
						
						$('#' + o.portId).css('background-color', color);
						$('#' + o.portId).css('color', 'black');
					}
					
					latestTest = msg[msg.length-1];
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
				url: "//" + window.location.host + resourcepath() + "/portConfig?id=" + id,
				success: function(msg){
					callback(msg);
				}
			});	
		}
		function postPortConfig(id, testConfig) {
			$.ajax({
				url: "//" + window.location.host + resourcepath() * "/postPortConfig?id=" + id,
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
				url: "//" + window.location.host + resourcepath() + "/copyConfig?id=" + id,
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
				url: "//" + window.location.host + resourcepath() + "/createScript?id=" + checked,
				success: function(msg){
					callback(msg);
				}
			});			
		}
		
		function doSortBy(sortBy, callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + resourcepath() + "/sortBy?sortBy=" + sortBy,
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
				url: "//" + window.location.host + resourcepath() + "/registerScript",
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
		
		function resourcepath() {
			return "/rest/testSpec/" + $('#' + projectName).val();
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
			width: 800,
			height: 600
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
		
		websocket.addListener('TESTRESULT', function(result) {
			retrievePortState();
		});
	}
}