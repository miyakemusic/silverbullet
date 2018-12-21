package jp.silverbullet.property2;

public class PropertyFactory {
	
	public PropertyDef2 create(String id, PropertyType2 type) {
		PropertyDef2 ret = new PropertyDef2();
		ret.setId(id);
		ret.setType(type);
		return ret;
	}

	public PropertyDef2 createList(String id) {
		return this.create(id, PropertyType2.List);
	}

	public PropertyDef2 createNumeric(String id) {
		return this.create(id, PropertyType2.Numeric);
	}

	public PropertyDef2 createBoolean(String id) {
		return this.create(id, PropertyType2.Boolean);
	}

	public PropertyDef2 createText(String id) {
		return this.create(id, PropertyType2.Text);
	}

	public PropertyDef2 createChart(String id) {
		return this.create(id, PropertyType2.Chart);
	}

	public PropertyDef2 createTable(String id) {
		return this.create(id, PropertyType2.Table);
	}

}
