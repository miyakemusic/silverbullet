package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbToggleButtonSingle extends SbWidget {

	private JToggleButton button;
	private ToggleHandler handler;
	
	public SbToggleButtonSingle(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		button = new JToggleButton();
		parent.add(button);
		
		if (uiProp.getType().equals(PropertyType2.List)) {
			handler = new ListHandler(pane.subId);
		}
		else if (uiProp.getType().equals(PropertyType2.Boolean)) {
			handler = new BooleanHandler();
		}
		handler.buildUi(pane, uiProp);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.setValue(button.isSelected());
			}
		});
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		handler.updateUi(uiProp);
	}

	@Override
	protected void onSize(int width, int height) {
		button.setPreferredSize(new Dimension(width, height));
	}
	
	abstract class ToggleHandler {
		abstract void buildUi(Pane pane, UiProperty uiProp);
		abstract void updateUi(UiProperty uiProp);
		abstract void setValue(boolean selected);		
	}
	
	class BooleanHandler extends ToggleHandler {

		@Override
		public void buildUi(Pane pane, UiProperty uiProp) {
			button.setText(uiProp.getTitle());
			updateState(uiProp);
		}
		
		@Override
		public void updateUi(UiProperty uiProp) {
			updateState(uiProp);
		}
		
		void updateState(UiProperty uiProp) {
			button.setSelected(uiProp.getCurrentValue().equals(DependencySpec.True));
			button.setEnabled(uiProp.isEnabled());
		}

		@Override
		void setValue(boolean selected) {
			SbToggleButtonSingle.this.setValue(selected ? DependencySpec.True : DependencySpec.False);
		}

		
	}
	
	class ListHandler extends ToggleHandler {

		private String subId;

		public ListHandler(String subId) {
			this.subId = subId;
		}

		@Override
		public void buildUi(Pane pane, UiProperty uiProp) {
			for (ListDetailElement e : uiProp.getElements()) {
				if (e.getId().equals(pane.subId)) {
					button.setText(e.getTitle());
				}
			}
			button.setSelected (uiProp.getCurrentSelectionId().equals(pane.subId));
		}

		@Override
		public void updateUi(UiProperty uiProp) {
			button.setSelected (uiProp.getCurrentSelectionId().equals(subId));
			button.setEnabled(!uiProp.getDisabledOption().contains(subId));
		}

		@Override
		void setValue(boolean selected) {
			if (selected) {
				button.setSelected(false);
				SbToggleButtonSingle.this.setValue(subId);
			}
			else {
				button.setSelected(true);
			}
		}
	}

	@Override
	protected void onPosition(int left, int top) {
		this.button.setLocation(left, top);
	}
}
