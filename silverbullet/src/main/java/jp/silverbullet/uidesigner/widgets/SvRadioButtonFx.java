package jp.silverbullet.uidesigner.widgets;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.property.ListDetailElement;

public class SvRadioButtonFx extends SvToggleButtonsFx {

	public SvRadioButtonFx(SvProperty prop, DependencyInterface svPanelHandler, Description description) {
		super(prop, svPanelHandler, description);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ToggleButton createButton(ListDetailElement e, Description description) {
		return new RadioButton(e.getTitle());
	}


}
