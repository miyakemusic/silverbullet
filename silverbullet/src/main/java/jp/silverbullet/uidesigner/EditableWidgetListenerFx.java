package jp.silverbullet.uidesigner;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.uidesigner.widgets.EditableWidgetFx;
import jp.silverbullet.uidesigner.widgets.SvPropertyWidgetFx;

public interface EditableWidgetListenerFx {

	void onEdit(SvProperty property);

	List<String> onRequestWidgetChange(SvPropertyWidgetFx widget);

	SvPropertyWidgetFx onChangeSelected(String option, EditableWidgetFx editableWidget);

	void onDependency(SvProperty property);

	void onDependency2(SvProperty property);

}
