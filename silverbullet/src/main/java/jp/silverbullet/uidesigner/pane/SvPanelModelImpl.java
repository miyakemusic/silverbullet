package jp.silverbullet.uidesigner.pane;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import jp.silverbullet.BuilderModel;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.uidesigner.DesignerModel;
import jp.silverbullet.uidesigner.widgets.Description;

public class SvPanelModelImpl implements SvPanelModel {

	private DesignerModel model;
	private String name;
	private Set<SvPanelModelListener> listeners = new HashSet<SvPanelModelListener>();
	private UiElement selectedElement;

	public SvPanelModelImpl(String name, DesignerModel model) {
		this.model = model;
		this.name = name;
		
		fireDataChanged();
	}

	@Override
	public BuilderModel getDi() {
		return model.getBuilderModel();
	}

	@Override
	public List<SvProperty> getAllProperties() {
		return this.model.getBuilderModel().getAllProperties();
	}

	@Override
	public void setLayout(LayoutConfiguration layout2) {
		this.model.setLayoutConfiguration(name, layout2);
	}

	@Override
	public LayoutConfiguration getLayout() {
		return this.model.getLayoutConfiguration(name);
	}

	@Override
	public void addElement(String id) {
		UiElement e = new UiElement(id);
		this.addToActivePane(e);
	}

	@Override
	public void removeElement(int i) {
		this.model.getLayoutConfiguration(name).removeElement(i);
	}

	@Override
	public List<UiElement> getElements() {
		return this.model.getElements(name);
	}

	@Override
	public SvProperty getProperty(String id) {
		return this.model.getBuilderModel().getProperty(id);
	}

