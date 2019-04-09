class MyChart {
	constructor(div, width, height) {
		this.chartId = div + "_chart";
		$('#' + div).append('<canvas id="' + this.chartId + '" width="' + width + '" height="' + height + '"></canvas>');
		this.canvas = $('#' + this.chartId).get(0);
	}
	
	setSize(width, height) {
		$('#' + this.chartId).width(width);
		$('#' + this.chartId).height(height);
	}
	
	update() {
		var ctx = this.canvas.getContext('2d');
//		ctx.beginPath();

//	ctx.scale(2,2);
//ctx.translate(0.5, 0.5);
		ctx.lineWidth = 1.0;
		ctx.rect(0, 0, this.canvas.width, this.canvas.height);
		ctx.stroke();
		
		var xmax = 10;
		var xmin = 0;
		var ymax = 1.5;
		var ymin = -1.5;
		
		var xres = this.canvas.width / (xmax - xmin);
		var yres = this.canvas.height / (ymax - ymin);
		
		var ix = 0;
		var iy = 0;
		
		ctx.beginPath();
		for (var x = 0; x < 10; x+= 0.1) {
//			ctx.moveTo(ix, iy);
			var y = Math.sin(x);
			ix = x * xres;
			iy = y * yres + this.canvas.height/2;
			
			ctx.lineTo(ix, iy);
		}
//		ctx.closePath();
		ctx.stroke();
	}
}
