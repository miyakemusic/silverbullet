package jp.silverbullet.swing;

import java.awt.Container;

import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public abstract class SbWidget {
	private UiModel uiModel;
	private UiProperty uiProperty;
	private Pane pane;
	
	public SbWidget(Pane pane, UiModel uiModel, Container parent) {
		this.uiModel = uiModel;
		this.pane = pane;
		this.uiModel.addListener(pane.id, new UiModelListener() {
			@Override
			public void onUpdate(UiProperty prop) {
				uiProperty = prop;
				SbWidget.this.onUpdate(prop);
			}
		});
		
		uiProperty = uiModel.getUiProperty(pane.id);
		onInit(pane, uiProperty, parent);
		
		if (pane.css("width") != null && pane.css("height") != null) {
			try {
				onSize((int)Float.parseFloat(pane.css("width")), (int)Float.parseFloat(pane.css("height")));
			}
			catch (Exception e) {
				
			}
		}
		
		if (pane.css("top") != null && pane.css("left") != null) {
			try {
				int left = Integer.valueOf(pane.css("top").replace("px", ""));
				int top = Integer.valueOf(pane.css("top").replace("px", ""));
				onPosition(left, top);
			}
			catch (Exception e) {
				onPosition(0, 0);
			}
		}
		
//		this.onUpdate(uiProperty);
	}

	protected abstract void onSize(int width, int height);
	protected abstract void onPosition(int left, int top);
	
	protected void setValue(String value) {
		this.uiModel.setValue(uiProperty.getId(), value);
	}
	
	protected UiProperty getUiProperty() {
		return this.uiProperty;
	}
	
	public UiModel getUiModel() {
		return uiModel;
	}

	public Pane getPane() {
		return pane;
	}

	abstract void onInit(Pane pane, UiProperty uiProp, Container parent);

	abstract protected void onUpdate(UiProperty uiProp);

}