	@Override
	public void save() {
		try {
			new XmlPersistent<LayoutConfiguration>().save(getLayout(), getFilename(), LayoutConfiguration.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private String getFilename() {
		return this.name + ".layout";
	}

	@Override
	public void load() {
		try {
			LayoutConfiguration content = new XmlPersistent<LayoutConfiguration>().load(getFilename(), LayoutConfiguration.class);
			this.model.setLayoutConfiguration(name, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addListener(SvPanelModelListener svPanelModelListener) {
		listeners.add(svPanelModelListener);
	}

	@Override
	public void fireDataChanged() {
		for (SvPanelModelListener listener : listeners) {
			listener.dataChanged();
			
		}
	}

	@Override
	public List<String> getAlternativeWidgets(SvProperty property) {
		if (property.isListProperty()) {
			return Arrays.asList(COMBO_BOX, RADIO_BUTTONS, TOGGLE_BUTTONS, ONE_BUTTON, LABEL, FUNCTIONKEY);
		}
		else if (property.isTableProperty()) {
			return Arrays.asList(TABLE, CHART_SCATTER, JFREECHART);
		}
		else if (property.isNumericProperty()) {
			return Arrays.asList(TEXT_BOX, SLIDER, LABEL, FUNCTIONKEY, GAUGE);
		}
		else if (property.isActionProperty()) {
			return Arrays.asList(FUNCTIONKEY);
		}
		else if (property.isTextProperty()) {
			return Arrays.asList(TEXT_BOX, LABEL, FUNCTIONKEY);
		}
		else if (property.isBooleanProperty()) {
			return Arrays.asList(CHECK_BOX, TOGGLE_BUTTON, FUNCTIONKEY);
		}
		return null;
	}

	@Override
	public void replaceType(Object widget, String simpleName) {
		this.getLayout().replaceType(widget, simpleName);
		this.fireDataChanged();
	}

	@Override
	public List<String> getAllTypes() {
		return this.model.getBuilderModel().getAllTypes();
	}

	@Override
	public List<String> getIds(String type) {
		return this.model.getBuilderModel().getIds(type);
	}

	@Override
	public void removeElement(Object widget) {
		this.model.getLayoutConfiguration(name).removeElement(widget);
		this.fireDataChanged();
	}

	@Override
	public PropertyHolder getPropertyHolder() {
		return this.model.getBuilderModel().getPropertyHolder();
	}

	@Override
	public SvPropertyStore getPropertyStore() {
		return this.model.getBuilderModel().getPropertyStore();
	}

	@Override
	public void updateStyle(Object pointer, String style) {
		this.getLayout().updateStyle(pointer, style);
		this.fireDataChanged();
	}

	@Override
	public void addStaticWidget(String type, String description, String style) {
		UiElement e = new UiElement();
		e.setWidgetType(type);
		e.setDescription(description);
		e.setStyle(style);
		if (this.selectedElement != null) {
			this.selectedElement.getLayout().getElements().add(e);
			//this.model.getLayoutConfiguration(name).findElement(this.selectedElement).getLayout().getElements().add(e);
		}
		else {
			this.model.getLayoutConfiguration(name).getElements().add(e);
		}
		this.fireDataChanged();
	}

	@Override
	public void cut(Object widget) {
		this.model.cut(name, widget);
		this.fireDataChanged();
		
	}

	@Override
	public void paste() {
		UiElement e = this.model.paste();
		addToActivePane(e);
		this.fireDataChanged();
	}

	protected void addToActivePane(UiElement e) {
		if (selectedElement != null && 
				(selectedElement.getWidgetType().equals(UiElement.Pane))) {
			this.selectedElement.getLayout().getElements().add(e);
		}
		else {
			this.getElements().add(e);
		}
	}

	@Override
	public void setSelected(Object pointer) {
		selectedElement = this.model.getLayoutConfiguration(name).findElement(pointer);
		this.model.getLayoutConfiguration(name).cancelSelection();
		if (selectedElement != null) {
			selectedElement.setSelected(true);
		}
	}

	@Override
	public String getStyle(Object pointer) {
		return this.model.getLayoutConfiguration(name).findElement(pointer).getStyle();
	}

	@Override
	public void updateDescription(Object pointer, String text) {
		this.getLayout().updateDescription(pointer, text);
		this.fireDataChanged();
	}

	@Override
	public String getDescription(Object pointer) {
		return this.model.getLayoutConfiguration(name).findElement(pointer).getDescription();
	}

	@Override
	public void updateLayout(Object pointer, UiElement.LayoutType layout) {
		String description = this.model.getLayoutConfiguration(name).findElement(pointer).getDescription();
		
		String ret = "";
		if (!description.contains(Description.LAYOUT + ":")) {
			if (!description.endsWith(";")) {
				ret = description + ";";
			}
			ret += Description.LAYOUT  + ":" + layout.toString() + ";";
		}
		else {
			List<String> lines = Arrays.asList(description.split(";"));
			
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				if (line.trim().startsWith(Description.LAYOUT + ":")) {
					ret += Description.LAYOUT + ":" + layout.toString();
				}
				else {
					ret += line;
				}
				ret += ";";
			}
		}
		this.model.getLayoutConfiguration(name).findElement(pointer).setDescription(ret);
		this.fireDataChanged();
	}

	@Override
	public void moveUp(Object pointer) {
		if (pointer == null) {
			pointer = this.selectedElement.getPointer();
		}
		this.model.getLayoutConfiguration(name).moveUp(pointer);
		this.fireDataChanged();
	}

	@Override
	public void moveDown(Object pointer) {
		if (pointer == null) {
			pointer = this.selectedElement.getPointer();
		}
		this.model.getLayoutConfiguration(name).moveDown(pointer);
		this.fireDataChanged();
	}

	@Override
	public void copy(Object pointer) {
		this.model.copy(this.name, pointer);
	}

	@Override
	public String getId(Object pointer) {
		return this.model.getLayoutConfiguration(name).findElement(pointer).getId();
	}

	@Override
	public UiElement getSelectedElement() {
		return selectedElement;
	}

	abstract class SelectionSwitcher {
		public SelectionSwitcher() {
			if (selectedElement == null)return;
			
			List<UiElement> all = model.getLayoutConfiguration(name).getAllElements();
			int index = getIndex(all.indexOf(selectedElement), all);
			selectedElement.setSelected(false);
			UiElement nextElement = all.get(index);
			nextElement.setSelected(true);
			selectedElement = nextElement;
		}

		abstract protected int getIndex(int indexOf, List<UiElement> all);
	}
	@Override
	public UiElement selectNextElement() {
		SelectionSwitcher switcher = new SelectionSwitcher() {
			@Override
			protected int getIndex(int indexOf, List<UiElement> all) {
				int index = all.indexOf(getSelectedElement());
				if (index == all.size()-1) {
					index = 0;
				}
				else {
					index++;
				}
				return index;
			}
		};
		return selectedElement;
	}

	@Override
	public UiElement selectPrevElement() {
		new SelectionSwitcher() {
			@Override
			protected int getIndex(int indexOf, List<UiElement> all) {
				int index = all.indexOf(getSelectedElement());
				if (index == 0) {
					index = all.size() -1;
				}
				else {
					index--;
				}
				return index;
			}
		};
		return selectedElement;
	}

	@Override
	public void updatePosition(double x, double y, Object pointer) {
		UiElement element = this.model.getLayoutConfiguration(name).findElement(pointer);
		if (element == null)return;
		String text = element.getDescription();
		Description description = new Description(text);
		description.update(Description.X, String.valueOf(x));
		description.update(Description.Y, String.valueOf(y));
		element.setDescription(description.get());
		this.fireDataChanged();
	}
}
