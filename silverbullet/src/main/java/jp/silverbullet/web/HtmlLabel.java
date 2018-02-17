package jp.silverbullet.web;

public class HtmlLabel extends HtmlWidget {


	public HtmlLabel(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvLabel(" + HtmlUtil.wrap(id2) + ", " + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(model.getTitle(id)) + ");\n";
//		script += "addWidget(" + HtmlUtil.wrap(id) + ", widget);\n";
		
//		String script = "$('#" + id2 + "').append('";
//		script += "<label id=" + HtmlUtil.wrap(id) + ">" + model.getTitle(id) + ":" + model.getValue(id) + "</label>";
//		script += "');\n";
		return script;
	}

}
