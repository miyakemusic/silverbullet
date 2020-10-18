package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbButton extends SbWidget {

	private JButton button;
	private ButtonHandler handler;
	interface ButtonHandler {
		void updateUi(UiProperty uiProp);
		void setValue();
	}
	
	class BooleanButtonHandler implements ButtonHandler {

		@Override
		public void updateUi(UiProperty uiProp) {
			updateButtonText(uiProp);
		}

		@Override
		public void setValue() {
			Boolean b = Boolean.valueOf(getUiProperty().getCurrentValue());
			b = !b;
			SbButton.this.setValue(b.toString());
		}
		
	};
	
	class ActionButtonHandler implements ButtonHandler {

		@Override
		public void updateUi(UiProperty uiProp) {
			button.setText("<html>" + uiProp.getTitle() + "</html>");			
		}

		@Override
		public void setValue() {
			Boolean b = Boolean.valueOf(getUiProperty().getCurrentValue());
			b = !b;
			SbButton.this.setValue(b.toString());
		}
		
	};
	
	class ListButtonHandler implements ButtonHandler {

		@Override
		public void updateUi(UiProperty uiProp) {
			String nextValue = uiProp.getElements().get(getNextIndex(uiProp)).getTitle();
			button.setText("<html><div style=\"text-align:center\">"  + uiProp.getTitle() + 
					"<br><font color=\"blue\">" + nextValue + "</font></div><html/>");
		}

		private int getNextIndex(UiProperty uiProp) {
			int currentIndex = -1;
			for (int i = 0; i < uiProp.getElements().size(); i++) {
				ListDetailElement e = uiProp.getElements().get(i);
				if (e.getId().contentEquals(uiProp.getCurrentSelectionId())) {
					currentIndex = i;
					break;
				}
			}
			int nextIndex = -1;
			if (currentIndex < uiProp.getElements().size() -1 ) {
				nextIndex = currentIndex + 1;
			}
			else {
				nextIndex = 0;
			}
			return nextIndex;
		}

		@Override
		public void setValue() {
			UiProperty uiProp = getUiProperty();
			int nextIndex = getNextIndex(uiProp);
			SbButton.this.setValue(uiProp.getElements().get(nextIndex).getId());
		}
		
	}
	public SbButton(Pane pane, UiModel uiModel, Container parent) {	
		super(pane, uiModel, parent);
	}
	
	@Override
	protected void onSize(int width, int height) {
		this.button.setPreferredSize(new Dimension(width, height));
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		button = new JButton();
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				handler.setValue();
			}
		});
		parent.add(button);
		if (uiProp.getType().equals(PropertyType2.Boolean)) {
			handler = new BooleanButtonHandler();
		}
		else if (uiProp.getType().equals(PropertyType2.Action)) {
			handler = new ActionButtonHandler();
		}
		else if (uiProp.getType().equals(PropertyType2.List)) {
			handler = new ListButtonHandler();
		}
		handler.updateUi(uiProp);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		handler.updateUi(uiProp);
	}
	
	private void updateButtonText(UiProperty uiProp) {
		button.setText("<html><div style=\"text-align:center\">"  + uiProp.getTitle() + "<br><font color=\"blue\">" + uiProp.getCurrentValue() + "</font></div><html/>");
	}



	@Override
	protected void onPosition(int left, int top) {
		this.button.setLocation(left, top);
	}
}
