package jp.silverbullet.core.ui.part2;

public interface UiBuilderListener {

	void onCssUpdate(String widgetId, String key, String value);

	void onTypeUpdate(String widgetId, WidgetType type);

	void onIdChange(String id, String subId);

	void onFieldChange(String id);

	void onLayoutChange(String id);

}
