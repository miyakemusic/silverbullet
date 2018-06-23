package jp.silverbullet.web.obsolute;

public class HtmlRadioButton extends HtmlWidget {


	public HtmlRadioButton(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		StringBuilder js = new StringBuilder();
		
		String divName = id+"_DIV";
		js.append("$('#" + id2 + "').append('<div id=" + HtmlUtil.wrap(divName) + "></div>');\n");
		js.append("$('#" + divName + "').append('<label>" + model.getTitle(id) + ":" + "</label>');\n");
		for (HtmlOptionInfo e : model.getOptionInfo(id)) {
			js.append("$('#" + divName + "').append('<input type=\"radio\" id=" + HtmlUtil.wrap(e.getId()) + " name=" + HtmlUtil.wrap(divName) + ">" + e.getTitle() + "');\n");
			js.append("$('#" + e.getId() + "').on('change', function(event) {\n");;
			js.append("  doDependency(" + HtmlUtil.wrap(id) + ", " + HtmlUtil.wrap(e.getId()) + ");\n");
			js.append("});\n");
		}
		//js.append("$('#" + model.getValue(id) + ").");
		js.append(HtmlUtil.setCheck(model.getSelectedId(id), "true"));
		
		js.append("$('#" + id + "').on('change', function(event) {\n");;
		js.append("  doDependency(" + HtmlUtil.wrap(id) + ", $('#" + id + "').prop('checked'));\n");
		js.append("});\n");
		
		return js.toString();
	}

}
