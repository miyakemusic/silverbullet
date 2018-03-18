package jp.silverbullet.uidesigner.widgets;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener.Flag;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.property.ListDetailElement;

public class SvToggleButtonsFx extends SvAbstractTitledWidgetFx {
	private Map<String, ToggleButton> buttons;
	private EventHandler<ActionEvent> listener;

	public SvToggleButtonsFx(SvProperty prop, DependencyInterface svPanelHandler, Description description) {
		super(prop, svPanelHandler, description);
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateUi(value);
	}

	protected void updateUi(String value) {
		if (buttons.keySet().contains(value)) {
			this.buttons.get(value).setSelected(true);
		}
		else {
		}
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		this.setDisable(!b);
		for (ToggleButton c : this.buttons.values()) {
			c.setDisable(!b);
		}
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {

	}

	private HBox currentHBox;
	@Override
	protected Node createContent(SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description) {
		ToggleGroup group = new ToggleGroup();

		VBox vbox = new VBox();
		buttons = new HashMap<>();

		listener = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (String id : buttons.keySet()) {
					if (buttons.get(id).equals(event.getSource())) {
						sendToDependency(id);
						break;
					}
				}
			}
		};
		
		int maxItems = 20;
		if (!description.getValue(Description.ITEMS_PER_LINE).isEmpty()) {
			maxItems = Integer.valueOf(description.getValue(Description.ITEMS_PER_LINE));
		}
		
		currentHBox = new HBox();
		vbox.getChildren().add(currentHBox);
		int count = 0;
		for (ListDetailElement e : prop.getListDetail()) {			
			ToggleButton radio = createButton(e, description);
			radio.setToggleGroup(group);
			
			buttons.put(e.getId(), radio);
			currentHBox.getChildren().add(radio);
			
			radio.setOnAction(listener);
			
			count++;
			if (count % maxItems == 0) {
				currentHBox = new HBox();
				vbox.getChildren().add(currentHBox);
			}
		}
		try {
		this.buttons.get(prop.getCurrentValue()).setSelected(true);
		}
		catch (Exception e) {
			
		}
		return vbox;
	}

	protected ToggleButton createButton(ListDetailElement e, Description description) {
		ToggleButton radio = new ToggleButton(e.getTitle());
		if (description.getValue(Description.BUTTON_WIDTH).isEmpty()) {
			radio.setMinWidth(100);
		}
		else {
			radio.setMinWidth(Double.valueOf(description.getValue(Description.BUTTON_WIDTH)));
		}
		
		if (description.isDefined(Description.LIST_ICONS)) {
			radio.setStyle(radio.getStyle() + ";-fx-graphic:" + description.getSubValue(Description.LIST_ICONS, e.getId()));
		}
		
		return radio;
	}

	protected void sendToDependency(String id) {
		try {
			getDependencyInterface().requestChange(getProperty().getId(), id);
		} catch (RequestRejectedException e) {
			updateUi(this.getProperty().getCurrentValue());
		}
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		if (this.isDisabled()) {
			return;
		}
		for (String key : this.buttons.keySet()) {
			boolean b = false;
			for (ListDetailElement e : this.getProperty().getAvailableListDetail()) {
				if (key.equals(e.getId())) {
					b = true;
					break;
				}
			}				
			this.buttons.get(key).setDisable(!b);
		}

	}
}
