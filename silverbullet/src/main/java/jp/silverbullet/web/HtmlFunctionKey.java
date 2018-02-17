package jp.silverbullet.web;

public class HtmlFunctionKey extends HtmlWidget {

	public HtmlFunctionKey(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		String script = "var widget = new SvFunctionButton(" + HtmlUtil.wrap(id2) + ", " + 
					HtmlUtil.wrap(id) + ", " + 
				HtmlUtil.wrap(model.getTitle(id)) +  ", " + HtmlUtil.wrap(model.getValue(id)) + ");\n";
		
		//script += "map['" + id + "'] = widget;\n";
//		script += "addWidget(" + HtmlUtil.wrap(id) + ", widget);\n";
		return script;
		
//		StringBuilder js = new StringBuilder();
//    	js.append("$('#" + id2 + "').append(\'<button id=" + HtmlUtil.wrap(id) + "></button>\');\n");
//    	js.append("$('#" + id + "').html('" + model.getTitle(id) + "<br>" + 
//    			"<font color=\"red\">" + model.getValue(id) + "</font>" 
//    			+ "');\n");
//    	return js.toString();
	}

}
