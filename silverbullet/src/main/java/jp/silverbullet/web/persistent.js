class IdSelector {
	constructor(div, listener) {
		this.idPath =  "//" + window.location.host + "/rest/id2";
		
		this.id = div + "_idlist";
 		var me = this;
 		
		$('#' + div).append('ID:<select id="' + me.id + '"></select>');
		$('#' + me.id).on('change', function (e) {
			if (listener != null) {
				listener($(this).val());
			}
		});
		
		get();
		
		websocket.addListener('ID', function(result) {
			get();
		});
		
		function get() {
		    $.ajax({
		        url: me.idPath + "/ids",
		        type:'GET'
		    })
		    .done( (data) => {
		    	$('#' + me.id).empty();
		    	for (var o of data) {
					$('#' + me.id).append($('<option>', { 
					    value: o,
					    text : o 
					}));
				}
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
		
		me.getId = function() {
			return $('#' + me.id).val();
		}
	}
	
	getId() {
		return this.getId();
	}
}
 
class Persistent {
	constructor(div) {
		this.storagePath = "//" + window.location.host + "/rest/storage";
		
		var me = this;
		var idSelector = new IdSelector(div, function(v) {
		
		});
		
		var idAdd = div + "_add";
		$('#' + div).append('<button id="' + idAdd + '">Add</button>');
		
		$('#' + idAdd).on('click', function(e) {
			addId(idSelector.getId());
		});
		
		var listId = div + "_list";
		$('#' + div).append('<div id="' + listId + '"></div>');
		
		updateList();
		
		function addId(id) {
		    $.ajax({
		        url: me.storagePath + "/add?id=" + id,
		        type:'GET'
		    })
		    .done( (data) => {
				updateList();
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
		
		function updateList() {
		    $.ajax({
		        url: me.storagePath + "/rest/storage/ids",
		        type:'GET'
		    })
		    .done( (data) => {
		    	$('#' + listId).empty();
		    	
				for (var o of data) {
					var buttonId = o + "_" + div;
					var str = o + '<button id="' + buttonId + '" name="' + o + '">Remove</button><br>';
					$('#' + listId).append(str);
					$('#' + buttonId).on('click', function(e) {
						removeId($(this).prop('name'));
					});
				}
				
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
		
		function removeId(id) {
		    $.ajax({
		        url: me.storagePath + "/removeId?id=" + id,
		        type:'GET'
		    })
		    .done( (data) => {
				updateList();				
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
	}
}
