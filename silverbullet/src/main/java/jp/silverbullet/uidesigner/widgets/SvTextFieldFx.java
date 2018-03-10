package jp.silverbullet.uidesigner.widgets;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.uidesigner.pane.SvCommonDialog;

public class SvTextFieldFx extends SvAbstractTitledWidgetFx {

	private TextField textField;
	private Label optionalInfo;
	private SvCommonDialog commonDialog;

	public SvTextFieldFx(SvProperty prop, DependencyInterface svPanelHandler, Description description, SvCommonDialog commonDialog) {
		super(prop, svPanelHandler, description);
		this.commonDialog = commonDialog;
	}

	@Override
	public void onValueChanged(String id, String value) {
		textField.setText(value);
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		textField.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		updateOptional(this.getProperty());
	}

	@Override
	protected Node createContent(final SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description) {
		
//		FlowPane pane = new FlowPane();
		textField = new TextField();
		textField.setText(prop.getCurrentValue());
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					updatePropertyValue(prop.getId(), textField.getText());
				}
			}
		});
		textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (prop.isNumericProperty()) {
					showTenkey(prop);
				}
			}
		});
		this.optionalInfo  = new Label();
		updateOptional(prop);
//		pane.getChildren().add(textField);
//		pane.getChildren().add(optionalInfo);
		optionalInfo.setVisible(false);
		return textField;
	}

	private void updateOptional(SvProperty prop) {
		if (prop.isNumericProperty()) {
			this.optionalInfo.setText("Min:" + prop.getMin() + ", Max:" + prop.getMax());
		}
	}

	protected void updatePropertyValue(String id, String text) {
		sendToDependency(id, text);
	}

	protected void sendToDependency(String id, String text) {
		try {
			getDependencyInterface().requestChange(id, text);
		} 
		catch (RequestRejectedException e) {
			textField.setText(this.getProperty().getCurrentValue());
			
			if (!e.getMessage().isEmpty()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	protected void showTenkey(final SvProperty prop) {
		try {
			String ret = commonDialog.showTenkey2(prop, this);
			this.getDependencyInterface().requestChange(prop.getId(), ret);
		} catch (Exception e1) {
			
		}
	}
}
