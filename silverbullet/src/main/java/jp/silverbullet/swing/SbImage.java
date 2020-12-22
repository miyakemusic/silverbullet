package jp.silverbullet.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import jp.silverbullet.core.property2.ImageProperty;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;
import jp.silverbullet.core.ui.part2.Pane;

public class SbImage extends SbWidget {

	private JLabel label;

	public SbImage(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);

	}

	@Override
	protected void onSize(int width, int height) {
		label.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {

	    label = new JLabel();
		//label.setPreferredSize(new Dimension(uiProp.get));
	    label.setBorder(new LineBorder(Color.black, 1));
		parent.add(label);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		String val = uiProp.getCurrentValue();
		byte[] bytes = ((ImageProperty)this.getUiModel().getBlob(uiProp.getId())).getImage();
		
//		if (!val.startsWith("data:image/png;base64")) {
//			val = this.getUiModel().getBlob(uiProp.getId()).toString();
//		}
//		byte[] bytes = Base64.decode(val.replace("data:image/png;base64,", ""));
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
			Image scaled = image.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
			label.setIcon(new ImageIcon(scaled));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
