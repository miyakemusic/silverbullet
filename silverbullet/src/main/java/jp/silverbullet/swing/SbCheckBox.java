package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbCheckBox extends SbWidget {

	private JCheckBox checkBox;

	public SbCheckBox(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSize(int width, int height) {
		
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		this.checkBox = new JCheckBox();
		this.checkBox.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SbCheckBox.super.setValue(checkBox.isSelected() ? DependencySpec.True : DependencySpec.False);
			}			
		});
		parent.add(checkBox);
		updateUi(uiProp);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		updateUi(uiProp);
	}

	private void updateUi(UiProperty uiProp) {
		this.checkBox.setText(uiProp.getTitle());
		this.checkBox.setSelected(uiProp.getCurrentValue().equals(DependencySpec.True));
	}

}
