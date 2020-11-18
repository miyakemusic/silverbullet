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

class ListBox {
	constructor(parent, title, path, resource) {
		var me = this;
		var listId = parent + "_listbox";
		$('#' + parent).append(title + '<select id="' + listId + '"></select>');
		
		this.getPath = path + "/get" + resource;
		this.setPath = path + "/set" + resource;
		this.listPath = path + "/get" + resource + "List";
		this.resource  = resource;
		
		getList();
		
		function getList() {
			$.ajax({
				type: "GET", 
				url: me.listPath,
				success: function(msg){
					for (var option of msg) {
						$('#' + listId).append($('<option />').val(option).html(option));
					}
					getValue();
				}
			});		
		}
		
		function getValue() {
			$.ajax({
				type: "GET", 
				url: me.getPath,
				success: function(msg){
					$('#' + listId).val(msg);
				}
			});
		}
		
		$('#' + listId).change(function() {
			$.ajax({
				type: "GET", 
				url: me.setPath + "?value=" + $(this).val(),
				success: function(msg){

				}
			});	
		});
	}
	
	path(path) {
		this.getPath = path + "/get" + this.resource;
		this.setPath = path + "/set" + this.resource;
		this.listPath = path + "/get" + this.resource + "List";
	}
}
