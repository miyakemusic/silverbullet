class RegisterClass {

	constructor(div) {
		$('#' + div).append('<button id="commit">Commit</button><select id="spec"><option value="spec">Specification</option><option value="map">Map</option></select>');
		$('#' + div).append('<div id="mainDiv" class="regtable"></div>');
		
		getListAsync();
		
		function getListAsync() {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/getRegisters",
			   success: function(msg){
					getList(msg);
			   }
			});	
		}
		
		function getList(obj) {
			$('#mainDiv').empty();
			$('#mainDiv').append('<table id="regTable"><thead></thead><tbody></tbody></table>');
			$('#regTable').addClass('regtable');
			$('#regTable').css("table-layout","fixed");
			$('#regTable').append('<colgroup><col style="width:10%"></colgroup>');
			$('#regTable').append('<colgroup><col style="width:15%"></colgroup>');
			$('#regTable').append('<colgroup><col style="width:20%"></colgroup>');
			$('#regTable').append('<colgroup><col style="width:55%"></colgroup>');
			
//			$("#regTable > thead").append('<tr><th style="width: 10px;">Address</th><th style="width: 10px;">Name</th><th>Description</th><th>Spec.</th></tr>');
			$("#regTable > thead").append('<tr><th>">Address</th><th>Name</th><th>Description</th><th>Spec.</th></tr>');
			
			var sign = '_';
			for (var i = 0; i < obj.registers.length; i++) {
				var register = obj.registers[i];
				
				var bitId = 'bit' + sign + i;
				var bitTable = '<table id="' + bitId + '"><thead></thead><tbody></tbody></table>';
				
				var addrId = "addr" + sign + i;
				var nameId = "name" + sign + i;
				var descId = "desc" + sign + i;
				
				var addButton = "add_" + i;
				var row	= '<tr><td><button id="' + addButton + '">+</button><div id="' + addrId + '"></div></td><td><div id="' + nameId + '"></div></td><td><div id="' + descId + '"></div></td><td>' + bitTable+ '</td></tr>';
				$("#regTable > tbody").append(row);
				
				createEditable(register.address, addrId, 'field');
				createEditable(register.name, nameId, 'field');
				createEditable(register.description, descId, 'area');
				
				$('#' + bitId).css("table-layout","fixed");
				$('#' + bitId).append('<colgroup><col style="width:15%"></colgroup>');
				$('#' + bitId).append('<colgroup><col style="width:15%"></colgroup>');
				$('#' + bitId).append('<colgroup><col style="width:35%"></colgroup>');
				$('#' + bitId).append('<colgroup><col style="width:35%"></colgroup>');
	
				var addBitId = "addBit_" + i;
				$('#' + bitId + '> thead').append('<tr><th>Bit<button id="' + addBitId + '">+</button></th><th>Size</th><th>R/W</th><th>Name</th><th>Description</th></tr>');
				var register = obj.registers[i];
										
				$('#' + addButton).click(function() {
					var obj = $(this);
					var id = $(this).prop('id');
					addRow(id.split('_')[1]);
				});
				$('#' + addBitId).click(function() {
					var obj = $(this);
					var id = $(this).prop('id');
					addBitRow(id.split('_')[1]);
				});
				for (var j in register.bits) {
					var bit = register.bits[j];
	
					var bitBitId = bitId + '_bit' + sign + j;
					var bitNameId = bitId + '_name' + sign + j;
					var bitDescId = bitId + '_desc' + sign + j;
					var bitTypeId = bitId + '_type' + sign + j;
					var bitSizeId = bitId + "_size" + sign + j;
					var bitRow = '<tr><td><div id="' + bitBitId + '"></div></td><td><div id="' + bitSizeId + '"></div></td><td><div id="' + bitTypeId + '"></div></td><td><div id="' + bitNameId + '"></div></td><td><div id="' + bitDescId + '"></div></td></tr>';				
					$('#' + bitId + '> tbody').append(bitRow);
					
					createEditable(bit.bit, bitBitId, 'field');
					createEditable(bit.size, bitSizeId, 'field');
					createCombo(bit.type, bitTypeId);
					createEditable(bit.name, bitNameId, 'field');
					createEditable(bit.description, bitDescId, 'field');
				}
			}
	
			$('#regTable table tr th').eq(0).css('width','10px');
			$('#regTable table tr th').eq(1).css('width','15px');
			$('#regTable table tr th').eq(2).css('width','20px');
			$('#regTable table tr th').eq(3).css('width','55px');
			
			$('#regTable td').attr('width', 5);
		}
		
		function createEditable(value, id, type) {
			if (value == '') {
				value = '----';
			}
			var labelId = 'L' + id;
			var editId = 'E' + id;
			$('#' + id).append('<div id="' + labelId + '"></div>');
			$('#' + labelId).text(value);
			
			if (type == 'field') {
				$('#' + id).append('<input type="text" id="' + editId + '">');	
			}
			else if (type == 'area') {
				$('#' + id).append('<textarea id="' + editId + '" cols="40" rows="5"></textarea>');	
			}
			
			$('#' + editId).val(value);
			$('#' + editId).keydown(function(event) {
				if (event.altKey) {
					if (event.which == 13) {
						$('#' + editId).val($('#' + editId).val() + '\n');
					}
				} 
				else if (event.which == 13) { // Enter
					$('#' + labelId).show();
					$('#' + editId).hide();
					$('#' + labelId).text($('#' + editId).val().replace('\n','<br>'));
					
					changes.set(id, $('#' + editId).val());
					commit();
				}
				else if (event.which == 27) { // Cancel
					$('#' + labelId).show();
					$('#' + editId).hide();
				}
			});
			$('#' + editId).hide();
		
			$('#' + labelId).click(function() {
				$('#' + labelId).hide();
				$('#' + editId).show();
			});
			$('#' + editId).focusout(function() {
				$('#' + labelId).show();
				$('#' + editId).hide();
			});
		}
		
		function createCombo(value, id) {
			var html = '<select id="' + id + '_combo"><option>RO</option><option>WO</option><option>RW</option><option>UNUSED</option></select>';
			$('#' + id).append(html);
			$('#' + id + '_combo').val(value);
			$('#' + id + '_combo').change(function() {
				changes.set(id, $('#' + id + '_combo').val());
				commit();
			});
		}
		
		function addRow(row) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/addRow?row=" + row,
			   success: function(msg){
					getListAsync();
			   }
			});	
		}
		
		function addBitRow(row) {
			$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/register/addBitRow?row=" + row,
			   success: function(msg){
					getListAsync();
			   }
			});	
		}
		
		function commit() {
			var changesList = [];
			for (const [key, value] of changes) {
				var obj = new Object();
				obj.key = key;
				obj.value = value;
				changesList.push(obj);
			}
			
			$.ajax({
			   type: "POST", 
			   url: "http://" + window.location.host + "/rest/register/postChanges",
			   contentType: 'application/json',
			   data :JSON.stringify(changesList),
			   success: function(msg){
					changes.clear();
					getListAsync();
			   },
			   error: function(XMLHttpRequest, textStatus, errorThrown) {
	                alert("error");
	           }
			});		
		}
		
		$('#commit').click(function() {
			commit();	
		});
		
		$('#spec').change(function() {
			if ($(this).val() == 'spec') {
				getListAsync();
			}
			else {
				createMap();
			}
		});
		
		function createMap() {
			$('#mainDiv').empty();
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
			$('#mainDiv').append('<table id="' + mapTableId + '"><thead></thead><tbody></tbody></table>');
			
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
						var buttonId = createButtonId(register.name, bit.name);
						var name = '<div>' + bit.name + '</div><div><Button id="' + buttonId + '" class="regButton"></Button></div>';
						getCurrentValue(buttonId, register.name, bit.name);
						row += '<td colspan="' + colSpan + '">' + name + '</td>';
						
						//$('#' + bit.name).css({width: (colSpan * 20) + 'px'});
						colSpan = 0;
						if (register.bits.length-1 > currentBit) {
							currentBit++;
							bit = register.bits[currentBit];
						}
					}
				}
				$('#' + mapTableId + ' > tbody').append('<tr>' + row + '</tr>');
			}
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
	
		function createButtonId(regName, bitName) {
			return stripChar(regName) + stripChar(bitName);
		}
		
		function stripChar(str) {
			return str.replace(/\//g, '_').replace(/\)/g,"").replace(/\(/g,"").replace(/\]/g,"").replace(/\[/g,"").replace(/\./g,'').replace(/\:/g,'').replace(/ /g, '_');
		}	
	}
}
