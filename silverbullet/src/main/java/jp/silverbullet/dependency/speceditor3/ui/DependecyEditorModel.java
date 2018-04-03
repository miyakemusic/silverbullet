package jp.silverbullet.dependency.speceditor3.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;

public class DependecyEditorModel {

	private static final String VISIBLE = "visible";
	private static final String ENABLED = "enabled";
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

	public void requestAddingSpec(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
		this.setProperty(this.store.getProperty(id));
		for (DependecyEditorModelListener listener : listeners) {
			listener.onRequestAdd(id, dependencyTargetElement, selectionId);
		}
	}

	public List<String> getAllElements(String id) {
		List<String> ret = new ArrayList<>();
		ret.add(DependencyTargetElement.Enabled.name());
		ret.add(DependencyTargetElement.Visible.name());
		ret.add(DependencyTargetElement.Value.name());
		
		SvProperty property = this.store.getProperty(id);
		if (property.isListProperty()) {
			for (ListDetailElement e: property.getListDetail()) {
				ret.add(e.getId() + ".enabled");
			}
			for (ListDetailElement e: property.getListDetail()) {
				ret.add(e.getId() + VISIBLE);
			}
		}
		else if (property.isNumericProperty()) {
			ret.add(DependencyTargetElement.Min.name());
			ret.add(DependencyTargetElement.Max.name());
			
		}
		
		return ret;
	}

	public DependencyTargetConverter getRealTargetElement(String elementName) {
		return new DependencyTargetConverter(elementName);
	}

	public String convertPresentationElement(String id, DependencyTargetElement e) {
		if (e.equals(DependencyTargetElement.ListItemEnabled)) {
			return id + "." + ENABLED;
		}
		else if (e.equals(DependencyTargetElement.ListItemVisible)) {
			return id + "." + VISIBLE;
		}
		else {
			return e.name();
		}
	}

}
