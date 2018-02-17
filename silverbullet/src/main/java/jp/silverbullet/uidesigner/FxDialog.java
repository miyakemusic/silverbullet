package jp.silverbullet.uidesigner;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FxDialog extends Stage {

	public FxDialog(String title, Pane pane) {
		setTitle(title);
		initOwner(pane.getScene().getWindow());
		initStyle(StageStyle.UTILITY);
		initModality(Modality.WINDOW_MODAL);
		Scene scene = new Scene(pane, 1000, 800);
		setScene(scene);
	}
	
	public void showDialog() {

		showAndWait();
	}
}
