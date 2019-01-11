package jp.silverbullet.remote;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SvTexHolder {
	private String chapter = "";
	private LinkedHashMap<String, SvTexArray> map = new LinkedHashMap<String, SvTexArray>();
	private String header = "";
	private String path = "";
	
	public void addTex(String section, SvTex tex) {
		if (!map.containsKey(section)) {
			map.put(section, new SvTexArray());
		}
		tex.setSection(section);
		map.get(section).add(tex);
	}

	public List<String> getSections() {
		return new ArrayList<String>(this.map.keySet());
	}

	public List<SvTex> getTexList(String section) {
		return this.map.get(section).getArray();
	}

	public String getChapter() {
		return this.chapter;
	}

	public List<SvTex> getAllTexs() {
		List<SvTex> ret = new ArrayList<SvTex>();
		for (String section : this.map.keySet()) {
			for (SvTex tex : map.get(section).getArray()) {
				ret.add(tex);
			}
		}
		
		return ret;
	}

	public void clear() {
		this.map.clear();
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return this.header;
	}
	
	public void analyzePair() {
		for (String section : this.map.keySet()) {
			List<SvTex> texs = this.map.get(section).getArray();
			SvTex prev = null;
			for (SvTex tex : texs) {
				if (prev != null) {
					if (tex.isQuery() && prev.getCommand().equals(tex.getCommand().replace("?", ""))) {
						prev.setPair(true);
					}
					else {
						prev.setPair(false);
					}
				}

				prev = tex;
			}
		}
	}

	public String getPath() {
		return path;
	}

	public boolean contains(String command) {
		return this.getTex(command) != null;
	}

	public void setPath(String jpPath) {
		this.path = jpPath;
	}

	public void remove(int row) {
		int count = 0;
		for (String section : this.map.keySet()) {
			List<SvTex> items = this.map.get(section).getArray();
			for (int i = 0; i < items.size(); i++) {
				if (row == count) {
					items.remove(i);
					return;
				}
				count++;
			}
		}
	}

	public void insertTexs(List<SvTex> texs, int row) {
		int count = 0;
		for (String section : this.map.keySet()) {
			List<SvTex> items = this.map.get(section).getArray();
			for (int i = 0; i < items.size(); i++) {
				if (row == count) {
					for (SvTex tex : texs) {
						tex.setSection(section);
						items.add(i++, tex);
					}
					return;
				}
				count++;
			}
		}
	}

	public void setChapter(String chapter2) {
		this.chapter = chapter2;
	}

	public SvTex getTex(String command) {
		for (String section : this.map.keySet()) {
			for (SvTex tex : map.get(section).getArray()) {
				if (tex.getCommand().equals(command)) {
					return tex;
				}
			}
		}
		return null;
	}

	public void replaceSection(String original, String newName) {
		LinkedHashMap<String, SvTexArray> newMap = new LinkedHashMap<String, SvTexArray>();
		for (String key : this.map.keySet()) {
			List<SvTex> elements = this.map.get(key).getArray();
			if (key.equals(original)) {
				newMap.put(newName, new SvTexArray(elements));
				for (SvTex tex : elements) {
					tex.setSection(newName);
				}
			}
			else {
				newMap.put(key, new SvTexArray(elements));
			}
		}
		this.map = newMap;
	}

	public void addAll(String section, List<SvTex> texes) {
		for (SvTex tex : texes) {
			this.addTex(section, tex);
		}
	}

	public LinkedHashMap<String, SvTexArray> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<String, SvTexArray> map) {
		this.map = map;
	}

	public void remove(SvTex tex) {
		for (SvTexArray arr : this.map.values()) {
			if (arr.getArray().contains(tex)) {
				arr.getArray().remove(tex);
			}
		}
	}

	public void addAll(SvTexHolder holder) {
		this.map.putAll(holder.map);
	}
}
