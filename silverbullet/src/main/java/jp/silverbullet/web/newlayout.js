class NewLayout {
	constructor(div) {
		this.divNumber = 0;
		var me = this;
		retreiveDesign();
		
		function retreiveDesign() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/newGui/getDesign",
			   success: function(design){
			   		build(design, div);
			   }
			});		
		}
		
		function build(design, div) {
			for (var pane of design.panes) {
				buildSub(pane, div);
			}
		}
		
		function buildSub(widget, d) {
			var id = getId();
//			id = escape(id);
			
			if (widget.type == 'Pane') {
				$('#' + d).append('<div class="design" id="' + id + '"></div>');
				for (var w of widget.widgets) {
					buildSub(w, id);
				}
			}		
			else if (widget.type == 'TabPane') {
				var content = '';
				var html = '<div id="' + id + '">';
				html += '<ul>';

				for (var i = 0; i < widget.panes.length; i++) {
					var w = widget.panes[i];
					var href = id + 'tabno' + i;
					html += '<li><a href="#' + href + '"><span>' + w.caption + '</span></a></li>';
					
					content += '<div id="' + href + '"></div>';
				}
				html += '</ul>';
	
				$('#' + d).append(html + content + '</div>');
				$('#' + id).tabs();
				
				for (var i = 0; i < widget.panes.length; i++) {
					var w = widget.panes[i];
					for (var w2 of w.widgets) {
						buildSub(w2, id + 'tabno' + i);
					}
				}	
			}
			else if (widget.type == 'CheckBox') {	
				$('#' + d).append('<div id="' + id + '"><input type="checkbox" id="' + id + 'check">' + widget.id + '</div>');
			}
			else if (widget.type == 'TextField') {
				$('#' + d).append('<input type="text" id="' + id + '">');
				$('#' + id).val(widget.id);
			}	
			else if (widget.type == 'ToggleButton') {
				$('#' + d).append('<button id="' + id + '">' +  widget.id + '</button>');
			}	
			else if (widget.type == 'ComboBox') {	
				$('#' + d).append('<select id="' + id + '"></select>');
				var option = $('<option>').val(widget.id).text(widget.id);
				$('#' + id).append(option);			
			}			
			else if (widget.type == 'StaticText') {	
				$('#' + d).append('<label id="' + id + '"></label>');	
				$('#' + id).val(widget.id);	
			}		
			for (var css of widget.css) {
				$('#' + id).css(css.key, css.value);
			}
		}
		
		function getId() {
			return 'div' + me.divNumber++;
		}

	}
}
