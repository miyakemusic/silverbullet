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

	public int getValue(String bitName) {
		return this.bitValues.get(bitName);
	}

	
	public byte[] getImages() {
		return images;
	}

	public void setValue(byte[] image) {
		this.images = image;
	}
}