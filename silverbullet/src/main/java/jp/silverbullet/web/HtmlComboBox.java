package jp.silverbullet.web;

public class HtmlComboBox extends HtmlWidget {

	public HtmlComboBox(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvComboBox(" + HtmlUtil.wrap(id2) + ", " + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(model.getTitle(id)) + ");\n";
		//script += "map['" + id + "'] = widget;\n";
//		script += "addWidget(" + HtmlUtil.wrap(id) + ", widget);\n";
		return script;
		
		
//		StringBuilder js = new StringBuilder();
//		js.append("$('#" + id2 + "').append('<label>" + model.getTitle(id) + "</label><select id=" + "\"" + id + "\"" + ">" + "</select>');\n");
//		for (HtmlOptionInfo e : model.getOptionInfo(id)) {
//			js.append("$('#" + id + "').append( new Option(" + HtmlUtil.wrap(e.getTitle()) + ", " + HtmlUtil.wrap(e.getId()) + "));\n");
//			
//		}
//		
//		js.append("$('#" + id + "').change(function() {");
//		js.append("  var val = $(this).val();");
//		js.append("  doDependency(" + HtmlUtil.wrap(id) + ", val);\n");
//		js.append("});");

//		return js.toString();
	}

}
