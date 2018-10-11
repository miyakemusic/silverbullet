package jp.silverbullet.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.silverbullet.trash.speceditor2.OkCancelPaneFx;

public class MyDialogFx extends Stage {
	
	private Pane parent;
	private String title;
	private double width  =1000;
	private double height = 800;
	private BorderPane vbox = new BorderPane();
	private Node control;
	private boolean okClicked = false;
	private boolean modal;
	private Node content;
	
	public MyDialogFx(String title, Pane parent) {
		this.parent = parent;
		this.title = title;
		okClicked = false;
		
		this.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
	
			}
		});
	}
	
	public boolean isOkClicked() {
		return okClicked;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public void showModal(Pane node) {
		this.content = node;
		showDialog(node, true);
	}

	protected void showDialog(Pane node, boolean modal) {
		this.content = node;
		this.setTitle(title);
		this.initOwner(parent.getScene().getWindow());
		this.initStyle(StageStyle.UTILITY);
		if (modal) {
			this.initModality(Modality.WINDOW_MODAL);
		}
		vbox.setCenter(node);
		if (control != null) {
			vbox.setBottom(control);
		}
		else {
			vbox.setBottom(new OkCancelPaneFx() {

				@Override
				protected void onCancel() {
					close();
				}

				@Override
				protected void onOk() {
					okClicked = true;
					close();
				}
				
			});
		}
		Scene scene = new Scene(vbox, width, height);
		this.setScene(scene);
		if (modal) {
			this.showAndWait();
		}
		else {
			this.show();
		}
	}

	public void setControl(Node node) {
		this.control = node;
	}

	public void showNoModal(Pane node) {
		this.content = node;
		this.modal = false;
		this.showDialog(node, false);
	}
	
}
