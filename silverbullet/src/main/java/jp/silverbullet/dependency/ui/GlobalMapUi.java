package jp.silverbullet.dependency.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import jp.silverbullet.dependency.DNode;
import jp.silverbullet.dependency.GlobalMap;

public class GlobalMapUi extends AnchorPane {
	private Map<String, Button> buttons = new HashMap<String, Button>();
	private List<Line> lines = new ArrayList<Line>();
	
	private EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			Button button = (Button)arg0.getSource();
			selectId(button.getText());
		}
	};
	private GlobalMap globalMap;
	private String currentId = "";
	private ContextMenu contextMenu;
	
	public GlobalMapUi(GlobalMap globalMap) {
		globalMap.addListener(new GlobalMapListener() {

			@Override
			public void onIdChange(String id) {
				selectId(id);
			}

			@Override
			public void onUpdated() {
				buildUi();
				if (!currentId.isEmpty()) {
					drawLines(currentId);
				}
			}
			
		});
		this.globalMap = globalMap;
		buildUi();
		
		contextMenu = new ContextMenu();	
		contextMenu = new ContextMenu();	
		new SpecAdditionMenu(this, contextMenu, globalMap.getDependencyEditorModel());

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				selectId(globalMap.getDependencyEditorModel().getSelectedProperty().getId());	
			}
		});
		
	}

	private void buildUi() {
		this.getChildren().clear();
		for (int level = 0; level < globalMap.getLevelCount(); level++) {
			List<DNode> nodes = globalMap.getNodes(level);
			for (int i = 0; i < nodes.size(); i++) {
				DNode node = nodes.get(i);
				Button button = getButton(node.getId());
				button.setUserData(node);
				this.getChildren().add(button);
				button.setLayoutX(level * 250);
				button.setLayoutY(i * 35);
			}
		}
	}
	
	protected void createLines(String id, List<String> experienced) {
		if (experienced.contains(id)) {
			return;
		}
		experienced.add(id);
		Button button = this.buttons.get(id);
		DNode node = (DNode)button.getUserData();
		for (DNode n : node.getChildren()) {
			Button next = this.buttons.get(n.getId());
			if (next == null) {
				continue;
			}
			List<String> subExperienced = new ArrayList<>();
			subExperienced.addAll(experienced);
			lines.add(new Line(button.getLayoutX() + button.getWidth(), button.getLayoutY() + button.getHeight()/2, next.getLayoutX(), next.getLayoutY() + next.getHeight()/2));
			createLines(n.getId(), subExperienced);
		}
	}

	protected void createLines2(String id, List<String> experienced) {
		if (experienced.contains(id)) {
			return;
		}
		experienced.add(id);
		Button button = this.buttons.get(id);
		DNode node = (DNode)button.getUserData();
		for (DNode n : node.getParents()) {
			List<String> subExperienced = new ArrayList<>();
			subExperienced.addAll(experienced);
			Button prev = this.buttons.get(n.getId());
			if (prev == null) {
				continue;
			}
			lines.add(new Line(prev.getLayoutX() + prev.getWidth(), prev.getLayoutY() + prev.getHeight()/2, button.getLayoutX(), button.getLayoutY() + button.getHeight()/2));
			createLines2(n.getId(), subExperienced);
		}
	}
	
	private Button getButton(String id) {
		if (!buttons.keySet().contains(id)) {
			Button button = null;
			buttons.put(id, button = new Button(id));
			button.setPrefWidth(200);
			button.setOnAction(eventHandler );
		}
		return buttons.get(id);
	}

	private void selectId(String id) {
		if (id.equals(this.currentId)) {
			return;
		}
		this.currentId = id;
		for (Button button : buttons.values()) {
			button.setStyle("-fx-background-color:lightgray;");
		}
		this.buttons.get(id).setStyle("-fx-background-color:lightblue;");
		drawLines(id);
		globalMap.setSelectedId(id);
	}

	private void drawLines(String id) {
		getChildren().removeAll(lines);
		lines.clear();
		createLines(id, new ArrayList<String>());
		createLines2(id, new ArrayList<String>());
		this.getChildren().addAll(lines);
	}
}
