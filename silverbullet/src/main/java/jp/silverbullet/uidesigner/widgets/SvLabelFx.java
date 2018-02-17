package jp.silverbullet.uidesigner.widgets;

import javafx.scene.Node;
import javafx.scene.control.Label;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;

public class SvLabelFx extends SvAbstractTitledWidgetFx {

	private Label label;

	public SvLabelFx(SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener, description);
	}

	private void updateLabel() {
		String value  = "";
		if (this.getProperty().isListProperty()) {
			value = this.getProperty().getSelectedListTitle();
		}
		else {
			value = this.getProperty().getCurrentValue();
		}
		label.setText(value);
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateLabel();
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Node createContent(SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description) {
		label = new Label();
		updateLabel();
		return label;
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

}
