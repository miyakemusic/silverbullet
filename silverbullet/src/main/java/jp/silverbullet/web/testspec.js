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
		    //ctx.closePath();
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
		
		function getRandomInt(min, max) {
			min = Math.ceil(min);
			max = Math.floor(max);
			return Math.floor(Math.random() * (max - min) + min); //The maximum is exclusive and the minimum is inclusive
		}
		function drawDiagram2(msg) {
			var outputId = divId + "_outoutButton";
			for (var node of msg.allNodes) {
				var divId = node.id + '_' + getRandomInt(0, 1000);
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
//					drawRect(o.left, o.top + offset, o.width, o.height);
//					drawString(o.id, o.left, o.top + offset + 15);
					createButton(o.id, o.left, o.top + offset, o.width, o.height);
				}
				
				for (var o of node.output) {
//					drawRect(o.left, o.top + offset, o.width, o.height);
//					drawString(o.id, o.left, o.top + offset + 15);
					createButton(o.id, o.left, o.top + offset, o.width, o.height);
				}
			}
			
			for (var line of msg.allLines) {
				drawLine(line.x1, line.y1 + offset, line.x2, line.y2 + offset);
			}
		}
		
		var dialogId = mainId + '_dialog';
		$('#' + mainId).append('<div id="' + dialogId + '"></div>');
		var leftId = dialogId + '_left';
		var rightId = dialogId + '_right';
		
		$('#' + dialogId).append('<table><thead><td>Left Side</td><td>Right Side</td></thead><tbody><tr><td><div id="' + leftId + '"></div></td><td><div id="' + rightId + '"></div></td></tr></tbody></table>');
		
		var comboIdLeft = dialogId + '_comboLeft';
		$('#' + leftId).append('<select id="' + comboIdLeft + '"></select><button>Add</button>');
		var options = ['Fiber end face inspection', 'OTDR', 'Optical Power Meter'];
		for (var o of options) {
			$('#' + comboIdLeft).append($('<option>').text(o).val(o));
		}
		
		var comboIdRight = dialogId + '_comboRight';
		$('#' + rightId).append('<select id="' + comboIdRight + '"></select><button>Add</button>');		
		for (var o of options) {
			$('#' + comboIdRight).append($('<option>').text(o).val(o));
		}
		
//		$('#' + leftId).append('<div><input type="checkbox">Fiber End-face Inspection</input></div>');
//		$('#' + leftId).append('<div><input type="checkbox">OTDR</input></div>');
//		$('#' + leftId).append('<div><input type="checkbox">Optical Power Meter</input></div>');
//		$('#' + leftId).append('<div><input type="checkbox">CPRI</input></div>');
		$('#' + leftId).css('width', '200px');

//		$('#' + rightId).append('<div><input type="checkbox">Fiber End-face Inspection</input></div>');
//		$('#' + rightId).append('<div><input type="checkbox">OTDR</input></div>');
//		$('#' + rightId).append('<div><input type="checkbox">Optical Power Meter</input></div>');
//		$('#' + rightId).append('<div><input type="checkbox">CPRI</input></div>');
		$('#' + rightId).css('width', '200px');
		
		var serial = 0;
		function createButton(text, x, y, width, height) {
			var id = "id" + serial++;
			$('#' + mainId).append('<button id="' + id + '"></button>');
			$('#' + id).text(text);
			$('#' + id).css({'position':'absolute'});
//			$('#' + id).css({'position':'relative'});
			$('#' + id).css('left',x + 'px');
			$('#' + id).css('top',y + 'px');
			$('#' + id).css('width',width + 'px');
			$('#' + id).css('height',height + 'px');
			$('#' + id).css('font', '10px sans-serif');
			
			$('#' + id).click(function() {
				$('#' + dialogId).dialog('open');
			});
		}
		
		$.ajax({
			type: "GET", 
			url: "//" + window.location.host + "/rest/testSpec/getDemo",
			success: function(msg){
				drawDiagram3(msg);
			}
		});	
		
		$('#' + dialogId).dialog({
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