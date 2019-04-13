class MyChart {
	constructor(div, width, height) {
		this.chartId = div + "_chart";
		$('#' + div).append('<canvas id="' + this.chartId + '" width="' + width  + "px" + '" height="' + height + 'px"></canvas>');
		this.canvas = $('#' + this.chartId).get(0);
		this.canvas.width = width*2;
		this.canvas.height = height*2;
		this.canvas.style.width = width + "px";
		this.canvas.style.height = height + "px";
		this.ctx = this.canvas.getContext('2d');
		
		this.grids = 10;
		this.offsetLeft = 100;
		this.offsetTop = 15;
		this.offsetRight = 15;
		this.offsetBottom = 40;
		
		this.update();
	}
	
	setSize(width, height) {
		$('#' + this.chartId).width(width);
		$('#' + this.chartId).height(height);
	}
	
	getDataPoints() {
		return this.canvas.width - this.offsetLeft - this.offsetRight;
	}
	
	update(data) {
		var ctx = this.ctx;
		
		ctx.strokeStyle = 'rgb(0, 0, 0)';
		ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

		ctx.lineWidth = 1.0;
		ctx.beginPath();
		ctx.rect(this.offsetLeft, this.offsetTop, this.canvas.width - this.offsetLeft - this.offsetRight, this.canvas.height - this.offsetTop - this.offsetBottom);
		ctx.stroke();
		
		var xstep = (this.canvas.width - this.offsetLeft - this.offsetRight) / this.grids;
		var ystep = (this.canvas.height - this.offsetTop - this.offsetBottom) / this.grids;
		
		ctx.setLineDash([1,5]);
		ctx.beginPath();
		
		for (var x = 1; x < this.grids ; x++) {
			ctx.moveTo(this.offsetLeft + x * xstep, this.offsetTop);
			ctx.lineTo(this.offsetLeft + x * xstep, this.canvas.height - this.offsetBottom);
			
		}
		for (var y = 1; y < this.grids; y++) {
			ctx.moveTo(this.offsetLeft, this.offsetTop + y * ystep);
			ctx.lineTo(this.canvas.width - this.offsetRight, this.offsetTop + y * ystep);
		}
		ctx.closePath();
		ctx.stroke();
		
		if (data == null) {
			return;
		}
						
		var ymin = parseFloat(data.ymin);
		var ymax = parseFloat(data.ymax);
		var stepY = (ymax - ymin) / this.grids;
		var xmin = parseFloat(data.xmin);
		var xmax = parseFloat(data.xmax);
		var points = data.y.length;
		var stepX = (xmax - xmin)/points;
		
		ctx.setLineDash([0]);
		
		ctx.font = "30px 'Monotype Corsiva'";
		ctx.textBaseline = "top";
		ctx.textAlign = "start";
		ctx.fillText(xmin, this.offsetLeft, this.canvas.height - this.offsetBottom);
		ctx.textAlign = "center";
		ctx.fillText((xmin + xmax)/2.0, this.offsetLeft + 5 * xstep, this.canvas.height - this.offsetBottom);
		ctx.textAlign = "end";
		ctx.fillText(xmax, this.offsetLeft + 10 * xstep, this.canvas.height - this.offsetBottom);
	
		ctx.textBaseline = "top";
		ctx.fillText(ymax, this.offsetLeft - 10, this.offsetTop + 0 * ystep);
		ctx.textBaseline = "middle";
		ctx.fillText((ymin+ymax)/2.0, this.offsetLeft - 10, this.offsetTop + 5 * ystep);
		ctx.textBaseline = "bottom";
		ctx.fillText(ymin, this.offsetLeft - 10, this.offsetTop + 10 * ystep);
		
		var xres = (this.canvas.width - this.offsetLeft - this.offsetRight) / (data.y.length);
		var yres = (this.canvas.height - this.offsetTop - this.offsetBottom) / (ymax - ymin);
		
		var ix = 0;
		var iy = 0;
		
		ctx.beginPath();
		ctx.strokeStyle = 'rgb(255, 0, 0)';
		var yy = this.offsetTop + (this.canvas.height - this.offsetTop - this.offsetBottom)/2;
		for (var i = 0; i < data.y.length; i++) {
			ix = i * xres + this.offsetLeft;
			iy = parseInt(data.y[i]) * yres + yy;
			
			ctx.lineTo(ix, iy);
		}
//		ctx.closePath();
		ctx.stroke();
	}
}
