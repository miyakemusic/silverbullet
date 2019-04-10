class MyChart {
	constructor(div, width, height) {
		this.chartId = div + "_chart";
		$('#' + div).append('<canvas id="' + this.chartId + '" width="' + width  + "px" + '" height="' + height + 'px"></canvas>');
		this.canvas = $('#' + this.chartId).get(0);
		this.canvas.width = width*2;
		this.canvas.height = height*2;
		this.canvas.style.width = width + "px";
		this.canvas.style.height = height + "px";
	}
	
	setSize(width, height) {
		$('#' + this.chartId).width(width);
		$('#' + this.chartId).height(height);
	}
	
	update() {
		var ctx = this.canvas.getContext('2d');
//		ctx.beginPath();

		ctx.lineWidth = 1.0;
		
		var grids = 10;
		var offsetLeft = 100;
		var offsetTop = 15;
		var offsetRight = 15;
		var offsetBottom = 40;
		
		var ymin = -2;
		var ymax = 2;
		var stepY = (ymax - ymin) / grids;
		var xmin = 1500;
		var xmax = 1600;
		var points = 100;
		var stepX = (xmax - xmin)/points;
		ctx.rect(offsetLeft, offsetTop, this.canvas.width - offsetLeft - offsetRight, this.canvas.height - offsetTop - offsetBottom);
		ctx.stroke();
		
		var xstep = (this.canvas.width - offsetLeft - offsetRight) / grids;
		var ystep = (this.canvas.height - offsetTop - offsetBottom) / grids;
		
		ctx.setLineDash([1,5]);
		ctx.beginPath();
		
		for (var x = 1; x < grids ; x++) {
			ctx.moveTo(offsetLeft + x * xstep, offsetTop);
			ctx.lineTo(offsetLeft + x * xstep, this.canvas.height - offsetBottom);
			
		}
		for (var y = 1; y < grids; y++) {
			ctx.moveTo(offsetLeft, offsetTop + y * ystep);
			ctx.lineTo(this.canvas.width - offsetRight, offsetTop + y * ystep);
		}
		ctx.closePath();
		ctx.stroke();
		ctx.setLineDash([0]);
		
		ctx.font = "30px 'Monotype Corsiva'";
		ctx.textBaseline = "top";
		ctx.textAlign = "start";
		ctx.fillText(xmin + 0 * stepX, offsetLeft, this.canvas.height - offsetBottom);
		ctx.textAlign = "center";
		ctx.fillText(xmin + 5 * stepX, offsetLeft + 5 * xstep, this.canvas.height - offsetBottom);
		ctx.textAlign = "end";
		ctx.fillText(xmin + 10 * stepX, offsetLeft + 10 * xstep, this.canvas.height - offsetBottom);
	
		ctx.textBaseline = "top";
		ctx.fillText(ymin + 10 * stepY, offsetLeft - 10, offsetTop + 0 * ystep);
		ctx.textBaseline = "middle";
		ctx.fillText(ymin + 5 * stepY, offsetLeft - 10, offsetTop + 5 * ystep);
		ctx.textBaseline = "bottom";
		ctx.fillText(ymin + 0 * stepY, offsetLeft - 10, offsetTop + 10 * ystep);
		
		var xres = (this.canvas.width - offsetLeft - offsetRight) / (xmax - xmin);
		var yres = (this.canvas.height - offsetTop - offsetBottom) / (ymax - ymin);
		
		var ix = 0;
		var iy = 0;
		
		ctx.beginPath();
		ctx.strokeStyle = 'rgb(255, 0, 0)';
		for (var x = xmin; x < xmax; x+= 0.1) {
			var y = Math.sin(x);
			ix = (x - xmin) * xres + offsetLeft;
			iy = y * yres + offsetTop + (this.canvas.height - offsetTop - offsetBottom)/2;
			
			ctx.lineTo(ix, iy);
		}
//		ctx.closePath();
		ctx.stroke();
	}
}
