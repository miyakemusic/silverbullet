package jp.silverbullet.core.ui.part2;

public abstract class PaneWalkThrough {
	abstract protected boolean handle(Pane widget, Pane parent);
	public void walkThrough(Pane pane, Pane parent) {
		if (!handle(pane, parent)) {
			return;
		}
		for (Pane w : pane.widgets) {
			if (w == null) {
				continue;
			}
			walkThrough( w, pane);
		}
	}
}
