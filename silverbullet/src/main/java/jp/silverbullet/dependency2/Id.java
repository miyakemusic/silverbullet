package jp.silverbullet.dependency2;

public class Id {

	private String id;
	private int index;

	public Id(String id, int index) {
		this.id = id;
		this.index = index;
	}

	public Id(String idWithIndex) {
		this.id = idWithIndex.split("@")[0];
		this.index = Integer.valueOf(idWithIndex.split("@")[1]);
	}

	public String getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return this.id + "@" + this.index;
	}

	@Override
	public boolean equals(Object arg0) {
		return this.toString().equals(arg0.toString());
	}
	
	

}
