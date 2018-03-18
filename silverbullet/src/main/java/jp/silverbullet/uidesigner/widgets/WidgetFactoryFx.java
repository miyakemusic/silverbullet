package jp.silverbullet.uidesigner.widgets;

import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.uidesigner.pane.SvCommonDialog;
import jp.silverbullet.uidesigner.pane.SvPanelModel;
import jp.silverbullet.uidesigner.pane.UiElement;

public class WidgetFactoryFx {
	private DependencyInterface svPanelHandler;
	private SvCommonDialog commonDialog;

	public WidgetFactoryFx(DependencyInterface widgetListener, SvCommonDialog commonDialog) {
		this.svPanelHandler = widgetListener;
		this.commonDialog = commonDialog;
	}

	public SvPropertyWidgetFx createRadioButton(SvProperty prop, Description description) {
		return new SvRadioButtonFx(prop, svPanelHandler, description);
	}

	public SvPropertyWidgetFx createComboBox(SvProperty prop, Description description) {
		return new SvComboBoxFx(prop, svPanelHandler, description);
	}

	private String decideWidgetType(SvProperty prop) {
		if (prop.isListProperty()) {
			if (prop.getAvailableListDetail().size() > 2) {
				return SvPanelModel.COMBO_BOX;
			}
			else {
				return SvPanelModel.RADIO_BUTTONS;
			}
		}
		else if (prop.isTextProperty()) {
			return SvPanelModel.TEXT_BOX;
		}
		else if (prop.isNumericProperty()) {
			return SvPanelModel.TEXT_BOX;
		}
		else if (prop.isBooleanProperty()) {
			return SvPanelModel.CHECK_BOX;
		}
		else if (prop.isActionProperty()) {
			return SvPanelModel.BUTTON;
		}
		else if (prop.isTableProperty()) {
			return SvPanelModel.TABLE;
		}
		else if (prop.isLabelProperty()) {
			return SvPanelModel.LABEL;
		}
		else if (prop.isImageProperty()) {
			return SvPanelModel.IMAGE;
		}
		
		return SvPanelModel.LABEL;
	}
	
//	private SvPropertyWidgetFx createAuto(SvProperty prop, UiElement element) {
//		SvPropertyWidgetFx ret = null;
//		Description description = new Description(element.getDescription());
//		Description style = new Description(element.getStyle());
//		if (prop.isListProperty()) {
//			if (prop.getAvailableListDetail().size() > 2) {
//				ret = createComboBox(prop, description);
//			}
//			else {
//				ret = createRadioButton(prop, description);//createComboBox(prop);
//			}
//			
//		}
//		else if (prop.isTextProperty()) {
//			ret = createText(prop, description);
//		}
//		else if (prop.isNumericProperty()) {
//			ret = createText(prop, description);
//		}
//		else if (prop.isBooleanProperty()) {
//			ret = createCheckBox(prop, description);
//		}
//		else if (prop.isActionProperty()) {
//			ret = createButton(prop, style, description);
//		}
//		else if (prop.isTableProperty()) {
//			ret = createTable(prop, description);
//		}
//		else if (prop.isLabelProperty()) {
//			ret = createLabel(prop, description);
//		}
//		else if (prop.isNumericNoneProperty()) {
//			ret = createCheckBoxText(prop, description);
//		}
//		return ret;
//	//	return new EditableWidgetFx(ret);
//	}
	
	private SvPropertyWidgetFx createCheckBoxText(SvProperty prop, Description description) {
		return new SvCheckBoxTextFx(prop, svPanelHandler, description);
	}

	private SvPropertyWidgetFx createLabel(SvProperty prop, Description description) {
		return new SvLabelFx(prop, this.svPanelHandler, description);
	}

	private SvPropertyWidgetFx createTable(SvProperty prop, Description description) {
		return new SvTableFx(prop, this.svPanelHandler, description);
	}

	public SvPropertyWidgetFx create(SvProperty prop, UiElement uiElement) {
		SvPropertyWidgetFx ret = null;
	
		String widgetType = uiElement.getWidgetType();
		
		Description description = new Description(uiElement.getDescription());
		Description style = new Description(uiElement.getStyle());
		
		if (widgetType == null || widgetType.isEmpty()) {
			//return createAuto(prop, uiElement);
			uiElement.setWidgetType(this.decideWidgetType(prop));
			widgetType = uiElement.getWidgetType();
		}

		if (widgetType.equals(SvPanelModel.COMBO_BOX)) {
			ret = createComboBox(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.RADIO_BUTTONS)){
			ret = createRadioButton(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TABLE)) {
			ret = createTable(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.CHART_SCATTER)) {
			ret = createChart(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TEXT_BOX)) {
			ret = createText(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.SLIDER)) {
			ret = createSlider(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TOGGLE_BUTTONS)) {
			ret = createToggleButtons(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.TOGGLE_BUTTON)) {
			ret = createToggleButton(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.LABEL)) {
			ret = createLabel(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.ONE_BUTTON)) {
			ret = createOneButton(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.FUNCTIONKEY)) {
			ret = createFunctionKey(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.CHECK_BOX)) {
			ret = createCheckBox(prop, description);
		}
		else if (widgetType.equals(SvPanelModel.BUTTON)) {
			ret = createButton(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.IMAGE)) {
			ret = createImage(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.GAUGE)) {
			ret = createGauge(prop, style, description);
		}
		else if (widgetType.equals(SvPanelModel.JFREECHART)) {
			ret = createJFreeChart(prop, style, description);
		}
		else {
			ret = createLabel(prop, description);
		}
		return ret;		
	}

	private SvPropertyWidgetFx createJFreeChart(SvProperty prop, Description style, Description description) {
		return new SvChartJFree(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createGauge(SvProperty prop, Description style, Description description) {
		return new SvGaugeFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createImage(SvProperty prop, Description style, Description description) {
		return new SvImageFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createToggleButton(SvProperty prop, Description style, Description description) {
		return new SvToggleButtonFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createFunctionKey(SvProperty prop, Description style, Description description) {
		return new SvFunctionKeyFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createOneButton(SvProperty prop, Description style, Description description) {
		return new SvOneButtonForListFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createToggleButtons(SvProperty prop, Description description) {
		return new SvToggleButtonsFx(prop, this.svPanelHandler, description);
	}

	private SvPropertyWidgetFx createSlider(SvProperty prop, Description description) {
		return new SvSliderFx(prop, this.svPanelHandler, description);
	}

	private SvPropertyWidgetFx createChart(SvProperty prop, Description description) {
		return new SvChartFx(prop, this.svPanelHandler, description);
	}

	private SvPropertyWidgetFx createButton(SvProperty prop, Description style, Description description) {
		return new SvButtonFx(prop, this.svPanelHandler, style, description);
	}

	private SvPropertyWidgetFx createCheckBox(SvProperty prop, Description description) {
		return new SvCheckBoxFx(prop, this.svPanelHandler, description);
	}

	private SvPropertyWidgetFx createText(SvProperty prop, Description description) {
		return new SvTextFieldFx(prop, this.svPanelHandler, description, commonDialog);
	}
}
