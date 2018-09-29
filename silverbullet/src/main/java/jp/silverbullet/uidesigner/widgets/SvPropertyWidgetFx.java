package jp.silverbullet.uidesigner.widgets;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.dependency.DependencyInterface;

public abstract class SvPropertyWidgetFx extends StackPane implements SvPropertyListener {

	
	private SvProperty property;
	private DependencyInterface dependencyInterface;

	public SvPropertyWidgetFx(SvProperty prop, DependencyInterface dependencyInterface) {
		this.property = prop;
		this.dependencyInterface = dependencyInterface;
		this.property.addListener(this);

	//	this.setStyle("-fx-spacing:2;-fx-alignment: center;-fx-padding:2;");
	//	this.setStyle("-fx-alignment: center;");
		this.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> arg0,
					Parent arg1, Parent arg2) {
				if (arg2 == null) {
					removeListener();
				}
			}
		});
	}

	public SvProperty getProperty() {
		return property;
	}

	public DependencyInterface getDependencyInterface() {
		return dependencyInterface;
	}

	@Override
	public void onVisibleChanged(String id, Boolean b) {
		this.setVisible(b);
	}

	private void removeListener() {
		this.property.removeListener(this);
	}
}
