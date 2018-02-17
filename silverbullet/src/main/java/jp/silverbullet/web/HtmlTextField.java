package jp.silverbullet.web;

public class HtmlTextField extends HtmlWidget {


	public HtmlTextField(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvTextField(" + HtmlUtil.wrap(id2) + ", " + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(model.getTitle(id)) + ");\n";
		script += "addWidget(" + HtmlUtil.wrap(id) + ", widget);\n";
		return script;
	}

}
