class IdSelector {
	constructor(div, listener, application) {
		this.idPath =  "//" + window.location.host + "/rest/" + application + "/id2";
		
		this.id = div + "_idlist";
 		var me = this;
 		
		$('#' + div).append('ID:<select id="' + me.id + '"></select>');
		$('#' + me.id).on('change', function (e) {
			if (listener != null) {
				listener($(this).val());
			}
		});
		
		get();
		
		this.listener = function(result) {
			get();
		};
		
		websocket.addListener('ID', listener);
		
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
	
	path(application) {
		this.idPath =  "//" + window.location.host + "/rest/" + application + "/id2";
	}
}
 
class Persistent {
	constructor(div, application) {
		this.storagePath = "//" + window.location.host + "/rest/" + application + "/storage";
		
		var me = this;
		this.idSelector = new IdSelector(div, function(v) {
		
		}, application);
		
		var idAdd = div + "_add";
		$('#' + div).append('<button id="' + idAdd + '">Add Trigger</button>');
		
		$('#' + idAdd).on('click', function(e) {
			addId(me.idSelector.getId());
		});
		
		var idAddTo = div + "_addTo";
		var idSetPath = div + "_addPathTo";
		var idTriggerIds = div + "_triggerIds";
		
		$('#' + div).append('<button id="' + idAddTo + '">Add Stored To</button><button id="' + idSetPath + '">Add Path To</button><select id="' + idTriggerIds + '"></select>');
	    $('#' + idAddTo).on('click', function(e) {
		    $.ajax({
		        url: me.storagePath + "/addStoredId?triggerId=" + $('#' + idTriggerIds).val() + "&storedId=" + me.idSelector.getId(),
		        type:'GET'
		    })
		    .done( (data) => {
				$('#' + idTriggerIds).empty();
		    	for (var o of data) {
					$('#' + idTriggerIds).append($('<option>', { 
					    value: o,
					    text : o 
					}));
				}
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
				updateList();
		    });
		});
	    
	    $('#' + idSetPath).on('click', function(e) {
		    $.ajax({
		        url: me.storagePath + "/setPath?triggerId=" + $('#' + idTriggerIds).val() + "&pathId=" + me.idSelector.getId(),
		        type:'GET'
		    })
		    .done( (data) => {

		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
				updateList();
		    });	    
	    });
	    
	    function getTriggerIds() {
		    $.ajax({
		        url: me.storagePath + "/triggerIds",
		        type:'GET'
		    })
		    .done( (data) => {
				$('#' + idTriggerIds).empty();
		    	for (var o of data) {
					$('#' + idTriggerIds).append($('<option>', { 
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
		
		getTriggerIds();
		var listId = div + "_list";
		$('#' + div).append('<div id="' + listId + '"></div>');
		
		updateList();
		
		function addId(id) {
		    $.ajax({
		        url: me.storagePath + "/addTriger?id=" + id,
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
		        url: me.storagePath + "/triggerIds",
		        type:'GET'
		    })
		    .done( (data) => {
		    	$('#' + listId).empty();
		    	$('#' + idTriggerIds).empty();
		    	
				for (var o of data) {
					var div1 = div + "_" + o;
					$('#' + listId).append('<div id="' + div1 + '"></div>');

					var buttonId = o + "_" + div;
					var str = 'Trigger:' + o + '<button id="' + buttonId + '" name="' + o + '">Remove</button><br>';
					$('#' + div1).append(str);
					$('#' + buttonId).on('click', function(e) {
						removeId($(this).prop('name'));
					});
					
					$('#' + idTriggerIds).append($('<option>', { 
					    value: o,
					    text : o 
					}));				
					
					var pathId = div1 + "_path";
					$('#' + div1).append('<div id="' + pathId + '"></div>');
					retrievePath(div1, o);
					retrieveStored(div1, o);	
				}
				
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
		
		function retrievePath(div, id) {
		    $.ajax({
		        url: me.storagePath + "/path?triggerId=" + id,
		        type:'GET'
		    })
		    .done( (data) => {
				$('#' + div).append('<div>Path:' + data + '</div>');
		    })
		    .fail( (data) => {
	
		    })
		    .always( (data) => {
		
		    });
		}
		
		function retrieveStored(div, id) {
		    $.ajax({
		        url: me.storagePath + "/storedIds?triggerId=" + id,
		        type:'GET'
		    })
		    .done( (data) => {
				$('#' + div).append('<div>Target IDs</div>');
				for (var o of data) {	
					$('#' + div).append('<div>' + o + '</div>');
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
	
	rebuild(application) {
		this.storagePath = "//" + window.location.host + "/rest/" + application + "/storage";
		this.idSelector.path(application);
	}
}
