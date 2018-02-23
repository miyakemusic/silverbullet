package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import jp.silverbullet.dependency.speceditor2.DependencySpecHolderListener.ChangeType;
@XmlRootElement
public class DependencySpecHolder {
	
	private HashMap<String, DependencySpec> specs = new HashMap<String, DependencySpec>();
	
	@XmlTransient
	private Set<DependencySpecHolderListener> listeners = new HashSet<DependencySpecHolderListener>();
		
	public void addDependencySpecHolderListener(DependencySpecHolderListener listener) {
		this.listeners.add(listener);
	}
	
	public void addSpec(String id, String element, DependencyFormula specification) {
		if (!specs.keySet().contains(id)) {
			specs.put(id, new DependencySpec(id));
		}
		DependencySpec spec = specs.get(id);
		spec.add(element, specification);
		fireEvent(ChangeType.Append);
	}

	private void fireEvent(ChangeType type) {
		for (DependencySpecHolderListener listener :this.listeners) {
			listener.onUpdate(type);
		}
	}

	@XmlElementWrapper
	public Map<String, DependencySpec> getSpecs() {
		return specs;
	}

	public List<DependencySpecDetail> getActiveRelations(String id) {
		List<DependencySpecDetail> ret = new ArrayList<DependencySpecDetail>();
		for (String id2 : specs.keySet()) {
			DependencySpec spec = specs.get(id2);
			ret.addAll(spec.findRelations(id));
		}
		return ret;
	}

	public List<DependencySpecDetail> getPassiveRelations(String id) {
		if (!this.specs.containsKey(id)) {
			return new ArrayList<DependencySpecDetail>();
		}
		return this.specs.get(id).getSpecs();
	}

	public void removeSpec(String id, DependencySpecDetail detail) {
		this.specs.get(id).removeSpec(detail);
		this.fireEvent(ChangeType.Remove);
	}

	public void removeSpec(String id, String item, String equation) {
		for (DependencySpecDetail s:  this.specs.get(id).getSpecs()) {
			if (s.getPassiveElement().equals(item) && s.getSpecification().getSample().equals(equation)) {
				this.specs.get(id).removeSpec(s);
				break;
			}
		}
		this.fireEvent(ChangeType.Remove);
	}

	public List<DependencySpecDetail> getPassiveRelations(String id,
			String element) {
		List<DependencySpecDetail> ret = new ArrayList<DependencySpecDetail>();
		for (DependencySpecDetail r : getPassiveRelations(id)) {
			if (r.getPassiveElement().equals(element)) {
				ret.add(r);
			}
		}
		return ret;
	}

	public void copy(DependencySpecDetail detail, List<String> ids) {
		for (String id : ids) {
			this.addSpec(id, detail.getPassiveElement(), detail.getSpecification().clone());
		}
	}
	
	public void copy(String id, List<String> selected) {
		DependencySpec spec = this.specs.get(id);
		
		for (String id2 : selected) {
			DependencySpec spec2 = spec.clone();
			spec2.setId(id2);
			this.specs.put(id2, spec2);
//			this.specs.get(id2).getSpecs().clear();
//			for (DependencySpecDetail e : spec.getSpecs()) {
//				this.specs.put(id2, e.clone());
//			}
		}
	}

	public void removeDependencySpecHolderListener(
			DependencySpecHolderListener dependencySpecHolderListener) {
		this.listeners.remove(dependencySpecHolderListener);
	}
}
