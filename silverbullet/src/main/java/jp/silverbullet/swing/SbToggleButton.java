package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbToggleButton extends SbWidget {

	private JPanel panel;
	private Map<String, JToggleButton> buttonMap;
	
	public SbToggleButton(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		
	}

	@Override
	protected void onUpdate(UiProperty prop) {
		buttonMap.get(prop.getCurrentSelectionId()).setSelected(true);
	}


	@Override
	void onInit(Pane pane, UiProperty prop, Container parent) {
		System.out.println(" SbToggleButton  " + prop.getId());
		buttonMap = new HashMap<>();
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		parent.add(panel);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		if (prop.getType().equals(PropertyType2.List)) {
			for (ListDetailElement e : prop.getElements()) {
				JToggleButton button = new JToggleButton(e.getTitle());
				buttonGroup.add(button);
				panel.add(button);
				
				buttonMap.put(e.getId(), button);
			}
			
			try {
				buttonMap.get(prop.getCurrentSelectionId()).setSelected(true);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (prop.getType().equals(PropertyType2.Boolean)){
			JToggleButton buttonOn = new JToggleButton("On");
			JToggleButton buttonOff = new JToggleButton("Off");
			buttonGroup.add(buttonOn);
			buttonGroup.add(buttonOff);
			panel.add(buttonOn);
			panel.add(buttonOff);
			
			buttonMap.put("true", buttonOn);
			buttonMap.put("false", buttonOff);
			
			buttonMap.get(prop.getCurrentValue()).setSelected(true);
		}

	}

	@Override
	protected void onSize(int width, int height) {
		panel.setSize(width, height);
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}


}
