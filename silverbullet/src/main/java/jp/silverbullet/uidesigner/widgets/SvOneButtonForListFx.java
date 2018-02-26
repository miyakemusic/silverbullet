package jp.silverbullet.uidesigner.widgets;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.property.ListDetailElement;


public class SvOneButtonForListFx extends SvPropertyWidgetFx {

	private Button button;
	private Description description;

	public SvOneButtonForListFx(SvProperty prop, DependencyInterface widgetListener, Description style, Description description) {
		super(prop, widgetListener);

		this.description = description;
		button = new Button(prop.getSelectedListTitle());
		this.getChildren().add(button);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				toggle();
			}
		});
		button.setStyle(style.get());
		if (!description.getValue(Description.HEIGHT).isEmpty()) {
			button.setMinHeight(Double.valueOf(description.getValue(Description.HEIGHT)));
		}
		else {
			button.setMinHeight(50);
		}
		
		if (!description.getValue(Description.WIDTH).isEmpty()) {
			button.setMinWidth(Double.valueOf(description.getValue(Description.WIDTH)));
		}
		else {
			button.setMinWidth(100);
		}
		updatePresentation(prop.getCurrentValue());
//		if (!description.getValue(Description.LIST_ICONS).isEmpty()) {
//			button.setStyle(button.getStyle() + ";-fx-graphic:" + description.getSubValue(Description.LIST_ICONS, prop.getCurrentValue()));
//		}
	//	button.setText(prop.getCurrentValue());
	}

	protected void setCurrent() {
		try {
			this.getDependencyInterface().requestChange(getProperty().getId(), getProperty().getCurrentValue());
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

	protected void toggle() {
		List<ListDetailElement> elements = this.getProperty().getAvailableListDetail();
		int index = 0;
		for (ListDetailElement e : elements) {
			if (e.getId().equals(getProperty().getCurrentValue())) {
				break;
			}
			index++;
		}

		if (index < elements.size() - 1) {
			index++;
		}
		else {
			index = 0;
		}
		try {
			this.getDependencyInterface().requestChange(getProperty().getId(), elements.get(index).getId());
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onValueChanged(String id, String value) {		
		updatePresentation(value);
	}

	protected void updatePresentation(String value) {
//		if (description.isDefined(Description.LIST_ICONS)) {
//			button.setStyle(button.getStyle() + ";-fx-graphic:" + description.getSubValue(Description.LIST_ICONS, value));
//		}
		DescriptionUtil.applyListIcon(button, value, description);
		button.setText(this.getProperty().getSelectedListTitle());
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		button.disableProperty().set(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		// TODO Auto-generated method stub
		
	}

}
