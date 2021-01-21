class TestSpec {
	constructor(div) {
	
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
			var divId = parent.id + '_' + node.id;
			var tag = '<div id="' + divId + '"><table><tr><td><button>'+ node.id + '</button></td><td>';
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
				var divId = node.serial;
				var tag = '<div id="' + divId + '"><table><tr><td><button>'+ node.id + '</button></td><td>';
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
		
		var offset = 20;
		function drawDiagram3(msg) {
			for (var node of msg.allNodes) {
				drawRect(node.left, node.top + offset, node.width, node.height);
				drawString(node.id, node.left + 10, node.top + offset - 5);
				
				for (var o of node.input) {
					createButton(o.serial, node.id, o.id, o.left, o.top + offset, o.width, o.height);
				}
				
				for (var o of node.output) {
					createButton(o.serial, node.id, o.id, o.left, o.top + offset, o.width, o.height);
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
		retrieveTestType(function(list) {
			for (var o of list) {
				$('#' + comboConnector).append($('<option>').text(o).val(o));
			}
		});
		
		$('#' + dialogId).append('<table><thead><td><label id="' + leftCaption + '">Left Side</td><td><label id="'+rightCaption+'">Right Side</label></td></thead><tbody><tr><td><div id="' + leftId + '"></div></td><td><div id="' + rightId + '"></div></td></tr></tbody></table>');
		
		var left_tests = [];
		var leftAdd = div + '_leftAdd';
		var comboIdLeft = dialogId + '_comboLeft';
		$('#' + leftId).append('<select id="' + comboIdLeft + '"></select><button id="' + leftAdd + '">Add</button>');
		var options = ['Fiber end-face inspection', 'OTDR', 'Optical Power Meter'];
		for (var o of options) {
			$('#' + comboIdLeft).append($('<option>').text(o).val(o));
		}
		
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
		for (var o of options) {
			$('#' + comboIdRight).append($('<option>').text(o).val(o));
		}
		var rightTests = div + '_rightTests';
		$('#' + rightId).append('<div id="' + rightTests + '"></div>');
		$('#' + rightAdd).click(function() {
			right_tests.push($('#' + comboIdRight).val());
			$('#' + rightTests).append('<div>' + $('#' + comboIdRight).val() + '</div>');
		});
		
		$('#' + rightId).css('width', '200px');
		
		
		
		function createButton(serial, nodeText, portText, x, y, width, height) {
			var id = serial;
			$('#' + mainId).append('<button name="' + nodeText + '_' + portText + '" id="' + serial + '"></button>');
			$('#' + id).text(portText);
			$('#' + id).css({'position':'absolute'});
			$('#' + id).css('left',x + 'px');
			$('#' + id).css('top',y + 'px');
			$('#' + id).css('width',width + 'px');
			$('#' + id).css('height',height + 'px');
			$('#' + id).css('font', '10px sans-serif');
			
			$('#' + id).click(function() {
				beingSelectedPort = $(this).prop('id');
				if ($(this).attr('id').startsWith('in')) {
					$('#' + rightCaption).text('Device Side');
					$('#' + leftCaption).text('Fiber Side');
				}
				else {
					$('#' + leftCaption).text('Device Side');
					$('#' + rightCaption).text('Fiber Side');
				}
				
				left_tests.splice(0);
				right_tests.splice(0);
				
				var title = $(this).prop('name');
				$('#' + dialogId).dialog({ title: title });
				$('#' + dialogId).dialog('open');
				
				retrievePortConfig(beingSelectedPort, function(portConfig) {
					$('#' + leftTests).empty();
					$('#' + rightTests).empty();
					$('#' + comboConnector).val(portConfig.connector);
					for (var v of portConfig.leftSideTest) {
						$('#' + leftTests).append('<div>' + v + '</div>');
						left_tests.push(v);
					}
					for (var v of portConfig.rightSideTest) {
						$('#' + rightTests).append('<div>' + v + '</div>');
						right_tests.push(v);
					}					
				});
			});
		}
		
		$.ajax({
			type: "GET", 
			url: "//" + window.location.host + "/rest/testSpec/getDemo",
			success: function(msg){
				drawDiagram3(msg);
			}
		});	
		
		function retrieveTestType(callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/testType",
				success: function(msg){
					callback(msg);
				}
			});	
		}
		
		function retrievePortConfig(serial, callback) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/testSpec/portConfig?serial=" + serial,
				success: function(msg){
					callback(msg);
				}
			});	
		}
		function postPortConfig(serial, testConfig) {
			$.ajax({
				url: "//" + window.location.host + "/rest/testSpec/postPortConfig?serial=" + serial,
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify(testConfig),
				processData: false
			})
			.done(function( data ) {

			});	
		}		
		
		function commitPortConfig() {
			var portConfig = new Object();
			portConfig.connector = $('#' + comboConnector).val();
			portConfig.leftSideTest = left_tests;
			portConfig.rightSideTest = right_tests;
			postPortConfig(beingSelectedPort, portConfig);
		}
		
		$('#' + dialogId).dialog({
			autoOpen: false,
			title: 'Test Target',
			closeOnEscape: true,
			modal: true,
			buttons: {
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
	}
}