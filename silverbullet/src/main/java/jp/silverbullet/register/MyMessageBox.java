package jp.silverbullet.register;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import jp.silverbullet.javafx.MyDialogFx;

public class MyMessageBox {

	public static String showInput(String defaultValue, Pane parent) throws Exception {
		HBox h = new HBox();
		TextField textField = new TextField(defaultValue);
		h.getChildren().add(textField);
		MyDialogFx dlg = new MyDialogFx("", parent);
		dlg.setSize(200, 90);
		dlg.showModal(h);
		if (dlg.isOkClicked()) {
			return textField.getText();
		}	
		else {
			throw new Exception();
		}
	}

}
