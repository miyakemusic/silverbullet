package jp.silverbullet.dependency2.design;

import java.util.List;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.property2.PropertyDef2;

public class RestrictionMatrix {

	public List<String> rowTitle;
	public List<String> colTitle;
	public RestrictionMatrixElement[][] value;

	private static RestrictionMatrix instance;
	
	public static RestrictionMatrix getInstance() {
		if (instance == null) {
			instance = new RestrictionMatrix();
		}
		return instance;
	}
	
	private RestrictionMatrix() {
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
//		this.value[0][1] = new RestrictionMatrixElement(true, "ID_APPLICATION=ID_APPLICATION_OTDR");
//		this.value[0][5] = new RestrictionMatrixElement(true, "ID_APPLICATION=ID_APPLICATION_OTDR");
	}

	public void updateEnabled(int row, int col, boolean checked) {
		this.value[row][col].enabled = checked;
		build();
	}
	
	private void build() {
		String trigger = "ID_DISTANCERANGE";
		String target = "ID_PULSEWIDTH";
		
		StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2().getSpec(target).clear();
		for (int r = 0; r < this.colTitle.size(); r++) {
			String option1 = this.colTitle.get(r);
			boolean touched = false;
			for (int c = 0; c < this.rowTitle.size(); c++) {
				if (this.value[r][c].enabled) {
					touched = true;
					
					String option2 = this.rowTitle.get(c);
					StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2().getSpec(target).addOptionEnabled(option1, DependencySpec.True, 
							"$" + trigger + "==%" + option2);
//					String calc = option1 + ".Enabled=true " + trigger + "==" + option2;
//					System.out.println(calc);
				}
			}
			if (touched) {
				StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2().getSpec(target).addOptionEnabled(option1, DependencySpec.False, 
						DependencySpec.Else);		
			}
		}
		System.out.println("---");
	}
}
