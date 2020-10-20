package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbTextField extends SbWidget {

//	private JLabel title;
	private JTextField field;
//	private JLabel unit;
//	private JPanel panel;

	public SbTextField(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
//		panel = new JPanel();
//		title = new JLabel(uiProp.getTitle());
		field = new JTextField(uiProp.getCurrentValue());
//		unit = new JLabel(uiProp.getUnit());
		
//		panel.setLayout(new FlowLayout());
//		panel.add(title);
//		panel.add(field);
//		panel.add(unit);
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				  int keycode = e.getKeyCode();
				  if (keycode == KeyEvent.VK_ENTER){
					  setValue(field.getText());
				  }
			}
		});
		parent.add(field);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
//		title.setText(uiProp.getTitle());
		field.setText(uiProp.getCurrentValue());
//		unit.setText(uiProp.getUnit());
	}

	@Override
	protected void onSize(int width, int height) {
		field.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		this.field.setLocation(left, top);
	}

}
