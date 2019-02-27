package jp.silverbullet.dependency2.design;

import java.util.List;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.PropertyDef2;

public class RestrictionMatrix {

	public List<String> rowTitle;
	public List<String> colTitle;
	public String[][] value;

	public RestrictionMatrix() {
		PropertyDef2 dist = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get("ID_DISTANCERANGE");
		PropertyDef2 pulse = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get("ID_PULSEWIDTH");
		
		this.rowTitle = dist.getOptionIds();
		this.colTitle = pulse.getOptionIds();
		this.value = new String[rowTitle.size()][colTitle.size()];
		
	}
}
