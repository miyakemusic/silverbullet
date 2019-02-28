package jp.silverbullet.dependency2.design;

import java.util.List;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.property2.PropertyDef2;

public class RestrictionMatrix {

	public List<String> rowTitle;
	public List<String> colTitle;
	public RestrictionMatrixElement[][] value;

	public RestrictionMatrix() {
		PropertyDef2 dist = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get("ID_DISTANCERANGE");
		PropertyDef2 pulse = StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().get("ID_PULSEWIDTH");
		
		this.rowTitle = dist.getOptionIds();
		this.colTitle = pulse.getOptionIds();
		this.value = new RestrictionMatrixElement[rowTitle.size()][colTitle.size()];
		
		for (int r = 0; r < this.colTitle.size(); r++) {
			for (int c = 0; c < this.rowTitle.size(); c++) {
				this.value[r][c] = new RestrictionMatrixElement();
			}
		}
		this.value[0][1] = new RestrictionMatrixElement(true, "ID_APPLICATION=ID_APPLICATION_OTDR");
		this.value[0][5] = new RestrictionMatrixElement(true, "ID_APPLICATION=ID_APPLICATION_OTDR");
	}
}
