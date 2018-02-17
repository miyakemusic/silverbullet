package jp.silverbullet.uidesigner.widgets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;

public class SvSliderFx extends SvPropertyWidgetFx {

	private Slider slider;
	private ChangeListener<Number> listener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> arg0,
				Number arg1, Number arg2) {
			try {
				getDependencyInterface().requestChange(getProperty().getId(), arg2.toString());
			} catch (RequestRejectedException e) {
				slider.setValue(Double.valueOf(getProperty().getCurrentValue()));
			}
		}
	};
	private Label title;
	
	public SvSliderFx(final SvProperty prop, final DependencyInterface depdencyInterface, Description description) {
		super(prop, depdencyInterface);

		this.getChildren().add(title = new Label(prop.getTitle() + ":"));
		this.getChildren().add(new Label(prop.getMin()));
		slider = new Slider();
		this.getChildren().add(slider);
		this.getChildren().add(new Label(prop.getMax()));
		
		slider.setPrefWidth(300);
		slider.setMin(Double.valueOf(prop.getMin()));
		slider.setMax(Double.valueOf(prop.getMax()));
		slider.setValue(Double.valueOf(prop.getCurrentValue()));
		slider.valueProperty().addListener(listener);
	}

	@Override
	public void onValueChanged(String id, String value) {
		slider.valueProperty().removeListener(listener);
		slider.setValue(Double.valueOf(getProperty().getCurrentValue()));
		slider.valueProperty().addListener(listener);
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		slider.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		slider.setMin(Double.valueOf(getProperty().getMin()));
		slider.setMax(Double.valueOf(getProperty().getMax()));
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTitleChanged(String id, String title) {
		this.title.setText(title);
	}

}
