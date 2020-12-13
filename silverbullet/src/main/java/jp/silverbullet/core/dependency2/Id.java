package jp.silverbullet.core.dependency2;

import jp.silverbullet.core.property2.RuntimeProperty;

public class Id {

	private String id;
	private int index;
	private String source = "";
	
	public Id() {}
	
	public Id(String id, int index) {
		this.id = id;
		this.index = index;
	}

	public Id(String id, int index, String source) {
		this.id = id;
		this.index = index;
		this.source = source;
	}
	
	public Id(String idWithIndex) {
		this.id = idWithIndex.split(RuntimeProperty.INDEXSIGN)[0];
		if (id.contains(RuntimeProperty.INDEXSIGN)) {
			this.index = Integer.valueOf(idWithIndex.split(RuntimeProperty.INDEXSIGN)[1]);
		}
		else {
			index = 0;
		}
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

	public String getSource() {
		return source;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	

}
