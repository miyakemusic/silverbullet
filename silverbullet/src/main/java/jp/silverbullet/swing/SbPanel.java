package jp.silverbullet.swing;

import java.awt.Container;

import javax.swing.JPanel;

import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public abstract class SbPanel extends SbWidget{
	abstract void processSubPane(Pane subPane);
	
	public SbPanel(Pane pane, UiModel uiModel, JPanel parent) {
		super(pane, uiModel, parent);
		for (Pane subPane : pane.widgets) {
			processSubPane(subPane);
		}
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		System.out.println();
	}

}
