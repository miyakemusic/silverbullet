package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;
import jp.silverbullet.core.ui.part2.UiBuilder.PropertyField;

public class SbLabel extends SbWidget {

	private JLabel label;

	public SbLabel(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSize(int width, int height) {
		this.label.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		this.label = new JLabel();
		parent.add(label);
		updateText(uiProp);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		updateText(uiProp);
		
	}

	private void updateText(UiProperty uiProp) {
		String fontSize = this.getPane().css("font-size");
		
		String body = "";
		if (this.getPane().field.equals(PropertyField.TITLE)) {
			body = uiProp.getTitle();
		}
		else if (this.getPane().field.equals(PropertyField.VALUE)) {
			body = uiProp.getCurrentValue();
		}
		else if (this.getPane().field.equals(PropertyField.UNIT)) {
			body = uiProp.getUnit();
		}
		
		String html = "<html>";
		if (fontSize != null) {
			this.label.setFont(new Font("Arial", Font.PLAIN, Integer.valueOf(fontSize.replace("px", ""))));
			//html += "<font size=\"" + fontSize.replace("", "") + "\">";
			html += body;
			//html += "</font>";
		}
		else {
			html += body;
		}
		html += "</html>";
		this.label.setText(html);
	}

}
