package jp.silverbullet.uidesigner.pane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.silverbullet.SvProperty;
import jp.silverbullet.javafx.MyDialogFx;

public class SvCommonDialog {

	private Stage primaryStage;

	public SvCommonDialog(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public static String showTenkey(SvProperty prop, Pane parent) throws Exception {
		MyDialogFx dlg = new MyDialogFx(prop.getTitle(), parent);
		dlg.setWidth(300);
		dlg.setHeight(300);
		TenKeyFx pane = new TenKeyFx(prop);
		dlg.showModal(pane);
		if (!dlg.isOkClicked()) {
			throw new Exception();
		}
		return pane.getValue();
	}
	
	public String showTenkey2(SvProperty prop, Pane parent) throws Exception {
		//MyDialogFx dlg = new MyDialogFx(prop.getTitle(), parent);
		//dlg.setWidth(300);
		//dlg.setHeight(300);
		Stage stage = new Stage();
	//	stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(parent.getScene().getWindow());
		stage.initStyle(StageStyle.UNDECORATED);
		//stage.setWidth(300);
		//stage.setHeight(300);
		BorderPane base = new BorderPane();
	
		TenKeyFx pane = new TenKeyFx(prop);
		base.setCenter(pane);
		base.setStyle("-fx-padding:10;-fx-border-width:1;-fx-border-color:black;");
		Scene scene = new Scene(base, 200, 300);
		stage.setScene(scene);
		//stage.setX(rootNode.getLayoutX() + rootNode.getLayoutBounds().getWidth() / 2);
		stage.setX(this.primaryStage.getX() + this.primaryStage.getWidth() / 2 - base.getWidth() / 2);
		stage.setY(this.primaryStage.getY() + this.primaryStage.getHeight() / 2 - base.getHeight() / 2);
		Button ok = new Button("OK");
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				stage.close();
			}
		});
		base.setBottom(ok);
		stage.showAndWait();
		//dlg.showModal(pane);
//		if (!dlg.isOkClicked()) {
//			throw new Exception();
//		}
		return pane.getValue();
	}
}
