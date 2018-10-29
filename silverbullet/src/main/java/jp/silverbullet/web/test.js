class TestClass {
	constructor(div) {
		var updateId = div + '_testUpdate';
		var recordId = div + '_testRecord';
		var stopId = div + '_testStop';
		var playId = div + '_testPlay';
		
		var mainId = div + '_testMmain';
		$('#' + div).append('<button id="' + updateId + '">Update</button>');
		$('#' + div).append('<button id="' + updateId + '">Record</button>');
		$('#' + div).append('<button id="' + updateId + '">Stop</button>');
		$('#' + div).append('<button id="' + updateId + '">Play</button>');
		
		$('#' + div).append('<div id="' + mainId + '"></div>');
		
		$('#' + updateId).click(function() {
			update();
		});
		
		function update() {
			$('#' + mainId).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/getTest",
			   success: function(msg){
					for (var obj of msg) {
						$('#' + mainId).append('<div>' + obj.id + "..." + obj.value + '</div>');
					}
			   }
			});	
		}
	}
}