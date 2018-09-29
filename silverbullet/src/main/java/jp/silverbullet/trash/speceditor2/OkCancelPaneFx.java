package jp.silverbullet.trash.speceditor2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public abstract class OkCancelPaneFx extends HBox {
	public OkCancelPaneFx() {
		Button okButton = new Button("OK");
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onOk();
			}
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onCancel();
			}
		});
		this.getChildren().add(okButton);
		this.getChildren().add(cancelButton);
	}

	abstract protected void onCancel();

	abstract protected void onOk();
}
