package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.property.ArgumentDefInterface;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;

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

	public RuntimeProperty getListProperty(String id, List<String> asList, String defaultId) {
		PropertyDef2 def = new PropertyDef2();
		def.setType(PropertyType2.List);
		def.setId(id);
		def.setDefaultId(defaultId);
		
		RuntimeProperty ret = new RuntimeProperty(def);
		
		for (String e : asList) {
			try {
				def.option(e, e, e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return ret;
	}

	public RuntimeProperty getDoubleProperty(String id, double defaultValue, String unit, double min, double max,
			int decimal) {
		PropertyDef2 def = new PropertyDef2();
		def.setType(PropertyType2.Numeric);
		def.setDefaultValue(String.valueOf(defaultValue));
		def.setMin(min);
		def.setMax(max);
		def.setDecimals(decimal);
		def.setId(id);
		RuntimeProperty ret = new RuntimeProperty(def);	
		return ret;
	}
}
