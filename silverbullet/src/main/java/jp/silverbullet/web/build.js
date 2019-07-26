class Build {
	constructor (div) {
		var pathId = div + "_path";
		$('#' + div).append('<div>Source Path: <input type="text" id="' + pathId + '"></div>');
		$('#' + pathId).css('width', '100%');
		getInfo(pathId, "getPath");
		
		var packageId = div + "_package";
		$('#' + div).append('<div>Package Name: <input type="text" id="' + packageId + '"></div>');
		$('#' + packageId).css('width', '100%');
		getInfo(packageId, "getPackage");
		
		var submitId = div + "_submit";
		$('#' + div).append('<button id="' + submitId + '">Submit</button>');
		$('#' + submitId).click(function() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/selfbuild/setInfo?path=" + $('#' + pathId).val() + "&package=" + 
					$('#' + packageId).val(),
					success: function(msg) {

					}		
			});	
		});
		
		var buildId = div + "_build";
		$('#' + div).append('<button id="' + buildId + '">Build</button>');
		$('#' + buildId).click(function() {
			$.ajax({
				type: "GET", 
				url: "//" + window.location.host + "/rest/selfbuild/build",
					success: function(msg) {

					}		
			});	
		});
				
		function getInfo(id, res) {
			$.ajax({
				type: "GET", 
				url: "://" + window.location.host + "/rest/selfbuild/" + res,
					success: function(msg) {
						$('#' + id).val(msg);
					}		
			});	
		}
		
	}
	
}