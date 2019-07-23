class EquationEditor {
	constructor(div) {
		var me = this;
		var idSelectorId = div + "_idselector";
		var idSelectorDialog = div + '_idSelectorDialog';
		var idSelectorDialogContent = div + '_idSelectorDialogContent';
		var newSpecDiv = div + "_newSpec";
		var valueText = div + "_valueText";
			
		var equationDialogId = div + "_equationDialog";
					
		$('#' + div).append('<div id="' + idSelectorDialog + '">' +
				'<div id="' + idSelectorDialogContent + '"></div>' + 
			'</div>');
		$('#' + idSelectorDialog).dialog({
			autoOpen:false,
		});
		
		var idSelector = new IdEditorClass(idSelectorDialogContent);
		var idClear = div + "_clear";
		
		$('#' + div).append('<div id="' + newSpecDiv+ '">' + 
			'<textarea id="' + valueText + '" rows=5 cols=100></textarea>' +
			'<br>' +
			'<div>' +
			'	<button class="copyValue" value="true">true</button>' +
			'	<button class="copyValue" value="false">false</button>' +
			'	<button class="copyValue" value=" == ">==</button>' +
			'	<button class="copyValue" value=" > ">></button>' +
			'	<button class="copyValue" value=" >= ">>=</button>' +
			'	<button class="copyValue" value=" < "><</button>' +
			'	<button class="copyValue" value=" <= "><=</button>' +
			'	<button class="copyValue" value=" != ">!=</button>' +
			'	<button class="copyValue" value=" || ">||</button>' +
			'	<button class="copyValue" value=" ( ) ">()</button>' +
			'	<button class="copyValue" value="*Else">*Else</button>' +
			'	<button class="copyValue" value="*BYUSER">*BYUSER</button>' +
			'	<button id="' + idSelectorId + '">ID Selector</button>' +
			'	<button id="' + idClear + '">Clear</button>' +
			'</div>');	
			
		$('.copyValue').on('click', function(e){
			var curValue = $('#' + valueText).val();
			$('#' + valueText).val(curValue + $(this).val()); 			
		});	
		
		$('#' + idClear).click(function() {
			$('#' + valueText).val('');
		});
				
		$("#" + idSelectorId).on('click', function(e) {
			showIdSelectDialog(function(id, subid) {
				var text;
				if (subid != "") {
					text = '$' + id + '==' + '%' + subid;
				}
				else {
					text = '$' + id;
				}
				$('#' + valueText).val($('#' + valueText).val() + text);			
			});
		});				
		
		function showIdSelectDialog(result) {
			$('#' + idSelectorDialog).dialog({
				autoOpen:false,
				modal:true,
				width: 800,
				height: 600,
				
				buttons: {
					"OK": function(){
						$(this).dialog('close');
						var id = idSelector.getCurrentId();
						var subId = idSelector.getSelectionId();
						result(id, subId);
				    }
				    ,
				    "Cancel": function(){
				      $(this).dialog('close');
				    }
				},
			});
			$('#' + idSelectorDialog).dialog("open");
			idSelector.update();
		}
				
		$('#' + newSpecDiv).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Name',
			  closeOnEscape: true,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	me.callback($('#' + valueText).val());
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 800,
			height: 500
		});		
		
		this.showDialog = function(defaultValue, callback) {
			$('#' + valueText).val(defaultValue);
			$('#' + newSpecDiv).dialog('open');
			me.callback = callback;
		};
	}
	
	show(defaultValue, callback) {
		this.showDialog(defaultValue, callback);
	}
}