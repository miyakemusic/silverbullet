package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbComboBox extends SbWidget {

//	private JPanel panel;
	private JComboBox<String> combo;
	
	public SbComboBox(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
	}

	@Override
	protected void onSize(int width, int height) {
		combo.setPreferredSize(new Dimension(width, height));
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		combo = new JComboBox<>();
		parent.add(combo);
		uiProp.getElements().forEach(e -> combo.addItem(e.getTitle()));
		
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setValue(uiProp.getElements().get(combo.getSelectedIndex()).getId());
			}
		});
		
		applySelectionToCombo(uiProp);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		applySelectionToCombo(uiProp);
	}

	private void applySelectionToCombo(UiProperty uiProp) {
		int index = -1;
		for (int i = 0; i < uiProp.getElements().size(); i++) {
			ListDetailElement e = uiProp.getElements().get(i);
			if (uiProp.getCurrentSelectionId().equals(e.getId())) {
				index = i;
			}
		}
		combo.setSelectedIndex(index);
	}

	@Override
	protected void onPosition(int left, int top) {
		this.combo.setLocation(left, top);
	}

}
