class TestClass {
	constructor(div) {
		var updateId = div + '_testUpdate';
		var recordId = div + '_testRecord';
		var stopId = div + '_testStop';
		var playId = div + '_testPlay';
		
		var mainId = div + '_testMmain';
		$('#' + div).append('<button id="' + updateId + '">Update</button>');
		$('#' + div).append('<input type="checkbox" id="' + recordId + '"><label for="' + recordId + '">Record</label>');
//		$('#' + div).append('<button id="' + stopId + '">Stop</button>');
		$('#' + div).append('<input type="checkbox" id="' + playId + '"><label for="' + playId + '">Play</label>');
		
		$('#' + div).append('<div id="' + mainId + '"></div>');
		
		$('#' + recordId).button();
		$('#' + playId).button();
		
		$('#' + updateId).click(function() {
			update();
		});
		$('#' + recordId).change(function() {
			if ($(this).prop('checked') == true) {
				record();
			}
			else {
				stop();
			}
		});	
		$('#' + stopId).click(function() {
			stop();
		});	
		$('#' + playId).change(function() {
			play();
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
		function record() {
			$('#' + mainId).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/startRecording",
			   success: function(msg){

			   }
			});	
		}
		function stop() {
			$('#' + mainId).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/stopRecording",
			   success: function(msg){

			   }
			});	
		}
		function play() {
			$('#' + mainId).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/playBack",
			   success: function(msg){

			   }
			});	
		}
	}
}