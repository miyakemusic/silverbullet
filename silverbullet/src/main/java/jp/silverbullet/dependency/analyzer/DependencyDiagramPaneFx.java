package jp.silverbullet.dependency.analyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorModelImpl;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorPaneFx;
import jp.silverbullet.uidesigner.widgets.EditableWidgetFx;

public abstract class DependencyDiagramPaneFx extends Pane {
	private static final int Y_STEP = 50;
	private static final int WIDGET_HEIGHT = 24;
	private static final int WIDGET_WIDTH = 220;
	private double targetY;
	private Button activeButton;
	private Map<Integer, Map<String, Button>> buttons = new HashMap<>();
	private double ymax = 600;
	private double xmax = 800;
	private double xOffset = 20;//this.getInsets().right;
	private double yOffset = 20;//this.getInsets().bottom;
	private Button target;
	private DependencyDiagramModel model;
	
	private EventHandler<MouseEvent> buttonHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			activeButton = (Button)event.getSource();
			onSelect(activeButton.getText());
	        if (event.isSecondaryButtonDown()) {
	            showPopup(activeButton, contextMenu, event);
	        }
		}
		
	};
	private ContextMenu contextMenu;
	
	public DependencyDiagramPaneFx(DependencyDiagramModel model) {
		this.model = model;
		
		contextMenu = new ContextMenu();
		 MenuItem newWindow = new MenuItem("New Window");
		 MenuItem edit = new MenuItem("Edit");
		 newWindow.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showNewWindow(activeButton.getText());
			}
		 });
		 edit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showEdit(activeButton.getText());
			}
		 });
		 contextMenu.getItems().addAll(newWindow, edit);
	}
	
	protected void showPopup(Button activeButton2, ContextMenu contextMenu2,
			MouseEvent event) {
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
	}

	abstract protected void showEdit(String id);
	abstract protected void showNewWindow(String id);
	abstract protected void onSelect(String id);
	
	public void setPropertyId(String id) {
		targetY = 0.0;
		this.drawItems(id);
		targetY = getOptimalTargetY();
		this.drawItems(id);
		this.setPrefSize(xmax + 250, ymax + 100);
	}

	private double getOptimalTargetY() {
		int max = 0;
		for (Map<String, Button> c : this.buttons.values()) {
			if (c.size() > max) {
				max = c.size();
			}
		}
		return Y_STEP * (max / 2);
	}

	private void drawItems(String id) {
		xmax = 600;
		ymax = 300;
		
		this.buttons.clear();
		removeAll();
		
		target = new Button(id);
		target.setTooltip(new Tooltip(id));
		this.getChildren().add(target);
		target.setTranslateX(xOffset + 250);
		target.setTranslateY(yOffset + targetY);
		target.setPrefSize(WIDGET_WIDTH, WIDGET_HEIGHT);
		target.setOnMousePressed(buttonHandler);
		target.setStyle("-fx-background-color:lightblue");
		
		Map<String, Button> map = getMap(-1);
		double y = yOffset;

		for (DependencySpecDetail detail : model.getPassiveDependencySpecDetail(id)) {
			if (map.containsKey(detail.getTargetId())) {
				Button tmp = map.get(detail.getTargetId());
			//	x = tmp.getTranslateX();
				y = tmp.getTranslateY();
			}
			
			Button button = createButton(detail.getTargetId());
			map.put(button.getText(), button);
			
			button.setTranslateX(xOffset);
			button.setTranslateY(y);
			button.setPrefSize(WIDGET_WIDTH, WIDGET_HEIGHT);
			map.put(button.getText(), button);
			
			this.getChildren().add(button);
			
			Line line = new Line(
					button.getTranslateX() + WIDGET_WIDTH,
					button.getTranslateY() + WIDGET_HEIGHT/2 , 
					target.getTranslateX(), 
					target.getTranslateY() + WIDGET_HEIGHT/2);
			this.getChildren().add(line);
			y += Y_STEP;
		}
		
		drawNext(0, target, id, 100);

	}
	
	private void drawNext(int layer, Button from, String id, int step) {
		Map<String, Button> map = getMap(layer);
		double y = 0;
		double x = 500 + layer * (WIDGET_WIDTH + 50) + xOffset;
		for (DependencySpecDetail detail : model.getActiveDependencySpecDetail(id)) {
			y = yOffset + map.keySet().size() * Y_STEP;
	
			if (map.containsKey(detail.getPassiveId())) {
				Button tmp = map.get(detail.getPassiveId());
				x = tmp.getTranslateX();
				y = tmp.getTranslateY();
			}
			
			Button button = createButton(detail.getPassiveId());//new JButton(detail.getPassiveId());

			button.setTooltip(new Tooltip(from.getTooltip().getText() + "->" + detail.getPassiveId()));
			button.setTranslateX(x);
			button.setTranslateY(y);
			button.setPrefSize(WIDGET_WIDTH, WIDGET_HEIGHT);
			
			this.getChildren().add(button);
			map.put(button.getText(), button);

			Line line = new Line(
					from.getTranslateX() + WIDGET_WIDTH, 
					from.getTranslateY() + WIDGET_HEIGHT/2, 
					button.getTranslateX(), 
					button.getTranslateY() + WIDGET_HEIGHT/2);
			this.getChildren().add(line);
			
			if (model.getActiveDependencySpecDetail(detail.getPassiveId()).size() > 0) {
				if (!isRecursive(button.getTooltip().getText())) {
					drawNext(layer + 1, button, detail.getPassiveId(), step / 2);
				}
				else {
					Line line2 = new Line(
							button.getTranslateX() + WIDGET_WIDTH, button.getTranslateY() + WIDGET_HEIGHT / 2, 
							button.getTranslateX() + WIDGET_WIDTH + 100, button.getTranslateY() + WIDGET_HEIGHT / 2);
					this.getChildren().add(line2);
				}
			}
		}
		
		if (x > xmax) {
			xmax = x;
		}
		if (y > ymax) {
			ymax = y;
		}
	}

	protected Button createButton(String name) {
		Button button = new Button(name);		
		button.setOnMousePressed(buttonHandler);
		//button.setStyle("-fx-underline: true");
		return button;
	}

	private void removeAll() {
		this.getChildren().clear();
	}

	private boolean isRecursive(String text) {
		List<String> tmp = Arrays.asList(text.split("->"));
		Set<String> set = new HashSet<String>(tmp);
		return tmp.size() != set.size();
	}
	
	protected Map<String, Button> getMap(int layer) {
		if (!this.buttons.keySet().contains(layer)) {
			this.buttons.put(layer, new HashMap<String, Button>());
		}
		Map<String, Button> map = this.buttons.get(layer);
		return map;
	}

	public void update() {
		this.setPropertyId(this.target.getText());
	}
}
