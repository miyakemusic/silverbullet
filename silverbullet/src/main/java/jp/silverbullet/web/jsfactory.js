
var map = new Object();
function addWidget(id, widget) {
	if (map[id] == undefined) {
		var list = new Array();
		list.push(widget);
		map[id] = list;
	}
	else {
		var list = map[id];
		list.push(widget);	
	}

}

var ws = new WebSocket("ws://localhost:8081/websocket"); //接続
ws.onopen = wsOnOpen; //接続できたとき
ws.onmessage = wsOnMessage; //接続できたとき
//  	ws.onerror = wsOnError; //接続エラーのとき
//  	ws.onclose = wsOnClose; //接続を閉じたとき
function wsOnOpen() {

}
function wsOnMessage(message) {
	var str = message.data;//toString();
	var ss = str.split(',');
	for (i = 0; i < ss.length; i++) {
		var s = ss[i];
		var list = map[s.trim()];
		if (list != undefined) {
			for (j = 0; j < list.length; j++) {
				var widget = list[j];
				widget.update();
			}
		}
	}
//   alert(message.data);
}
	
function doDependency(id, value) {
	var _id = trimId(id);
	 $.ajax({
	   type: "GET",
	   url: 'http://' + window.location.host + '/rest/test/dependency?id=' + _id + '&value=' + value,
	   	   success: function(msg){
	   	   }
	});
}
var serial = 0;	
function createSerialId(id) {
	serial++;
	return id + '-' + serial;
//	return id;
}
function trimId(id) {
	var s = id;
	return s.split('-')[0];
//	return id;
}

class SvWidget {
	constructor(parent) {
		this.parent = parent;
	}
	appendBr() {
		$('#' + this.parent).append('<BR>');
	}
}
class SvButton extends SvWidget {
	constructor(parent, id, title) {
		super(parent);
		this.myId = createSerialId(id);
		$('#' + parent).append('<button id=' + this.myId + '>' + title + '</button>');

		$('#' + this.myId).on('click', function() {
		  doDependency(id ,'**ANY**');
		});
	}
	update() {
		var _id = trimId(this.myId);
		var _myId = this.myId;
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/property?id="  + _id + "",
			   success: function(msg){
				   $('#' + _myId).prop("disabled", !msg.enabled);
			   }
		});	   				
	}
}

class SvCheckBox extends SvWidget{
	constructor(parent, id, title) {
		super(parent);
		this.myId = createSerialId(id);
		$('#' + parent).append('<input type="checkbox" id=' + this.myId + '><span id=' + this.myId + '_text>' + title + '</span>');
		
		var _myId = this.myId;
		$('#' + this.myId).on('change', function(event) {
			doDependency(id, $('#' + _myId).prop('checked'));
		}); 
		this.update();
	}
	update() {
		var id = trimId(this.myId);
		var _myId = this.myId;
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/property?id="  + id + "",
			   success: function(msg){
				   $('#' + _myId).prop("disabled", !msg.enabled);
				   $('#' + _myId).prop("checked", msg.currentValue == "true" ? true : false);
			   }
		});	    				
	}
}

class SvTextField extends SvWidget{
	constructor(parent, id, title) {
		super(parent);
		this.myId = createSerialId(id);
		$('#' + parent).append('<span id=' + this.myId + '_text>' + title + '</span><input type="text" id=' + this.myId + '>');
		
		var _myId = this.myId;
		$('#' + this.myId).on('change', function(event) {
			doDependency(id, $('#' + _myId).val());
		});
		this.update();
	}
	update() {
		var _id = trimId(this.myId);
		var _myId = this.myId
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/property?id="  + _id + "",
			   success: function(msg){
				   $('#' + _myId).prop("disabled", !msg.enabled);
				   $('#' + _myId).val(msg.currentValue);
			   }
		});	    				
	}	
}

class SvComboBox extends SvWidget{
	constructor(parent, id, title) {
		super(parent);
		this.myId = createSerialId(id);
		$('#' + parent).append('<label id=' + this.myId + '_text>' + title + '</label><select id=' + this.myId + '></select>');
		
		var _myId = this.myId;
		$('#' + this.myId).on('change', function(event) {
			var v = $('#' + _myId).val();
			doDependency(id, v);
		});
		this.update();
	}

	update() {
		var _id = trimId(this.myId);
		var _myId = this.myId;
		$.ajax({
			   type: "GET", 
			   url: "http://" + window.location.host + "/rest/test/property?id="  + _id + "",
			   success: function(msg){
				   $('#' + _myId).prop("disabled", !msg.enabled);
				   for (var i = 0; i < msg.listDetail.length; i++) {
					   var e = msg.listDetail[i];
					   $('#' + _myId).append(new Option(e.title, e.id));
				   }
			   }
		});	    				
	}
}
class SvFunctionButton extends SvWidget{
	constructor(parent, id, title, value) {
		super(parent);
		this.myId = createSerialId(id);
    	$('#' + parent).append('<button id="' + this.myId + '"></button>');
    	$('#' + this.myId).html(title + '<br><font color="red">' + value + '</font>');
	}
	
	update() {
		
	}
}

class SvLabel extends SvWidget {
	constructor(parent, id, title) {
		super(parent);
	}
	
	onUpdated(msg) {
		
	}
}
