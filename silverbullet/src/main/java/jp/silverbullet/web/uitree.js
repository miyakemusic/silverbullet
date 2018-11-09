class UiTree {
	constructor(div, callback) {
		this.treeId = div + '_tree';
		$('#' + div).append('Widget: <select id="' + this.treeId + '"></select>');
//		$('#' + this.treeId).jstree();
		
		$('#' + this.treeId).change(function() {
			callback($(this).val());
		});
		
		var me = this;
		$.ajax({
			type: "GET", 
			url: "http://" + window.location.host + "/rest/design/getDesign",
			success: function(msg){
				me.setTree(msg);
			}
		});	
	}
	
	setTree(node) {
		var me = this;
		var data = [];
//		var newNode = { id:node.unique + node.id, text:node.id}
//		$('#' + this.treeId).jstree("create_node", $('#root'), 'inside', newNode, false, false);
		addChild("#", node, 0);
		
		function addChild(parentId, node2, layer) {
			for (var child of node2.children) {
				var myId = child.unique;
				
				var space = '';
				for (var i = 0; i < layer; i++) {
					space += '_';
				}
				var obj = new Object();
				obj.id = myId;
				obj.parent = parentId;
				obj.text = space + child.id + "(" + child.widgetType + ")";
				data.push(obj);
//				var childNode = { id:createId(child), text:child.id}
//				$('#' + me.treeId).jstree("create_node", parent, 'inside', childNode, false, false);
				addChild(myId, child, layer+1);
			}
		}

		for (var d of data) {
			var option = $('<option>').val(d.id).text(d.text);
			$('#' + me.treeId).append(option);
		}
		
//		$('#' + this.treeId).jstree({ 'core' : {
//		    'data' : data
//		} });
	}
}