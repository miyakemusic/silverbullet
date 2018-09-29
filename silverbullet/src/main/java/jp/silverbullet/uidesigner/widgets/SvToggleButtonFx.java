package jp.silverbullet.uidesigner.widgets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.trash.speceditor2.DependencyFormula;

public class SvToggleButtonFx extends SvPropertyWidgetFx {

	private ToggleButton button;

	public SvToggleButtonFx(SvProperty prop, DependencyInterface dependencyInterface, Description style, Description description) {
		super(prop, dependencyInterface);

		button = new ToggleButton(prop.getTitle());
		this.getChildren().add(button);
		
		DescriptionUtil.applyDescription(button, description);
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				try {
					dependencyInterface.requestChange(prop.getId(), getOpposite(prop.getCurrentValue()));
				} catch (RequestRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	protected String getOpposite(String value) {
		return DependencyFormula.getOppositeValue(value);
	}

	@Override
	public void onValueChanged(String id, String value) {
		button.setSelected(Boolean.valueOf(value));
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		button.setDisable(!b);
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
		// TODO Auto-generated method stub
		
	}

}
