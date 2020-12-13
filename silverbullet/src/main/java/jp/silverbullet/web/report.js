class ReportBuilder {
	constructor(div) {
		var application = "silverbullet";
		
		var list = div + "_list";
		
		$('#' + div).append('<select id="' + list + '"></select>');
		$('#' + list).change(function() {
			getReport($('#' + list).val());
		});
		
		var uploadId = div + "_upload";
		var fileId = div + "_file";
		$('#' + div).append('<input type="file" id="' + fileId + '"><button id="' + uploadId + '">Upload</button>');

		$('#' + uploadId).click(function() {
			postFile();
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
				url: "//" + window.location.host + "/rest/" + application + "/report/getReport/" + target,
				success: function(html){
					$('#' + main).html(html);
				}
			});			
		}
		
		function postFile() {
			var file = $('#' + fileId)[0].files[0];
			var reader = new FileReader();
			reader.readAsDataURL(file);
			reader.onload = function(event) {
				$.ajax({
		            url: "//" + window.location.host + "/rest/" + application + "/report/html",
		            type: 'POST',
		            contentType: 'text/plain',
					data: event.target.result,
					processData: false
		        })
		        .done(function( data ) {
					$('#' + main).html(data);
		        });			
			}
		}
	}
}