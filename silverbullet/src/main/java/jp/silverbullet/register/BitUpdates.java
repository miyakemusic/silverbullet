package jp.silverbullet.register;

public class BitUpdates {

	private String name;
	private int val;

	public BitUpdates(String name, int val) {
		this.name = name;
		this.val = val;
	}

	public String getName() {
		return name;
	}

	public int getVal() {
		return val;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVal(int val) {
		this.val = val;
	}

}
