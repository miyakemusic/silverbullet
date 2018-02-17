package jp.silverbullet.uidesigner;

import java.util.List;

import jp.silverbullet.BuilderModel;
import jp.silverbullet.uidesigner.pane.LayoutConfiguration;
import jp.silverbullet.uidesigner.pane.UiElement;

public interface DesignerModel {

	int getTabCount();

	String getTabName(int i);

	BuilderModel getBuilderModel();

	void save(String filename);

	LayoutConfiguration getLayoutConfiguration(String tabName);

	void addNewTab(String value);

	void removeTab(String tabName);

	void addModelListener(DesignerModelListener designerModelListener);

	void setLayoutConfiguration(String tabName, LayoutConfiguration layout2);

	List<UiElement> getElements(String tabName);

	void addWidgets(String tabName, List<String> ids);

	void load(String designerTmp);

	void cut(String name, Object widget);

	UiElement paste();

	void copy(String name, Object pointer);
}
