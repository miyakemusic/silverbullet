package jp.silverbullet.web.obsolute;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.uidesigner.widgets.Description;

public class HtmlPane extends HtmlWidget {

	private Description description;
	private String layout;

	public HtmlPane(String id, HtmlDi model, String string) {
		super(id, model);

		description = new Description(string);
		layout = description.getValue(Description.LAYOUT);
	}

	public HtmlPane(String id, HtmlDi model) {
		super(id, model);

		layout = "Horizontal";
	}

	private List<HtmlWidget> widgets = new ArrayList<HtmlWidget>();
	
	public void add(HtmlWidget widget) {
		widgets.add(widget);
	}
	
	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		//String script = "$('#" + parent + "').append('"+ "<div id=" + HtmlUtil.wrap(id) + "></div>');\n";
		//String script = "$('#" + id2 + "').append('";
		String script = "$('#" + id2 + "').append('<div id=" + HtmlUtil.wrap(id) + " style=" + HtmlUtil.wrap("border-width:1px;border-color:black;border-style:solid") + "></div>');\n";
		for (HtmlWidget widget : widgets) {
			//script += "$('#" + id + "').append('" + widget.getScript() + "');\n";
			script += widget.getScript(id, layout.equals("Vertical"));

			script += "addWidget(" + HtmlUtil.wrap(id) + ", widget);\n";
		}
		return script;
	}
}
