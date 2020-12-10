class ReportBuilder {
	constructor(div) {
		var application = "silverbullet";
		
		var list = div + "_list";
		
		$('#' + div).append('<select id="' + list + '"></select>');
		$('#' + list).change(function() {
			getReport($('#' + list).val());
		});
		
		var main = div + "_main";
		$('#' + div).append('<div id="' + main + '"></div>');
		
		getList();
		
		function getList() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/report/getList",
				success: function(result){
					for (var val of result) {
						var option = $('<option>').val(val).text(val);
						$('#' + list).append(option);	
					}				
				}
			});			
		}
				
		function getReport(target) {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/" + application + "/report/getReport?target=" + target,
				success: function(html){
					$('#' + main).html(html);
				}
			});			
		}
	}
}