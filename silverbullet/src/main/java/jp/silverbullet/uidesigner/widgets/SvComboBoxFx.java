package jp.silverbullet.uidesigner.widgets;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.property.ListDetailElement;

import javax.swing.JLabel;

public class SvComboBoxFx extends SvAbstractTitledWidgetFx {

	private ComboBox<String> combo;
	private boolean ignoreEvent = false;
	private Map<String, Integer> options;
	
	public SvComboBoxFx(SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener, description);			
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateValue(id, value);		
	}

	protected void updateValue(String id, String value) {
		ignoreEvent = true;
		if (!getProperty().getId().equals(id)) {
			return;
		}
		combo.getSelectionModel().select(options.get(value));
		ignoreEvent = false;
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		combo.setDisable(!b);
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		updateComboBox();	
	}

	@Override
	protected Node createContent(final SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description) {
		combo = new ComboBox<String>();
		combo.setMinWidth(100);
		options = new HashMap<>();
		updateComboBox();	
//		combo.getSelectionModel().select(0);
		
		combo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (ignoreEvent)return;
				int index = combo.getSelectionModel().getSelectedIndex();
				String value = prop.getAvailableListDetail().get(index).getId();
				
				sendToDependency(prop, value);

			}
		});
		return combo;
	}

	private void updateComboBox() {
		ignoreEvent = true;
		combo.getItems().clear();
		int i = 0;
		for (ListDetailElement e : getProperty().getAvailableListDetail()) {
			combo.getItems().add(e.getTitle());
			options.put(e.getId(), i++);
		}
		try {
			combo.getSelectionModel().select(options.get(getProperty().getCurrentValue()));
		}
		catch (Exception e) {
			System.out.println(getProperty().getId() + "," + getProperty().getCurrentValue());
			e.printStackTrace();
		}
		ignoreEvent = false;
	}

	protected void sendToDependency(final SvProperty prop, String value) {
		try {
			getDependencyInterface().requestChange(
					prop.getId(), value);
		} catch (RequestRejectedException e) {
			updateValue(prop.getId(), prop.getCurrentValue());	
		}
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		updateComboBox();	
	}
	


}
