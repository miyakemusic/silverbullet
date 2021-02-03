package jp.silverbullet.core.property2;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.core.dependency2.IdValue;

public class IdValues {
	public IdValues() {}
	
	public IdValues(String application) {
		this.application = application;
	}
	
	public String application;
	public List<IdValue> idValue = new ArrayList<IdValue>();

	public List<IdValue> getIdValue() {
		return idValue;
	}

	public void setIdValue(List<IdValue> idValue) {
		this.idValue = idValue;
	}

	public String value(String id) {
		for (IdValue v : this.idValue) {
			if (v.getId().toString().equals(id)) {
				return v.getValue();
			}
		}
		return "";
	}
	
}