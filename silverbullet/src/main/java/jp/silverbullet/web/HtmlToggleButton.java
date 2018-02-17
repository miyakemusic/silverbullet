package jp.silverbullet.web;

public class HtmlToggleButton extends HtmlWidget {

	public HtmlToggleButton(String id, HtmlDi model) {
		super(id, model);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String generateScript(String id, String id2, HtmlDi model) {
		StringBuilder js = new StringBuilder();
		String divName = id;
		js.append("$('#" + id2 + "').append('<div id=" + HtmlUtil.wrap(divName) + " role=\"group\">"+ model.getTitle(id) + ":</div>');\n");
		for (HtmlOptionInfo e : model.getOptionInfo(id)) {
			js.append("$('#" + divName + "').append('<button type=\"button\" id=" + HtmlUtil.wrap(e.getId()) + ">" +e.getTitle()  + "</button>\');\n");
		//	js.append("$('#" + divName + "').append('<input type=\"button\" style=\"border:outset 2px;\" onmousedown=\"this.style.border='inset 2px'\">');\n");
			if (model.getSelectedId(id).equals(e.getId())) {
				js.append("$('#" + e.getId() + "').addClass('active');\n");
			}
			else {
				js.append("$('#" + e.getId() + "').removeClass('active');\n");
			}
		}
		
		return js.toString();
	}

}
