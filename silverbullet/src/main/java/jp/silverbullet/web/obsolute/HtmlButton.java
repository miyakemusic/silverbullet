package jp.silverbullet.web.obsolute;

public class HtmlButton extends HtmlWidget {

	public HtmlButton(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	protected String getTag() {
		return "Button";
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvButton(" + HtmlUtil.wrap(id2) + ", " + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(model.getTitle(id)) + ");\n";		
		return script;
	}

	
}
