package jp.silverbullet.uidesigner.widgets;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.uidesigner.pane.SvCommonDialog;
import jp.silverbullet.uidesigner.pane.TenKeyFx;

public class SvFunctionKeyFx extends SvPropertyWidgetFx {

	private static final int BUTTON_HEIGHT = 70;
	private static final int BUTTON_WIDTH = 130;
	private static final int SELECTION_HEIGHT = 40;
	private static final int SELECTION_WIDTH = 120;
	private Label value;
	private Stage options = new Stage(StageStyle.UNDECORATED);
	private Label title;
	public SvFunctionKeyFx(final SvProperty prop,
			DependencyInterface dependencyInterface, Description style, Description description) {
		super(prop, dependencyInterface);
		
		AnchorPane anchor = new AnchorPane();
		this.getChildren().add(anchor);
		
		VBox vbox = new VBox();
				
		double button_height = BUTTON_HEIGHT;
		double button_width = BUTTON_WIDTH;
		
		if (!description.getValue(Description.WIDTH).isEmpty()) {
			button_width  = Double.valueOf(description.getValue(Description.WIDTH));
		}
		if (!description.getValue(Description.HEIGHT).isEmpty()) {
			button_height  = Double.valueOf(description.getValue(Description.HEIGHT));
		}
		vbox.setMinHeight(button_height);
		vbox.setMaxHeight(button_height);
		vbox.setMinWidth(button_width);
		vbox.setMaxWidth(button_width);
//		this.setStyle("-fx-padding:1;");
//		vbox.setStyle("-fx-border-width:1;-fx-border-color:black;");
		
		title = new Label(prop.getTitle());
		title.setTextAlignment(TextAlignment.CENTER);
		vbox.getChildren().add(title);
		
		value = new Label();
		updateValue(prop);
		value.setTextAlignment(TextAlignment.CENTER);
		value.setStyle("-fx-font-weight:bold;-fx-text-fill:red;");
		vbox.getChildren().add(value);
		
		vbox.setAlignment(Pos.CENTER);
		
		vbox.setMouseTransparent(true);
		
		Button button = new Button();
		String g = style.getValue("-fx-graphic");
		button.setStyle(style.get());
		button.setMinHeight(vbox.getMinHeight());
		button.setMaxHeight(vbox.getMinHeight());
		button.setMinWidth(vbox.getMinWidth());
		button.setMaxWidth(vbox.getMinWidth());
		
		anchor.getChildren().add(button);
		anchor.getChildren().add(vbox);
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (prop.isListProperty()) {
					if (prop.getAvailableListDetail().size() > 2) {
						onTouched(prop);
					}
					else {
						toggleList(prop);
					}
				}
				else if (prop.isBooleanProperty()) {
					String val = DependencyFormula.TRUE;
					if (prop.getCurrentValue().equals(DependencyFormula.TRUE)) {
						val = DependencyFormula.FALSE;
					}
					try {
						getDependencyInterface().requestChange(prop.getId(), val);
					} catch (RequestRejectedException e) {
						e.printStackTrace();
					}
				}
				else if (prop.isNumericProperty()){
					showTenKey(prop);
				}
			}
		});
	}

	protected void showTenKey(SvProperty prop) {
		try {
			String ret = SvCommonDialog.showTenkey(prop, this);
			this.getDependencyInterface().requestChange(prop.getId(), ret);
		} catch (Exception e1) {

		}
	}

	protected void toggleList(SvProperty prop) {
		int index = 0;
		for (ListDetailElement e: prop.getAvailableListDetail()) {
			if (e.getId().equals(prop.getCurrentValue())) {
				break;
			}
			index++;
		}
		String val = "";
		if (index == 0) {
			val = prop.getAvailableListDetail().get(1).getId();
		}
		else {
			val = prop.getAvailableListDetail().get(0).getId();
			
		}
		try {
			this.getDependencyInterface().requestChange(getProperty().getId(), val);
		} catch (RequestRejectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	protected void updateValue(final SvProperty prop) {
		if (prop.isListProperty()) {
			value.setText(prop.getSelectedListTitle());
		}
		else if (prop.isBooleanProperty()) {
			value.setText(prop.getCurrentValue().equals("true") ? "ON" : "OFF");
		}
		else {
			value.setText(prop.getCurrentValue() + prop.getUnit());
		}
	}

	protected void onTouched(final SvProperty prop) {
		if (options.isShowing()) {
			options.hide();
			return;
		}
		if (options.getOwner() != null) {
			options.initOwner(this.getScene().getWindow());
		}
		HBox hbox = new HBox();
		final Map<Button, String> map = new HashMap<>();
		if (prop.isListProperty()) {
			for (ListDetailElement e: prop.getAvailableListDetail()) {
				Button button = new Button(e.getTitle());
				button.setPrefHeight(SELECTION_HEIGHT);
				button.setPrefWidth(SELECTION_WIDTH);
				hbox.getChildren().add(button);
				button.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						options.hide();
						try {
							getDependencyInterface().requestChange(prop.getId(), map.get(arg0.getSource()));
						} catch (RequestRejectedException e) {
							e.printStackTrace();
						}
					}
				});
				
				map.put(button, e.getId());
			}
		}

		Scene scene = new Scene(hbox, hbox.getChildren().size() * SELECTION_WIDTH, SELECTION_HEIGHT);
		options.setScene(scene);
		
		options.show();
		
		Bounds bounds = this.localToScene(this.getBoundsInLocal());
		
		//options.setX(this.getScene().getWindow().getX());
		options.setX(bounds.getMaxX() + this.getScene().getWindow().getX() - scene.getWidth() - this.widthProperty().get() + 10);
		options.setY(bounds.getMaxY() + this.getScene().getWindow().getY() - 20);
		hbox.requestFocus();
		hbox.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {

				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
							//	stage.hide();
							}
						});
					}
				}.start();
				
			}
			
		});
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateValue(this.getProperty());
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		this.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {

	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		this.title.setText(title);
	}

}
