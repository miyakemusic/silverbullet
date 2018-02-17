package jp.silverbullet.uidesigner;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.bind.JAXBException;

import jp.silverbullet.BuilderModel;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.uidesigner.pane.LayoutConfiguration;
import jp.silverbullet.uidesigner.pane.UiElement;

public class DesignerModelImpl implements DesignerModel {
	private XmlPersistent<LayoutHolder> layoutPersister = new XmlPersistent<>();
	private Stack<UiElement> cutLayouts = new Stack<>();
	
	private LayoutHolder holder;
	private BuilderModel di;
	public static final String LAYOUT_XML = "layout.xml";

	public DesignerModelImpl(BuilderModel di) {
		this.di = di;
		holder = new LayoutHolder();
	}

	@Override
	public int getTabCount() {
		return holder.getLayouts().size();
	}

	@Override
	public String getTabName(int i) {
		return new ArrayList<String>(this.holder.getLayouts().keySet()).get(i);
	}

	@Override
	public BuilderModel getBuilderModel() {
		return di;
	}

	@Override
	public void save(String folder) {
		try {
			layoutPersister.save(this.holder, folder + "/" + LAYOUT_XML, LayoutHolder.class);
			di.save(folder);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public LayoutConfiguration getLayoutConfiguration(String tabName) {
		return this.holder.getLayouts().get(tabName);
	}

	@Override
	public void addNewTab(String tabName) {
		LayoutConfiguration layout = new LayoutConfiguration();
		this.holder.getLayouts().put(tabName, layout);
		fireModelChanged();
	}

	@Override
	public void removeTab(String tabName) {
		this.holder.getLayouts().remove(tabName);
		fireModelChanged();
	}

	private Set<DesignerModelListener> listeners = new HashSet<>();
	
	@Override
	public void addModelListener(DesignerModelListener designerModelListener) {
		listeners.add(designerModelListener);
	}

	private void fireModelChanged() {
		for (DesignerModelListener listener : listeners) {
			listener.dataChanged();
		}
	}

	@Override
	public void setLayoutConfiguration(String name, LayoutConfiguration layout2) {
		this.holder.getLayouts().put(name, layout2);
		this.fireModelChanged();
	}

	@Override
	public List<UiElement> getElements(String tabName) {
		return this.holder.getLayouts().get(tabName).getElements();
	}

	@Override
	public void addWidgets(String tabName, List<String> ids) {
		LayoutConfiguration layout = this.holder.getLayouts().get(tabName);
		for (String id : ids) {
			layout.addElement(id);
		}
		this.fireModelChanged();
		
	}

	@Override
	public void load(String folder) {
		try {
			holder = layoutPersister.load(folder + "/" + LAYOUT_XML, LayoutHolder.class);
			di.load(folder);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cut(String name, Object widget) {
		UiElement e = this.holder.getLayouts().get(name).findElement(widget);
		this.holder.getLayouts().get(name).removeElement(widget);
		cutLayouts.push(e);
	}

	@Override
	public UiElement paste() {
		return this.cutLayouts.lastElement();
	}

	@Override
	public void copy(String name, Object pointer) {
		UiElement e = this.holder.getLayouts().get(name).findElement(pointer).clone();
		cutLayouts.push(e);
	}
}
