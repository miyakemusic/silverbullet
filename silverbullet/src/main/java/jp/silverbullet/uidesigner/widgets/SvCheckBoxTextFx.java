package jp.silverbullet.uidesigner.widgets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;

public class SvCheckBoxTextFx extends SvPropertyWidgetFx {

	private TextField text;
	private CheckBox checkBox;

	public SvCheckBoxTextFx(final SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);

		checkBox = new CheckBox(prop.getTitle());
		this.getChildren().add(checkBox);
		checkBox.selectedProperty().set(true);
		text = new TextField(prop.getCurrentValue());
		this.getChildren().add(text);
		
		Label unit = new Label(prop.getUnit());
		this.getChildren().add(unit);
		
		unit.visibleProperty().bind(text.visibleProperty());
		
		text.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					updatePropertyValue(prop.getId());
				}
			}
		});
		
		checkBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updatePropertyValue(prop.getId());
			}
		});
	}

	protected void updatePropertyValue(String id) {
		sendToDependency(id);
	}

	protected void sendToDependency(String id) {
		try {
			getDependencyInterface().requestChange(id, this.checkBox.isSelected() + ";" + text.getText());
		} catch (RequestRejectedException e) {
			updateUi(this.getProperty().getCurrentValue());
		}
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateUi(value);
	}

	protected void updateUi(String value) {
		if (value.contains(";")) {
			String[] tmp = value.split(";");
			Boolean check = new Boolean(tmp[0]);
			this.checkBox.selectedProperty().set(check);
			if (check) {
				this.text.setText(tmp[1]);
				this.text.visibleProperty().set(true);
			}
			else {
				this.text.visibleProperty().set(false);
			}
		}
		else {
			this.text.setText(value);
		}
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		this.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		this.checkBox.setText(title);
	}

}
