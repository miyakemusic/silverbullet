package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbImage extends SbWidget {

	private JLabel label;

	public SbImage(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);

	}

	@Override
	protected void onSize(int width, int height) {
		label.setSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {

		label = new JLabel();
		parent.add(label);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		String val = uiProp.getCurrentValue();
		byte[] bytes = Base64.decode(val.replace("data:image/png;base64,", ""));
		label.setIcon(new ImageIcon(bytes));
	}

}
