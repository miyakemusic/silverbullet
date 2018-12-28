package jp.silverbullet.dependency2;

import jp.silverbullet.property2.RuntimeProperty;

public class Id {

	private String id;
	private int index;

	public Id() {}
	
	public Id(String id, int index) {
		this.id = id;
		this.index = index;
	}

	public Id(String idWithIndex) {
		this.id = idWithIndex.split(RuntimeProperty.INDEXSIGN)[0];
		this.index = Integer.valueOf(idWithIndex.split(RuntimeProperty.INDEXSIGN)[1]);
	}

	public String getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return RuntimeProperty.createIdText(this.id, this.index);
	}

	@Override
	public boolean equals(Object arg0) {
		return this.toString().equals(arg0.toString());
	}
	
	

}
