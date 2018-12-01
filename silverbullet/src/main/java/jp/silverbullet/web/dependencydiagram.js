class DependencyDiagram2 {
	constructor(div) {
	    var $ = go.GraphObject.make;  // for conciseness in defining templates
	
	    // Must name or refer to the DIV HTML element
	    this.myDiagram =
	      $(go.Diagram, div,
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