package jp.silverbullet.testspec;

public class TsPresentationNode {
	private String id;
	private int left;
	private int height;
	private int top;
	private int width;
	public int unitHeight = 20;
	
	private String[] output;
	private TsPresentationNode parent;
	private TsNode node;
		
	public TsPresentationNode() {}
	public TsPresentationNode(TsNode node, TsPresentationNode parent2, String[] output2, int left2, int height2) {
		this.id = node.getId();
		this.left = left2;
		this.output = output2;
		this.height = height2 * unitHeight;
		this.parent = parent2;
		this.node = node;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public String[] getOutput() {
		return output;
	}
	public void setOutput(String[] output) {
		this.output = output;
	}
	public int parentTop() {
		if (this.parent != null) {
			return this.parent.top;
		}
		else {
			return 0;
		}
	}

	public TsPresentationNode parent() {
		return this.parent;
	}

	public TsNode node () {
		return this.node;
	}
}
