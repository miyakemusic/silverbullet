package jp.silverbullet.uidesigner.pane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;

public class CommonPopupFx {

	public ContextMenu create(final CommonWidgetListener commonListener, final Pane parent) {
		final ContextMenu contextMenu = new ContextMenu(); 
		
		MenuItem add = new MenuItem("Add");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onAdd(parent);
			}
		});
		
		MenuItem cut = new MenuItem("Cut");
		cut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onCut(parent);
			}
		});
		 
		MenuItem copy = new MenuItem("Copy");
		copy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onCopy(parent);
			}
		});
		
		MenuItem paste = new MenuItem("Paste");
		paste.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onPaste(parent);
				
			}
		});
		 
		MenuItem remove = new MenuItem("Remove");
		remove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				commonListener.onRemove(parent);
			}
			 
		});
		 
		MenuItem style = new MenuItem("Style");
		style.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				commonListener.onStyleChange(parent);
			}
			 
		});
	
		MenuItem descriptionMenu = new MenuItem("Description");
		descriptionMenu.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				commonListener.onDescription(parent);
			}
			 
		});

//		MenuItem properties = new MenuItem("GUI Properties");
//		properties.setOnAction(new EventHandler<ActionEvent>() {
//
//			@Override
//			public void handle(ActionEvent event) {
//				commonListener.onGuiProperties(parent);
//			}
//			 
//		});
//		contextMenu.getItems().add(properties);
		
		Menu layout = new Menu("Layout");
		MenuItem flowLayout = new MenuItem("Flow");
		MenuItem verticalLayout = new MenuItem("Vertical");
		MenuItem horizontalLayout = new MenuItem("Horizontal");
		//MenuItem gridLayout = new MenuItem("Grid");
		flowLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				commonListener.onLayout(LayoutType.Flow, parent);
			}
		});
		verticalLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onLayout(LayoutType.Vertical, parent);
			}
		});
		horizontalLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onLayout(LayoutType.Horizontal, parent);
			}
		});
//		gridLayout.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent arg0) {
//				commonListener.onLayout(LayoutType.Grid, MyPaneFx.this);
//			}
//		});
		layout.getItems().addAll(flowLayout, verticalLayout, horizontalLayout);
			
		
		Menu move = new Menu("Move");
		MenuItem up = new MenuItem("Up");
		up.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onMoveUp(parent);
			}
		});
		MenuItem down = new MenuItem("Down");
		down.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				commonListener.onMoveDown(parent);
			}
		});
		move.getItems().addAll(up, down);
		
		contextMenu.getItems().addAll(add, copy, cut, paste, remove, style, descriptionMenu, layout, move);
		
		parent.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				commonListener.onSelect(parent);
				if (event.isSecondaryButtonDown()) {
					contextMenu.show(parent, event.getScreenX(), event.getScreenY());
				}
				event.consume();
			}
		});
		
		return contextMenu;
	}
}
