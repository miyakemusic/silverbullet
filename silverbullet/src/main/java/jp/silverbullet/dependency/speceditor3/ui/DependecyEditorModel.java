package jp.silverbullet.dependency.speceditor3.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.property.PropertyHolder;

public class DependecyEditorModel {

	private SvProperty property;
	private DependencySpecHolder2 dependencySpecHolder2;
	private PropertyHolder propertyHolder;
	private SvPropertyStore store;
	private Set<DependecyEditorModelListener> listeners = new HashSet<>();
	private SvProperty mainProperty;
	public DependecyEditorModel(SvProperty property, DependencySpecHolder2 dependencySpecHolder2 ,
			PropertyHolder propertyHolder, SvPropertyStore store) {
		
		this.property = property;
		this.mainProperty = property;
		this.dependencySpecHolder2 = dependencySpecHolder2;
		this.propertyHolder = propertyHolder;
		this.store = store;
	}

	public PropertyHolder getPropertyHolder() {
		return this.propertyHolder;
	}

	public SvProperty getSelectedProperty() {
		return this.property;
	}

	public DependencySpecHolder2 getDependencySpecHolder() {
		return this.dependencySpecHolder2;
	}

	public SvProperty getProperty(String id) {
		return store.getProperty(id);
	}

	public void setProperty(SvProperty property2) {
		this.property = property2;
	}

	public void fireModelUpdated() {
		for (DependecyEditorModelListener listener : listeners) {
			listener.onSpecUpdate();
		}
	}

	public void addtListener(DependecyEditorModelListener listener) {
		this.listeners.add(listener);
	}

	public void fireSelectionChanged(String id) {
		for (DependecyEditorModelListener listener : listeners) {
			listener.onSelectionChanged(id);
		}
	}

	public SvProperty getMainProperty() {
		return this.mainProperty;
	}

}
