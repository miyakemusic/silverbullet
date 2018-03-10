package jp.silverbullet.uidesigner.pane;

import java.util.List;

import jp.silverbullet.BuilderModel;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;

public interface SvPanelModel {
	public static final String RADIO_BUTTONS = "Radio Buttons";
	public static final String COMBO_BOX = "Combo Box";
	public static final String TABLE = "Table";
	public static final String CHART_SCATTER = "Scatter Chart";
	public static final String TEXT_BOX = "Text Box";
	public static final String SLIDER = "Slider";
	public static final String TOGGLE_BUTTONS = "Toggle Buttons";
	public static final String TOGGLE_BUTTON = "Toggle Button";
	public static final String LABEL = "Static Label";
	public static final String ONE_BUTTON = "One Button";
	public static final String FUNCTIONKEY = "Function Key";
	public static final String CHECK_BOX = "Check Box";
	public static final String BUTTON = "Button";
	public static final String IMAGE = "Image";
	
	
	BuilderModel getDi();

	List<SvProperty> getAllProperties();

	void setLayout(LayoutConfiguration layout2);

	LayoutConfiguration getLayout();

	void addElement(String id);

	void removeElement(int i);

	List<UiElement> getElements();

	SvProperty getProperty(String id);

	void save();

	void load();

	void addListener(SvPanelModelListener svPanelModelListener);

	List<String> getAlternativeWidgets(SvProperty property);

	void replaceType(Object widget, String simpleName);

	List<String> getAllTypes();

	List<String> getIds(String type);

	void removeElement(Object widget);

	void fireDataChanged();

	PropertyHolder getPropertyHolder();

	SvPropertyStore getPropertyStore();

	void updateStyle(Object pointer, String style);

	void addStaticWidget(String type, String text, String style);

	void cut(Object pointer);

	void paste();

	void setSelected(Object pointer);

	String getStyle(Object pointer);

	void updateDescription(Object pointer, String text);

	String getDescription(Object pointer);

	void updateLayout(Object pointer, LayoutType layout);

	void moveUp(Object pointer);

	void moveDown(Object pointer);

	void copy(Object pointer);

	String getId(Object pointer);

	UiElement getSelectedElement();

	UiElement selectNextElement();

	UiElement selectPrevElement();

	void updatePosition(double x, double y, Object pointer);

}
