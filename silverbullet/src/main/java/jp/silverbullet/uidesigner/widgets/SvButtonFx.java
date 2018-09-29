package jp.silverbullet.uidesigner.widgets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.trash.speceditor2.DependencyFormula;

public class SvButtonFx extends SvPropertyWidgetFx {

	private Button button;

	public SvButtonFx(final SvProperty prop, DependencyInterface svPanelHandler, Description style, Description description) {
		super(prop, svPanelHandler);
		
		button = new Button(prop.getTitle());
		this.getChildren().add(button);
		button.setStyle(style.get());
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sendToDepenency(prop);
			}
		});
	}

	@Override
	public void onValueChanged(String id, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		this.button.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	protected void sendToDepenency(final SvProperty prop) {
		try {
			getDependencyInterface().requestChange(prop.getId(), DependencyFormula.ANY);
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
		// TODO Auto-generated method stub
		
	}
}
