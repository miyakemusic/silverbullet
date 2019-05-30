class EditableText {
	constructor(div, value, changed) {
		var labelId = 'label_' + div;
		var editId = 'edit_' + div;
		
		$('#' + div).append('<div class="labelElement" id="' + labelId + '"></div>');

		$('#' + div).append('<input class="editElement" type="text" id="' + editId + '">');
		
		$('#' + editId).hide();
		
		setLabelValue(value);

		$('#' + editId).val(value);
		
		$('#' + labelId).show();
		$('#' + editId).hide();
		
		$('#' + labelId).click(function() {
			$('#' + labelId).hide();
			$('#' + editId).show();			
		});
		$('#' + editId).keydown(function(event) {
			if (event.altKey) {
				if (event.which == 13) {
					$('#' + editId).val($('#' + editId).val() + '\n');
				}
			} 
			else if (event.which == 13) { // Enter
				$('#' + labelId).show();
				$('#' + editId).hide();
				setLabelValue($('#' + editId).val().replace('\n','<br>'));
				
				changed($('#' + editId).val());
			}
			else if (event.which == 27) { // Cancel
				$('#' + labelId).show();
				$('#' + editId).hide();
			}
		});	
		$('#' + editId).focusout(function() {
			$('#' + labelId).show();
			$('#' + editId).hide();
		});	
		
		function setLabelValue(value) {
			$('#' + labelId).text(value);
		}
		
		this.text = function text(text) {
			$('#' + editId).val(text);
			$('#' + labelId).text(text);
		}
	}
	
	setText(text) {
		this.text(text);
	}
}