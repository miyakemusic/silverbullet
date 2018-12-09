package jp.silverbullet.property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class PropertyDef implements Cloneable {
	
	@Override
	public PropertyDef clone() {
		try {
			PropertyDef ret = (PropertyDef)super.clone();
			List<String> others = new ArrayList<String>(this.others);
			//others.addAll(this.others);
			List<ListDetailElement> elements = new ArrayList<ListDetailElement>();
			for (ListDetailElement e : this.listDetail) {
				elements.add(e.clone());
			}
			ret.setOthers(others);
			ret.setListDetail(elements);
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String toString() {
		return this.id + ";" + this.type + ";" + this.title + ";" + this.comment + this.others.toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		String oldId = this.id;
		this.id = id;
		for (ListDetailElement e : listDetail) {
			e.setId(e.getId().replace(oldId, id));
		}
		
		if (!oldId.isEmpty()) {
			this.fireIdChangedEvent(oldId, id);
		}
	}
	private void fireIdChangedEvent(String oldId, String newId) {
		for (PropertyDefListener listener : listeners) {
			listener.onIdChanged(oldId, newId);
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		this.fireEvent();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public int getSize() {
		return size;
	}
	public String getGroup() {
		return group;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public List<String> getOthers() {
		return others;
	}
	public void setOthers(List<String> others) {
		this.others = others;
		this.fireEvent();
	}
	private String id = "ID_UNDEFINED";
	private String type = "ListProperty";
	private String title = "Untitled";
	private String comment = "";
	private int size = 1;
	private String group = "";
	
	private List<String> others = new ArrayList<String>();
	private List<ListDetailElement> listDetail = new ArrayList<>();
	
	@XmlTransient
	private ArgumentDefInterface argumetnDefInterface;
	
	@XmlTransient
	private Set<PropertyDefListener> listeners = new HashSet<>();

	public void addPropertyDefListener(PropertyDefListener listener) {
		this.listeners.add(listener);
	}
	
	public List<ListDetailElement> getListDetail() {
		return listDetail;
	}
	
	public void replaceListId(int index, String value) {
		this.listDetail.get(index).setId(value);
		updateListItems();
		fireEvent();
	}
	
	public void setListDetail(List<ListDetailElement> listDetail) {
		this.listDetail = listDetail;
	}
	private void fireEvent() {
		for (PropertyDefListener listener : listeners) {
			listener.onChanged(this);
		}
	}
	public void addListItem(int subSelectedRow) {
		ListDetailElement e = new ListDetailElement();
		e.setId(this.id + "_NEW_MEMBER");
		e.setComment("");
		e.setTitle("New Member");
		this.listDetail.add(e);
		updateListItems();
		fireEvent();
	}
	
	public void addListItem(ListDetailElement e) {
		this.listDetail.add(e);
		updateListItems();
		fireEvent();
	}
	
	public void updateListItems() {
		int index = findArgumentIndex("choices");
		String s = "";
		for (ListDetailElement e: this.listDetail) {
			s += e.getTitle() + ",";
		}
		
		Collections.sort(this.listDetail, new Comparator<ListDetailElement>() {
			@Override
			public int compare(ListDetailElement arg0, ListDetailElement arg1) {
				return arg0.getId().compareTo(arg1.getId());
			};
		});
		
		this.updateArgument(index, s);
	}
	protected int findArgumentIndex(String key) {
		int index = this.argumetnDefInterface.get(type).indexOf(key);
		return index;
	}
	public void removeListItem(int index) {
		String oldId = this.listDetail.get(index).getId();
		for (ListDetailElement e : this.listDetail) {
			if (e.getId().equals(oldId)) {
				this.listDetail.remove(e);
				break;
			}
		}
		updateListItems();
		fireEvent();
	}
	public void updateArgument(int index, String value) {
		if (this.others.size() > index) {
			try {
			this.others.remove(index);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		this.others.add(index, value);
	}
	
	public void setArgumentDef(ArgumentDefInterface argDef) {
		this.argumetnDefInterface = argDef;
	}
	
	public String getArgumentValue(String key) {
//		if (key.equals("id")) {
//			return this.id;
//		}
//		else if (key.equals("title")) {
//			return this.title;
//		}
//		else if (key.equals("type")) {
//			return this.type;
//		}
//		else if (key.equals("comment")) {
//			return this.comment;
//		}
		int index = this.findArgumentIndex(key);
		if (index >= 0) {
			return this.others.get(index);
		}
		else {
			return "";
		}
	}
	public void removePropertyDefListener(PropertyDefListener listener) {
		this.listeners.remove(listener);
	}
	public void initId(String id) {
		this.id = id;
	}
	public void initArgumentValues() {
		this.others.clear();
		for (int i = 0; i < this.argumetnDefInterface.get(type).size(); i++) {
			others.add("");
		}
	}
	public void updateArgument(String key, String value) {
		int index = this.argumetnDefInterface.get(type).indexOf(key);
		this.updateArgument(index, value);
	}
}
