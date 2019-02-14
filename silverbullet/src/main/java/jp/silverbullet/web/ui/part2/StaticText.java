package jp.silverbullet.web.ui.part2;

public class StaticText extends WidgetBase {

	public String text;

	public StaticText(String text) {
		super(WidgetType.StaticText, "");
		this.text = text;
	}

}
