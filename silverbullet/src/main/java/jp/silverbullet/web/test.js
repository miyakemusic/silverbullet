class TestClass {
	constructor(div) {
		var toDialogId = div + '_toDialog';
		$('#' + div).append('<button id="' + toDialogId + '">To Dialog</button>');
				
		var mainId = div + '_testMmain';
		$('#' + div).append('<div id="' + mainId + '"></div>');

		$('#' + toDialogId).click(function() {
			$('#' + mainId).dialog({
				autoOpen: true,
				title: this.title,
				closeOnEscape: false,
				modal: false,
				buttons: {
					"OK": function(){
						$(this).dialog('destroy');
						
					}

				},
				width: 800,
				height: 300
			});	
		});
		
//		var commandType = ['PROPERTY', 'PROPERTY_TEST', 'REGISTER', 'REGISTER_TEST', 'CONTROL'];
		var dialogId = div + '_dialogId';
		var commandTypeId = div + '_commandType';
		var editId = div + '_edit';
		$('#' + div).append('<div id="' + dialogId + '"><input type="text" id="' + editId + '"></div>');

//		for (var type of commandType) {
//			$('#' + commandTypeId).append($('<option>').val(type).text(type));
//		}
		
		$('#' + dialogId).dialog({
//			  dialogClass: "no-titlebar", 
			  autoOpen: false,
			  title: 'Add/Edit',
			  closeOnEscape: false,
			  modal: false,
			  buttons: {
			    "OK": function(){
			    	dialogClosed();
			    	$(this).dialog('close');
			    },
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			  },
			width: 500,
			height: 300
		});	

		var headers = ['No.', 'Type', 'Target', 'Value', 'Expected', 'Result', 'PassFail', 'Elaplsed', ''];
		var headerId = div + '_header';
						
		var selectedData;
		var selectedCol;	
		var selectedSerial;
			
		var table;
		
		build(mainId);
		
		function build(div2) {
			var testListId = div2 + '_testList';
			var updateId = div2 + '_testUpdate';
			var recordId = div2 + '_testRecord';
			var stopId = div2 + '_testStop';
			var playId = div2 + '_testPlay';
			var saveId = div2 + '_save';
			var tableId = div2 + '_testTable';
			var upId = div2 + '_moveUp';
			var downId = div2 + '_moveDown';
			
			$('#' + div2).append('Test: <select id="' + testListId + '">');
			$('#' + div2).append('<button id="' + updateId + '">Update</button>');
			$('#' + div2).append('<input type="checkbox" id="' + recordId + '"><label for="' + recordId + '">Record</label>');
			$('#' + div2).append('<input type="checkbox" id="' + playId + '"><label for="' + playId + '">Play</label>');

			$('#' + div2).append('<button id="' + saveId + '">Save</button>');

			$('#' + div2).append('<button id="' + upId + '"  class="action">Move Up</button>');
			$('#' + div2).append('<button id="' + downId + '"  class="action">Move Down</button>');


			var mainId2 = div + '_testMmain2';
			$('#' + div2).append('<div id="' + mainId2 + '"></div>');
			$('#' + mainId2).append('<table id="' + tableId + '"><thead></thead><tbody></tbody></table>');
						
			var tr = '<tr>';
			for (var head of headers) {
				tr += '<td>' + head + '</td>';
			}
			tr += '</tr>';
			$('#' + tableId + ' > thead').append(tr);
			
			$('#' + tableId).css("table-layout","fixed");
			$('#' + tableId).css("width","100%");
			
			$('#' + tableId).append('<colgroup><col style="width:40px"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:200px"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:20%"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:20%"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:20%"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:20%"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:50px"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:50px"></colgroup>');
			$('#' + tableId).append('<colgroup><col style="width:1px"></colgroup>');
			
			$('#' + updateId).button();
			$('#' + recordId).button();
			$('#' + playId).button();
			$('#' + saveId).button();
			$('.action').button();
			
			$('#' + updateId).click(function() {
				getTestList();
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
			$('#' + saveId).click(function() {
				save();
			});	
			
			$('.action').click(function() {
				action($(this).prop('id'));
			});	
			
			getTestList();
			
			function action(id) {
				var url = id.split('_')[id.split('_').length-1];
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + '/rest/test/' + url + '?serial=' + selectedSerial,
				   success: function(msg){
						update();
				   }
				});	
			}
			
			$('#' + testListId).change(function() {
				var test = $(this).val();
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/selectTest?testName=" + test,
				   success: function(msg){
						update();
				   }
				});	
			});
			
			function getTestList() {
				$('#' + testListId).empty();
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/getTestList",
				   success: function(msg){
				   		for (var test of msg) {
					   		var option = $('<option>').val(test).text(test);
							$('#' + testListId).append(option);
						}
				   }
				});					
			}
			
			function selectRow() {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/selectRow?serial=" + selectedSerial,
				   success: function(msg){
	
				   }
				});	
			}			
			function record() {
				clearTable();
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/startRecording",
				   success: function(msg){
	
				   }
				});	
			}
			function stop() {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/stopRecording",
				   success: function(msg){
						update();
				   }
				});	
			}
			function play() {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/playBack",
				   success: function(msg){
	
				   }
				});	
			}
			
			
			function save() {
				var defaultValue = $('#' + testListId).val();
				var dialog = new TextInputDialog(div2, "test name", "test name", defaultValue, function() {
					var name = this.getText();
					$.ajax({
					   type: "GET", 
					   url: window.location.origin + "/rest/test/save?testName=" + name,
					   success: function(msg){
		
					   }
					});					
				});
				dialog.showModal();
			}					
			new MyWebSocket(function(msg) {
				if (msg == 'TestFinished') {
					update();
					$('#' + playId).prop('checked', false).button('refresh');
				}
			}
			, 'TEST');
			
			table = $('#' + tableId).DataTable({
				ordering: false,
				paging: false
			});
			$('#' + tableId + ' tbody').on( 'click', 'td', function () {
				selectedCol = $(this).index();
			});
			
			$('#' + tableId + ' tbody').on( 'click', 'tr', function () {
				selectedData =  table.row( this ).data();
				selectedSerial = selectedData[8];
		        if ( $(this).hasClass('selected') ) {
		            $(this).removeClass('selected');
		        }
		        else {
		            //table.$('tr.selected').removeClass('selected');
		            $('#' + tableId + ' tr.selected').removeClass('selected');
		            $(this).addClass('selected');
		        }
		        selectRow();
		    });

		    $('#' + div2).contextmenu({
				delegate: "#" + tableId + " tr",
				autoFocus: true,
				preventContextMenuForPopup: true,
				preventSelect: true,
				taphold: true,
				menu: [
					{title: "Edit <kbd>[F2]</kbd>", cmd: "edit"},
					{title: "Delete <kbd>Ctrl+D</kbd>", cmd: "delete"},
					{title: "----"},
					{title: "Add Property Set<kbd>Ctrl+D</kbd>", cmd: "setProperty"},
					{title: "Add Property Test<kbd>Ctrl+D</kbd>", cmd: "testProperty"},
					{title: "----"},
					{title: "Add Register Set<kbd>Ctrl+D</kbd>", cmd: "setRegister"},
					{title: "Add Register Test<kbd>Ctrl+D</kbd>", cmd: "testRegister"},
					{title: "----"},
					{title: "Add Wait<kbd>Ctrl+D</kbd>", cmd: "addWait"},
					{title: "Add Comment<kbd>Ctrl+D</kbd>", cmd: "addComment"},
					{title: "----"},
					{title: "Move Up<kbd>Ctrl+D</kbd>", cmd: "moveUp"},
					{title: "Move Down<kbd>Ctrl+D</kbd>", cmd: "moveDown"},
			      	{title: "----"},
					{title: "Cut <kbd>Ctrl+X</kbd>", cmd: "cut"},
					{title: "Copy <kbd>Ctrl+C</kbd>", cmd: "copy"},
					{title: "Paste <kbd>Ctrl+V</kbd>", cmd: "paste"},
			      ],
			    // Handle menu selection to implement a fake-clipboard
			    select: function(event, ui) {
					var $target = ui.target;
					switch(ui.cmd){
					case "delete":
						deleteRow();
						break;
					case "edit":
						editRow();
						break;
					case "addWait":
						addCommand('CONTROL', 'WAIT', '1000');
						break;
					case "moveUp":
						move('up');
						break;
					case "moveDown":
						move('down');
						break;
					case "copy":
//			        CLIPBOARD = $target.text();
			        break;
					case "paste":
//			        CLIPBOARD = "";
			        break;
			      }
//			      alert("select " + ui.cmd + " on " + $target.text());
			      // Optionally return false, to prevent closing the menu now
			    },
			    // Implement the beforeOpen callback to dynamically change the entries
			    beforeOpen: function(event, ui) {
			      var $menu = ui.menu,
			        $target = ui.target,
			        extraData = ui.extraData; // passed when menu was opened by call to open()
			      // Optionally return false, to prevent opening the menu now
			    }
			  });
			  
			  function deleteRow() {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/deleteRow?serial=" + selectedSerial,
				   success: function(msg){
						update();
				   }
				});				  
			  }
			  
			  function addCommand(type, target, value) {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/addCommand?type=" + type + "&target=" + target + "&value=" + value + "&serial=" + selectedSerial,
				   success: function(msg){
						update();
				   }
				});	
			  }
			  
			  function move(type) {
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/move?type=" + type + "&serial=" + selectedSerial,
				   success: function(msg){
						update();
				   }
				});	
			  }			  
			  function editRow() {
			  	$('#' + editId).val(selectedData[selectedCol]);
			  	$('#' + dialogId).dialog('open');
			  }
		}	
		function dialogClosed() {
			var sel = headers[selectedCol];
			var value = $('#' + editId).val();
			
			if (sel == 'Value') {		
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/updateValue?serial=" + selectedSerial + "&value=" + value,
				   success: function(msg){
						update();
				   }
				});	 
			}
			else if (sel == 'Expected') {		
				$.ajax({
				   type: "GET", 
				   url: window.location.origin + "/rest/test/updateExpected?serial=" + selectedSerial + "&value=" + value,
				   success: function(msg){
						update();
				   }
				});	 
			}		  			  	
		}
		
		function update() {
			clearTable();
			$.ajax({
			   type: "GET", 
			   url: window.location.origin + "/rest/test/getTest",
			   success: function(msg){
					for (var obj of msg.items) {
						table.row.add([obj.number, obj.type, obj.target, obj.value, obj.expected, obj.result, obj.passFail, obj.time, obj.serial]).draw(false);
					}
			   }
			});	
		}
		
		function clearTable() {
			table.clear().draw();
		}	
	}
}