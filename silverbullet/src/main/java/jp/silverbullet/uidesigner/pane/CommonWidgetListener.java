package jp.silverbullet.uidesigner.pane;

import javafx.scene.layout.Pane;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;

public interface CommonWidgetListener {

	void onSelect(Object pointer);

	void onCut(Object pointer);
	
	void onRemove(Object pointer);
	
	void onStyleChange(Object pointer);

	void onPaste(Object myPane);

	void onDescription(Object pointer);

	void onLayout(LayoutType layout, Object pointer);

	void onMoveUp(Object pointer);

	void onMoveDown(Object pointer);

	void onCopy(Object pointer);

	void onAdd(Pane myPaneFx);

	void onHandlerProperty(String id);

	void onGuiProperties(Object pointer);

	void onMoved(double x, double y, Object pointer);

}
