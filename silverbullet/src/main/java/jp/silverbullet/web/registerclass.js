class RegisterClass {
	constructor(div) {
		var selectId = div + '_depselect';		
		$('#' + div).append('<div>Type:<select id="' + selectId + '"></select></div>');
		
		var regDiv = div + '_regDiv';
		$('#' + div).append('<div id="' + regDiv + '"></div>');
		
		$('#' + selectId).append($('<option>').text('Specification').val('Specification'));
		$('#' + selectId).append($('<option>').text('Map').val('Map'));
		$('#' + selectId).change(function() {
			$('#' + regDiv).empty();
			if ($(this).val() == 'Specification') {
				new RegisterSpec(regDiv);
			}
			else {
				new RegisterMap(regDiv);
			}
		})
	}
}