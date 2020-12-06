class ReportBuilder {
	constructor(div) {
		var application = "silverbullet";
		$.ajax({
			type: "GET", 
			url: "//" + window.location.host + "/rest/" + application + "/report/getReport",
			success: function(html){
				$('#' + div).html(html);
			}
		});			
	}
}