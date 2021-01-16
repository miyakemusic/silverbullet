class TestSpec {
	constructor(div) {
		var canvasId = div + '_canvas';
		$('#' + div).append('<canvas id="' + canvasId + '" width="800" height="400"></canvas>');
	
		drawLine();
		
		function drawLine() {
		    var canvas = $("#" + canvasId);
		    var ctx = canvas[0].getContext("2d");
		    ctx.beginPath();
		    ctx.moveTo( 10, 10);
		    ctx.lineTo(190,190);
		    ctx.lineTo( 10,190);
		    //ctx.closePath();
		    ctx.stroke();
		    return false;
		}	
		
		function drawDiagram(msg) {
			$('#' + div).append('<button>' + msg.rootNode.id + '</button>');
			recursive(msg.rootNode.subNodes);
		}
		
		function recursive(sunNodes) {
			for (var key in sunNodes) {
				var v = sunNodes[key];
				$('#' + div).append('<button>' + v.id + '</button>');
				
				recursive(v.subNodes);
			}
		}
		
		$.ajax({
			type: "GET", 
			url: "//" + window.location.host + "/rest/testSpec/getDemo",
			success: function(msg){
				drawDiagram(msg);
			}
		});	
	}
}