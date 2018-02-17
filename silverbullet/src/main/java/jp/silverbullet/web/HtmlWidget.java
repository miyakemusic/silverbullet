package jp.silverbullet.web;

public abstract class HtmlWidget {
	private String id;
	private HtmlDi model;

	public HtmlWidget(String id, HtmlDi model) {
		this.id = id;
		this.model = model;
	}
	public String getScript(String id2, boolean vertical) {
		String script = generateScript(id, id2, model);
		if (vertical) {
			script += "widget.appendBr();\n";
		}
		return script;
	}

	public String getId() {
		return id;
	}
	abstract protected String generateScript(String id, String id2, HtmlDi model);
}
