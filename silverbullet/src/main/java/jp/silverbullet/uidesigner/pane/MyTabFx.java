package jp.silverbullet.uidesigner.pane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;

public class MyTabFx extends TabPane {

	public MyTabFx(LayoutType layoutType, final CommonWidgetListener commonListener) {
		final ContextMenu contextMenu = new ContextMenu();
		
		MenuItem description = new MenuItem("Description");
		description.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				commonListener.onDescription(MyTabFx.this);
			}
			 
		});
		
		contextMenu.getItems().addAll(description);
		
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				commonListener.onSelect(MyTabFx.this);
				if (event.isSecondaryButtonDown()) {
					contextMenu.show(MyTabFx.this, event.getScreenX(), event.getScreenY());
				}
				event.consume();
			}
		});
	}

}
