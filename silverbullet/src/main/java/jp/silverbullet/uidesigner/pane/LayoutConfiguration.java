package jp.silverbullet.uidesigner.pane;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LayoutConfiguration {

	private List<UiElement> elements = new ArrayList<UiElement>();
	
	public void setElements(List<UiElement> elements) {
		this.elements = elements;
	}

	public void addElement(String id) {
		this.elements.add(new UiElement(id));
	}
	
	public void removeElement(int row) {
		this.elements.remove(row);
	}

	public List<UiElement> getElements() {
		return elements;
	}

	public void replaceType(Object widget, String type) {
		UiElement e = this.findElement(widget);
		if (e != null) {
			e.setWidgetType(type);
		}
	}

	public void removeElement(Object widget) {
		removeElement(widget, this.elements);
	}

	private void removeElement(Object widget, List<UiElement> elements2) {
		for (UiElement e : elements2) {
			if (e.getPointer().equals(widget)) {
				elements2.remove(e);
				break;
			}
			else if(e.getWidgetType().equals("Pane")) {
				removeElement(widget, e.getLayout().elements);
			}
		}
	}

	public void updateStyle(Object pointer, String style) {
		UiElement uiElement = this.findElement(pointer);
		uiElement.setStyle(style);
//		for (UiElement e : uiElement.getLayout().elements) {
//			e.setStyle(style);
//		}
	}

	public UiElement findElement(Object widget) {
		UiElement ret = findElement(this.elements, widget);

		return ret;
	}

	private UiElement findElement(List<UiElement> elements, Object pointer) {
		for (UiElement e : elements) {
			if (e.getPointer().equals(pointer)) {
				return e;
			}
			UiElement ret = findElement(e.getLayout().getElements(), pointer);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	public void updateDescription(Object pointer, String text) {
		this.findElement(pointer).setDescription(text);
	}

	private List<UiElement> findParent(List<UiElement> elements, Object pointer) {
		for (UiElement e : elements) {
			if (e.getPointer().equals(pointer)) {
				return elements;
			}
			List<UiElement> ret = findParent(e.getLayout().getElements(), pointer);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
	
	private int findTargetIndex(List<UiElement> elements, Object pointer) {
		int index = 0;
		for (UiElement e: elements) {
			if (e.getPointer().equals(pointer)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	public void moveUp(Object pointer) {
		List<UiElement> elements = findParent(this.elements, pointer);
		
		int index = findTargetIndex(elements, pointer);
		UiElement target = elements.get(index);
		
		if (index > 0) {
			elements.remove(index);
			elements.add( index-1, target);		
		}
	}

	public void moveDown(Object pointer) {
		List<UiElement> elements = findParent(this.elements, pointer);
		
		int index = findTargetIndex(elements, pointer);
		UiElement target = elements.get(index);
		
		if (index < elements.size()-1) {
			elements.remove(index);
			elements.add( index+1, target);		
		}
	}

	public void cancelSelection() {
		new ElementWalkThrough(this.elements) {
			@Override
			protected void handle(UiElement e) {
				e.setSelected(false);
			}
		};
	}

	public List<UiElement> getAllElements() {
		final List<UiElement> all = new ArrayList<UiElement>();
		new ElementWalkThrough(this.elements) {
			@Override
			protected void handle(UiElement e) {
				all.add(e);
			}
		};
		return all;
	}	
	
}
abstract class ElementWalkThrough {
	abstract protected void handle(UiElement e);
	public ElementWalkThrough(List<UiElement> elements) {
		walk(elements);
	}

	protected void walk(List<UiElement> elements) {
		for (UiElement e : elements) {
			handle(e);
			walk(e.getLayout().getElements());
		}
	}
}
