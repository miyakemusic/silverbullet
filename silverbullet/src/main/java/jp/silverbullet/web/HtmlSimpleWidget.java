package jp.silverbullet.web;

public abstract class HtmlSimpleWidget extends HtmlWidget {

	public HtmlSimpleWidget(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script ="$('#" + id2 + "').append('";
		script += "<" + getTag() + " id=" + HtmlUtil.wrap(id) + ">" + getTitle(id, model) + "</" + getTag() + ">";
		script += "');\n";
		return script;
	}
	protected String getTitle(String id, HtmlDi model) {
		return model.getTitle(id);
	}
	abstract protected String getTag();
}
