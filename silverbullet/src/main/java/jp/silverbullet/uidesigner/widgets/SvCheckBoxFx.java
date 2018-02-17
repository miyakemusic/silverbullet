package jp.silverbullet.uidesigner.widgets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;

public class SvCheckBoxFx extends SvPropertyWidgetFx {

	private CheckBox check;

	public SvCheckBoxFx(final SvProperty prop, DependencyInterface svPanelHandler, Description description) {
		super(prop, svPanelHandler);
		
		check = new CheckBox(prop.getTitle());
		this.getChildren().add(check);
		updateCheckBoxValue(prop, check);
		check.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sendToDependency(prop);

			}
		});
	}

	private void updateCheckBoxValue(SvProperty prop, CheckBox check) {
		check.setSelected(prop.getCurrentValue().equals("true"));
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateCheckBoxValue(getProperty(), check);
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		check.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	protected void sendToDependency(final SvProperty prop) {
		try {
			getDependencyInterface().requestChange(prop.getId(), check.isSelected() ? "true" : "false");
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		this.check.setText(title);
	}
}
