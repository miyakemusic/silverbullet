class TestSpec {
	constructor(div) {
		var canvasId = div + '_canvas';
		$('#' + div).append('<canvas id="' + canvasId + '" width="1000" height="900"></canvas>');
	
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
		
		$.ajax({
			type: "GET", 
			url: "//" + window.location.host + "/rest/testSpec/getDemo",
			success: function(msg){
				drawDiagram2(msg);
			}
		});	
	}
}