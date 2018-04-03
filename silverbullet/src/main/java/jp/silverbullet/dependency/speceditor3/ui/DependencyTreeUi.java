package jp.silverbullet.dependency.speceditor3.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.dependency.speceditor3.DependencyBuilder3;
import jp.silverbullet.dependency.speceditor3.DependencyExpression;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolderMap;
import jp.silverbullet.dependency.speceditor3.DependencyNode;
import jp.silverbullet.dependency.speceditor3.DependencyProperty;
import jp.silverbullet.dependency.speceditor3.DependencySpec2;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.dependency.speceditor3.IdCollector;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;

public class DependencyTreeUi extends AnchorPane {
	private EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getButton().equals(MouseButton.PRIMARY)){
				dependencyEditorModel.fireSelectionChanged( ((Button)event.getSource()).getText());
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

	private DependecyEditorModel dependencyEditorModel;
	private Map<Integer, List<DependencyNode>> registeredNodes = new HashMap<>();
	private Map<DependencyNode, Button> buttonMap = new HashMap<>();
	private List<Button> triggerButtons = new ArrayList<>();
	private Button mainButton;
	private ContextMenu contextMenu = new ContextMenu();
		
	public DependencyTreeUi(DependecyEditorModel dependencyEditorModel) {
		this.setStyle("-fx-border-width:1;-fx-border-color:black;");
		this.setPrefHeight(300);
		
		contextMenu = new ContextMenu();	
		MenuItem targetMenu = new MenuItem("Add target");
		targetMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showIdSelector(dependencyEditorModel.getPropertyHolder());
			}
		});
		contextMenu.getItems().addAll(targetMenu);
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.SECONDARY)) {
					showPopup(contextMenu, event);
				}
			}
		});
		this.dependencyEditorModel = dependencyEditorModel;
		dependencyEditorModel.addtListener(new DependecyEditorModelListener() {
			@Override
			public void onSpecUpdate() {
				buildUi();
			}

			@Override
			public void onSelectionChanged(String id) {
			}

			@Override
			public void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
				// TODO Auto-generated method stub
				
			}

		});
		buildUi();
	}

	protected void showPopup(ContextMenu contextMenu,
			MouseEvent event) {
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
	}

	private void buildUi() {
		this.registeredNodes.clear();
		this.getChildren().clear();
		DependencyBuilder3 builder = new DependencyBuilder3(dependencyEditorModel.getMainProperty().getId(), dependencyEditorModel.getDependencySpecHolder());
		DependencyNode tree = builder.getTree();
		
		DependencySpec2 spec = dependencyEditorModel.getDependencySpecHolder().get(dependencyEditorModel.getMainProperty().getId());
		DependencyProperty depProp = new DependencyProperty(dependencyEditorModel.getMainProperty().getId(), 
				DependencyTargetElement.Any, "", "", null);
		IdCollector collector = new IdCollector();
		Set<String> ids = new HashSet<>();
		for (DependencyExpressionHolderMap map2 : spec.getDepExpHolderMap().values())  {
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				for (DependencyExpressionHolder expressionHolder : list) {
					for (String value : expressionHolder.getExpressions().keySet()) {
						ids.addAll(collector.collectIds(value));
						for (DependencyExpression exp : expressionHolder.getExpressions().get(value).getDependencyExpressions()) {
							String expression = exp.getExpression().getExpression();
							ids.addAll(collector.collectIds(expression));
						}
					}
				}
			}
		}
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
		add(0, tree);
		workThrough(tree.getChildren(), 1);
		
		drawTree();
		drawLine();
	}

	private void drawLine() {
		for (Button button : this.triggerButtons) {
			Line line = new Line();
			line.setStartX(xwidth + button.getLayoutX());
			line.setStartY(yheight / 2 + button.getLayoutY());
			
			line.setEndX(mainButton.getLayoutX());
			line.setEndY(yheight / 2 + mainButton.getLayoutY());
			this.getChildren().add(line);
		}
		
		for (int layer : this.registeredNodes.keySet()) {
			List<DependencyNode> nodes = this.registeredNodes.get(layer);
			for (int n = 0; n < nodes.size(); n++) {
				DependencyNode node = nodes.get(n);
				for (DependencyNode nextNode : node.getChildren()) {
					Button start = buttonMap.get(node);
					Button end = buttonMap.get(nextNode);
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

	private void workThrough(List<DependencyNode> nodes, int layer) {
		for (DependencyNode n : nodes) {
			if (n.getDependencyProperty().getCondition().equals(DependencyExpression.ELSE)) {
				continue;
			}
			add(layer, n);
			if (n.isLeaf()) {
				return;
			}
			workThrough(n.getChildren(), layer + 1);
		}
	}

	private void add(int layer, DependencyNode node) {
		if (!registeredNodes.containsKey(layer)) {
			registeredNodes.put(layer, new ArrayList<DependencyNode>());
		}

		// Check if already added
		for (DependencyNode n : registeredNodes.get(layer)) {
			if (node.getDependencyProperty().getId().equals(n.getDependencyProperty().getId())) {
				return;
			}
		}
		
		registeredNodes.get(layer).add(node);
	}

	protected void showIdSelector(PropertyHolder propertyHolder) {
		MyDialogFx dialog = new MyDialogFx("ID", this);
		final PropertyEditorPaneFx node = new PropertyEditorPaneFx(propertyHolder) {
			@Override
			protected void onClose() {
				removeListener();
				dialog.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				removeListener();
				String id = selected.get(0);
				showElementSelector(id);
			}
		};

		dialog.showModal(node);
	}

	protected void showElementSelector(String id) {
		MyDialogFx dialog = new MyDialogFx("Target Element", this);
		dialog.setMaxHeight(200);
		dialog.setMaxWidth(400);
		VBox vbox = new VBox();
		ComboBox<String> combo = new ComboBox<>();
		combo.getItems().addAll(this.dependencyEditorModel.getAllElements(id));
		vbox.getChildren().add(combo);
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			DependencyTargetConverter e = dependencyEditorModel.getRealTargetElement(combo.getSelectionModel().getSelectedItem());

			dependencyEditorModel.requestAddingSpec(id, e.getElement(), e.getSelectionId());
		}
	}
}
