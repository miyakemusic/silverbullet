package jp.silverbullet.web;

import jp.silverbullet.uidesigner.pane.SvPanelModel;
import jp.silverbullet.uidesigner.pane.UiElement;

public class HtmlWidgetFactory {

	public static HtmlWidget create(UiElement e, HtmlDi htmlDi) {
		String widgetType = e.getWidgetType();
		String id = e.getId();
		if (widgetType.equals(SvPanelModel.COMBO_BOX)) {
			return new HtmlComboBox(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.RADIO_BUTTONS)){
			return new HtmlRadioButton(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.TABLE)) {
			//ret = createTable(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.CHART_SCATTER)) {
			//ret = createChart(pane, prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TEXT_BOX)) {
			return new HtmlTextField(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.SLIDER)) {
			//ret = createSlider(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TOGGLE_BUTTONS)) {
			return new HtmlRadioButton(id, htmlDi);
			//return new HtmlToggleButton(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.LABEL)) {
			return new HtmlLabel(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.ONE_BUTTON)) {
			//ret = createOneButton(pane, prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.FUNCTIONKEY)) {
			return new HtmlFunctionKey(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.CHECK_BOX)) {
			//ret = createCheckBox(pane, prop, description);
			return new HtmlCheckBox(id, htmlDi);
		}
		else if (widgetType.equals(SvPanelModel.BUTTON)) {
			//ret = createButton(pane, prop, style, description);
			return new HtmlButton(id, htmlDi);
		}
		else {
		//	ret = createLabel(pane, prop, description);
		}
		return new HtmlUndefined(id, htmlDi);
	}
}
