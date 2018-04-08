package jp.silverbullet.dependency.speceditor3.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor3.DependencyBuilder3;
import jp.silverbullet.dependency.speceditor3.DependencyExpression;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolderMap;
import jp.silverbullet.dependency.speceditor3.DependencyNode;
import jp.silverbullet.dependency.speceditor3.DependencyProperty;
import jp.silverbullet.dependency.speceditor3.DependencySpec2;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.dependency.speceditor3.IdCollector;

public class DependencyTreeUi extends AnchorPane {
	private EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getButton().equals(MouseButton.PRIMARY)){
				dependencyEditorModel.setSelectedId( ((Button)event.getSource()).getText());
				event.consume();
			}
		}
	};
	    
	private static final double xoffset = 300;
	private static final double yoffset = 10;
	private static final double xwidth = 220;
	private static final double yheight = 30;
	private static final double xstep = 250;
	private static final double ystep = 40;

	private DependencyEditorModel dependencyEditorModel;
	private Map<Integer, List<DependencyNode>> registeredNodes = new HashMap<>();
	private Map<DependencyNode, Button> buttonMap = new HashMap<>();
	private List<Button> triggerButtons = new ArrayList<>();
	private Button mainButton;
	private ContextMenu contextMenu = new ContextMenu();
		
	public DependencyTreeUi(DependencyEditorModel dependencyEditorModel) {
		this.setStyle("-fx-border-width:1;-fx-border-color:black;");
		this.setPrefHeight(300);
		
		contextMenu = new ContextMenu();	
		new SpecAdditionMenu(this, contextMenu, dependencyEditorModel);

		this.dependencyEditorModel = dependencyEditorModel;
		dependencyEditorModel.addtListener(new DependecyEditorModelListener() {
			@Override
			public void onSpecUpdate() {
				buildUi();
			}

			@Override
			public void onSelectionChanged(String id) {
				buildUi();
			}

			@Override
			public void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
				// TODO Auto-generated method stub
				
			}

		});
		buildUi();
	}

	private void buildUi() {
		this.registeredNodes.clear();
		this.triggerButtons.clear();
		this.buttonMap.clear();
		this.getChildren().clear();
		SvProperty property = dependencyEditorModel.getSelectedProperty();
		
		DependencyBuilder3 builder = new DependencyBuilder3(property.getId(), dependencyEditorModel.getDependencySpecHolder());
		DependencyNode tree = builder.getTree();
		
		DependencySpec2 spec = dependencyEditorModel.getDependencySpecHolder().get(property.getId());
		DependencyProperty depProp = new DependencyProperty(property.getId(), 
				DependencyTargetElement.Any, "", "", null);

		Set<String> ids = spec.getTriggerIds();
		tree.setDependencyProperty(depProp);
		int i = 0;
		for (String id : ids) {
			id = id.split("\\.")[0];
			Button button = createButton(id);
			triggerButtons.add(button);
			this.getChildren().add(button);
			button.setLayoutX(xoffset - xstep);
			button.setLayoutY(yoffset + i* ystep); 
			i++;
		}
		List<PendingItem> pendingList = new ArrayList<>();
		
		add(0, tree, pendingList);
		workThrough(tree.getChildren(), 1, pendingList);
		for (PendingItem item : pendingList) {
			item.execute();
		}
		drawTree();
		drawLine();
	}
	
	private void workThrough(List<DependencyNode> nodes, int layer, List<PendingItem> pendingList) {
		for (DependencyNode n : nodes) {
			if (n.getDependencyProperty().getCondition().equals(DependencyExpression.ELSE)) {
				continue;
			}
			add(layer, n, pendingList);
			if (n.isLeaf()) {
				return;
			}
			workThrough(n.getChildren(), layer + 1, pendingList);
		}
	}
	private void add(int layer, DependencyNode node, List<PendingItem> pendingList) {
		if (!registeredNodes.containsKey(layer)) {
			registeredNodes.put(layer, new ArrayList<DependencyNode>());
		}

		// Check if already added
		for (DependencyNode n : registeredNodes.get(layer)) {
			if (node.getDependencyProperty().getId().equals(n.getDependencyProperty().getId())) {	
				//pendingList.add(new PendingItem(node.getParent(), n));
				//node.getParent().addChild(n);
				//return;
			}
		}
		
		registeredNodes.get(layer).add(node);
	}
	private void drawLine() {
		// Trigger buttons
		for (Button button : this.triggerButtons) {
			Line line = new Line();
			line.setStartX(xwidth + button.getLayoutX());
			line.setStartY(yheight / 2 + button.getLayoutY());
			
			line.setEndX(mainButton.getLayoutX());
			line.setEndY(yheight / 2 + mainButton.getLayoutY());
			this.getChildren().add(line);
		}
		
		// Relation buttons
		for (int layer : this.registeredNodes.keySet()) {
			List<DependencyNode> nodes = this.registeredNodes.get(layer);
			for (int n = 0; n < nodes.size(); n++) {
				DependencyNode node = nodes.get(n);
				for (DependencyNode nextNode : node.getChildren()) {
					Button start = buttonMap.get(node);
					Button end = buttonMap.get(nextNode);
//					System.out.println(layer + ":" + start.getText() + " -> " + end.getText());
					if (end == null) {
						continue;
					}
					Line line = new Line();
					line.setStartX(xwidth + start.getLayoutX());
					line.setStartY(yheight / 2 + start.getLayoutY());
					
					line.setEndX(end.getLayoutX());
					line.setEndY(yheight / 2 + end.getLayoutY());
					this.getChildren().add(line);
				}

			}
		}
	}

	private Button createButton(String id) {
		Button button = new Button(id);
		button.setOnMouseClicked( eventHandler );
		button.setPrefWidth(xwidth);
		button.setPrefHeight(yheight);
		return button;
	}

	private void drawTree() {		
		for (int layer : this.registeredNodes.keySet()) {
			List<DependencyNode> nodes = this.registeredNodes.get(layer);
			for (int n = 0; n < nodes.size(); n++) {
				double x = xoffset + xstep * layer;
				double y = yoffset + ystep * n;
				DependencyNode node = nodes.get(n);
				Button button = createButton(node.getDependencyProperty().getId());
				button.setLayoutX(x);
				button.setLayoutY(y);
				if (node.isRecursive()) {
					button.setStyle("-fx-border-color:red;-fx-border-width:1;");
				}
				if (layer == 0) {
					button.setStyle("-fx-background-color:lightblue");
					mainButton = button;
				}
				this.getChildren().add(button);
			
				buttonMap .put(node, button);
			}
		}
	}
}
class PendingItem {

	private DependencyNode parent;
	private DependencyNode child;

	public PendingItem(DependencyNode parent, DependencyNode child) {
		this.parent = parent;
		this.child = child;
	}
	
	public void execute() {
		this.parent.addChild(child);
	}
}
