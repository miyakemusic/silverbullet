package jp.silverbullet.uidesigner;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;

public class MyCommonDialogFx {

	public static String showInput(String string, Pane parent) {
		TextField text = new TextField(string);
		MyDialogFx dlg = new MyDialogFx("Input", parent);
		VBox vbox = new VBox();
		vbox.getChildren().add(text);
		dlg.showModal(vbox);
		if (dlg.isOkClicked()) {
			return text.getText();
		}
		return null;
	}

}
