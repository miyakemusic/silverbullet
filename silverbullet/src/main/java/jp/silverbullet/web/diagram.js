class DependencyDiagram {	
	constructor(diagram) {
	    var $ = go.GraphObject.make;  // for conciseness in defining templates
	
	    // Must name or refer to the DIV HTML element
	    this.myDiagram =
	      $(go.Diagram, diagram,
	        { // automatically scale the diagram to fit the viewport's size
	          initialAutoScale: go.Diagram.Uniform,
	          // start everything in the middle of the viewport
	          initialContentAlignment: go.Spot.Center,
	          // disable user copying of parts
	          allowCopy: false,
	          "animationManager.isEnabled": false,
	          // position all of the nodes and route all of the links
	          layout:
	            $(go.LayeredDigraphLayout,
	              { direction: 0,
	                layerSpacing: 150,
	                columnSpacing: 30,
	                setsPortSpots: false })
	      });
	
	    // replace the default Node template in the nodeTemplateMap
	    this.myDiagram.nodeTemplate =
	      $(go.Node, "Vertical",  // the whole node panel
	        $(go.TextBlock,  // the text label
	          new go.Binding("text", "key"))//,
	//	        $(go.Picture,  // the icon showing the logo
	          // You should set the desiredSize (or width and height)
	          // whenever you know what size the Picture should be.
	//	          { desiredSize: new go.Size(75, 50) },
	//	          new go.Binding("source", "key", convertKeyImage))
	      );
	
	    // replace the default Link template in the linkTemplateMap
	    this.myDiagram.linkTemplate =
	      $(go.Link,  // the whole link panel
	        { curve: go.Link.Bezier, toShortLength: 2 },
	        $(go.Shape,  // the link shape
	          { strokeWidth: 1.5 }),
	        $(go.Shape,  // the arrowhead
	          { toArrow: "Standard", stroke: null }),
	        $(go.TextBlock, new go.Binding("text", "text"))
	      );
	      
	}
		
	update(id) {
		var me = this;
		$.ajax({
		   type: "GET", 
		   url: window.location.origin + "/rest/dependencySpec/target?id=" + id,
		   success: function(msg){
			   var link = [];
				for (var i in msg) {
					var from = msg[i].from.id + "(" + msg[i].from.element + ")";
					var to = msg[i].to.id + "(" + msg[i].to.element + ")";
					$('#message').html($('#message').html() + "<BR>" + from + ":" + to);
					
					var pos = $('#' + msg[i].from.id + '_panel').position();
					var width = $('#' + msg[i].from.id + '_panel').width();
					var height = $('#' + msg[i].from.id + '_panel').height();
					
					var pos2 = $('#' + msg[i].to.id + '_panel').position();
					var width2 = $('#' + msg[i].to.id + '_panel').width();
					var height2 = $('#' + msg[i].to.id + '_panel').height();

					var path = {from:msg[i].from.id, to:msg[i].to.id, text: msg[i].to.element};
					link.push(path);
					
				}
				
				me.draw(link)
		   }
		});	
	}
	
	draw(link) {
		var $ = go.GraphObject.make;  // for conciseness in defining templates
	    // create the model and assign it to the Diagram
	    this.myDiagram.model =
	      $(go.GraphLinksModel,
	        { // automatically create node data objects for each "from" or "to" reference
	          // (set this property before setting the linkDataArray)
	          archetypeNodeData: {},
	          // process all of the link relationship data
	          linkDataArray: link
	        });		
	}
}