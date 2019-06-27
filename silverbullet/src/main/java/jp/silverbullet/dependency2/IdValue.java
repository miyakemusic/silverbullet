package jp.silverbullet.dependency2;

import jp.silverbullet.property2.RuntimeProperty;

public class IdValue {

	private Id id;
	private String value;

	public IdValue() {}
	
	public IdValue(String id, int index, String value) {
		this.id = new Id(id, index);
		this.value = value;
	}

	public IdValue(String id, String value) {
		int index = 0;
		if (id.contains(RuntimeProperty.INDEXSIGN)) {
			index = Integer.valueOf(id.split(RuntimeProperty.INDEXSIGN)[1]);
			id = id.split(RuntimeProperty.INDEXSIGN)[0];
		}
		this.id = new Id(id, index);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public Id getId() {
		return id;
	}

	@Override
	public boolean equals(Object arg0) {
		return this.toString().equals(arg0.toString());
	}

	@Override
	public String toString() {
		return this.id.toString() + "." + value;
	}
	
	
}
