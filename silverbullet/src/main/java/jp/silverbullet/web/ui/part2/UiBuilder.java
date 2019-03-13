package jp.silverbullet.web.ui.part2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UiBuilder {
	public List<Pane> panes = new ArrayList<>();
	public enum PropertyField {
		VALUE, TITLE, UNIT, STATICTEXT, NONE
	}

	public UiBuilder() {

	}

	public Pane createPane(Layout layout) {
		Pane pane = new Pane(layout);
		this.panes.add(pane);
		return pane;
	}

	public void nameAll() {
		for (Pane pane : panes) {
			pane.applyWidgetId();
		}
	}
}
