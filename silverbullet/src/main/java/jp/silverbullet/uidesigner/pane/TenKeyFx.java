package jp.silverbullet.uidesigner.pane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;

public class TenKeyFx extends VBox {

	private TextField textField;

	public TenKeyFx(SvProperty prop) {
		textField = new TextField(prop.getCurrentValue());
		this.getChildren().add(textField);

		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		hbox.getChildren().add(new Label("Min:" + prop.getMin() + ", Max:" + prop.getMax()));

		GridPane grid = new GridPane();
		this.getChildren().add(grid);
		
		EventHandler<ActionEvent> handelr = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				textField.setText(textField.getText() + ((Button)arg0.getSource()).getText());
			}
		};
		int i = 0;
		for (Integer x = 0; x < 3; x++) {
			for (Integer y = 0; y < 3; y++) {
				Button button = new Button();
				button.setPrefHeight(70);
				button.setPrefWidth(80);
				button.setText(String.valueOf(i++));
				grid.add(button, x, y);
				button.setOnAction(handelr);
			}
		}
	}

	public String getValue() {
		return this.textField.getText();
	}
}
