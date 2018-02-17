package jp.silverbullet.uidesigner;

import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlRootElement;

import jp.silverbullet.uidesigner.pane.LayoutConfiguration;
@XmlRootElement
public class LayoutHolder {
	private LinkedHashMap<String, LayoutConfiguration> layouts = new LinkedHashMap<String, LayoutConfiguration>();

	public LinkedHashMap<String, LayoutConfiguration> getLayouts() {
		return layouts;
	}

	public void setLayouts(LinkedHashMap<String, LayoutConfiguration> layouts) {
		this.layouts = layouts;
	}
	
}
