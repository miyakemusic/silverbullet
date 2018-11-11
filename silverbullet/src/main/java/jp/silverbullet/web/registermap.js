class RegisterMap {

	constructor(div) {
		var idEnabledSimulators = div + 'addesSim';
		var idSimulator = div + '_simulator';
		var simButton = div + '_simButton';
		
		$('#' + div).append('<select id="' + idSimulator+ '">Simulator</select><button id="' + simButton + '">Load Simulator</button>');
		$('#' + div).append('<div id="' + idEnabledSimulators + '"></div>');
		
		var idInterrupt = div + '_interrupt';
		$('#' + div).append('<div><button id="' + idInterrupt + '" class="interruptButton">Interrupt</button></div>');
		var mainDiv = div + '_mainDiv';
		$('#' + div).append('<div id="' + mainDiv + '" class="regtable"></div>');
			
		this.bitInfo = new Map();
		
		var me = this;
		var selectedValueId;
		
		// dialog for changing value
		var dialogId = div + 'changeValueDialog';
		var editValue = div + 'editValue';
		var idBitName = div + 'dialogBitName';
		$('#' + div).append('<div id="' + dialogId + '"><label id="' + idBitName + '"></label><br><input type="text" id="' + editValue + '"></div>');
		$('#' + dialogId).dialog({
			  autoOpen: false,
			  title: 'Edit',
			  closeOnEscape: false,
			  modal: true,
			  buttons: {
			    "OK": function(){
			    	$(this).dialog('close');
			    	changeBitValue(selectedValueId, $('#' + editValue).val());
			    }
			    ,
			    "Cancel": function(){
			    	$(this).dialog('close');
			    }
			    ,
			    "Create Shortcut": function() {
			    	$(this).dialog('close');
			    	createShortCut(selectedValueId);
			    }
			    ,
			    "Add to Test": function() {
			    	$(this).dialog('close');
			    	addToTest(selectedValueId);
			    }
			  },
			width: 400,
			height: 300
		});	
		
		initWebSocket();
		getSimulators();
		
		$('#' + idInterrupt).click(function() {
			interrupt();	
		});
		
		function interrupt() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/interrupt",
			   success: function(msg){
			   }
			});		
		}
		
		getAddesSimulators();
		function getAddesSimulators() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getAddedSimulators",
			   success: function(msg){
			   		$('#' + idEnabledSimulators).empty();
			   		$('#' + idEnabledSimulators).append('Loaded Simulators:');
//			   		var val = '';
			   		for (var i = 0; i < msg.length; i++) {
						var val = msg[i];
//						val += sim + " ";
						$('#' + idEnabledSimulators).append('<button name="' + val + '" class="simulatorDisable">Unload ' + val + '</button>');

					}
					$('.simulatorDisable').click(function() {
						unloadSimulator($(this).prop('name'));
					});
					
			   }
			});				
		}
		
		function unloadSimulator(sim) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/unloadSimulator?simulator=" + sim,
			   success: function(msg){
					getAddesSimulators();
			   }
			});			
		}
		
		function getSimulators() {
			$('#' + simButton).click(function() {
				$.ajax({
				   type: "GET", 
				   url: "http://" + window.location.host + "/rest/register/loadSimulator?simulator=" + $('#' + idSimulator).val(),
				   success: function(msg){
				   		for (var i = 0; i < msg.length; i++) {
							var sim = msg[i];
							$('#' + idSimulator).append('<option value="' + sim + '">' + sim + '</option>');
						}
						getAddesSimulators();
				   }
				});				
			});
			
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getSimulators",
			   success: function(msg){
			   		for (var i = 0; i < msg.length; i++) {
						var sim = msg[i];
						$('#' + idSimulator).append('<option value="' + sim + '">' + sim + '</option>');
					}
			   }
			});	
		}
		
		function resetButtonColor() {
			setTimeout(function() {
				$('.regButton').removeClass('changed');
				$('.interruptButton').removeClass('changed');
			}, 200);
		}
		
		function initWebSocket() {
			new MyWebSocket(function(msg) {				
				var obj = JSON.parse(msg);
				
				var regName = obj.name;
				if (regName == '@Interrupt@') {
					resetButtonColor();
					$('#' + idInterrupt).addClass('changed');
					return;
				}
				var address = obj.address;
				
				for (var i = 0; i < obj.bits.length; i++) {
					var bit = obj.bits[i];
					var bitName = bit.name;
					var bitVal = bit.val;
					
					var buttonId = '#' + getButtonId(regName, bitName);
					$(buttonId).html(bitVal);
					
					resetButtonColor();
					$(buttonId).addClass('changed');
				}
			}
			, 'REGVAL');
		}
		function createMap() {
			$('#' + mainDiv).empty();
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getRegisters",
			   success: function(msg){
					createMapContent(msg);
			   }
			});	
		}
		
		function createMapContent(obj) {
			var mapTableId = 'mapTable';
			$('#' + mainDiv).append('<table id="' + mapTableId + '"><thead></thead><tbody></tbody></table>');
			
			$('#' + mapTableId + ' > thead').append('<td>Address</td>'); 
			$('#' + mapTableId + ' > thead').append('<td>Name</td>'); 
			for (var i = 31; i >= 0; i--) {
				$('#' + mapTableId + ' > thead').append('<td>' + i + '</td>'); 
			}
			$('#' + mapTableId).css("table-layout","fixed");
			$('#' + mapTableId).append('<colgroup><col style="width:100px"></colgroup>');
			$('#' + mapTableId).append('<colgroup><col style="width:200px"></colgroup>');
			for (var i = 31; i>= 0; i--) {
				$('#' + mapTableId).append('<colgroup><col style="width:20px"></colgroup>');
			}
			for (var i = 0; i < obj.registers.length; i++) {
				var register = obj.registers[i];
				var row = '<td>' + register.address + '</td><td>' + register.name + '</td>';
				
				var currentBit = 0;
				var bit = register.bits[currentBit];
				if (bit == undefined) {
					continue;
				}
				
				var colSpan = 0;
				var unusedColSpan = 0;
				
				if (register.address.match(/-/)) {
					var buttonId = getButtonId(register.name, '');
					var buttonInfoObj = new Object();
					buttonInfoObj.address = register.address;
					buttonInfoObj.register = register.name;
					//buttonInfoObj.bitName = ;''
					//buttonInfoObj.bit = bit.bit;
					me.bitInfo.set(buttonId, buttonInfoObj);
							
					var name = '<div>' + bit.name + '</div><div><input type="file" id="' + register.name + '"class="blockData"></div>';
					//getCurrentValue(buttonId, register.name, bit.name);
					colSpan = 32;
					row += '<td colspan="' + colSpan + '">' + name + '</td>';
				}
				else {
					for (var j = 31; j >= 0; j--) {
						var startBit = getStartBit(bit.bit);
						var endBit = getEndBit(bit.bit);
					
						if (j <= endBit && j >= startBit) { // In used bit
							if (unusedColSpan > 0) {
								row += '<td colspan="' + unusedColSpan + '" class="unused"></td>';
								unusedColSpan = 0;
							}
							colSpan++;
						}
						else { // out of used bit
							//row += '<td></td>';
							unusedColSpan++;
						}
						
						if (startBit == j) {
							var buttonId = getButtonId(register.name, bit.name);
							var buttonInfoObj = new Object();
							buttonInfoObj.address = register.address;
							buttonInfoObj.register = register.name;
							buttonInfoObj.bitName = bit.name;
							buttonInfoObj.bit = bit.bit;
							me.bitInfo.set(buttonId, buttonInfoObj);
							
							var name = '<div>' + bit.name + '</div><div><Button id="' + buttonId + '" class="regButton"></Button></div>';
							getCurrentValue(buttonId, register.name, bit.name);
							row += '<td colspan="' + colSpan + '">' + name + '</td>';
							
							colSpan = 0;
							if (register.bits.length-1 > currentBit) {
								currentBit++;
								bit = register.bits[currentBit];
							}
						}
					}
				}
				$('#' + mapTableId + ' > tbody').append('<tr>' + row + '</tr>');
				
				$('.regButton').click(function() {
					var id = $(this).prop('id');
					selectedValueId = id;
					var text = $(this).text();
					
					var obj = me.bitInfo.get(id);
					$('#'+ idBitName).html('Register: ' + obj.register + '(' + obj.address + ')' + '<br>' + 
					'Bit: ' + obj.bitName + '[' + obj.bit + ']');
					$('#' + editValue).val(text);
					$('#' + dialogId).dialog('open');
				});
				
				$('.blockData').change(function(e) {
					e.stopImmediatePropagation();
					var reg = $(this).prop('id');
					var file = $(this)[0].files[0];
					console.log(file.name);
					var reader = new FileReader();
				    reader.readAsDataURL(file);
				    reader.onload = function(event) {
						$.ajax({
						   type: "POST", 
		//				   contentType: 'application/octet-stream'
						   contentType: 'text/plain',
						   data: event.target.result,
		//				   contentType: false,
        				   processData: false,
						   url: "http://" + window.location.host + "/rest/register/setBlockData?regName=" + reg,
						   success: function(msg){
						   }
						});	
				    }
//					$(this).val('');
				});
			}
		}
		function changeBitValue(buttonId, value) {
			var obj = me.bitInfo.get(buttonId);
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/setCurrentValue?regName=" + obj.register + '&bitName=' + obj.bitName + '&value=' + value,
			   success: function(msg){
			   }
			});		
		}
		function createShortCut(buttonId) {
			var obj = me.bitInfo.get(buttonId);
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/createShortCut?regName=" + obj.register + '&bitName=' + obj.bitName,
			   success: function(msg){
			   }
			});		
		}	
		function addToTest(buttonId) {
			var obj = me.bitInfo.get(buttonId);
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/addToTest?regName=" + obj.register + '&bitName=' + obj.bitName,
			   success: function(msg){
			   }
			});		
		}		
		function getCurrentValue(id, regName, bitName) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getCurrentValue?regName=" + regName + '&bitName=' + bitName,
			   success: function(msg){
					$('#' + id).html(msg);
			   }
			});		
		}
		function getStartBit(bit) {
			return getBitNumber(bit, 1);
		}
		
		function getEndBit(bit) {
			return getBitNumber(bit, 0);
		}
		
		function getBitNumber(bit, index) {
			if (bit.includes(":")) {
				return bit.split(':')[index];
			}
			else {
				return bit;
			}
		}
	
		function getButtonId(regName, bitName) {
			return stripChar(regName) + stripChar(bitName);
		}
		
		function stripChar(str) {
			return str.replace(/\//g, '_').replace(/\)/g,"").replace(/\(/g,"").replace(/\]/g,"").replace(/\[/g,"").replace(/\./g,'').replace(/\:/g,'').replace(/ /g, '_');
		}	
		createMap();
	}
}