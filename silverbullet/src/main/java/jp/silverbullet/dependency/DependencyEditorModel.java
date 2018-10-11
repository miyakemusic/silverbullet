package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;

public class DependencyEditorModel {

	public static final String VISIBLE = "visible";
	public static final String ENABLED = "enabled";
	private SvProperty property;
	private DependencySpecHolder dependencySpecHolder2;
	private PropertyHolder propertyHolder;
	private SvPropertyStore store;
	private Set<DependecyEditorModelListener> listeners = new HashSet<>();
	private SvProperty mainProperty;
	public DependencyEditorModel(SvProperty property, DependencySpecHolder dependencySpecHolder2 ,
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

	public DependencySpecHolder getDependencySpecHolder() {
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

	private void fireSelectionChanged(String id) {
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
				ret.add(e.getId() + "." + ENABLED);
			}
			for (ListDetailElement e: property.getListDetail()) {
				ret.add(e.getId() + "." + VISIBLE);
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

	public void setSelectedId(String id) {
		if (!id.equals(this.getSelectedProperty().getId())) {
			this.setProperty(this.getProperty(id));
			this.fireSelectionChanged(id);
		}
	}

	public DependencyTargetElement convertElementFromPresentation(String element) {
		if (element.contains(".")) {
			if (element.endsWith("." + VISIBLE)) {
				return DependencyTargetElement.ListItemVisible;
			}
			else if (element.endsWith("." + ENABLED)) {
				return DependencyTargetElement.ListItemEnabled;
			}
		}
		else {
			return DependencyTargetElement.valueOf(element);
		}
		return null;
	}

	public String getSelectionId(String element) {
		if (element.contains(".")) {
			return element.split("\\.")[0];
		}
		return "";
	}

}
