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
		String[] order = {"7","8","9","4","5","6","1","2","3","0","+/-", "BS"};
		for (Integer y = 0; y < 4; y++) {
			for (Integer x = 0; x < 3; x++) {
			
				Button button = new Button();
				button.setPrefHeight(50);
				button.setPrefWidth(60);
				button.setText(order[i++]);
				grid.add(button, x, y);
				button.setOnAction(handelr);
			}
		}
	}

	public String getValue() {
		return this.textField.getText();
	}
}
