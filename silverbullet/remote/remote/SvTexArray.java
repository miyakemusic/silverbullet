package jp.silverbullet.remote;

import java.util.ArrayList;
import java.util.List;

public class SvTexArray {
	private List<SvTex> array = new ArrayList<>();

	public SvTexArray() {}
	public SvTexArray(List<SvTex> elements) {
		this.array = elements;
	}

	public List<SvTex> getArray() {
		return array;
	}

	public void setArray(List<SvTex> array) {
		this.array = array;
	}

	public void add(SvTex tex) {
		this.array.add(tex);
	}
	
}
