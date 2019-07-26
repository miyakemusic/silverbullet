class RegisterSpec {
	constructor(div) {
		$('#' + div).append('<button id="addNew">Add New</button>');

		$('#' + div).append('<div id="mainDiv" class="regtable"></div>');
		
		this.changes = new Map();
		
		var me = this;
		var selectedValueId;

		getListAsync();
		
		function getListAsync() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/getRegisters",
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
			
			$("#regTable > thead").append('<tr><th>Address</th><th>Name</th><th>Description</th><th>Spec.</th></tr>');
			
			var sign = '_';
			for (var i = 0; i < obj.registers.length; i++) {
				var register = obj.registers[i];
				
				var bitId = 'bit' + sign + i;
				var bitTable = '<table id="' + bitId + '"><thead></thead><tbody></tbody></table>';
				
				var addrId = "addr" + sign + i;
				var nameId = "name" + sign + i;
				var descId = "desc" + sign + i;
				
				var addButton = "add_" + i;
				var delButton = "del_" + i;
				var row	= '<tr><td><button id="' + addButton + '">+</button><button id="' + delButton + '">-</button><div id="' + addrId + '"></div></td><td><div id="' + nameId + '"></div></td><td><div id="' + descId + '"></div></td><td>' + bitTable+ '</td></tr>';
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
				$('#' + delButton).click(function() {
					var obj = $(this);
					var id = $(this).prop('id');
					delRow(id.split('_')[1]);
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
			if (value == '' || value == null) {
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
					
					me.changes.set(id, $('#' + editId).val());
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
			var html = '<select id="' + id + '_combo"><option>RO</option><option>WO</option><option>RW</option><option>RC</option><option>UNUSED</option></select>';
			$('#' + id).append(html);
			$('#' + id + '_combo').val(value);
			$('#' + id + '_combo').change(function() {
				me.changes.set(id, $('#' + id + '_combo').val());
				commit();
			});
		}
		
		function addRow(row) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/addRow?row=" + row,
			   success: function(msg){
					getListAsync();
			   }
			});	
		}
		function delRow(row) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/deleteRow?row=" + row,
			   success: function(msg){
					getListAsync();
			   }
			});	
		}		
		function addBitRow(row) {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/addBitRow?row=" + row,
			   success: function(msg){
					getListAsync();
			   }
			});	
		}
		
		function commit() {
			var changesList = [];
			for (const [key, value] of me.changes) {
				var obj = new Object();
				obj.key = key;
				obj.value = value;
				changesList.push(obj);
			}
			
			$.ajax({
			   type: "POST", 
			   url: "//" + window.location.host + "/rest/register2/postChanges",
			   contentType: 'application/json',
			   data :JSON.stringify(changesList),
			   success: function(msg){
					me.changes.clear();
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
		
		
		$('#addNew').click(function() {
			$.ajax({
			   type: "GET", 
			   url: "//" + window.location.host + "/rest/register2/addNew",
			   success: function(msg){
					getListAsync();
			   }
			});				
		});	
	}
}
