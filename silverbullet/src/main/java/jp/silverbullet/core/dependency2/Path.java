package jp.silverbullet.core.dependency2;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private DependencyNode node;
	private Path parent;
	public Path(DependencyNode node) {
		this.node = node;
	}
	public Path() {
		// TODO Auto-generated constructor stub
	}
	public void add(Path path) {
		path.setParent(this);
		System.out.println(this.getText());
	}

	private void setParent(Path path) {
		this.parent = path;
	}
	public Path getParent() {
		return parent;
	}
	public String getText() {
		List<String> list = new ArrayList<>();
		dig(this, list);
		
		String text = "";
		for (int i = list.size()-1; i >=0; i--) {
			text += list.get(i) + " -> ";
		}
		return text;
	}
	
	private void dig(Path path, List<String> text) {
		if (path.getNode() == null) {
			return;
		}
		text.add(path.getNode().getId());
		dig(path.getParent(), text);
	}
	public DependencyNode getNode() {
		return node;
	}

}
