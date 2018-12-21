package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.property.ArgumentDefInterface;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.SvProperty;

public class SvPropertyFactory  {
	private ArgumentDefInterface argumentDef = new ArgumentDefInterface() {
		private List<String> arguments = Arrays.asList("defaultKey", "defaultValue", "min", "max", "decimal");
		@Override
		public int indexOf(String type, String key) {
			return arguments.indexOf(key);
		}

		@Override
		public List<String> get(String type) {
			return arguments;
		}
		
	};

	public SvProperty getListProperty(String id, List<String> asList, String defaultId) {
		PropertyDef def = new PropertyDef();
		def.setType("ListProperty");
		def.setOthers(Arrays.asList(defaultId));
		def.setArgumentDef(argumentDef);
		def.setId(id);
		SvProperty ret = new SvProperty(def);
		
		for (String e : asList) {
			def.getListDetail().add(new ListDetailElement(e));
		}
		return ret;
	}

	public SvProperty getDoubleProperty(String id, double defaultValue, String unit, double min, double max,
			int decimal) {
		PropertyDef def = new PropertyDef();
		def.setType("DoubleProperty");
		def.setOthers(new ArrayList<String>(Arrays.asList("", String.valueOf(defaultValue), String.valueOf(min), String.valueOf(max), String.valueOf(decimal))));
		def.setArgumentDef(argumentDef);
		def.setId(id);
		SvProperty ret = new SvProperty(def);	
		return ret;
	}
}
