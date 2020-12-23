package jp.silverbullet.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Layout;
import jp.silverbullet.core.ui.part2.Pane;

public abstract class SbPanel extends SbWidget{
	private JPanel panel;

	abstract void processSubPane(JPanel panel, Pane subPane);
	
	public SbPanel(Pane pane, UiModel uiModel, JPanel parent) {
		super(pane, uiModel, parent);
	}

	@Override
	protected void onSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		System.out.println("widgetId:" + pane.widgetId);
		panel = new JPanel();
		parent.add(panel);
		
	//	panel.setBorder(new TitledBorder(pane.widgetId));
		//if (!pane.id.isEmpty()) {
			//panel.setBorder(new TitledBorder(this.getUiModel().getUiProperty(pane.id).getTitle()));
//			panel.setBorder(new TitledBorder(pane.widgetId + ":" + pane.subId));
		//}
		
		
		if (pane.layout.equals(Layout.VERTICAL)) {
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		}
		else if (pane.layout.equals(Layout.HORIZONTAL)) {
			try {
				int padding = Integer.valueOf(pane.css("padding").replace("px", ""));
				panel.setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));
			}
			catch (Exception e) {
				panel.setLayout(new FlowLayout(FlowLayout.LEFT));
			}
		}
		else if (pane.layout.equals(Layout.ABSOLUTE)){
			panel.setLayout(new VerticalLayout());
		}
		else {
			System.err.println("Invalid Layout");
		}
		
//		panel.add(new JLabel(pane.widgetId + ":" + pane.subId));
		
		if (!pane.subId.isEmpty()) {
			onUpdate(uiProp);
		}
		for (Pane subPane : pane.widgets) {
			processSubPane(panel, subPane);
		}
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		if (this.getPane().subId.isEmpty()) {
			return;
		}
		panel.setVisible(this.getPane().subId.equals(uiProp.getCurrentSelectionId()));
	//	System.out.println(this.getPane().subId);
	}

}
