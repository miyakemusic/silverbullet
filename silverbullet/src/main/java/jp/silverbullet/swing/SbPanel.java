package jp.silverbullet.swing;

import javax.swing.JPanel;

import jp.silverbullet.core.ui.part2.Pane;

public abstract class SbPanel {
	abstract void processSubPane(Pane subPane);
	
	public SbPanel(Pane pane, UiModel uiModel, JPanel parent) {
		for (Pane subPane : pane.widgets) {
			processSubPane(subPane);
		}
	}

}
