package jp.silverbullet.web.obsolute;

public class HtmlCheckBox extends HtmlWidget {


	public HtmlCheckBox(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvCheckBox(" + HtmlUtil.wrap(id2) + ", " + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(model.getTitle(id)) + ");\n";
		//script += "map['" + id + "']= widget;\n";
		return script;
//		String script = "$('#" + id2 + "').append('";
//		script += "<input type=\"checkbox\" id="+ HtmlUtil.wrap(id) + ">" + model.getTitle(id);
//		script += "');\n";
//		script += HtmlUtil.setCheck(id, model.getValue(id));
//		
//		script += "$('#" + id + "').on('change', function(event) {\n";
//		script += "  doDependency(" + HtmlUtil.wrap(id) + ", $('#" + id + "').prop('checked'));\n";
//		script += "});\n";
//		return script;
	}

}
