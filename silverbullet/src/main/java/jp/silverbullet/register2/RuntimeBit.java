package jp.silverbullet.register2;

import java.util.HashMap;
import java.util.Map;

public class RuntimeBit {
	private Map<String, Integer> bitValues = new HashMap<>();
	private byte[] images = null;
	
	public void setValue(String bitName, Integer value) {
		this.bitValues.put(bitName, value);
	}

	public void clear() {
		this.bitValues.clear();
	}

	public Integer getValue(String bitName) {
		if (!this.bitValues.keySet().contains(bitName)) {
			this.bitValues.put(bitName, 0);
		}
		return this.bitValues.get(bitName);
	}

	
	public byte[] getImage() {
		return images;
	}

	public void setValue(byte[] image) {
		this.images = image;
	}
}